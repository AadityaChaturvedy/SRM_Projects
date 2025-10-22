/**
 * @file performance_monitor_esp.v
 * @brief Verilog module for monitoring CPU performance and transmitting data to an ESP32 via UART.
 * @author [Your Name/Project Group]
 * @date 2025-10-22
 *
 * @details This module counts key performance metrics of a CPU, such as instructions executed,
 * branch mispredictions, and cycles. It calculates Cycles Per Instruction (CPI) and misprediction rate,
 * and periodically sends this data in a packetized format over a UART connection.
 * The module can switch between monitoring a single-cycle and a 5-stage pipelined CPU.
 */

module performance_monitor_esp (
    // --- Inputs ---
    input wire clk,                 // System clock
    input wire rst_n,               // Active-low reset
    input wire instr_valid,         // Signal indicating a valid instruction is being executed
    input wire is_branch,           // Signal indicating the current instruction is a branch
    input wire branch_taken,         // Signal indicating if the branch was actually taken
    input wire branch_predicted,    // Signal indicating the predicted outcome of the branch
    input wire [0:0] mode,          // CPU mode: 0 for single-cycle, 1 for 5-stage pipelined
    
    // --- Outputs ---
    output wire uart_tx             // UART transmit line
);

// --- Parameters ---
parameter CLK_FREQ = 27000000;      // Clock frequency in Hz (27 MHz)
parameter BAUD_RATE = 115200;       // UART baud rate
parameter UPDATE_INTERVAL = CLK_FREQ; // Interval for sending data (approx. 1 second)

// --- Registers and Wires ---
reg [31:0] cycle_counter;         // Counts total clock cycles
reg [31:0] instr_counter;         // Counts total valid instructions
reg [31:0] branch_counter;        // Counts total branch instructions
reg [31:0] mispred_counter;       // Counts total branch mispredictions

reg [31:0] pipe_cycle_counter;    // Estimated cycles for a pipelined CPU
reg [31:0] sing_cycle_counter;    // Estimated cycles for a single-cycle CPU

reg [31:0] current_cpi;           // Calculated CPI (16.16 fixed-point)
reg [31:0] current_mispred_rate;  // Calculated misprediction rate (16.16 fixed-point)

reg [27:0] update_timer;          // Timer to trigger UART transmission
reg update_trigger;               // Flag to start UART transmission

// --- UART Interface ---
reg uart_tx_en;                   // Enable signal for the UART transmitter
reg [7:0] uart_tx_data;           // 8-bit data to be transmitted
wire uart_tx_busy;                 // Busy signal from the UART transmitter

// --- UART Packet Transmission State Machine ---
reg [7:0] tx_state;               // State of the transmission FSM
reg [7:0] data_packet [0:26];     // Buffer for the data packet to be sent
reg [7:0] tx_index;               // Index for the data packet buffer

// --- Performance Counters ---
// This block increments the counters on each clock cycle based on the input signals.
always @(posedge clk or negedge rst_n) begin
    if (!rst_n) begin
        cycle_counter <= 0;
        instr_counter <= 0;
        branch_counter <= 0;
        mispred_counter <= 0;
    end else begin
        cycle_counter <= cycle_counter + 1;
        if (instr_valid) instr_counter <= instr_counter + 1;
        if (instr_valid && is_branch) branch_counter <= branch_counter + 1;
        // A misprediction occurs if the predicted outcome does not match the actual outcome
        if (instr_valid && is_branch && (branch_taken != branch_predicted))
            mispred_counter <= mispred_counter + 1;
    end
end

// --- Cycle Calculation for Different CPU Types ---
// This block provides a simplified model of cycle counts for each CPU type.
always @* begin
    // For a single-cycle CPU, instructions and cycles are the same.
    sing_cycle_counter = instr_counter;
    // For a 5-stage pipeline, total cycles is roughly instructions + 4 (for the initial fill).
    if (instr_counter > 0)
        pipe_cycle_counter = instr_counter + 4;
    else
        pipe_cycle_counter = 0;
end

// --- Update Timer ---
// This timer triggers a UART update at the specified UPDATE_INTERVAL.
always @(posedge clk or negedge rst_n) begin
    if (!rst_n) begin
        update_timer <= 0;
        update_trigger <= 0;
    end else if (update_timer >= UPDATE_INTERVAL) begin
        update_timer <= 0;
        update_trigger <= 1; // Set trigger to start transmission
    end else begin
        update_timer <= update_timer + 1;
        update_trigger <= 0;
    end
