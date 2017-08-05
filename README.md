# Font Awesome Icon
We developers hate PNG and JPEG as they are not scalable. For one simple image asset we have to add multiple copies of same image for different resolution. So for day to day use cases we prefer SVG over PNG or JPEG. But wouldn't it be great if a huge list of scalable graphics made available to use which is not difficult to implement ? Well it is quite easy all thanks to Font Awesome. As the name suggests, it is basically a Font but instead of characters it represents icons which can be scalled up using `SP`. This can be set to any `TextView` as it's only a font at core.

This project showcases how to implement Font Awesome TTF in a TextView and by using this App you can generate the complete icon dictionary i.e. `icons.xml` file which can be used anywhere in Android project along with Font Awesome TTF to show scalable icon. 

App is available at Play Store : https://play.google.com/store/apps/details?id=com.sumit.fontawesomedemo

JSOUP library is used to parse Font Awesome web page and generate the icon name and unicode character dictionary which can be used just like any string resource. The `icons.xml` file is generated to device SD card (this requires write permission to storage).

More about JSOUP : `https://jsoup.org/` <br />
More about Font Awesome : `http://fontawesome.io/` <br />
Font Awesome icon list : `http://fontawesome.io/icons/`

To show any alert or message App uses Alerter which is quite cool, have a look here : `https://github.com/Tapadoo/Alerter`

#### Screenshots :

<p align="center">
  <img src="https://github.com/sumitsahoo/FontAwesomeIcon/blob/master/screenshots/device-2017-08-05-234922.png" width="350"/>
  <img src="https://github.com/sumitsahoo/FontAwesomeIcon/blob/master/screenshots/device-2017-08-05-234937.png" width="350"/>
  <img src="https://github.com/sumitsahoo/FontAwesomeIcon/blob/master/screenshots/device-2017-08-05-234950.png" width="350"/>
  <img src="https://github.com/sumitsahoo/FontAwesomeIcon/blob/master/screenshots/device-2017-08-05-235016.png" width="350"/>
  <img src="https://github.com/sumitsahoo/FontAwesomeIcon/blob/master/screenshots/device-2017-08-05-235041.png" width="350"/>
  <img src="https://github.com/sumitsahoo/FontAwesomeIcon/blob/master/screenshots/device-2017-08-05-235059.png" width="350"/>
</p><br />
Happy Coding :) <br /> 
Fork -> Improve -> Share -> Repeat

