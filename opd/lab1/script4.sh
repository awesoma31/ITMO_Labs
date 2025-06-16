#!/bin/bash

cd lab0
# ПУНКТ 4 ---------------------------------------------
shopt -s globstar
wc -m **/m* | sort # 1 DONE

# ls -Rltr ./*/h* | tail -4 2>/dev/null # 2 ???
ls -Rltr **/h* | tail -4 2>/dev/null


cat **/b* 2>&1 | sort # 3


ls -ltrR ./staryu5 2>/tmp/s409856_p4_4 # 4 Done -r ???


ls -lR ./roselia8 | sort -r 2>&1 # 5 Done ????


# smoochum, yanma, bellsprout, krookodile, chingling, joltik, musharna

cat ./cherubi0/smoochum ./cherubi0/yanma ./cherubi0/bellsprout ./roselia8/krookodile ./roselia8/chingling ./roselia8/joltik ./staryu5/musharna | wc -l > /tmp/s409856_p4_5 # 6 Done


