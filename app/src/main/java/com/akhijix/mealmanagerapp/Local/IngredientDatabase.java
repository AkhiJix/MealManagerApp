package com.akhijix.mealmanagerapp.Local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.akhijix.mealmanagerapp.Model.Ingredient;

import static com.akhijix.mealmanagerapp.Local.IngredientDatabase.DATABASE_VERSION;

@Database(entities = Ingredient.class, version = DATABASE_VERSION)
public abstract class IngredientDatabase extends RoomDatabase {

    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME= "WFD-DB1";

    public abstract IngredientDAO iDAO();

    public static IngredientDatabase dbInstance;

    public static IngredientDatabase getDbInstance(Context context){
        if(dbInstance == null){
            dbInstance = Room.databaseBuilder(context,IngredientDatabase.class,DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return dbInstance;
    }
}
