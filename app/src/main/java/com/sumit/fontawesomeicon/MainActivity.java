package com.sumit.fontawesomeicon;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sumit.fontawesomeicon.adapter.DataAdapter;
import com.sumit.fontawesomeicon.model.fa.Attributes;
import com.sumit.fontawesomeicon.model.fa.FAIcon;
import com.sumit.fontawesomeicon.util.FontManager;
import com.sumit.fontawesomeicon.util.Util;
import com.tapadoo.alerter.Alerter;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final int GRID_COLUMN = 4;
    private static final int ALERTER_DURATION_IN_MILLIS = 8000;
    private static final int PERMISSIONS_REQUEST_CODE = 101;

    private TextView textViewFab;
    private Context context;
    private RecyclerView recyclerView;

    private CreateXmlTask createXmlTask;
    private String generatedXmlFilePath;
    private int externalStoragePermissionCheck;

    private ArrayList<FAIcon> iconArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initFontAwesome();

        renderIcons();
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), GRID_COLUMN);
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            // Change icon color and refresh adapter
            renderIcons();
        });

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    // Add font to TextView
    private void initFontAwesome() {
        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONT_AWESOME);
        FontManager.markAsIconContainer(textViewFab, iconFont);
    }

    private void renderIcons() {
        iconArrayList = getIconArrayList();
        refreshAdapter(iconArrayList, "");
    }

    // Get all icon list from icons.xml string array
    private ArrayList<FAIcon> getIconArrayList() {
        ArrayList<FAIcon> faIcons = new ArrayList<>();
        String[] faIconUnicodes = getResources().getStringArray(R.array.array_fa_icon_unicode);
        String[] faIconClassNames = getResources().getStringArray(R.array.array_fa_icon_class_name);

        for (int i = 0; i < faIconUnicodes.length; i++) {
            Attributes attributes = new Attributes();
            attributes.setIconColor(Util.getRandomColor());
            attributes.setUnicode(faIconUnicodes[i]);
            attributes.setId(faIconClassNames[i]);

            FAIcon faIcon = new FAIcon();
            faIcon.setSequence(i);
            faIcon.setId(faIconClassNames[i]);
            faIcon.setAttributes(attributes);

            faIcons.add(faIcon);
        }

        return faIcons;
    }

    // Feed adapter data to RecyclerView
    private void refreshAdapter(ArrayList<FAIcon> fontAwesomeIcons, String iconNameToSearch) {
        DataAdapter adapter;

        if (StringUtils.isNotEmpty(iconNameToSearch)) {
            // Search based on icon class name
            ArrayList<FAIcon> iconSearchResults = new ArrayList<>();
            for (FAIcon fontAwesomeIcon : fontAwesomeIcons) {
                if (StringUtils.containsIgnoreCase(fontAwesomeIcon.getId(), iconNameToSearch)) {
                    iconSearchResults.add(fontAwesomeIcon);
                }
            }

            adapter = new DataAdapter(this, iconSearchResults);
        } else {
            // Populate all icons
            adapter = new DataAdapter(this, fontAwesomeIcons);
        }

        recyclerView.setAdapter(adapter);
    }

    // Request storage access permission
    private void verifyAndRequestPermission() {
        // For marshmallow and above we need to manually check and prompt for permission
        externalStoragePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (externalStoragePermissionCheck != PackageManager.PERMISSION_GRANTED) {
            // Ask for permission
            ActivityCompat.requestPermissions(this,
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

        if (requestCode != PERMISSIONS_REQUEST_CODE)
            return;

        if (grantResults.length <= 0)
            return;

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { // Permission granted
            externalStoragePermissionCheck = PackageManager.PERMISSION_GRANTED;
            initHtmlParserTask();
        } else {                                                    // Permission denied
            Alerter.create(this)
                .setTitle(getString(R.string.error))
                .setBackgroundColorRes(R.color.md_red_500)
                .setText(getString(R.string.error_permission_denied))
                .setDuration(ALERTER_DURATION_IN_MILLIS)
                .show();
        }
    }

    // Send xml file path to an Intent to view the generated XML file
    private void viewXmlFile(String path) {
        File xmlFile = new File(path);

        if (xmlFile.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            // From API 24 onwards file:// Uri sharing is not allowed through intent.
            // Use FileProvider to share Uri instead

            Uri xmlFileUri = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    xmlFile);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(xmlFileUri, "text/xml");

            if (intent.resolveActivity(getPackageManager()) != null) {
                // If multiple intents are available then an intent chooser will be shown
                startActivity(intent);
            } else {
                Alerter.create(this)
                        .setTitle(getString(R.string.error))
                        .setBackgroundColorRes(R.color.md_red_500)
                        .setText(getString(R.string.error_no_app_to_view))
                        .setDuration(ALERTER_DURATION_IN_MILLIS)
                        .show();
            }
        }
    }

    // Check if any intent is available which can show xml content
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

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        refreshAdapter(iconArrayList, newText);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            // Show about App information
            Alerter.create(this)
                    .setTitle(getString(R.string.title_about))
                    .setBackgroundColorRes(R.color.colorPrimary)
                    .setText(getString(R.string.about_app_description))
                    .setDuration(ALERTER_DURATION_IN_MILLIS)
                    .show();
        } else if (id == R.id.action_export) {
            // Ask for permission if Android version is greater or equal to Marshmallow
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
                verifyAndRequestPermission();
            else
                initHtmlParserTask();
        }

        return super.onOptionsItemSelected(item);
    }

    // Start HTML parse task and export xml to SD storage
    private void initHtmlParserTask() {
        // Cancel async task if it is running
        if (createXmlTask != null) {
            createXmlTask.cancel(true);
            createXmlTask = null;
        }

        createXmlTask = new CreateXmlTask();
        createXmlTask.execute();
    }

    // Initiate FontAwesome HTML Page parsing
    private class CreateXmlTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            ArrayList<FAIcon> fontAwesomeIcons = Util.getFreeToUseSolidIconList(Util.getAllIcons(context));
            if (fontAwesomeIcons != null && fontAwesomeIcons.size() > 0) {
                // Generate XML and save to SD storage
                // Return generated file path. Return null if file creation fails
                return Util.exportXmlToSdCard(context, Util.createXmlContent(fontAwesomeIcons));
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
                        .setBackgroundColorRes(R.color.md_green_500)
                        .setText(message)
                        .setOnClickListener(view -> {
                            if (isEnableClickEvent)
                                viewXmlFile(generatedXmlFilePath);
                        })
                        .setDuration(ALERTER_DURATION_IN_MILLIS)
                        .enableSwipeToDismiss()
                        .show();
            } else {
                Alerter.create((MainActivity) context)
                        .setTitle(getString(R.string.error))
                        .setBackgroundColorRes(R.color.md_red_500)
                        .setText(getString(R.string.error_description))
                        .show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (createXmlTask != null) {
            createXmlTask.cancel(true);
            createXmlTask = null;
        }
    }
}
