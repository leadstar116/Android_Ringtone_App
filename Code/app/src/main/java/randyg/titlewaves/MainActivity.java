package randyg.titlewaves;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import randyg.titlewaves.activities.SettingsActivity;
import randyg.titlewaves.activities.TutorialActivity;
import randyg.titlewaves.music.*;
import randyg.titlewaves.views.*;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class MainActivity
    extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener,
               MidiPlayer2.ProgressListener {

    static final int MENU_ITEM_VIEW_KEYMAP = 6;
    static final int MENU_ITEM_LOAD_MELODY = 7;
    static final int MENU_ITEM_SAVE_MELODY = 8;
    static final int MENU_ITEM_SET_RINGTONE = 10;
    static final int MENU_ITEM_GENERATE_RAND = 11;
    static final int MENU_ITEM_SETTINGS = 13;
    static final int MENU_ITEM_TUTORIAL = 14;

    static final int LIVE_MIDI_VELOCITY = 75;

    GridView editorGrid;
    GridViewModel gridViewModel;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    Database database;
    MidiPlayer2 midiPlayer;
    LiveMidiPlayer realTimeMidi;

    static final int SPLASH_DISPLAY_TIME_MS = 1000;

    public SongBlueprint getSongBlueprint()
    {
        return gridViewModel.songBlueprint;
    }

    public void setSongBlueprint(SongBlueprint sbp)
    {
        gridViewModel.songBlueprint = sbp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Utilities.initialize(this);
        midiPlayer = new MidiPlayer2(this, this);
        realTimeMidi = new LiveMidiPlayer();

        if (savedInstanceState == null) {
            setContentView(R.layout.splashscreen);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        MainActivity.this.run();
                    } catch (Exception e) {
                        CrashReport.logException("creating main activity", e);
                    }
                }
            }, SPLASH_DISPLAY_TIME_MS);
        } else {
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
            run();
        }
    }

    void stopPlayback()
    {
        if (midiPlayer != null && midiPlayer.isPlaying())
        {
            midiPlayer.stop();
            //toolbar.getMenu().findItem(R.id.action_play).setIcon(R.drawable.ic_play_arrow_white_32dp);
        }
    }

    void loadMelodyFromData(SongBlueprint blueprint)
    {
        stopPlayback();
        setSongBlueprint(blueprint);
        editorGrid.setText(blueprint.getText());
        updateHeader();
    }

    SongBlueprint saveMelodyData(String name, boolean makeSureNameIsUnique)
    {
        SongBlueprint blueprint = getSongBlueprint();
        int copyId = 1;
        while (makeSureNameIsUnique && database.hasName(name)) {
            name = name + " (" + copyId + ")";
            copyId++;
        }
        blueprint.setName(name);
        return blueprint;
    }

    void updateHeader()
    {
        View headerView = navigationView.getHeaderView(0);
        TextView h1Text = headerView.findViewById(R.id.nav_header_h1_text);
        h1Text.setText(getSongBlueprint().getInstrumentName());
        TextView h2Text = headerView.findViewById(R.id.nav_header_h2_text);
        if (getSongBlueprint().getMode() == 0) {
            h2Text.setText(getSongBlueprint().getRootNoteName() + " / " + getSongBlueprint().getScaleName());
        } else {
            h2Text.setText(getSongBlueprint().getMMScaleName());
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void run()
    {
        setContentView(R.layout.activity_main);

        gridViewModel = ViewModelProviders.of(this).get(GridViewModel.class);
        editorGrid = findViewById(R.id.editor_grid);
        editorGrid.init(this, gridViewModel);
        editorGrid.setEventListener_(new GridView.EventListener() {
            @Override
            public void onLongPress(int x, int y, int ch) {
                if (midiPlayer.isPlaying())
                    return;

                String[] items = new String[] {
                        "Edit Row",
                        "Edit Column",
                        "Edit Cell",
                        "Paste",
                        "Clear Field"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Row: " + (y+1) + " Column: " + (x+1));
                builder.setItems(items, (dialog, which) -> {
                    String itemName = items[which];

                    if (itemName.compareTo("Edit Row") == 0) {
                        openSettingsDialog(x, y, SongBlueprint.OVERRIDE_ROW);
                    }
                    else if (itemName.compareTo("Edit Column") == 0) {
                        openSettingsDialog(x, y, SongBlueprint.OVERRIDE_COLUMN);
                    }
                    else if (itemName.compareTo("Edit Cell") == 0) {
                        openSettingsDialog(x, y, SongBlueprint.OVERRIDE_CELL);
                    }
                    else if (itemName.compareTo("Paste") == 0)
                    {
                        ClipboardManager clipboard = (ClipboardManager) MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                        if (clipboard.hasPrimaryClip()
                        &&  clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))
                        {
                            Utilities.createConfirmationDialog("The current text will be overwritten. Proceed?", (ok) -> {
                                if (!ok) return;
                                ClipData.Item clipData = clipboard.getPrimaryClip().getItemAt(0);
                                String pasteData = clipData.getText().toString();
                                editorGrid.setText(pasteData);
                            });

                        }
                    }
                    else if (itemName.compareTo("Clear Field") == 0)
                    {
                        clearEditText();
                    }

                    editorGrid.invalidate();
                });
                builder.show();
            }

            @Override
            public void onTileTouched(int x, int y, int ch)
            {
                playCharNote(ch, x, y);
            }

            @Override
            public void onCharAdded(int x, int y, int ch)
            {
                playCharNote(ch, x, y);
            }

            public void playCharNote(int ch, int x, int y)
            {
                if (ch != 0)
                {
                    int[] notes = getSongBlueprint().getMidiNote(ch, x, y);

                    if (notes != null && notes[0] != 0)
                    {
                        SongBlueprint.LineOverride override = getSongBlueprint().getOverride(x, y);
                        if (override != null)
                            realTimeMidi.setInstrument(override.instrument);
                        else
                            realTimeMidi.setInstrument(getSongBlueprint().getInstrument());
                        realTimeMidi.playNote(0, notes, 80, 200);
                    }
                }
            }

            @Override
            public void onTextChanged(int ch)
            {
                String s = gridViewModel.toString();
                getSongBlueprint().setText(s);
            }
        });

        // Live Drum Overlay
        editorGrid.setOnTouchListener((v, event) -> {
            if (editorGrid == null)
                return false;

            if (midiPlayer == null || !midiPlayer.isPlaying())
                return false;

            if (getSongBlueprint().getDrumPattern() != DrumPatterns.findIndex("Live"))
                return true;

            final int nPointers = event.getPointerCount();

            int maskedAction = event.getActionMasked();

            if (event.getAction() == MotionEvent.ACTION_DOWN || maskedAction == MotionEvent.ACTION_POINTER_DOWN)
            {
                final int D_BASS = 36;
                final int D_SNARE = 38;
                final int D_CLAP = 39;
                final int D_HIHAT_C = 42;
                final int D_HIHAT_O = 46;
                final int D_CRASH = 49;
                final int D_LO_TOM = 45;
                final int D_HI_TOM = 50;
                final int D_RIDE_CYMBAL_2 = 59;
                final int D_RIMSHOT = 37;

                int[] drumNotes = new int[] {
                        D_SNARE,   D_HIHAT_O,
                        D_RIMSHOT, D_HIHAT_C,
                        D_LO_TOM,  D_CRASH,
                        D_BASS,    D_RIDE_CYMBAL_2
                };

                float view_w = editorGrid.getWidth();
                float view_h = editorGrid.getHeight();
                float region_w = view_w / 2;
                float region_h = view_h / 4;

                int actionIndex = event.getActionIndex();

                float touch_x = event.getX(actionIndex);
                float touch_y = event.getY(actionIndex);

                int index = 0;
                boolean found = false;
                for (int y = 0; y < 4 && !found; y++) {
                    for (int x = 0; x < 2 && !found; x++) {
                        float rx1 = x * region_w;
                        float rx2 = rx1 + region_w;

                        float ry1 = y * region_h;
                        float ry2 = ry1 + region_h;

                        if (touch_x >= rx1 && touch_x < rx2 && touch_y >= ry1 && touch_y < ry2)
                        {
                            realTimeMidi.playNote(9, drumNotes[index], LIVE_MIDI_VELOCITY, 250);
                            found = true;
                        }

                        index++;
                    }
                }
            }

            return true;
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerToggle.syncState();
        drawerLayout.addDrawerListener(drawerToggle);

        drawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) { }

                    @Override
                    public void onDrawerOpened(View drawerView) { }

                    @Override
                    public void onDrawerClosed(View drawerView) { }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        Utilities.hideKeyboard();
                    }
                });

        drawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
        drawerLayout.setDrawerShadow(android.R.color.transparent, GravityCompat.START);
        drawerLayout.setElevation(0f);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        populateNavMenuMain();

        database = new Database(this);

        AdUtil.setupAds(this);

        updateHeader();

        AppRate.with(this)
                .setInstallDays(3)
                .setLaunchTimes(5)
                .setRemindInterval(2)
                .setShowLaterButton(true)
                .setDebug(false)
                .setOnClickButtonListener(new OnClickButtonListener() {
                    @Override
                    public void onClickButton(int which) {
                        Log.d(MainActivity.class.getName(), Integer.toString(which));
                    }
                })
                .monitor();

        AppRate.showRateDialogIfMeetsConditions(this);
        UtilitiesMisc.deleteCache(this);
    }

    @Override
    public void onMidiPlaybackStarted(Song song)
    {
        try {
            editorGrid.setClickable(false);
            Utilities.hideKeyboard();
        } catch (Exception e) {
            CrashReport.logException("playback started", e);
        }
    }

    @Override
    public void onMidiPlaybackFinished(Song song)
    {
        try {
            editorGrid.setClickable(true);
            editorGrid.clearHighlights();
            toolbar.getMenu().findItem(R.id.action_play).setIcon(R.drawable.ic_play_arrow_white_32dp);
        } catch (Exception e) {
            CrashReport.logException("playback finished", e);
        }
    }

    @Override
    public void onMidiPlaybackProgress(Song song, int pos, int len)
    {
        try {
            int segment_i = getCurrentNotePlaying(song.getRawData().segments.size(), pos, len);
            if (segment_i == -1)
                return;

            SongRawData.Segment segment = song.getRawData().segments.get(segment_i);
            editorGrid.clearHighlights();
            for (SongRawData.CharLocation loc : segment.chars)
            {
                editorGrid.addHighlight(loc.col, loc.row, 0xFF5B748C);
            }
        } catch (Exception e) {
            CrashReport.logException("playback progress", e);
        }
    }

    public int getCurrentNotePlaying(int numNotes, int pos, int len)
    {
        if (numNotes == 0)
            return -1;

        double progress = (double)pos / len;
        return Math.min(numNotes-1, (int)Math.round(progress * numNotes));
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (realTimeMidi != null) {
            realTimeMidi.start();
        }

        if (editorGrid != null)
            editorGrid.rereadPreferences();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (realTimeMidi != null) {
            realTimeMidi.stop();
        }

        stopPlayback();
    }

    public void populateNavMenuMain()
    {
        Menu menu = navigationView.getMenu();
        menu.clear();

        if (!BuildConfig.FLAVOR.toLowerCase().contains("demo"))
        {
            menu.add(0, MENU_ITEM_LOAD_MELODY, 0, "Load Melody");
            menu.add(0, MENU_ITEM_SAVE_MELODY, 0, "Save Melody");
            menu.add(0, MENU_ITEM_SET_RINGTONE, 0, "Export Ringtone");

        }

        menu.add(0, MENU_ITEM_VIEW_KEYMAP, 0, "Keymap");
        menu.add(0, MENU_ITEM_GENERATE_RAND,0,"Randomizer");
        menu.add(0, MENU_ITEM_SETTINGS, 0, "Preferences");
        menu.add(0, MENU_ITEM_TUTORIAL, 0, "About");
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true; // super.onCreateOptionsMenu(menu);
    }

    public void clearEditText()
    {
        String txt = editorGrid.getText()
                .replace(" ", "")
                .replace("\n", "");

        if (!midiPlayer.isPlaying() && txt.length() > 0 && !txt.isEmpty())
        {
            Utilities.createConfirmationDialog("Clear Field", yes -> {
                if (yes) {
                    editorGrid.setText("");
                    getSongBlueprint().getCellOverride().clear();
                    getSongBlueprint().getHorizontalOverride().clear();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    if (drawerLayout.isDrawerOpen(GravityCompat.START))
                        drawerLayout.closeDrawer(GravityCompat.START);
                    else
                        drawerLayout.openDrawer(GravityCompat.START);
                    return true;

                case R.id.action_play:
                    playSongFromText();
                    return true;

                /*case R.id.action_clear_text:
                    clearEditText();
                    return true;*/

                case R.id.action_settings:
                    openSettingsDialog(-1, -1, 0);
                    return true;
            }
        }
        catch (Exception e) {
            CrashReport.logException("Options item selected", e);
        }

        return super.onOptionsItemSelected(item);
    }

    public void playSongFromText() throws Exception
    {
        Song song = new Song(getSongBlueprint());

        String text = song.getBlueprint().getText();

        if (text == null || text.length() == 0)
            return;

        MenuItem item = toolbar.getMenu().findItem(R.id.action_play);
        if (!midiPlayer.isPlaying())
        {
            item.setIcon(R.drawable.ic_stop_white_32dp);

            double progress = 0.0;

            int[] rowCol = editorGrid.getRowCol();

            // find segment
            int segment_i = -1;

            if (rowCol != null)
            {
                ArrayList<SongRawData.Segment> segments = song.getRawData().segments;
                for (int segi=0; segi<segments.size(); segi++) {
                    for (SongRawData.CharLocation loc : segments.get(segi).chars) {
                        if (loc.row == rowCol[0] && loc.col == rowCol[1]) {
                            segment_i = segi;
                            break;
                        }
                    }
                }

                if (segment_i != -1)
                {
                    progress = (double) segment_i / (double) segments.size();
                }
            }

            midiPlayer.play(song, progress);
        } else {
            item.setIcon(R.drawable.ic_play_arrow_white_32dp);

            midiPlayer.stop();
        }
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    public boolean verifyStoragePermissions() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, REQUEST_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }

    public static final int CODE_WRITE_SETTINGS_PERMISSION = 17630;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            final boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            switch (requestCode) {
                case REQUEST_EXTERNAL_STORAGE:
                case CODE_WRITE_SETTINGS_PERMISSION:
                {
                    if (granted) {
                        setMidiRingtone();
                    }
                    break;
                }
            }
        }
        catch (Exception e) {
            CrashReport.logException("Request permission result", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            switch (requestCode) {
                case CODE_WRITE_SETTINGS_PERMISSION:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        boolean canWrite = Settings.System.canWrite(this);
                        if (canWrite) {
                            setMidiRingtone();
                        }
                    }
                    break;
            }
        }
        catch (Exception e) {
            CrashReport.logException("Activity result", e);
        }
    }

    public boolean checkWriteSettingsPermission(){
        boolean hasPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasPermission = Settings.System.canWrite(this);
        } else {
            hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }

        if (!hasPermission) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                startActivityForResult(intent, MainActivity.CODE_WRITE_SETTINGS_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_SETTINGS}, MainActivity.CODE_WRITE_SETTINGS_PERMISSION);
            }
        }

        return hasPermission;
    }

    public void saveRingtone(Song song, String typeName)
    {
        try {
            String ringtoneuri = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + typeName + "s";
            (new File(ringtoneuri)).mkdirs();
            File file = new File(ringtoneuri, "TitleWaves-" + typeName + ".mid");
            if (!file.exists())
                file.createNewFile();
            else {
                file.delete();
                file.createNewFile();
            }
            try {
                song.getMidiData().writeToFile(file);
            } catch (IOException e) {
                CrashReport.logException("Write midi to file", e);
                return;
            }
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
            values.put(MediaStore.MediaColumns.TITLE, "Title Waves " + typeName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/midi");
            values.put(MediaStore.MediaColumns.SIZE, file.length());
            values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
            values.put(MediaStore.Audio.Media.IS_RINGTONE, typeName.equalsIgnoreCase("Ringtone"));
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, typeName.equalsIgnoreCase("Notification"));
            values.put(MediaStore.Audio.Media.IS_ALARM, typeName.equalsIgnoreCase("Alarm"));
            values.put(MediaStore.Audio.Media.IS_MUSIC, false);

            Uri uri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
            getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + file.getAbsolutePath() + "\"", null);
            Uri newUri = getContentResolver().insert(uri, values);

            int type;
            if (typeName.equalsIgnoreCase("Alarm"))
                type = RingtoneManager.TYPE_ALARM;
            else if (typeName.equalsIgnoreCase("Notification"))
                type = RingtoneManager.TYPE_NOTIFICATION;
            else
                type = RingtoneManager.TYPE_RINGTONE;

            RingtoneManager.setActualDefaultRingtoneUri(MainActivity.this, type, newUri);
        } catch (Exception e) {
            CrashReport.logException("Save ringtone", e);
        }
    }

    public void setMidiRingtone() {
        String text = editorGrid.getText().toString();

        if (text.trim().length() == 0)
            return;

        if (!verifyStoragePermissions()
        ||  !checkWriteSettingsPermission())
            return;

        final View view = getLayoutInflater().inflate(R.layout.export_ringtone_dialog, null);

        DialogInterface.OnClickListener callback = new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    dialog.cancel();
                    return;
                }

                boolean exportAsPhoneRingtone = ((CheckBox)view.findViewById(R.id.checkbox_phone_ringtone)).isChecked();
                boolean exportAsNotificationRingtone = ((CheckBox)view.findViewById(R.id.checkbox_notification_ringtone)).isChecked();
                boolean exportAsAlarmRingtone = ((CheckBox)view.findViewById(R.id.checkbox_alarm_ringtone)).isChecked();

                try {
                    Song song = new Song(getSongBlueprint());
                    song.getMidiData();

                    if (exportAsPhoneRingtone)
                        saveRingtone(song, "Ringtone");

                    if (exportAsNotificationRingtone)
                        saveRingtone(song, "Notification");

                    if (exportAsAlarmRingtone)
                        saveRingtone(song, "Alarm");

                    drawerLayout.closeDrawer(GravityCompat.START);
                } catch (Exception e) {
                    CrashReport.logException("Export ringtone", e);
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(Utilities.mainActivity);
        builder.setTitle("Export Ringtone");
        builder.setView(view);
        builder.setPositiveButton("Export", callback);
        builder.setNegativeButton("Cancel", callback);
        builder.show();
    }

    public void openSaveDialog()
    {
        UtilitiesMisc.createGenericLoadSaveDialog(this, true);
    }

    public void openLoadDialog()
    {
        UtilitiesMisc.createGenericLoadSaveDialog(this,false);
    }

    public void showKeymapView()
    {
        try {

            View view = getLayoutInflater().inflate(R.layout.keymap, null);
            LinearLayout keymapContainer = view.findViewById(R.id.keymapContainer);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            //params.width = LinearLayout.LayoutParams.WRAP_CONTENT;

            int[] scaleIntervals = getSongBlueprint().getScaleIntervals();

            final int accentColor = getResources().getColor(R.color.colorAccent);
            final int transparentColor = getResources().getColor(android.R.color.transparent);
            final int whiteColor = getResources().getColor(android.R.color.white);

            int numRowItems = getSongBlueprint().getMode() == SongBlueprint.MODE_SINGLE_NOTE
                            ? 4
                            : 3;

            char ch = 'A';
            while (ch <= 'Z')
            {
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setLayoutParams(params);
                row.setBackgroundColor(whiteColor);

                for (int i = 0; i < numRowItems; i++)
                {
                    if (ch>'Z')
                    {
                        Button button = new Button(this);
                        button.setLayoutParams(params);
                        button.setVisibility(View.INVISIBLE);
                        row.addView(button);
                        continue;
                    }

                    String noteStr;
                    int[] notes = null;
                    if (getSongBlueprint().getMode() == SongBlueprint.MODE_SINGLE_NOTE)
                    {
                        int note = MidiTransform.charToMidiNote(
                                ch,
                                getSongBlueprint().getBaseOctave(),
                                getSongBlueprint().getOctaveRange(),
                                getSongBlueprint().getRootNote(),
                                scaleIntervals);

                        notes = new int[] { note };
                        noteStr = MidiUtils.getMidiNoteSPN(note);
                    }
                    else
                    {
                        LinkedHashMap<Integer, Chrods.Chord> chords = getSongBlueprint().getMMScale() == 0
                                ? Chrods.chords_majMin
                                : Chrods.chords_augDim;

                        if (chords.containsKey((int)ch)) {
                            Chrods.Chord chord = chords.get((int)ch);
                            noteStr = chord.name;
                            notes = chord.notes;
                        } else {
                            noteStr = "N/A";
                        }
                    }

                    Button button = new Button(this);
                    button.setText(ch + " ➞ " + noteStr);
                    button.setAllCaps(false);
                    button.setLayoutParams(params);
                    button.setBackgroundColor(transparentColor);

                    final int[] constNotes = notes;
                    if (constNotes != null) {
                        button.setOnTouchListener((v, event) -> {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                button.setBackgroundColor(accentColor);
                                realTimeMidi.noteOn(0, constNotes, LIVE_MIDI_VELOCITY - 15);
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                button.setBackgroundColor(transparentColor);
                                realTimeMidi.noteOff(0, constNotes);
                            }
                            return true;
                        });
                    }

                    row.addView(button);

                    ch++;
                }

                keymapContainer.addView(row);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Keymap");
            builder.setView(view);
            builder.setPositiveButton("OK", null);
            realTimeMidi.setInstrument(getSongBlueprint().getInstrument());
            builder.show();
        }
        catch (Exception e) {
            CrashReport.logException("Show keymap", e);
        }
    }

    private int tmp_instrument;
    private int tmp_rootNote;
    private int tmp_scale;
    private int tmp_octaveRange;
    private int tmp_baseOctave;
    private int tmp_mmScale;
    private int tmp_drumPattern;
    private int tmp_tempo;

    public HashMap<Integer, SongBlueprint.LineOverride> getOverrideMap(int type)
    {
        switch (type)
        {
            case SongBlueprint.OVERRIDE_ROW:
                return getSongBlueprint().getHorizontalOverride();
            case SongBlueprint.OVERRIDE_COLUMN:
                return getSongBlueprint().getVerticalOverride();
            case SongBlueprint.OVERRIDE_CELL:
                return getSongBlueprint().getCellOverride();
        }
        return null;
    }

    public int getOverrideKey(int type, int cellX, int cellY)
    {
        switch (type)
        {
            case SongBlueprint.OVERRIDE_ROW:
                return cellY;
            case SongBlueprint.OVERRIDE_COLUMN:
                return cellX;
            case SongBlueprint.OVERRIDE_CELL:
                return SongBlueprint.cellOverrideKey(cellX, cellY);
        }
        return 0;
    }

    public void openSettingsDialog(int cellX, int cellY, int overrideType)
    {
        if (midiPlayer.isPlaying())
            return;

        final boolean isMusicianMode = getSongBlueprint().getMode() == 1;

        int overrideKey = getOverrideKey(overrideType, cellX, cellY);
        HashMap<Integer, SongBlueprint.LineOverride> overrideMap = getOverrideMap(overrideType);
        SongBlueprint.LineOverride overrideObj = null;

        if (overrideType != 0)
        {
            overrideObj = overrideMap.get(overrideKey);
        }

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(overrideType == 0 ? "Settings" : "Edit Settings");

            View view = getLayoutInflater().inflate(R.layout.melody_settings_dialog, null);
            view.setVerticalScrollBarEnabled(true);

            LinearLayout scaleSettingsContainer = view.findViewById(R.id.scaleSettingsContainer);
            LinearLayout musicianModeSettingsContainer = view.findViewById(R.id.musicianModeSettingsContainer);

            if (isMusicianMode) {
                scaleSettingsContainer.setVisibility(View.GONE);
                musicianModeSettingsContainer.setVisibility(View.VISIBLE);
            }

            // Chord mode?
            {
                Switch chordModeSwitch = view.findViewById(R.id.chordModeSwitch);
                chordModeSwitch.setVisibility(overrideType == 0 ? View.VISIBLE : View.GONE);
                chordModeSwitch.setChecked(isMusicianMode);
                chordModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        getSongBlueprint().setMode( isChecked ? 1 : 0 );

                        if (isChecked) {
                            scaleSettingsContainer.setVisibility(View.GONE);
                            musicianModeSettingsContainer.setVisibility(View.VISIBLE);
                        } else {
                            scaleSettingsContainer.setVisibility(View.VISIBLE);
                            musicianModeSettingsContainer.setVisibility(View.GONE);
                        }

                        updateHeader();
                        populateNavMenuMain();
                    }
                });
            }

            // instrument spinner
            int oldInstrument = overrideObj == null
                              ? getSongBlueprint().getInstrument()
                              : overrideObj.instrument;


            tmp_instrument = oldInstrument;
            {
                Spinner instrumentSpinner = view.findViewById(R.id.instrumentSpinner);
                Utilities.setupSpinner(instrumentSpinner, Instruments.getNames(), Instruments.findIndex(oldInstrument), (pos, item) -> {
                    tmp_instrument = Instruments.getId(item);
                });
            }

            // root note spinner
            int oldRootNote = overrideObj == null
                            ? getSongBlueprint().getRootNote()
                            : overrideObj.rootNote;
            tmp_rootNote = oldRootNote;
            {
                Spinner rootNoteSpinner = view.findViewById(R.id.rootNoteSpinner);
                Utilities.setupSpinner(rootNoteSpinner, Keys.getNames(), oldRootNote, (pos, item) -> {
                    tmp_rootNote = pos;
                });
            }

            // scale spinner
            int oldScale = overrideObj == null
                         ? getSongBlueprint().getScale()
                         : overrideObj.scale;
            tmp_scale = oldScale;
            {
                Spinner scaleSpinner = view.findViewById(R.id.scaleSpinner);
                Utilities.setupSpinner(scaleSpinner, Scales.getNames(), oldScale, (pos, item) -> {
                    tmp_scale = pos;
                });
            }

            // musician mode scale
            int oldMmScale = overrideObj == null
                           ? getSongBlueprint().getMMScale()
                           : overrideObj.mmScale;
            tmp_mmScale = oldMmScale;
            {
                Spinner mmscaleSpinner = view.findViewById(R.id.mmscaleSpinner);
                Utilities.setupSpinner(mmscaleSpinner, 
                    new String[]{ "Major - Minor", "Augmented - Diminished" }, oldMmScale, (pos, item) -> {

                    tmp_mmScale = pos;
                });
            }

            // base octave seeker
            int oldBaseOctave = overrideObj == null
                              ? getSongBlueprint().getBaseOctave()
                              : overrideObj.baseOctave;
            tmp_baseOctave = oldBaseOctave;
            {
                SeekBar baseOctaveSeekBar = view.findViewById(R.id.baseOctaveSeekBar);
                Utilities.setupSeekBar(baseOctaveSeekBar, oldBaseOctave - 1, (progress) -> {
                    tmp_baseOctave = progress + 1;
                    Utilities.makeToast("Base Octave: " + (tmp_baseOctave + 1));
                });
            }

            // octave range seeker
            int oldOctaveRange = overrideObj == null
                               ? getSongBlueprint().getOctaveRange()
                               : overrideObj.octaveRange;
            tmp_octaveRange = oldOctaveRange;
            {
                SeekBar octaveRangeSeekBar = view.findViewById(R.id.octaveRangeSeekBar);
                Utilities.setupSeekBar(octaveRangeSeekBar, oldOctaveRange, (progress) -> {
                    tmp_octaveRange = progress;
                    Utilities.makeToast("Octave Range: " + (tmp_octaveRange + 1));
                });
            }

            // drums
            int oldDrumPattern = getSongBlueprint().getDrumPattern();
            tmp_drumPattern = oldDrumPattern;
            {
                Spinner drumsSpinner = view.findViewById(R.id.drumPatternSpinner);
                Utilities.setupSpinner(drumsSpinner, DrumPatterns.getNames(), oldDrumPattern, (pos, item) -> {
                    tmp_drumPattern = pos;
                });
            }

            // tempo seeker
            int oldTempo = getSongBlueprint().getTempo();
            tmp_tempo = oldTempo;
            {
                SeekBar tempoSeekBar = view.findViewById(R.id.tempoSeekBar);
                Utilities.setupSeekBar(tempoSeekBar, (oldTempo - 60) / 10, progress -> {
                    tmp_tempo = 60 + progress * 10;
                    Utilities.makeToast("Tempo: " + tmp_tempo);
                });
            }

            if (overrideType != 0)
            {
                view.findViewById(R.id.drumPatternSpinner).setVisibility(View.GONE);
                view.findViewById(R.id.drumPatternTextView).setVisibility(View.GONE);

                view.findViewById(R.id.tempoSeekBar).setVisibility(View.GONE);
                view.findViewById(R.id.tempoTextView).setVisibility(View.GONE);
            }

            builder.setView(view);

            builder.setPositiveButton("OK", (dialog, which) -> {
                editorGrid.invalidate();

                if (overrideType == 0)
                {
                    if (tmp_instrument != oldInstrument) {
                        getSongBlueprint().setInstrument(tmp_instrument);
                        updateHeader();
                    }
                    if (tmp_rootNote != oldRootNote) {
                        getSongBlueprint().setRootNote(tmp_rootNote);
                        updateHeader();
                    }
                    if (tmp_scale != oldScale) {
                        getSongBlueprint().setScale(tmp_scale);
                        updateHeader();
                    }
                    if (tmp_mmScale != oldMmScale) {
                        getSongBlueprint().setMMScale(tmp_mmScale);
                        updateHeader();
                    }
                    if (tmp_baseOctave != oldBaseOctave) {
                        getSongBlueprint().setBaseOctave(tmp_baseOctave);
                    }
                    if (tmp_octaveRange != oldOctaveRange) {
                        getSongBlueprint().setOctaveRange(tmp_octaveRange);
                    }
                    if (tmp_drumPattern != oldDrumPattern) {
                        getSongBlueprint().setDrumPattern(tmp_drumPattern);
                    }
                    if (tmp_tempo != oldTempo) {
                        getSongBlueprint().setTempo(tmp_tempo);
                    }
                }
                else
                {
                    SongBlueprint.LineOverride over = overrideMap.get(overrideKey);

                    if (over != null)
                    {
                        over.instrument = tmp_instrument;
                        over.rootNote = tmp_rootNote;
                        over.scale = tmp_scale;
                        over.mmScale = tmp_mmScale;
                        over.baseOctave = tmp_baseOctave;
                        over.octaveRange = tmp_octaveRange;
                    }
                    else
                    {
                        /*if (tmp_instrument != songBlueprint.getInstrument()
                        ||  tmp_rootNote != songBlueprint.getRootNote()
                        ||  tmp_scale != songBlueprint.getScale()
                        ||  (isMusicianMode && tmp_mmScale != songBlueprint.getMMScale())
                        ||  tmp_baseOctave != songBlueprint.getBaseOctave()
                        ||  tmp_octaveRange != songBlueprint.getOctaveRange())*/
                        {
                            over = new SongBlueprint.LineOverride();
                            over.instrument = tmp_instrument;
                            over.rootNote = tmp_rootNote;
                            over.scale = tmp_scale;
                            over.mmScale = tmp_mmScale;
                            over.baseOctave = tmp_baseOctave;
                            over.octaveRange = tmp_octaveRange;
                            overrideMap.put(overrideKey, over);
                        }
                    }
                }
            });
            builder.setNegativeButton(overrideType == 0 ? "Cancel" : "Reset", (dialog, which) -> {
                editorGrid.invalidate();
                if (overrideType != 0) {
                    SongBlueprint.LineOverride over = overrideMap.get(overrideKey);
                    if (over != null) {
                        overrideMap.remove(overrideKey);
                    }
                }
            });
            builder.show();
        }
        catch (Exception e) {
            CrashReport.logException("Settings dialog", e);
        }
    }

    public void openRandomizerDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Randomizer");
        final View view = getLayoutInflater().inflate(R.layout.randomizer_dialog, null);
        builder.setView(view);
        builder.setPositiveButton("OK", (dialog, which) -> {
            EditText wordEdit = view.findViewById(R.id.randomizerWords);
            setRandomMelody(wordEdit.getText().toString());
            drawerLayout.closeDrawer(GravityCompat.START);
        });
        builder.show();
    }

    public void setRandomMelody(String words)
    {
        String[] wordsArray = words.split("\\s+");
        String song = Randomizer.generateRandom(wordsArray, 8);
        stopPlayback();
        editorGrid.setText(song);
        editorGrid.clearOverrides();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case MENU_ITEM_VIEW_KEYMAP:
                    showKeymapView();
                    break;
                case MENU_ITEM_LOAD_MELODY:
                    openLoadDialog();
                    break;
                case MENU_ITEM_SAVE_MELODY:
                    openSaveDialog();
                    break;
                case MENU_ITEM_SET_RINGTONE:
                    setMidiRingtone();
                    break;
                case MENU_ITEM_GENERATE_RAND:
                    openRandomizerDialog();
                    break;
                case MENU_ITEM_SETTINGS:
                    startActivity(new Intent(this, SettingsActivity.class));
                    break;
                case MENU_ITEM_TUTORIAL:
                    startActivity(new Intent(this, TutorialActivity.class));
                    break;
            }
        } catch (Exception e) {
            CrashReport.logException("navigation item selected", e);
        }

        return true;
    }
}

