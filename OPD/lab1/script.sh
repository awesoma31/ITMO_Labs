#!/bin/bash

# ПУНКТ 1
cd lab0
# lab0/

chmod -R 777 .
rm -rf *

mkdir cherubi0
mkdir roselia8
mkdir staryu5

echo "Способности  Overgrow Chlorophyll Flower" > bellossom4
echo "Gift" >> bellossom4

echo "Живет" > grimer9
echo "Urban" >> grimer9

echo "Живет  Cave Mountain" > volcarona6

cd cherubi0
# lab0/cherubi0
mkdir grumpig

echo "Способности  Freezing Point Mind Mold Oblivious" > smoochum
echo "Forewarn" >> smoochum

echo "Способности  Foresight Tackle Quick Attack Double Team" > yanma
echo "Sonicboom Detect Supersonic Uproar Pursuit Ancientpower Hypnosis Wing" >> yanma
echo "Attack Screech U-Turn Air Slash Bug Buzz" >> yanma

echo "Способности  Vine" > bellsprout
echo "Whip Growth Wrap Sleep Powder Poisonpowder Stun Spore Acid Knock Off" >> bellsprout
echo "Sweet Scent Gastro Acid Razor Leaf Slam Wring Out" >> bellsprout

cd -
# lab0/
cd roselia8
# lab0/roselia8/
mkdir mandibuzz
mkdir dragonair

echo "Способности  Landslide Dark Art Intimidate" > krookodile
echo "Moxie" >> krookodile

echo "satk=7 sdef=5 spd=5" > chingling

echo "satk=6 sdef=5" > joltik
echo "spd=7" >> joltik

cd -
# lab0/
cd staryu5
# lab0/staryu5/
mkdir stunky
mkdir haunter

echo "Развитые Способности  Telepathy" > musharna

echo "Способности" > happiny
echo "Charm Pound Defence Curl Copycat Refresh Sweet" >> happiny
echo "Kiss" >> happiny

echo "Способности  Swarm Overgrow Effect Spore Dry" > parasect
echo "Skin" >> parasect

cd -
# lab0







# ПУНКТ 2 --------------------------------------
chmod 046 bellossom4
chmod u=r,g=r grimer9
chmod 006 volcarona6

cd cherubi0
# lab0/cherubi0/
chmod 046 smoochum
chmod 006 yanma
chmod 305 grumpig
chmod u=rw,g=w,u=w bellsprout

cd -
# lab0/
cd roselia8
# lab0/roselia8/

chmod 357 mandibuzz
chmod 060 krookodile
chmod 400 chingling
chmod 537 dragonair
chmod 004 joltik

cd -
# lab0/
cd staryu5
# lab0/staryu5/

chmod 404 musharna
chmod 577 stunky
chmod 404 happiny
chmod 752 haunter
chmod 604 parasect

cd -


chmod 357 cherubi0
chmod u=wx,g=wx,u=rx roselia8
chmod 573 staryu5
#lab0



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

# ПУНКТ 5 -------------------------------
rm -f bellossom4 # 1

rm -f ./cherubi0/smoochum # 2

chmod -R a+rwx ./roselia8
rm -f ./roselia8/krookodilegrim* # 3

chmod -R a+rwx ./staryu5
rm -f ./staryu5/parasectbelloss* # 4

chmod -R a+rwx ./staryu5
rmdir ./staryu5/stunky # 6

cd staryu5
rm -rf * # 5
cd -
rmdir staryu5

