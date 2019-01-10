package com.akhijix.mealmanagerapp.Local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.akhijix.mealmanagerapp.Model.Recipe;

import static com.akhijix.mealmanagerapp.Local.RecipeDatabase.DATABASE_VERSION;

@Database(entities = Recipe.class, version = DATABASE_VERSION)
public abstract class RecipeDatabase extends RoomDatabase {

    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME= "WFD-DB";

    public abstract RecipeDAO rDAO();

    public static RecipeDatabase dbInstance;

    public static RecipeDatabase getDbInstance(Context context){
        if(dbInstance == null){
            dbInstance = Room.databaseBuilder(context,RecipeDatabase.class,DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return dbInstance;
    }
}
