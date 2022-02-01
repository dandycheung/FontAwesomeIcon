package com.sumit.fontawesomeicon.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.sumit.fontawesomeicon.R;
import com.sumit.fontawesomeicon.model.fa.FAIcon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class Util {
    public static final String DIR_NAME = "FontAwesome";
    public static final String FILE_NAME = "icons.xml";

    // Generate random color in ARGB
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

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on)
            winParams.flags |= bits;
        else
            winParams.flags &= ~bits;

        win.setAttributes(winParams);
    }

    // Get JSON string file from raw resource
    public static String getJsonStringFromRawFile(Context context, int rawId) {
        String json = null;

        InputStream inputStream = context.getResources().openRawResource(rawId);
        try {
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return json;
    }

    public static ArrayList<FAIcon> getAllIcons(Context context) {
        return new ArrayList<>(Arrays.asList(new Gson().fromJson(getJsonStringFromRawFile(context, R.raw.icons), FAIcon[].class)));
    }

    // Get free icons from JSON
    public static ArrayList<FAIcon> getFreeToUseSolidIconList(ArrayList<FAIcon> icons) {
        ArrayList<FAIcon> freeToUseIcons = new ArrayList<>();

        int sequence = 0;
        for (FAIcon faIcon : icons) {
            try {
                if (faIcon.getAttributes().getMembership().getFree().contains("solid")) {
                    faIcon.getAttributes().setUnicode("&#x" + faIcon.getAttributes().getUnicode() + ";");
                    faIcon.getAttributes().setIconColor(getRandomColor());
                    faIcon.setSequence(sequence++);

                    freeToUseIcons.add(faIcon);
                }
            } catch (Exception ignored) {
            }
        }

        return freeToUseIcons;
    }

    // Generate icons.xml content
    public static String createXmlContent(ArrayList<FAIcon> fontAwesomeIcons) {
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
        // Remove below comments to add string array

        // xmlContent += "\n\n" + generateIconUnicodeArray(fontAwesomeIcons);
        // xmlContent += "\n\n" + generateIconClassNameArray(fontAwesomeIcons);

        xmlContent += "\n\n </resources>";

        return xmlContent;
    }

    // Generate icon unicode string resource
    private static String generateIconStringResource(ArrayList<FAIcon> fontAwesomeIcons) {
        StringBuilder iconStringResource = new StringBuilder();

        for (FAIcon fontAwesomeIcon : fontAwesomeIcons) {
            iconStringResource.append("\n\t<string name=\"")
                .append(fontAwesomeIcon.getId().replace("-", "_"))
                .append("\">")
                .append(fontAwesomeIcon.getAttributes().getUnicode())
                .append("</string>");
        }

        return iconStringResource.toString();
    }

    private static String generateIconUnicodeArray(ArrayList<FAIcon> fontAwesomeIcons) {
        StringBuilder iconUnicodeArray = new StringBuilder("\t<string-array name=\"array_fa_icon_unicode\">");

        for (FAIcon fontAwesomeIcon : fontAwesomeIcons) {
            iconUnicodeArray.append("\n\t\t<item>@string/")
                .append(fontAwesomeIcon.getId().replace("-", "_"))
                .append("</item>");
        }

        iconUnicodeArray.append("\n\t</string-array>");

        return iconUnicodeArray.toString();
    }

    private static String generateIconClassNameArray(ArrayList<FAIcon> fontAwesomeIcons) {
        StringBuilder iconClassNameArray = new StringBuilder("\t<string-array name=\"array_fa_icon_class_name\">");

        for (FAIcon fontAwesomeIcon : fontAwesomeIcons) {
            iconClassNameArray.append("\n\t\t<item>")
                .append(fontAwesomeIcon.getId().replace("-", "_"))
                .append("</item>");
        }

        iconClassNameArray.append("\n\t</string-array>");

        return iconClassNameArray.toString();
    }

    // Check if write permission is allowed
    private static boolean isWriteOnExternalStorageAllowed() {
        // get the state of external storage
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    // Save generated XML content to SD storage
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

            // Delete file if already exists
            if (file.exists())
                file.delete();

            // Create new file
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(file);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
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
