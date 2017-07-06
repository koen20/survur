import time
import Adafruit_GPIO.SPI as SPI 
import Adafruit_SSD1306
import subprocess

# Raspberry Pi pin configuration:
RST = None     # on the PiOLED this pin isnt used

disp = Adafruit_SSD1306.SSD1306_128_64(rst=RST)
disp.begin()
# Clear display.
disp.clear()
disp.display()
