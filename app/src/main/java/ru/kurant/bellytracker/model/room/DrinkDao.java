package ru.kurant.bellytracker.model.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.kurant.bellytracker.model.Drink;

@Dao
public interface DrinkDao {

    @Insert(entity = Drink.class)
    void insertDrink (Drink drink);

    @Update(entity = Drink.class)
    void updateDrink (Drink drink);

    @Delete(entity = Drink.class)
    void deleteDrink (Drink drink);

    @Query(value = "SELECT * FROM drinks ORDER BY time DESC")
    List<Drink> selectAllDrinks();

    @Query(value = "SELECT * FROM drinks WHERE id = :id")
    Drink getDrinkById (int id);

    @Query("SELECT * FROM drinks WHERE time LIKE :time")
    List<Drink> getDrinkByDate(String time);

    @Query("SELECT * FROM drinks WHERE time BETWEEN :time1 AND :time2")
    List<Drink> getDrinkByDate(String time1, String time2);
}
