echo "installing deps..."

# Core runtime deps
sudo pip3 install Flask --break-system-packages
sudo pip3 install PyYAML --break-system-packages
sudo pip3 install requests --break-system-packages
sudo pip3 install paho-mqtt --break-system-packages
sudo pip3 install python-dotenv --break-system-packages

# Raspberry Pi GPIO (safe to install on Pi; may fail elsewhere)
sudo pip3 install RPi.GPIO --break-system-packages

# Arduino dependencies
curl -fsSL https://raw.githubusercontent.com/arduino/arduino-cli/master/install.sh | BINDIR=/usr/local/bin sh
