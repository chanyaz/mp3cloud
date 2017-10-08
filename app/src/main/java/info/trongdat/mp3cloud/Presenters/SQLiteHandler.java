package info.trongdat.mp3cloud.Presenters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Alone on 9/14/2016.
 */

public class SQLiteHandler extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    private Context dbContext;
    private static final String DB_PATH = "/data/data/info.trongdat.mp3cloud/databases/";
    private static final String DB_NAME = "mp3db.sqlite";
    private static final int DB_VERSION = 1;
    private final String PATH = DB_PATH + DB_NAME;

    public SQLiteHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.dbContext = context;
        openDB();
    }

    public void openDB() {
        if (checkDB()) {
            db = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READWRITE);
        } else {
            copyDBToSDCard();
//            Toast.makeText(dbContext, "Copy database to SDCard", Toast.LENGTH_LONG).show();
        }
    }

    public void closeDB() {
        db.close();
    }

    public void execute(String sql) {
        db.execSQL(sql);
    }

    public Cursor rawQuery(String sql) {
        db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }


    public void insertSong(int songID, String songName, String lyrics, int genreID, String description, int duration, String path, String cover, int favorite) {
        openDB();
        String sql = "INSERT INTO Songs VALUES(?,?,?,?,?,?,?,?,?)";
        SQLiteStatement insertStmt = db.compileStatement(sql);
        insertStmt.clearBindings();
        insertStmt.bindString(1, Integer.toString(songID));
        insertStmt.bindString(2, songName);
        insertStmt.bindString(3, lyrics);
        insertStmt.bindString(4, Integer.toString(genreID));
        insertStmt.bindString(5, description);
        insertStmt.bindString(6, Integer.toString(duration));
        insertStmt.bindString(7, path);
        insertStmt.bindString(8, cover);
        insertStmt.bindString(9, Integer.toString(favorite));
        insertStmt.executeInsert();
        closeDB();
    }

    private boolean checkDB() {
        try {
            File file = new File(PATH);
            if (file.exists()) return true;
        } catch (Exception io) {
            io.printStackTrace();
            return false;
        }
        return false;
    }

    private void copyDBToSDCard() {
        this.getReadableDatabase();
        try {
            InputStream inputStream = dbContext.getAssets().open(DB_NAME);
            File file = new File(PATH);
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        SQLiteDatabase.deleteDatabase(new File(PATH));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
