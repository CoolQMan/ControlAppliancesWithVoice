# Smart Home Voice Control

An Android application that allows you to control your home appliances using voice commands via Bluetooth communication with microcontroller hardware.

## Overview

Smart Home Voice Control is a modern solution for home automation developed by us. This application serves as a bridge between users and their home appliances, enabling control through an intuitive interface and voice commands. The app communicates with connected hardware via Bluetooth to toggle appliances on and off.

## Features

- **Dashboard Interface**: Easily view and control all connected appliances
- **Voice Command Control**: Control appliances using natural speech commands
- **Bluetooth Connectivity**: Seamlessly connect to your home automation hardware
- **Dark/Light Mode**: Choose your preferred visual theme
- **Appliance Management**: Add, rename, or delete appliances
- **Persistent Storage**: Appliance settings are preserved between sessions

## Requirements

- Android device running Android 7.0 (API level 24) or higher
- Bluetooth capability
- Microphone access for voice commands
- Compatible hardware setup (See Hardware Setup section)

## Installation

1. Download the latest APK from the [Releases](https://github.com/CoolQMan/ControlAppliancesWithVoice/releases) section
2. Enable installation from unknown sources in your device settings if required
3. Install the application
4. Grant necessary permissions when prompted (Bluetooth, Microphone)

## Usage

### Setting Up Appliances

1. Open the app and navigate to the Dashboard tab
2. Tap the "+" icon in the top-right corner to add a new appliance
3. Enter a unique name and ID for the appliance
4. The appliance will appear in your dashboard with a toggle switch

### Connecting to Hardware

1. Go to the Settings tab
2. Tap "Connect to Bluetooth Device" 
3. Select your HC-05 Bluetooth module from the list of paired devices
4. Once connected, the Bluetooth icon in the top bar will turn blue

### Using Voice Commands

1. Navigate to the Voice Command tab
2. Tap the microphone button to start listening
3. Speak commands in the format: "turn/switch on [appliance name]" or "turn/switch off [appliance name]"
4. The app will process your command and send it to the connected hardware

### Supported Voice Commands

- "Turn on [appliance name]" - Turns on the specified appliance
- "Turn off [appliance name]" - Turns off the specified appliance
- "Switch on [appliance name]" - Alternative command to turn on
- "Switch off [appliance name]" - Alternative command to turn off

## Architecture

The application follows a modular architecture:

- **MainActivity**: Main entry point and controller for navigation
- **DashboardFragment**: UI for displaying and interacting with appliances
- **VoiceFragment**: Handles speech recognition and command processing
- **SettingsFragment**: Manages app settings and Bluetooth connections
- **BluetoothManager**: Handles Bluetooth communication with hardware
- **ApplianceDatabaseHelper**: Manages SQLite database operations
- **ApplianceModel**: Data model for appliance objects

## Hardware Setup

> **Important:** This repository contains only the Android application. For the complete project including hardware setup, microcontroller code, and circuit diagrams, please visit our Hardware Repository (coming soon).

The hardware component uses:
- STM32 Blue Pill microcontroller
- HC-05 Bluetooth module
- Relay modules for appliance control
- Power supply circuitry

The hardware setup accepts commands in the format "A[ID]:[STATE]" where:
- ID is the appliance identifier
- STATE is either "ON" or "OFF"

## Permissions

The application requires the following permissions:
- `BLUETOOTH` - For connecting to hardware
- `BLUETOOTH_ADMIN` - For managing Bluetooth connections
- `BLUETOOTH_CONNECT` - For secure Bluetooth operations
- `BLUETOOTH_SCAN` - For discovering Bluetooth devices
- `RECORD_AUDIO` - For voice command functionality
- `INTERNET` - For speech recognition services

## Contributors

Developed by:
- Suyash
- Karik
- Lakshay

## License

MIT License

Copyright (c) 2025 Team 17

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

## Troubleshooting

### Common Issues

1. **Bluetooth Connection Fails**
   - Ensure the HC-05 module is powered on
   - Check that you've paired the device in Android's Bluetooth settings first
   - Try restarting both the application and the hardware

2. **Voice Commands Not Recognized**
   - Speak clearly and in a quiet environment
   - Make sure you're using the supported command format
   - Check that the appliance name exactly matches what's in your dashboard

3. **Appliances Not Responding**
   - Verify Bluetooth connection status
   - Ensure the hardware is properly set up and powered
   - Check that relays are properly connected to the appliances

---