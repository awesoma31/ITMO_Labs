## Nexys A7-100T (xc7a100tcsg324-1) — lab01
## Pins taken from Digilent Nexys-A7-100T-Master.xdc
## Port names match design_1_wrapper (clk_100MHz, reset_rtl_0, gpio_rtl_0_tri_o)

## Clock 100 MHz
set_property -dict { PACKAGE_PIN E3  IOSTANDARD LVCMOS33 } [get_ports { clk_100MHz }]
create_clock -add -name sys_clk_pin -period 10.00 -waveform {0 5} [get_ports { clk_100MHz }]

## Reset, active HIGH -> BTNC (center button, 1 while pressed)
set_property -dict { PACKAGE_PIN N17 IOSTANDARD LVCMOS33 } [get_ports { reset_rtl_0 }]
## If you switch reset to ACTIVE LOW, use CPU_RESETN instead (0 while pressed):
#set_property -dict { PACKAGE_PIN C12 IOSTANDARD LVCMOS33 } [get_ports { reset_rtl_0 }]

## LEDs LD0..LD15 (AXI GPIO, 16 bit, All Outputs)
set_property -dict { PACKAGE_PIN H17 IOSTANDARD LVCMOS33 } [get_ports { gpio_rtl_0_tri_o[0] }]
set_property -dict { PACKAGE_PIN K15 IOSTANDARD LVCMOS33 } [get_ports { gpio_rtl_0_tri_o[1] }]
set_property -dict { PACKAGE_PIN J13 IOSTANDARD LVCMOS33 } [get_ports { gpio_rtl_0_tri_o[2] }]
set_property -dict { PACKAGE_PIN N14 IOSTANDARD LVCMOS33 } [get_ports { gpio_rtl_0_tri_o[3] }]
set_property -dict { PACKAGE_PIN R18 IOSTANDARD LVCMOS33 } [get_ports { gpio_rtl_0_tri_o[4] }]
set_property -dict { PACKAGE_PIN V17 IOSTANDARD LVCMOS33 } [get_ports { gpio_rtl_0_tri_o[5] }]
set_property -dict { PACKAGE_PIN U17 IOSTANDARD LVCMOS33 } [get_ports { gpio_rtl_0_tri_o[6] }]
set_property -dict { PACKAGE_PIN U16 IOSTANDARD LVCMOS33 } [get_ports { gpio_rtl_0_tri_o[7] }]
set_property -dict { PACKAGE_PIN V16 IOSTANDARD LVCMOS33 } [get_ports { gpio_rtl_0_tri_o[8] }]
set_property -dict { PACKAGE_PIN T15 IOSTANDARD LVCMOS33 } [get_ports { gpio_rtl_0_tri_o[9] }]
set_property -dict { PACKAGE_PIN U14 IOSTANDARD LVCMOS33 } [get_ports { gpio_rtl_0_tri_o[10] }]
set_property -dict { PACKAGE_PIN T16 IOSTANDARD LVCMOS33 } [get_ports { gpio_rtl_0_tri_o[11] }]
set_property -dict { PACKAGE_PIN V15 IOSTANDARD LVCMOS33 } [get_ports { gpio_rtl_0_tri_o[12] }]
set_property -dict { PACKAGE_PIN V14 IOSTANDARD LVCMOS33 } [get_ports { gpio_rtl_0_tri_o[13] }]
set_property -dict { PACKAGE_PIN V12 IOSTANDARD LVCMOS33 } [get_ports { gpio_rtl_0_tri_o[14] }]
set_property -dict { PACKAGE_PIN V11 IOSTANDARD LVCMOS33 } [get_ports { gpio_rtl_0_tri_o[15] }]

## Bitstream config
set_property CONFIG_VOLTAGE 3.3 [current_design]
set_property CFGBVS VCCO [current_design]
