package ru.kurant.bellytracker.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.kurant.bellytracker.R;
import ru.kurant.bellytracker.controller.share.StringPatterns;

@Entity (tableName = "drinks", indices = {@Index(value = {"id", "drink_type"})},
        foreignKeys = {@ForeignKey(entity = DrinkType.class,
        parentColumns = "drink_type_id", childColumns = "drink_type",
        onDelete = ForeignKey.RESTRICT, onUpdate = ForeignKey.CASCADE)})

public class Drink {

    @PrimaryKey (autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo (name = "time")
    @NonNull
    private String drinkTime;

    @ColumnInfo (name = "drink_type", index = true)
    @NonNull
    private int drinkType;

    @ColumnInfo (name = "volume")
    @NonNull
    private double volume;

    public Drink(String drinkTime, int drinkType, double volume) {
        this.setDrinkTime(drinkTime);
        this.setDrinkType(drinkType);
        this.setVolume(volume);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getDrinkTime() {
        return this.drinkTime;
    }

    public void setDrinkTime(@NonNull String drinkTime) {
        this.drinkTime = drinkTime;
    }

    public int getDrinkType() {
        return drinkType;
    }

    public void setDrinkType(int drinkType) {
        this.drinkType = drinkType;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }
}
