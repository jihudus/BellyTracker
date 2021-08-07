package ru.kurant.bellytracker.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "drink_types", indices = {@Index(value = "drink_type", unique = true)})
public class DrinkType {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "drink_type_id")
    private int drinkTypeId;

    @ColumnInfo(name = "drink_type")
    @NonNull
    private String drinkName;

    @ColumnInfo(name = "alcohol")
    private int alcoholPercent;

    @ColumnInfo(name = "sugar")
    private boolean hasSugar;

    @ColumnInfo(name = "hot")
    private boolean isHot;

    public DrinkType(@NonNull String drinkName, int alcoholPercent, boolean hasSugar, boolean isHot) {
        this.setDrinkName(drinkName);
        this.setAlcoholPercent(alcoholPercent);
        this.setHasSugar(hasSugar);
        this.setHot(isHot);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DrinkType drinkType = (DrinkType) o;
        return getDrinkTypeId() == drinkType.getDrinkTypeId() &&
                getAlcoholPercent() == drinkType.getAlcoholPercent() &&
                isHasSugar() == drinkType.isHasSugar() &&
                isHot() == drinkType.isHot() &&
                getDrinkName().equals(drinkType.getDrinkName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDrinkTypeId(), getDrinkName());
    }

    public int getDrinkTypeId() {
        return drinkTypeId;
    }

    public void setDrinkTypeId(int drinkTypeId) {
        this.drinkTypeId = drinkTypeId;
    }

    @NonNull
    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(@NonNull String drinkName) {
        this.drinkName = drinkName;
    }

    public int getAlcoholPercent() {
        return alcoholPercent;
    }

    public void setAlcoholPercent(int alcoholPercent) {
        this.alcoholPercent = alcoholPercent;
    }

    public boolean isHasSugar() {
        return hasSugar;
    }

    public void setHasSugar(boolean hasSugar) {
        this.hasSugar = hasSugar;
    }

    public boolean isHot() {
        return isHot;
    }

    public void setHot(boolean hot) {
        isHot = hot;
    }
}
