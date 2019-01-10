package com.akhijix.mealmanagerapp.Database;

import java.util.List;

import com.akhijix.mealmanagerapp.Model.Recipe;
import io.reactivex.Flowable;

public interface IRecipeDataSource {
    Flowable<Recipe> getRecipeById(int recipeId);
    Flowable<List<Recipe>> getAllRecipes();
    void insertRecipe(Recipe... recipes);
    void deleteRecipe(Recipe... recipes);
}
