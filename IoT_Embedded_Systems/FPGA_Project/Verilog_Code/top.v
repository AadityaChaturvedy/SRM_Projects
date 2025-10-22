/**
 * @file top.v
 * @brief Top-level module for the FPGA project, integrating the performance monitor.
 * @author [Your Name/Project Group]
 * @date 2025-10-22
 *
 * @details This module serves as the main wrapper for the project. It instantiates the
 * `performance_monitor_esp` module and provides it with generated test signals to simulate
 * CPU activity. It also includes a simple LED blinker for a visual indication that the
 * FPGA is running.
 */

module top (
    // --- Inputs ---
    input wire sys_clk,     // System clock
    input wire sys_rst_n,   // Active-low system reset
    
    // --- Outputs ---
    output wire uart_tx_esp, // UART transmit line to the ESP32
    output wire led          // LED for status indication
);

// --- LED Blinker ---
// A simple counter to make the onboard LED blink, confirming the clock is running.
reg [22:0] blink_counter;
always @(posedge sys_clk or negedge sys_rst_n)
    if (!sys_rst_n)
        blink_counter <= 0;
    else
        blink_counter <= blink_counter + 1;

// The LED will toggle based on one of the higher bits of the counter.
assign led = blink_counter[21];

// --- Test Signal Generation ---
// These registers generate a pseudo-random stream of signals to simulate a CPU's behavior
// for testing the performance monitor.
reg [23:0] counter;             // A free-running counter to drive the test signals
reg test_instr_valid;           // Simulated instruction valid signal
reg test_is_branch;             // Simulated is_branch signal
reg test_branch_taken;          // Simulated branch_taken signal
reg test_branch_predicted;      // Simulated branch_predicted signal
reg mode;                       // Simulated CPU mode (0 for single-cycle, 1 for pipelined)

// --- Instantiate the Performance Monitor ---
performance_monitor_esp perf_mon (
    .clk(sys_clk),
    .rst_n(sys_rst_n),
    .instr_valid(test_instr_valid),
    .is_branch(test_is_branch),
    .branch_taken(test_branch_taken),
    .branch_predicted(test_branch_predicted),
    .mode(mode),
    .uart_tx(uart_tx_esp)
);

// --- Test Signal Logic ---
// This block generates changing test patterns based on the free-running counter.
always @(posedge sys_clk or negedge sys_rst_n) begin
    if (!sys_rst_n) begin
        counter <= 24'd0;
        test_instr_valid <= 1'b0;
        test_is_branch <= 1'b0;
        test_branch_taken <= 1'b0;
        test_branch_predicted <= 1'b0;
        mode <= 1'b0; // Default to single-cycle mode
    end else begin
        counter <= counter + 1;
        // Generate a pulse for instr_valid periodically
        test_instr_valid <= (counter[6:0] == 7'd0);
        // Generate a pattern for is_branch
        test_is_branch <= (counter[7:6] == 2'b00);
        // Generate changing patterns for taken and predicted signals
        test_branch_taken <= counter[8];
        test_branch_predicted <= counter[9];
        
        // Example of how to switch mode during simulation for testing purposes
        // mode <= (counter[19]); // Toggles the mode when the 19th bit of the counter is high
    end
end

endmodule