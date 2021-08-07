package ru.kurant.bellytracker.model.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import ru.kurant.bellytracker.model.Drink;
import ru.kurant.bellytracker.model.DrinkType;

@Database(entities = {Drink.class, DrinkType.class}, version = 3)
public abstract class DrinkDatabase extends RoomDatabase {

    public abstract DrinkDao getDrinkDao();

    public abstract DrinkTypeDao getDrinkTypeDao();
}
