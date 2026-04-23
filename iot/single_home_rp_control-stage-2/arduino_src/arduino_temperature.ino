#include <Arduino.h>
#include <math.h>

const float SERIES_RESISTOR = 10000.0;     // Ом
const float NOMINAL_RESISTANCE = 10000.0;  // Ом
const float NOMINAL_TEMPERATURE = 25.0;    // °C
const float B_COEFFICIENT = 3950.0;        // B3950

const int analogPins[] = {A0, A1, A2, A3, A4, A5};
const int numPins = sizeof(analogPins)/sizeof(analogPins[0]);

int pinStringToNumber(const char *p) {
  if (p[0] == 'A' && isdigit(p[1])) {
    int idx = p[1] - '0';
    if (idx >= 0 && idx < numPins) return analogPins[idx];
  }
  return -1;
}


float readTemperature(int pin) {
  int adc = analogRead(pin);
  if (adc == 0) return NAN;
  float voltage = adc * (5.0 / 1023.0);
  float resistance = SERIES_RESISTOR / (1023.0 / adc - 1.0);
  float steinhart = resistance / NOMINAL_RESISTANCE;
  steinhart = log(steinhart);
  steinhart /= B_COEFFICIENT;
  steinhart += 1.0 / (NOMINAL_TEMPERATURE + 273.15);
  steinhart = 1.0 / steinhart;
  steinhart -= 273.15;
  return steinhart;
}

String input = "";

void setup() {
  Serial.begin(9600);
  delay(1000);
  Serial.println("Arduino Nano готов к измерениям");
}

void loop() {
  while (Serial.available()) {
    char ch = Serial.read();
    if (ch == '\n' || ch == '\r') {
      if (input.length() > 0) {
        String response = "[";
        bool first = true;
        int pos = 0;
        while (pos < input.length()) {
          int apos = input.indexOf('A', pos);
          if (apos == -1) break;
          int dpos = apos + 1;
          if (dpos < input.length() && isDigit(input[dpos])) {
            String pname = input.substring(apos, dpos + 1);
            int pnum = pinStringToNumber(pname.c_str());
            if (pnum >= 0) {
              float temp = readTemperature(pnum);
              if (!first) response += ",";
              response += isnan(temp) ? "null" : String(temp, 2);
              first = false;
            }
          }
          pos = apos + 1;
        }
        response += "]\n";
        Serial.print(response);
      }
      input = "";
    } else if (isPrintable(ch)) {
      input += ch;
    }
  }
}
