# services setup
sudo cp ./services/* $1/etc/systemd/system/

sudo ln -s $1/etc/systemd/system/wifi-setup.service $1/etc/systemd/system/multi-user.target.wants/

# config setup
sudo mkdir $1/etc/cryptoterm
sudo mkdir $1/etc/cryptoterm/config
sudo cp ./config/* $1/etc/cryptoterm/config/

# initializers setups
sudo mkdir $1/opt/cryptoterm
sudo mkdir $1/opt/cryptoterm/init
sudo cp ./setups/* $1/opt/cryptoterm/init

# initializing mql
sudo mkdir $1/opt/cryptoterm/mql
sudo cp -r ./mql $1/opt/cryptoterm
sudo cp ./mql/.env $1/opt/cryptoterm/mql
# removing all unnecessary stuff
sudo rm -rf $1/opt/cryptoterm/mql/tests
sudo rm -rf $1/opt/cryptoterm/mql/.gitignore
sudo rm -rf $1/opt/cryptoterm/mql/.git
sudo rm -rf $1/opt/cryptoterm/mql/Readme.md

# initializing notification service
sudo mkdir $1/opt/cryptoterm/notif
sudo cp ./notification_service/* $1/opt/cryptoterm/notif

# initializing arduino src directory
sudo mkdir $1/opt/cryptoterm/arduino
sudo mkdir $1/opt/cryptoterm/arduino/arduino_temperature
sudo cp ./arduino_src/arduino_temperature.ino $1/opt/cryptoterm/arduino/arduino_temperature

# initializing log directory
sudo mkdir $1/var/log/cryptoterm