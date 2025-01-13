# DarkEase
### A Guiding Light for Peaceful and Secure Sleep for Users with Nyctophobia or Similar Preferences

## Project Overview

This is the mobile application of the project DarkEase that monitors heartbeat and blood oxygen level and control smart lighting to make smooth sleep environment.

## Features

### Real-time sensor value Monitoring:
- Monitor real time heartbeat and SPO2 levels.

### MQTT Communication:
- Streamlined communication with raspberrypi using MQTT message broker.

### Manual lighting mode
- Allow user to swich on, off and dim the light manually

### Automatic lighting mode
- Allow app to swich on, off and dim the light according to the sleep mode detected from real time sensor values

### View sleep history
- Allow user to view the history of sleep records and bulb status.

## Prerequisites

### Hardware Requirements:
- A Raspberry Pi 2 Model B
- MAX30102 ( Heartbeat and SPO2 sensor module)
- LEDVANCE SMART+ Bulb

### Software Requirements:
- MQTT Broker
- Android device.(minSdk = 24)
- LEDVANCE SMART+ App
- SmartThings 

### Network Configuration:
- Raspberry Pi and Android device must be connected to the same network for optimal performance.

## Installation
1. Clone this repository from github: https://github.com/SakuGunarathna/IoT-Mobile.
2. Open in Android Studio.
3. Build the project with branch release/1.0.0
4. Connect Android device to use as emulator.
5. Run the app.
