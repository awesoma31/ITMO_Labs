/usr/local/bin/arduino-cli config init --config-file /etc/arduino-cli/arduino-cli.yaml
/usr/local/bin/arduino-cli core update-index
/usr/local/bin/arduino-cli core install arduino:avr
/usr/local/bin/arduino-cli compile --fqbn arduino:avr:nano /opt/cryptoterm/arduino/arduino_temperature
/usr/local/bin/arduino-cli upload -p /dev/ttyUSB0 --fqbn arduino:avr:nano /opt/cryptoterm/arduino/arduino_temperature