end

// --- Metric Calculation ---
// This block calculates CPI and misprediction rate when the update is triggered.
// The results are stored in 16.16 fixed-point format.
always @(posedge clk or negedge rst_n) begin
    if (!rst_n) begin
        current_cpi <= 0;
        current_mispred_rate <= 0;
    end else if (update_trigger && tx_state == 0) begin
        // Select the appropriate cycle counter based on the CPU mode
        if (mode == 1'b0) begin // Single-cycle mode
            current_cpi <= (instr_counter > 0) ? ((sing_cycle_counter << 16) / instr_counter) : 0;
        end else begin // Pipelined mode
            current_cpi <= (instr_counter > 0) ? ((pipe_cycle_counter << 16) / instr_counter) : 0;
        end
        // Calculate misprediction rate
        current_mispred_rate <= (branch_counter > 0) ? ((mispred_counter << 16) / branch_counter) : 0;
    end
end

// --- UART Transmit State Machine ---
// This FSM constructs and sends the data packet when triggered.
always @(posedge clk or negedge rst_n) begin
    if (!rst_n) begin
        tx_state <= 0;
        tx_index <= 0;
        uart_tx_en <= 0;
    end else begin
        case (tx_state)
            // State 0: Idle, waiting for trigger
            0: if (update_trigger) begin
                // --- Construct the data packet ---
                data_packet[0] <= 8'hAA; // Start byte 1
                data_packet[1] <= 8'h55; // Start byte 2
                // CPI (32-bit)
                data_packet[2] <= current_cpi[31:24];
                data_packet[3] <= current_cpi[23:16];
                data_packet[4] <= current_cpi[15:8];
                data_packet[5] <= current_cpi[7:0];
                // Misprediction count (32-bit)
                data_packet[6] <= mispred_counter[31:24];
                data_packet[7] <= mispred_counter[23:16];
                data_packet[8] <= mispred_counter[15:8];
                data_packet[9] <= mispred_counter[7:0];
                // Instruction count (32-bit)
                data_packet[10] <= instr_counter[31:24];
                data_packet[11] <= instr_counter[23:16];
                data_packet[12] <= instr_counter[15:8];
                data_packet[13] <= instr_counter[7:0];
                // Branch count (32-bit)
                data_packet[14] <= branch_counter[31:24];
                data_packet[15] <= branch_counter[23:16];
                data_packet[16] <= branch_counter[15:8];
                data_packet[17] <= branch_counter[7:0];
                // Misprediction rate (32-bit)
                data_packet[18] <= current_mispred_rate[31:24];
                data_packet[19] <= current_mispred_rate[23:16];
                data_packet[20] <= current_mispred_rate[15:8];
                data_packet[21] <= current_mispred_rate[7:0];
                // Single-cycle counter (upper 16 bits)
                data_packet[22] <= sing_cycle_counter[31:24];
                data_packet[23] <= sing_cycle_counter[23:16];
                // Pipelined cycle counter (upper 16 bits)
                data_packet[24] <= pipe_cycle_counter[31:24];
                data_packet[25] <= pipe_cycle_counter[23:16];
                // End byte
                data_packet[26] <= 8'hFF;
                
                tx_index <= 0;
                tx_state <= 1; // Move to sending state
            end
            // State 1: Sending data bytes
            1: if (!uart_tx_busy && tx_index < 27) begin
                uart_tx_data <= data_packet[tx_index];
                uart_tx_en <= 1;
                tx_index <= tx_index + 1;
            end else if (tx_index >= 27) begin
                tx_state <= 0; // Done sending, return to idle
                uart_tx_en <= 0;
            end else begin
                uart_tx_en <= 0; // Wait for UART to be ready
            end
            default: tx_state <= 0;
        endcase
    end
end

// --- UART Transmitter Instantiation ---
uart_tx #(
    .BIT_RATE(BAUD_RATE),
    .CLK_HZ(CLK_FREQ)
) uart_transmitter (
    .clk(clk),
    .resetn(rst_n),
    .uart_txd(uart_tx),
    .uart_tx_busy(uart_tx_busy),
    .uart_tx_en(uart_tx_en),
    .uart_tx_data(uart_tx_data)
);

endmodule