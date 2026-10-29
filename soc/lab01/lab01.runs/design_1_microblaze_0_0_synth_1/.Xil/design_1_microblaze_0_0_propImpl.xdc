set_property SRC_FILE_INFO {cfile:c:/Users/gwert/itmo/soc/lab01/lab01.srcs/sources_1/bd/design_1/ip/design_1_microblaze_0_0/design_1_microblaze_0_0.xdc rfile:../../../lab01.srcs/sources_1/bd/design_1/ip/design_1_microblaze_0_0/design_1_microblaze_0_0.xdc id:1 order:EARLY scoped_inst:U0} [current_design]
current_instance U0
set_property src_info {type:SCOPED_XDC file:1 line:2 export:INPUT save:INPUT read:READ} [current_design]
create_waiver -internal -quiet -user microblaze -tags 12436 -type CDC -id CDC-26 -description "Invalid LUTRAM collision warning" -to [get_pins -quiet "LOCKSTEP_Out_reg\[*\]/R"]
set_property src_info {type:SCOPED_XDC file:1 line:4 export:INPUT save:INPUT read:READ} [current_design]
create_waiver -internal -quiet -user microblaze -tags 12436 -type CDC -id CDC-26 -description "Invalid LUTRAM collision warning" -to [get_pins -quiet "MicroBlaze_Core_I/*Interrupt_DFF/Single_Synchronize.use_sync_reset.sync_reg/D"]
