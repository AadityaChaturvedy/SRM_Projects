/**
 * @file uart_tx.v
 * @brief A synthesizable Verilog module for a basic UART transmitter.
 * @author [Your Name/Project Group]
 * @date 2025-10-22
 *
 * @details This module implements a standard 8-N-1 (8 data bits, no parity, 1 stop bit) UART
 * transmitter. It takes an 8-bit data word and sends it serially when enabled.
 * The baud rate is determined by the `BIT_RATE` and `CLK_HZ` parameters.
 */

module uart_tx #(
    parameter BIT_RATE = 115200,    // Desired baud rate
    parameter CLK_HZ = 27000000     // System clock frequency in Hz
)(
    // --- Inputs ---
    input wire clk,                 // System clock
    input wire resetn,              // Active-low reset
    input wire uart_tx_en,          // Enable transmission for one byte
    input wire [7:0] uart_tx_data,  // 8-bit data to transmit
    
    // --- Outputs ---
    output reg uart_txd,            // UART transmit line
    output wire uart_tx_busy        // High when the transmitter is busy
);

// --- Local Parameters ---
localparam CYCLES_PER_BIT = CLK_HZ / BIT_RATE; // Number of clock cycles per bit period

// --- State Machine Definitions ---
localparam IDLE  = 2'd0; // Idle state, waiting for tx_en
localparam START = 2'd1; // Sending the start bit
localparam DATA  = 2'd2; // Sending the 8 data bits
localparam STOP  = 2'd3; // Sending the stop bit

// --- Registers ---
reg [1:0] state;                // Current state of the FSM
reg [15:0] cycle_counter;       // Counter for timing each bit
reg [2:0] bit_counter;          // Counts which data bit is being sent
reg [7:0] data_buffer;          // Buffer to hold the data being transmitted

// --- Assignments ---
// The transmitter is busy if it is not in the IDLE state.
assign uart_tx_busy = (state != IDLE);

// --- State Machine Logic ---
always @(posedge clk or negedge resetn) begin
    if (!resetn) begin
        // Reset all registers to a known state
        state <= IDLE;
        uart_txd <= 1'b1; // UART line is high when idle
        cycle_counter <= 16'd0;
        bit_counter <= 3'd0;
        data_buffer <= 8'd0;
    end else begin
        case (state)
            // IDLE State: Wait for the enable signal.
            IDLE: begin
                uart_txd <= 1'b1; // Keep the line high
                cycle_counter <= 16'd0;
                bit_counter <= 3'd0;
                if (uart_tx_en) begin
                    data_buffer <= uart_tx_data; // Latch the data to be sent
                    state <= START; // Move to the START state
                end
            end
            
            // START State: Send the start bit (logic 0).
            START: begin
                uart_txd <= 1'b0;
                if (cycle_counter < CYCLES_PER_BIT - 1) begin
                    cycle_counter <= cycle_counter + 1;
                end else begin
                    cycle_counter <= 16'd0;
                    state <= DATA; // Move to the DATA state
                end
            end
            
            // DATA State: Send the 8 data bits, LSB first.
            DATA: begin
                uart_txd <= data_buffer[bit_counter];
                if (cycle_counter < CYCLES_PER_BIT - 1) begin
                    cycle_counter <= cycle_counter + 1;
                end else begin
                    cycle_counter <= 16'd0;
                    if (bit_counter < 7) begin
                        bit_counter <= bit_counter + 1;
                    end else begin
                        bit_counter <= 3'd0;
                        state <= STOP; // All 8 bits sent, move to STOP state
                    end
                end
            end
            
            // STOP State: Send the stop bit (logic 1).
            STOP: begin
                uart_txd <= 1'b1;
                if (cycle_counter < CYCLES_PER_BIT - 1) begin
                    cycle_counter <= cycle_counter + 1;
                end else begin
                    cycle_counter <= 16'd0;
                    state <= IDLE; // Return to IDLE state
                end
            end
            
            default: state <= IDLE;
        endcase
    end
end

endmodule