# Simple demo of of the PCA9685 PWM servo/LED controller library.
# This will move channel 0 from min to max position repeatedly.
# Author: Tony DiCola
# License: Public Domain
from __future__ import division
import time
import sys

# Import the PCA9685 module.
import Adafruit_PCA9685


# Uncomment to enable debug output.
#import logging
#logging.basicConfig(level=logging.DEBUG)

# Initialise the PCA9685 using the default address (0x40).
pwm = Adafruit_PCA9685.PCA9685()

# Alternatively specify a different address and/or bus:
#pwm = Adafruit_PCA9685.PCA9685(address=0x41, busnum=2)

# Configure min and max servo pulse lengths
servo_min = 0  # Min pulse length out of 4096
servo_max = 4095  # Max pulse length out of 4096
forward = 1
turn_direction = 1
if len(sys.argv) != 0:
    servo_max = sys.argv[0]

if sys.argv[1] is not None:
    forward = sys.argv[1]

if sys.argv[2] is not None:
    turn_direction = sys.argv[2]


# Helper function to make setting a servo pulse width simpler.
def set_servo_pulse(channel, pulse):
    pulse_length = 1000000    # 1,000,000 us per second
    pulse_length //= 60       # 60 Hz
    print('{0}us per period'.format(pulse_length))
    pulse_length //= 4096     # 12 bits of resolution
    print('{0}us per bit'.format(pulse_length))
    pulse *= 1000
    pulse //= pulse_length
    pwm.set_pwm(channel, 0, pulse)


# Set frequency to 60hz, good for servos.
pwm.set_pwm_freq(60)

print('Moving servo on channel 0, press Ctrl-C to quit...')
# while True:
#     # Move servo on channel O between extremes.
#     pwm.set_pwm(7, 0, servo_min)
#     time.sleep(1)
#     pwm.set_pwm(7, 0, servo_max)
#     time.sleep(1)
if forward == 1
    pwm.set_pwm(0, 0, speed);
    pwm.set_pwm(1,0,0);
else
    pwm.set_pwm(0,0,0);
    pwm.set_pwm(1,0,speed);

if turn_direction == 1
    pwm.set_pwm(3, 0, turn_amount);
    pwm.set_pwm(4,0,0);
else
    pwm.set_pwm(3,0,0);
    pwm.set_pwm(4,0,turn_amount);
time.sleep(5)