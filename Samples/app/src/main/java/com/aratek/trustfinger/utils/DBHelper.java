package com.aratek.trustfinger.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.aratek.trustfinger.Config;
import com.aratek.trustfinger.bean.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hecl on 2018/10/10.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String TAG = "DBHelper";
    public static final String TABLE_NAME_USER = "user";

    public static final String COLUMN_NAME_USER_ID = "UserID";
    public static final String COLUMN_NAME_FIRST_NAME = "FirstName";
    public static final String COLUMN_NAME_LAST_NAME = "LastName";
    public static final String COLUMN_NAME_FINGER_DATA = "FingerData";

    public static final String DB_NAME = "db.db";
    public static final int VERSION = 1;


    public DBHelper(Context context, boolean saveToSDcard) {
        super(context, getMyDatabaseName(context, saveToSDcard), null, VERSION);
    }

    private static String getMyDatabaseName(Context context, boolean saveToSDcard) {
        boolean isSdcardEnable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            isSdcardEnable = true;
        }
        String dbPath;
        if (saveToSDcard && isSdcardEnable) {
            dbPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AratekTrustFinger/DB/";
        } else {
            dbPath = context.getFilesDir().getPath() + "/db/";
        }
        File dbp = new File(dbPath);
        if (!dbp.exists()) {
            dbp.mkdirs();
        }
        return dbPath + DB_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TBL_USER = " create table " + TABLE_NAME_USER
                + "(_id integer primary key autoincrement,"
                + COLUMN_NAME_USER_ID + " text NOT NULL UNIQUE,"
                + COLUMN_NAME_FIRST_NAME + " text NOT NULL,"
                + COLUMN_NAME_LAST_NAME + " text,"
                + COLUMN_NAME_FINGER_DATA + " text"
                + ") ";
        db.execSQL(CREATE_TBL_USER);
        db.execSQL("CREATE INDEX if not exists index_userID ON "
                + TABLE_NAME_USER + "(" + COLUMN_NAME_USER_ID + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insertUser(User user) {
        if (user == null) {
            return false;
        }
        SQLiteDatabase mysql = getReadableDatabase();
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    arrayOutputStream);
            objectOutputStream.writeObject(user);
            objectOutputStream.flush();
            byte data[] = arrayOutputStream.toByteArray();
            objectOutputStream.close();
            arrayOutputStream.close();
            ContentValues mC = new ContentValues();
            mC.put(COLUMN_NAME_USER_ID, user.getId());
            mC.put(COLUMN_NAME_FIRST_NAME, user.getFirstName());
            mC.put(COLUMN_NAME_LAST_NAME, user.getLastName());
            mC.put(COLUMN_NAME_FINGER_DATA, data);
            mysql.beginTransaction();
            mysql.insert(TABLE_NAME_USER, null, mC);
            mysql.setTransactionSuccessful();
            mysql.endTransaction();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getUserList() {
        SQLiteDatabase mysql = getReadableDatabase();
        List<User> userList = new ArrayList<User>();
        User user = null;
        Cursor cursor = mysql.rawQuery("select * from " + TABLE_NAME_USER,
                null);
        byte data[];
        ByteArrayInputStream arrayInputStream = null;
        ObjectInputStream inputStream = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                data = cursor
                        .getBlob(cursor.getColumnIndex(COLUMN_NAME_FINGER_DATA));
                arrayInputStream = new ByteArrayInputStream(data);
                try {
                    inputStream = new ObjectInputStream(arrayInputStream);
                    user = (User) inputStream.readObject();
                    userList.add(user);
                    inputStream.close();
                    arrayInputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return userList;
    }

    public boolean removeUser(User user) {
        if (user == null) {
            return false;
        }
        SQLiteDatabase mysql = getReadableDatabase();
        try {
            mysql.beginTransaction();
            mysql.delete(TABLE_NAME_USER, COLUMN_NAME_USER_ID + " = ?",
                    new String[]{user.getId()});
            mysql.setTransactionSuccessful();
            mysql.endTransaction();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkUserExist(String userId) {
        if (userId == null || TextUtils.isEmpty(userId)) {
            return false;
        }
        SQLiteDatabase mysql = getReadableDatabase();
        Cursor cursor = mysql.rawQuery("select * from " + TABLE_NAME_USER + "  where " + COLUMN_NAME_USER_ID + " =? ", new String[]{userId});
        while (cursor.moveToNext()) {
            return true;
        }
        return false;
    }


    public boolean removeAll() {
        SQLiteDatabase mysql = getReadableDatabase();
        try {
            mysql.beginTransaction();
            String DELETE_TABLE = "delete from " + TABLE_NAME_USER;
            mysql.execSQL(DELETE_TABLE);
            String RESET_SEQUENCE = "DELETE FROM sqlite_sequence";
            mysql.execSQL(RESET_SEQUENCE);
            mysql.setTransactionSuccessful();
            mysql.endTransaction();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
