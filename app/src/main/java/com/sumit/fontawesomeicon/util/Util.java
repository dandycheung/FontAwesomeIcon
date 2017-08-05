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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
        String[] faIcons = context.getResources().getStringArray(R.array.array_fa_icon_unicode);
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

        xmlContent += generateIconStringResource(fontAwesomeIcons);

        // To display all icons an array is needed both for unicode and icon names

        xmlContent += "\n\n" + generateIconUnicodeArray(fontAwesomeIcons);
        xmlContent += "\n\n" + generateIconClassNameArray(fontAwesomeIcons);

        xmlContent += "\n\n </resources>";

        return xmlContent;
    }

    private static String generateIconStringResource(ArrayList<FontAwesomeIcon> fontAwesomeIcons) {

        String iconStringResource = "";

        for (FontAwesomeIcon fontAwesomeIcon : fontAwesomeIcons) {
            iconStringResource += "\n\t<string name=\"" + fontAwesomeIcon.getIconClassName() + "\">" + fontAwesomeIcon.getIconUnicode() + "</string>";
        }

        return iconStringResource;
    }

    private static String generateIconUnicodeArray(ArrayList<FontAwesomeIcon> fontAwesomeIcons) {
        String iconUnicodeArray = "\t<string-array name=\"array_fa_icon_unicode\">";

        for (FontAwesomeIcon fontAwesomeIcon : fontAwesomeIcons) {
            iconUnicodeArray += "\n\t\t<item>@string/" + fontAwesomeIcon.getIconClassName() + "</item>";
        }

        iconUnicodeArray += "\n\t</string-array>";

        return iconUnicodeArray;
    }

    private static String generateIconClassNameArray(ArrayList<FontAwesomeIcon> fontAwesomeIcons) {
        String iconClassNameArray = "\t<string-array name=\"array_fa_icon_class_name\">";

        for (FontAwesomeIcon fontAwesomeIcon : fontAwesomeIcons) {
            iconClassNameArray += "\n\t\t<item>" + fontAwesomeIcon.getIconClassName() + "</item>";
        }

        iconClassNameArray += "\n\t</string-array>";

        return iconClassNameArray;
    }

    private static boolean isWriteOnExternalStorageAllowed() {
        // get the state of external storage
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static String exportXmlToSdCard(Context context, String xmlContent) {

        if (isWriteOnExternalStorageAllowed()) {
            // get the path to sdcard
            File sdcard = Environment.getExternalStorageDirectory();
            // to this path add a new directory path
            File dir = new File(sdcard.getAbsolutePath() + "/" + DIR_NAME + "/");
            // create this directory if not already created
            if (!dir.exists())
                dir.mkdir();

            // create the file in which we will write the contents
            File file = new File(dir, FILE_NAME);
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(file);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                bufferedWriter.write(xmlContent, 0, xmlContent.length());

                fileOutputStream.flush();
                outputStreamWriter.flush();
                bufferedWriter.flush();

                bufferedWriter.close();
                outputStreamWriter.close();
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
