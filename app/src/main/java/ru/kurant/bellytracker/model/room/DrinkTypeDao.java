package ru.kurant.bellytracker.model.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.kurant.bellytracker.model.DrinkType;

@Dao
public interface DrinkTypeDao {

    @Insert(entity = DrinkType.class)
    void insertDrinkType(DrinkType drinkType);

    @Update(entity = DrinkType.class)
    void updateDrinkType(DrinkType drinkType);

    @Delete(entity = DrinkType.class)
    void deleteDrinkType(DrinkType drinkType);

    @Query(value = "SELECT * FROM drink_types ORDER BY drink_type_id")
    List<DrinkType> selectAllDrinkTypes ();

    @Query(value = "SELECT * FROM drink_types WHERE drink_type_id = :id")
    DrinkType selectDrinkTypeById (int id);

    @Query(value = "SELECT drink_type FROM drink_types ORDER BY drink_type")
    List<String> selectDrinkTypeNames ();

    @Query(value = "SELECT * FROM drink_types WHERE drink_type = :type")
    DrinkType selectDrinkTypeByName (String type);
}
