package com.sumit.fontawesomeicon.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;

import com.sumit.fontawesomeicon.R;
import com.sumit.fontawesomeicon.model.FontAwesomeIcon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by Sumit on 7/27/2017.
 */

public class Util {

    public static final String DIR_NAME = "FontAwesome";
    public static final String FILE_NAME = "icons.xml";

    public static int getRandomColor() {
        Random random = new Random();
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public static String getRandomFontAwesomeString(Context context) {
        String[] faIcons = context.getResources().getStringArray(R.array.array_fa_icons);
        return faIcons[new Random().nextInt(faIcons.length)];
    }

    // Check network connectivity

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String createXmlContent(ArrayList<FontAwesomeIcon> fontAwesomeIcons) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());

        String xmlContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "\n<resources>"
                + "\n\n<!-- Source : http://fontawesome.io/cheatsheet/ -->"
                + "\n<!-- Generated On : "
                + currentDateTime
                + " -->\n";

        for (FontAwesomeIcon fontAwesomeIcon : fontAwesomeIcons) {
            xmlContent += "\n\t<string name=\"" + fontAwesomeIcon.getIconClassName() + "\">" + fontAwesomeIcon.getIconUnicode() + "</string>";
        }

        xmlContent += "\n\n </resources>";

        return xmlContent;
    }

    public static boolean isWriteOnExternalStorageAllowed() {
        // get the state of external storage
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static String exportXmlToSdCard(Context context, String xmlContent) {

        if(isWriteOnExternalStorageAllowed()){
            // get the path to sdcard
            File sdcard = Environment.getExternalStorageDirectory();
            // to this path add a new directory path
            File dir = new File(sdcard.getAbsolutePath() + "/" + DIR_NAME + "/");
            // create this directory if not already created
            if(!dir.exists())
                dir.mkdir();

            // create the file in which we will write the contents
            File file = new File(dir, FILE_NAME);
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(xmlContent.getBytes());
                fileOutputStream.close();

                // Initiate media scan and put the new things into the path array to
                // make the scanner aware of the location and the files you want to see in PC via MTP protocol

                Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri fileContentUri = Uri.fromFile(file);
                mediaScannerIntent.setData(fileContentUri);
                context.sendBroadcast(mediaScannerIntent);

                return fileContentUri.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
