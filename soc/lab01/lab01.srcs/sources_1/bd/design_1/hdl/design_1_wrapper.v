//Copyright 1986-2019 Xilinx, Inc. All Rights Reserved.
//--------------------------------------------------------------------------------
//Tool Version: Vivado v.2019.1 (win64) Build 2552052 Fri May 24 14:49:42 MDT 2019
//Date        : Thu Oct 29 11:48:19 2026
//Host        : awesoma-win running 64-bit major release  (build 9200)
//Command     : generate_target design_1_wrapper.bd
//Design      : design_1_wrapper
//Purpose     : IP block netlist
//--------------------------------------------------------------------------------
`timescale 1 ps / 1 ps

module design_1_wrapper
   (clk_100MHz,
    gpio_rtl_0_tri_o,
    reset_rtl_0);
  input clk_100MHz;
  output [15:0]gpio_rtl_0_tri_o;
  input reset_rtl_0;

  wire clk_100MHz;
  wire [15:0]gpio_rtl_0_tri_o;
  wire reset_rtl_0;

  design_1 design_1_i
       (.clk_100MHz(clk_100MHz),
        .gpio_rtl_0_tri_o(gpio_rtl_0_tri_o),
        .reset_rtl_0(reset_rtl_0));
endmodule
