package com.akhijix.mealmanagerapp.Local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import com.akhijix.mealmanagerapp.Model.Recipe;
import io.reactivex.Flowable;

@Dao
public interface RecipeDAO {

    @Query("SELECT * FROM Recipes WHERE id=:recipeId")
    Flowable<Recipe> getRecipeById(int recipeId);

    @Query("SELECT * FROM Recipes")
    Flowable<List<Recipe>> getAllRecipes();

    @Insert
    void insertRecipe(Recipe... recipes);

    @Delete
    void deleteRecipe(Recipe... recipes);

}
