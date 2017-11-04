# PiYouTube
DIAL YouTube player ( acts as a smart TV on your network ). Intended for Raspberry Pi, but can be run on any device supporting Chrome and ChromeDriver.

#### Wait, what?
Once you run the program, the YouTube app on your smartphone will be able to see your device and will allow you to play and control the playback of YouTube videos on that device, from your smartphone, without any third-party apps installed on the smartphone itself.

# Installation
This project requires Java 1.8, Chrome(Chromium) and ChromeDriver.

This is the fastest way to get Chromium and suitable ChromeDriver on Raspbian **Stretch**:
1) Add ```deb http://security.debian.org/debian-security stretch/updates main``` to ```/etc/apt/sources.list```
    ```
    sudo nano /etc/apt/sources.list
    ```
2) Refresh package lists
    ```
    sudo apt-get update && sudo apt-get upgrade
    ```
    
3) Remove any previous installations of chromium/chrome.
4) Install chromium and chromedriver
    ```
    sudo apt-get install chromium
    sudo apt-get install chromium-driver
    ```
   For other systems, check this [link](https://sites.google.com/a/chromium.org/chromedriver/downloads).
   
Now you can clone this repository by doing 
```git clone https://github.com/Spajker7/PiYouTube/```
After this you can just do ```./run_rpi_video.sh``` or ```./run_rpi_audio.sh```

If running for the first time, the program will exit. Simply edit out the newly generated ```config.json``` and put chromedriver's location under ```driver```.
**For Linux, you must also define a network interface.**

Example for a Raspberry Pi:
```
{
    "iface": "wlan0",
    "chromeParams": [
        "--headless",
        "--disable-gpu"
    ],
    "driver": "/usr/bin/chromedriver",
    "chrome": "auto",
    "name": "PiYouTube Player"
}
```

# Contribution

I welcome any contribution to the project. If you encounter any issues, please open an Issue here, in this repository.

# License
The program is distributed under [GNU GPLv3 license](https://www.gnu.org/licenses/gpl-3.0.en.htmlm).
