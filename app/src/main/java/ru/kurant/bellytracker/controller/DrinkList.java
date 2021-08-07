package ru.kurant.bellytracker.controller;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ru.kurant.bellytracker.model.Drink;
import ru.kurant.bellytracker.model.DrinkType;
import ru.kurant.bellytracker.model.room.DrinkDao;
import ru.kurant.bellytracker.model.room.DrinkTypeDao;
import ru.kurant.bellytracker.model.room.SingleDBService;

/*  Класс для обработки списков объектов, полученных из базы данных
*   Class manipulates with db requests results */

/*  This class should be in model package that incapsulates db working.
    Besides another class needed to incapsulate ui working (handle user input and prepare data to show in ui).
    Fragment classes need know nothing about model classes.
    Этот класс надо перенести в модель, и дописать второй, чтобы обрабатывал пользовательский ввод
*   и готовил данные к показу. Чтобы фрагменты получали уже готовые данные и не знали ничего о классах модели */
public class DrinkList {

    private final ExecutorService dbThread = Executors.newSingleThreadExecutor();
    private final DrinkDao drinkDao;
    private final DrinkTypeDao drinkTypeDao;
    private ArrayList<Drink> drinkArrayList;
    private ArrayList<DrinkType> beverageTypeArrayList;
    private ArrayList<String> drinkTypeNamesArrayList;

    private final SingleDBService dbService;

    public DrinkList (Context context) {
        this.dbService = SingleDBService.getInstance(context);
        this.drinkDao = dbService.getDrinkDao();
        this.drinkTypeDao = dbService.getDrinkTypeDao();
    }

    public Drink getDrinkFromList(int number) {
        if (drinkArrayList == null) {
            return getDrinkList().get(number);
        }
        else return drinkArrayList.get(number);
    }

    public Drink getDrinkById (int id) {
        Drink drink = null;
        Future<Drink> future = dbThread.submit(new Callable<Drink>() {
            @Override
            public Drink call() {
                return drinkDao.getDrinkById(id);
            }
        });
        try {
            drink = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drink;
    }

    public ArrayList<Drink> getDrinkList() {
        Future<ArrayList<Drink>> future = dbThread.submit(new Callable<ArrayList<Drink>>() {
            @Override
            public ArrayList<Drink> call() {
                return (ArrayList<Drink>) drinkDao.selectAllDrinks();
            }
        });
        try {
            drinkArrayList = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drinkArrayList;
    }

    /*  Get db objects where time column that begins with particular date
    *   Запрос объекта из базы по начальным цифрам даты */
    public List<Drink> getDrinksByDate(String time) {
        List<Drink> drinks = new ArrayList<>();
        Future<List<Drink>> future = dbThread.submit(new Callable<List<Drink>>() {
            @Override
            public List<Drink> call() {
                return drinkDao.getDrinkByDate(time + "%");
            }
        });
        try {
            drinks = future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return drinks;
        }
        return drinks;
    }

    public ArrayList<DrinkType> getDrinkTypeList () {
        if (beverageTypeArrayList == null) {
            Future <ArrayList<DrinkType>> future = dbThread.submit(new Callable<ArrayList<DrinkType>>() {
                @Override
                public ArrayList<DrinkType> call() {
                    return (ArrayList<DrinkType>) drinkTypeDao.selectAllDrinkTypes();
                }
            });
            try {
                beverageTypeArrayList = future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return beverageTypeArrayList;
    }

    public DrinkType getDrinkType (final String name) {
        Future<DrinkType> future = dbThread.submit(new Callable<DrinkType>() {
            @Override
            public DrinkType call() {
                return drinkTypeDao.selectDrinkTypeByName(name);
            }
        });
        DrinkType beverageType;
        try {
            beverageType = future.get();
        } catch (Exception e) {
            e.printStackTrace();
            beverageType = null;
        }
        return beverageType;
    }

    public List<String> getDrinkTypeNames () {
        if (drinkTypeNamesArrayList == null) {
            Future<ArrayList<String>> future = dbThread.submit(new Callable<ArrayList<String>>() {
                @Override
                public ArrayList<String> call() {
                    return (ArrayList<String>) drinkTypeDao.selectDrinkTypeNames();
                }
            });
            try {
                drinkTypeNamesArrayList = future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return drinkTypeNamesArrayList;
    }

    public void addDrink (final Drink drink) {
        dbThread.execute(new Runnable() {
            @Override
            public void run() {
                drinkDao.insertDrink(drink);
            }
        });
    }

    public void updateDrink (final Drink drink) {
        dbThread.execute(new Runnable() {
            @Override
            public void run() {
                drinkDao.updateDrink(drink);
            }
        });
    }

    public void removeDrink (final Drink drink) {
        dbThread.execute(new Runnable() {
            @Override
            public void run() {
                drinkDao.deleteDrink (drink);
            }
        });
    }

    public void addDrinkType (final DrinkType beverageType) {
        dbThread.execute(new Runnable() {
            @Override
            public void run() {
                drinkTypeDao.insertDrinkType(beverageType);
            }
        });
    }

    public void updateDrinkType (final DrinkType beverageType) {
        dbThread.execute(new Runnable() {
            @Override
            public void run() {
                drinkTypeDao.updateDrinkType(beverageType);
            }
        });
    }

    public void removeDrinkType (final DrinkType beverageType) {
        dbThread.execute(new Runnable() {
            @Override
            public void run() {
                drinkTypeDao.deleteDrinkType(beverageType);
            }
        });
    }

    public int getDrinkTypeId (final String drinkTypeName) {
        Future<Integer> future = dbThread.submit(new Callable<Integer>() {
            @Override
            public Integer call() {
                DrinkType beverageType = drinkTypeDao.selectDrinkTypeByName(drinkTypeName);
                return beverageType.getDrinkTypeId();
            }
        });
        int id;
        try {
            id = future.get();
        } catch (Exception e) {
            e.printStackTrace();
            id = -1;
        }
        return id;
    }

    public DrinkType getDrinkTypeById(final int drinkTypeId) {
        Future<DrinkType> future = dbThread.submit(new Callable<DrinkType>() {
            @Override
            public DrinkType call() throws Exception {
                return drinkTypeDao.selectDrinkTypeById(drinkTypeId);
            }
        });
        DrinkType beverageType;
        try {
            beverageType = future.get();
        } catch (Exception e) {
            e.printStackTrace();
            beverageType = null;
        }
        return beverageType;
    }
}
