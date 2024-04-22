#!/bin/bash

cd lab0
# ПУНКТ 3 ------------------------------------
chmod 777 roselia8
ln -s grimer9 ./roselia8/krookodilegrimer # 1 DONE
chmod u=wx,g=wx,u=rx roselia8

ln -s roselia8 Copy_13 # 2 DONE

chmod -R a+rwx cherubi0
cp -R cherubi0 ./cherubi0/grumpig # 3 Done

# cannot copy directory into itself
chmod  305 ./cherubi0/grumpig
chmod  046 ./cherubi0/smoochum
chmod  006 ./cherubi0/yanma
chmod 357 cherubi0

# grumpig: права 305
# smoochum: права 046
# yanma: права 006

chmod a+rwx cherubi0
cd cherubi0
# lab0/cherubi0/
chmod a+rwx yanma
cd -
# lab0
cat ./staryu5/happiny ./cherubi0/yanma > grimer9_36 # 4 DONE
cd cherubi0
# lab0/cherubi0/
chmod 006 yanma
cd -
# lab0
chmod 357 cherubi0


chmod a=rwx bellossom4
cp bellossom4 ./staryu5/haunter/ # 5 DONE
chmod 046 bellossom4


chmod -R a+rwx roselia8
chmod 777 volcarona6
cp volcarona6 ./roselia8/chinglingvolcarona # 6 DONE
cd roselia8
chmod 357 mandibuzz
chmod 060 krookodile
chmod 400 chingling
chmod 537 dragonair
chmod 004 joltik
cd -
chmod u=wx,g=wx,u=rx roselia8
chmod 006 volcarona6


chmod a=rwx bellossom4
chmod a=rwx staryu5
ln -P bellossom4 ./staryu5/parasectbellossom # 7 DONE
chmod 046 bellossom4
chmod 573 staryu5
