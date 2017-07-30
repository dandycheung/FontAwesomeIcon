package com.sumit.fontawesomeicon;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sumit.fontawesomeicon.adapter.DataAdapter;
import com.sumit.fontawesomeicon.model.FontAwesomeIcon;
import com.sumit.fontawesomeicon.util.FontAwesomeHtmlParser;
import com.sumit.fontawesomeicon.util.FontManager;
import com.sumit.fontawesomeicon.util.Util;
import com.tapadoo.alerter.Alerter;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Sumit on 7/27/2017.
 */

public class MainActivity extends AppCompatActivity {

    private final int GRID_COLUMN = 4;
    private final int ALERTER_DURATION_IN_MILLIS = 8000;
    private final int PERMISSIONS_REQUEST_CODE = 101;

    private TextView textViewFab;
    private Context context;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    private HtmlParserTask htmlParserTask;
    private String generatedXmlFilePath;
    private int externalStoragePermissionCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        initViews();
        initFontAwesome();
        resetAdapter(getIconArrayList());
    }

    private void resetAdapter(ArrayList<FontAwesomeIcon> fontAwesomeIcons) {

        DataAdapter adapter = new DataAdapter(getApplicationContext(), fontAwesomeIcons);
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<FontAwesomeIcon> getIconArrayList() {

        ArrayList<FontAwesomeIcon> fontAwesomeIcons = new ArrayList<>();
        String[] faIcons = context.getResources().getStringArray(R.array.array_fa_icons);

        for (int i = 0; i < faIcons.length; i++) {
            FontAwesomeIcon fontAwesomeIcon = new FontAwesomeIcon();
            fontAwesomeIcon.setIconUnicode(faIcons[i]);
            fontAwesomeIcon.setIconColor(Util.getRandomColor());
            fontAwesomeIcon.setIconClassName("#" + (i + 1));
            fontAwesomeIcon.setId(i);
            fontAwesomeIcons.add(fontAwesomeIcon);
        }

        return fontAwesomeIcons;
    }

    private void initViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textViewFab = (TextView) findViewById(R.id.text_fab);
        textViewFab.setText(getString(R.string.fa_paint_brush));

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), GRID_COLUMN);
        recyclerView.setLayoutManager(layoutManager);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetAdapter(getIconArrayList());
            }
        });
    }

    private void initFontAwesome() {
        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONT_AWESOME);
        FontManager.markAsIconContainer(textViewFab, iconFont);
    }

    // Request storage access permission

    private void verifyAndRequestPermission() {

        // For marshmallow and above we need to manually check and prompt for permission

        externalStoragePermissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);


        if (externalStoragePermissionCheck != PackageManager.PERMISSION_GRANTED) {

            // Ask for permission

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_CODE);
        } else {
            // Permission is already granted
            initHtmlParserTask();
        }

    }

    // Permission request accept/deny callback

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        externalStoragePermissionCheck = PackageManager.PERMISSION_GRANTED;
                        initHtmlParserTask();
                    } else {
                        // Permission not granted
                        Alerter.create((MainActivity) context)
                                .setTitle(getString(R.string.error))
                                .setBackgroundColorRes(R.color.red_500)
                                .setText(getString(R.string.error_permission_denied))
                                .setDuration(ALERTER_DURATION_IN_MILLIS)
                                .show();
                    }
                }
            }
        }
    }

    private void viewXmlFile(String path) {

        File xmlFile = new File(path);

        if (xmlFile.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            // From API 24 onwards file:// Uri sharing is not allowed through intent.
            // Use FileProvider to share Uri instead

            Uri xmlFileUri = FileProvider.getUriForFile(MainActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    xmlFile);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(xmlFileUri, "text/xml");

            if (intent.resolveActivity(getPackageManager()) != null) {
                // If multiple intents are available then an intent chooser will be shown
                startActivity(intent);
            }
        }
    }

    private boolean isViewIntentAvailable(String path) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(path);
        intent.setDataAndType(uri, "text/xml");

        return intent.resolveActivity(getPackageManager()) != null;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Alerter.create((MainActivity) context)
                    .setTitle(getString(R.string.action_about))
                    .setBackgroundColorRes(R.color.colorPrimary)
                    .setText(getString(R.string.about_app_description))
                    .setDuration(ALERTER_DURATION_IN_MILLIS)
                    .show();
        } else if (id == R.id.action_export) {

            // Ask for permission if Android version is greater or equal to Marshmallow

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
                verifyAndRequestPermission();
            else initHtmlParserTask();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initHtmlParserTask() {

        if (htmlParserTask != null) {
            htmlParserTask.cancel(true);
            htmlParserTask = null;
        }

        htmlParserTask = new HtmlParserTask();
        htmlParserTask.execute();
    }

    // Initiate FontAwesome HTML Page parsing

    private class HtmlParserTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            if (Util.isNetworkAvailable(context)) {
                ArrayList<FontAwesomeIcon> fontAwesomeIcons = FontAwesomeHtmlParser.getAllFontIconList(context);
                if (fontAwesomeIcons != null && fontAwesomeIcons.size() > 0) {
                    return Util.exportXmlToSdCard(context, Util.createXmlContent(fontAwesomeIcons));

                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(final String filePath) {
            super.onPostExecute(filePath);

            // Show Success/Failure Message

            if (StringUtils.isNotEmpty(filePath)) {

                generatedXmlFilePath = Environment.getExternalStorageDirectory() + "/" + Util.DIR_NAME + "/" + Util.FILE_NAME;

                String message = getString(R.string.file_copy) + " " + generatedXmlFilePath;
                final boolean isEnableClickEvent = isViewIntentAvailable(generatedXmlFilePath);

                if (isEnableClickEvent)
                    message += getString(R.string.view_file) + " " + getString(R.string.swipe_to_dismiss);
                else message += "\n" + getString(R.string.swipe_to_dismiss);

                Alerter.create((MainActivity) context)
                        .setTitle(getString(R.string.success))
                        .setBackgroundColorRes(R.color.green_500)
                        .setText(message)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (isEnableClickEvent)
                                    viewXmlFile(generatedXmlFilePath);
                            }
                        })
                        .setDuration(ALERTER_DURATION_IN_MILLIS)
                        .enableSwipeToDismiss()
                        .show();
            } else {
                Alerter.create((MainActivity) context)
                        .setTitle(getString(R.string.error))
                        .setBackgroundColorRes(R.color.red_500)
                        .setText(getString(R.string.error_description))
                        .show();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(htmlParserTask != null){
            htmlParserTask.cancel(true);
            htmlParserTask = null;
        }
    }
}
