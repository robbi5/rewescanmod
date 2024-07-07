reweScanMod
===========

A xposed module for the app of the german supermarket [REWE](https://www.rewe.de/) to enable [scanning of products (Scan&Go)](https://www.rewe.de/service/scan-and-go/) with the built-in imager of Zebra Touch Computers.

https://github.com/robbi5/rewescanmod/assets/172415/16dc0870-cee0-4b52-a888-fac95a2bee75

## Requirements (non-ROOT)
- Zebra Touch Computer Device (like TC27, ...)
- LSPatch (this fork works: https://github.com/JingMatrix/LSPatch)
- Shizuku (https://shizuku.rikka.app/)
- reweScanMod ([releases](https://github.com/robbi5/rewescanmod/releases))
- REWE app version 3.18.6* ([APKMirror](https://www.apkmirror.com/apk/rewe-markt-gmbh/rewe-angebote-coupons/rewe-angebote-coupons-3-18-6-release/rewe-online-supermarkt-3-18-6-android-apk-download/))

\* the version of the REWE app is pinned in the module (see `TARGET_REWE_VERSION` in [app/build.gradle.kts](app/build.gradle.kts))

## Installation
* Install Shizuku, install LSPatch
* Install REWE apk and the reweScanMod apk
* Open Shizuku, use one of the provided ways to run the service
* Open LSPatch, patch REWE-apk, install patched version
* Touch on REWE at the LSPatch app list, choose module scope and activate reweScanMod (don't forget to save with the button on the bottom right!)

### Installation of the Zebra Scan Intent
* Open Zebra Data Wedge Utility
* Create a new profile
* Associated apps: `de.rewe.app.mobile` (select `*` for Activity)
* Disable Keystroke Output
* Enable Intent Output
* Intent Action: `com.rewescanmod.SCAN`
* Intent Delivery: _Broadcast intent_

## Usage
* Open REWE app, choose Scan&Go
* Scan the entry aztec code provided at the stores entrance (or test with the one captured on [this press photo](https://www.imago-images.de/st/0401800361))
* Choose Scan Products
* Point your device to a products barcode and press one of the side buttons
* _\*beep\*_ 🥳