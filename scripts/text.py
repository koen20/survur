import time
import sys
import Adafruit_GPIO.SPI as SPI 
import Adafruit_SSD1306
from PIL import Image 
from PIL import ImageDraw
from PIL import ImageFont
import subprocess

# Raspberry Pi pin configuration:
RST = None     # on the PiOLED this pin isnt used

disp = Adafruit_SSD1306.SSD1306_128_64(rst=RST)
disp.begin()
# Clear display.
disp.clear()
disp.display()

image = Image.new('1', (disp.width, disp.height))
draw = ImageDraw.Draw(image)
#font = ImageFont.load_default()
font = ImageFont.truetype('/usr/share/fonts/truetype/roboto/Roboto-Thin.ttf', 29)
draw.text((1, 1),sys.argv[1], font=font, fill=255)
draw.text((1, 26),sys.argv[2], font=font, fill=255)
disp.image(image)
disp.display()