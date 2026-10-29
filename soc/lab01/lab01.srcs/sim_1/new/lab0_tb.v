`timescale 1ns / 1ps

module lab0_tb();

    localparam CLK_PERIOD = 10;            // 100 MHz

    reg         clk_100MHz  = 1'b0;
    reg         reset_rtl_0 = 1'b1;        
    wire [15:0] gpio_rtl_0_tri_o;

    wire ld0 = gpio_rtl_0_tri_o[0];
    wire ld1 = gpio_rtl_0_tri_o[1];

    realtime t_start = 0;
    realtime t_done  = 0;
    integer  k;
    reg [15:0] elem;
    time       t_elem;

    design_1_wrapper dev (
        .clk_100MHz      (clk_100MHz),
        .gpio_rtl_0_tri_o(gpio_rtl_0_tri_o),
        .reset_rtl_0     (reset_rtl_0)
    );

    always #(CLK_PERIOD / 2) clk_100MHz = ~clk_100MHz;

    initial begin
        reset_rtl_0 = 1'b1;
        #(20 * CLK_PERIOD);
        reset_rtl_0 = 1'b0;
    end

    // log every LED change
    always @(gpio_rtl_0_tri_o)
        $display("[%0t ns] LED = %b", $time, gpio_rtl_0_tri_o);

    initial begin
        $timeformat(-9, 0, "", 0);          // %t prints in ns
        @(posedge ld0);
        t_start = $realtime;
        $display("[%0t ns] START: LD0 on", $time);

        @(posedge ld1);
        t_done = $realtime;
        $display("[%0t ns] DONE: LD1 on", $time);
        $display("Algorithm time: %0.0f ns = %0d clock cycles at 100 MHz",
                 t_done - t_start, $rtoi((t_done - t_start) / CLK_PERIOD));

        // after LD1 the program shows C[i][j] one by one, with 0 in between.
        // An element is the value held right before each 0 gap: C[0][0] may
        // equal the LD1 code (0x0002), so the bus does not always change.
        for (k = 0; k < 25; k = k + 1) begin
            elem   = gpio_rtl_0_tri_o;
            t_elem = $time;
            @(gpio_rtl_0_tri_o);
            while (gpio_rtl_0_tri_o != 16'h0000) begin
                elem   = gpio_rtl_0_tri_o;
                t_elem = $time;
                @(gpio_rtl_0_tri_o);
            end
            $display("[%0t ns] C[%0d][%0d] = %0d", t_elem, k / 5, k % 5, elem);
            if (k < 24)
                @(gpio_rtl_0_tri_o);        // skip the gap, wait for next element
        end

        #(100 * CLK_PERIOD);
        $finish;
    end

endmodule
