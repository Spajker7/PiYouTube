# PiYouTube
DIAL YouTube player ( acts as a smart TV on your network ). Intended for Raspberry Pi, but can be run on any device supporting Chrome and ChromeDriver.

#### Wait, what?
Once you run the program, the YouTube app on your smartphone will be able to see your device and will allow you to play and control the playback of YouTube videos on that device, from your smarthphone, without any third-party apps installed on the smartphone itself.

# Installation
This project requires Java 1.8, Chrome(Chromium) and ChromeDriver.

To get Chromium and suitable ChromeDriver on Raspbian, follow these instructions:
1) Make sure you are running Raspbian Stretch
2) Make sure you have Chrome(Chromium) > 59.
    ```
    sudo apt-get install chromium
    ```
3) Get suitable chromedriver.
    For Raspbian do the following:
    ```
    sudo apt-get install libminizip1
    sudo apt-get install libwebpmux2
    ```
    
    If running Raspbian Lite ( without GUI, also run this)
    ``` 
    sudo apt-get install libgtk-3-0 
    ```
    
    Then run this. chromedriver should be installed under ```/usr/bin/```
    ```
    wget -O chromium.deb http://http.us.debian.org/debian/pool/main/c/chromium-browser/chromium-driver_61.0.3163.100-1~deb9u1_armhf.deb && sudo dpkg -i chromium.deb
    ```

Now you can grab a pre-built jar from this repository or build one by yourself from source. 

Run the jar with ``` sudo java -jar piyoutube.jar ```

If running for the first time, the program will exit. Simply edit out the newly generated ```config.json``` and put chromedriver's location under ```driver```.

Example for a Raspberry Pi:
```
{
    "iface": "all",
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
