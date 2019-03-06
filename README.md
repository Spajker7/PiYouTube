# PiYouTube
DIAL YouTube player ( acts as a smart TV on your network ). Intended for Raspberry Pi, but can be run on any device.

#### Wait, what?
Once you run the program, the YouTube app on your smartphone will be able to see your device and will allow you to play and control the playback of YouTube videos on that device, from your smartphone, without any third-party apps installed on the smartphone itself.

# Installation
This project requires Java 1.8 and any web browser.
   
You can grab an already compiled JAR file from the [Releases](https://github.com/Spajker7/PiYouTube/releases) page.

To run it, simply do ```java -jar piyoutube.jar```

If running for the first time, the program will exit. Simply edit out the newly generated ```config.json``` and set the appropriate config.

# Configuration

```config.json``` file contains the program configuration. Running the program will generate an empty config file.
These are the available configuration options:
* ```iface``` - Specifies which network interface to use
* ```name``` - Specifies device name
* ```command``` Specifies which command to run to open the web-browser. ```{url}``` will be replaced by a youtube link.

**For Linux, you must define a specific network interface. ( wlan0 for wifi or eth0 for ethernet)**

Example config for a Raspberry Pi:
```
{
    "iface": "wlan0",
    "name": "PiYouTube Player",
    "command": "chromium-browser --new-window --kiosk --fullscreen {url}"
}
```

Example config for Windows:
```
{
    "iface": "all",
    "name": "YouTube Player",
    "command": "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe --new-window --kiosk --fullscreen {url}"
}
```

# Contribution

I welcome any contribution to the project. If you encounter any issues, please open an Issue here, in this repository.

# License
The program is distributed under [GNU GPLv3 license](https://www.gnu.org/licenses/gpl-3.0.en.htmlm).
