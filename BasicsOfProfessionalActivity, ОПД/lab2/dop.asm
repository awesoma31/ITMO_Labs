ORG 0x199

WORD 0x0100 ; R
WORD 0xC001 ; Y=-16383
WORD 0x0123 ; Z

CLA
LD 0x1A2 ; load X
ADD 0x19A ; x+y
OR 0x19B ; (x+y) | z
ST 0x199 ; store to R
HLT

WORD 0xC000 ; X=-16384






 