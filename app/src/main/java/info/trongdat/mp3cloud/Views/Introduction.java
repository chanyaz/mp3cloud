package info.trongdat.mp3cloud.Views;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import info.trongdat.mp3cloud.Models.Entities.Song;
import info.trongdat.mp3cloud.Presenters.SQLiteHandler;
import info.trongdat.mp3cloud.Presenters.SongPresenter;
import info.trongdat.mp3cloud.R;
import info.trongdat.mp3cloud.Views.Tabs.OfflineMusic;

public class Introduction extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Collection<Song>> {
    SQLiteHandler db;

    public static final String TAG = "phantrongdat";

    private static final int MUSIC_LOADER_ID = 1;
    private static final int OVERLAY_PERMISSION_REQ_CODE = 1;
    private static final int EXT_STORAGE_PERMISSION_REQ_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new SQLiteHandler(this);
        setContentView(R.layout.activity_introduction);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));

        // check if we can draw overlays
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(Introduction.this)) {
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                        onPermissionsNotGranted();
                    }
                }
            };
            new AlertDialog.Builder(Introduction.this)
                    .setTitle(getString(R.string.permissions_title))
                    .setMessage(getString(R.string.draw_over_permissions_message))
                    .setPositiveButton(getString(R.string.btn_continue), listener)
                    .setNegativeButton(getString(R.string.btn_cancel), listener)
                    .setCancelable(false)
                    .show();
