package com.akhijix.mealmanagerapp.Local;

import java.util.List;

import com.akhijix.mealmanagerapp.Database.IRecipeDataSource;
import com.akhijix.mealmanagerapp.Model.Recipe;
import io.reactivex.Flowable;

public class RecipeDataSource implements IRecipeDataSource {

    private RecipeDAO recipeDAO;
    private static RecipeDataSource dbInstance;

    public RecipeDataSource(RecipeDAO recipeDAO) {
        this.recipeDAO = recipeDAO;
    }

    public static RecipeDataSource getInstance(RecipeDAO recipeDAO){
        if(dbInstance == null){
            dbInstance = new RecipeDataSource(recipeDAO);
        }
        return dbInstance;
    }

    @Override
    public Flowable<Recipe> getRecipeById(int recipeId) {
        return recipeDAO.getRecipeById(recipeId);
    }

    @Override
    public Flowable<List<Recipe>> getAllRecipes() {
        return recipeDAO.getAllRecipes();
    }

    @Override
    public void insertRecipe(Recipe... recipes) {
        recipeDAO.insertRecipe(recipes);
    }

    @Override
    public void deleteRecipe(Recipe... recipes) {
        recipeDAO.deleteRecipe(recipes);
    }
}
