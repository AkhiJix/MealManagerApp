package com.akhijix.mealmanagerapp.Database;

import java.util.List;

import com.akhijix.mealmanagerapp.Model.Recipe;
import io.reactivex.Flowable;

public class RecipeRepository implements IRecipeDataSource{

    private IRecipeDataSource mLocalDataSource;
    private static RecipeRepository mInstance;

    public RecipeRepository(IRecipeDataSource mLocalDataSource) {
        this.mLocalDataSource = mLocalDataSource;
    }

    public static RecipeRepository getmInstance(IRecipeDataSource mLocalDataSource){
        if(mInstance == null){
            mInstance = new RecipeRepository(mLocalDataSource);
        }
        return mInstance;
    }

    @Override
    public Flowable<Recipe> getRecipeById(int recipeId) {
        return mLocalDataSource.getRecipeById(recipeId);
    }

    @Override
    public Flowable<List<Recipe>> getAllRecipes() {
        return mLocalDataSource.getAllRecipes();
    }

    @Override
    public void insertRecipe(Recipe... recipes) {
        mLocalDataSource.insertRecipe(recipes);
    }

    @Override
    public void deleteRecipe(Recipe... recipes) {
        mLocalDataSource.deleteRecipe(recipes);
    }
}