//            return;
        }
        checkReadStoragePermission();

        MP3AsyncTask mp3AsyncTask = new MP3AsyncTask(this);
        mp3AsyncTask.execute("initialize");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(Introduction.this)) {
                onPermissionsNotGranted();
            } else {
                checkReadStoragePermission();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXT_STORAGE_PERMISSION_REQ_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permissions[i]) &&
                        grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    loadMusic();
                    return;
                }
            }
            onPermissionsNotGranted();
        }
    }

    /**
     * Check if we have necessary permissions.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void checkReadStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            ActivityCompat.requestPermissions(Introduction.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXT_STORAGE_PERMISSION_REQ_CODE);
                        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                            onPermissionsNotGranted();
                        }
                        dialog.dismiss();
                    }
                };
                new AlertDialog.Builder(this)
                        .setTitle(R.string.permissions_title)
                        .setMessage(R.string.read_ext_permissions_message)
                        .setPositiveButton(R.string.btn_continue, onClickListener)
                        .setNegativeButton(R.string.btn_cancel, onClickListener)
                        .setCancelable(false)
                        .show();
                return;
            }
            ActivityCompat.requestPermissions(Introduction.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXT_STORAGE_PERMISSION_REQ_CODE);
            return;
        }
        loadMusic();
    }

    /**
     * Load music.
     */
    private void loadMusic() {
        getSupportLoaderManager().initLoader(MUSIC_LOADER_ID, null, this);
    }

    /**
     * Permissions not granted. Quit.
     */
    private void onPermissionsNotGranted() {
        Toast.makeText(this, R.string.toast_permissions_not_granted, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public Loader<Collection<Song>> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Collection<Song>> loader, Collection<Song> data) {

    }

    @Override
    public void onLoaderReset(Loader<Collection<Song>> loader) {

    }

    public class MP3AsyncTask extends AsyncTask<String, String, String> {
        private Context context;
        private SQLiteHandler db;
        private final Uri URI_SONG = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        private final Uri URI_ALBUM = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        private final Uri URI_GENRE = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
        private final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");

        public MP3AsyncTask(Context context) {
            this.context = context;
            db = new SQLiteHandler(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected String doInBackground(String... strings) {
            String action = strings[0];
            SongPresenter presenter = new SongPresenter(context);

            if (action.equals("initialize")) {
                if (getCountOfSong() > presenter.getCountOfSong() || presenter.getCountOfSong() == 0) {
                    initSongs();
                    initAlbums();
                    initArtists();
                    initGenres();
                    initComposers();
                }
                if (getCountOfSong() < presenter.getCountOfSong()) {
                    fixSongs();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            finish();
            Intent intent = new Intent(Introduction.this, OfflineMusic.class);
            overridePendingTransition(0, 0);
            startActivity(intent);
        }


        public void initSongs() {
            String displayName, title, path;
            int id, duration, genreID, artistID, albumID, composerID;

            String[] data = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.COMPOSER};

            Cursor cursor = context.getContentResolver().query(URI_SONG, data, MediaStore.Audio.Media.IS_MUSIC + "=1", null,
                    MediaStore.Audio.Media.TITLE + " ASC");

            while (cursor.moveToNext()) {
                id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                artistID = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
                albumID = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                String cover = ContentUris.withAppendedId(albumArtUri, albumID).toString();
                // handler get genre id for song.
                String[] genresProjection = {
                        MediaStore.Audio.Genres.NAME,
                        MediaStore.Audio.Genres._ID};
                Uri uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", id);
                Cursor genresCursor = context.getContentResolver().query(uri,
                        genresProjection, null, null, null);
                if (genresCursor.moveToFirst()) {
//                genresCursor.moveToFirst();
                    genreID = genresCursor.getInt(genresCursor.getColumnIndex(MediaStore.Audio.Genres._ID));
                } else genreID = 999999;
                genresCursor.close();
                // handler for song of album
                Cursor albumc = db.rawQuery("SELECT albumID FROM AlbumDetails WHERE albumID=" + albumID + " AND songID=" + id);
                if (albumc.getCount() == 0)
                    db.execute("INSERT INTO AlbumDetails VALUES(" + albumID + "," + id + ")");
                albumc.close();

                // handler for song of artist
                Cursor artistc = db.rawQuery("SELECT artistID FROM ArtistSong WHERE artistID=" + albumID + " AND songID=" + id);
                if (artistc.getCount() == 0)
                    db.execute("INSERT INTO ArtistSong VALUES(" + artistID + "," + id + ")");
                artistc.close();

                Cursor songc = db.rawQuery("SELECT songID FROM Songs WHERE songID=" + id);
                if (songc.getCount() == 0)
                    db.insertSong(id, title, "", genreID, "", duration, path, cover, 0);
//                db.execute("INSERT INTO Songs VALUES(" + id + ",'" + title + "',' '," + genreID + ",' '," + duration + ",'" + path + "')");
                songc.close();
            }
            cursor.close();
        }

        public void fixSongs() {

            ArrayList<Song> songs = new SongPresenter(context).getSongs();
            for (Song song : songs) {
                if (!checkSong(song.getPath().toString())) {
                    db.execute("DELETE FROM Songs WHERE SongID=" + song.getSongID());
                    db.execute("DELETE FROM AlbumDetails WHERE SongID=" + song.getSongID());
                    db.execute("DELETE FROM ArtistSong WHERE SongID=" + song.getSongID());
                }
            }
        }

        public int getCountOfSong() {
            String[] data = {MediaStore.Audio.Media._ID};
            Cursor cursor = context.getContentResolver().query(URI_SONG, data, MediaStore.Audio.Media.IS_MUSIC + "=1", null, null);
            return cursor.getCount();
        }

        public boolean checkSong(String path) {
            File file = new File(path);
            return file.exists();
        }

        public void initAlbums() {
            String albumName, albumArt;
            int id;
            String[] data = {MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART};
            Cursor cursor = context.getContentResolver().query(URI_ALBUM, data, null, null, MediaStore.Audio.Albums.ALBUM + " ASC");
            while (cursor.moveToNext()) {
                id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums._ID));
                albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
//                albumArt = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                albumArt = ContentUris.withAppendedId(albumArtUri, id).toString();

                System.err.println(id + " " + albumName + " " + albumArt);
                Cursor c = db.rawQuery("SELECT albumID FROM Albums WHERE albumID=" + id);
                if (c.getCount() == 0)
                    db.execute("INSERT INTO Albums VALUES(" + id + ", '" + albumName + "',' ','" + albumArt + "')");
            }
            cursor.close();
        }

        public void initGenres() {
            String genreName;
            int genreID;

            String[] data = {MediaStore.Audio.Genres._ID, MediaStore.Audio.Genres.NAME};
            Cursor cursor = context.getContentResolver().query(URI_GENRE, data, null, null, MediaStore.Audio.Genres.NAME + " ASC");
            while (cursor.moveToNext()) {
                genreID = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Genres._ID));
                genreName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Genres.NAME));
                System.out.println(genreName + "type");
                Cursor c = db.rawQuery("SELECT genreID FROM Genres WHERE genreID=" + genreID);
                if (c.getCount() == 0)
                    db.execute("INSERT INTO Genres VALUES(" + genreID + ", '" + genreName + "')");
                c.close();
            }
            Cursor c = db.rawQuery("SELECT genreID FROM Genres WHERE genreID=999999");
            if (c.getCount() == 0)
                db.execute("INSERT INTO Genres VALUES(999999, 'Thể loại khác')");
            c.close();
            cursor.close();
        }

        public void initComposers() {
            String composerName;
            int id;
            String[] data = {MediaStore.Audio.Media.COMPOSER};
            Cursor cursor = context.getContentResolver().query(URI_SONG, data,
                    MediaStore.Audio.Media.IS_MUSIC + "=1", null, MediaStore.Audio.Media.COMPOSER + " ASC");
            while (cursor.moveToNext()) {
                composerName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER));
                System.out.println(composerName + " composer");
                Cursor c = db.rawQuery("SELECT composerName FROM Composers WHERE composerName=" + composerName);
                if (c.getCount() == 0)
                    db.execute("INSERT INTO Composers VALUES(null, '" + composerName + "',null,null,null,null)");
                c.close();
            }
            cursor.close();
        }

        public void initArtists() {
            String artistName;
            int id;
            String[] data = {MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ARTIST_ID};
            Cursor cursor = context.getContentResolver().query(URI_SONG, data,
                    MediaStore.Audio.Media.IS_MUSIC + "=1", null, MediaStore.Audio.Media.ARTIST + " ASC");
            while (cursor.moveToNext()) {
                id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
                artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                System.out.println(id + " " + artistName);
                Cursor c = db.rawQuery("SELECT artistID FROM Artists WHERE artistID=" + id);
                if (c.getCount() == 0)
                    db.execute("INSERT INTO Artists VALUES(" + id + ", '" + artistName + "',null,null,null,null)");
                c.close();
            }
            cursor.close();
        }
    }
}
