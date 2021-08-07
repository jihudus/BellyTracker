package ru.kurant.bellytracker.model.room;

import android.content.Context;
import android.net.Uri;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/*  Sinletone for db requests incapsulation
*   Инкапсулирует запросы к базе данных */

public class SingleDBService {

    public static final String DRINKS_DB = "drinks";
    public static final String DB_FILE_TYPE = "application/*"; // For export/import db


    private static SingleDBService instance;
    private static DrinkDatabase db;

    public static synchronized SingleDBService getInstance (Context context) {
        if (instance == null) {
            instance = new SingleDBService();
        }
        db = Room.databaseBuilder(context, DrinkDatabase.class, DRINKS_DB)
                .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                .build();
        return instance;
    }

    public static synchronized boolean exportDB (Context context, Uri uri) {
        if (db != null) db.close();
        try {
            FileInputStream input = new FileInputStream(context.getDatabasePath(SingleDBService.DRINKS_DB));
            FileOutputStream output = (FileOutputStream) context.getContentResolver().openOutputStream(uri);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
                output.flush();
            }
            input.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static synchronized boolean importDB(Context context, Uri uri) {
        if (db != null) db.close();
        try {
            FileInputStream input = (FileInputStream) context.getContentResolver().openInputStream(uri);
            FileOutputStream output = new FileOutputStream(context.getDatabasePath(DRINKS_DB));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
                output.flush();
            }
            input.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public DrinkDao getDrinkDao() {
        return db.getDrinkDao();
    }

    public DrinkTypeDao getDrinkTypeDao() {
        return db.getDrinkTypeDao();
    }
}
