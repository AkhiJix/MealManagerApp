package com.akhijix.mealmanagerapp.Local;

import java.util.List;

import com.akhijix.mealmanagerapp.Database.IIngredientDataSource;
import com.akhijix.mealmanagerapp.Model.Ingredient;
import io.reactivex.Flowable;

public class IngredientDataSource implements IIngredientDataSource {

    private IngredientDAO ingredientDAO;
    private static IngredientDataSource dbInstance;

    public IngredientDataSource(IngredientDAO ingredientDAO) {
        this.ingredientDAO = ingredientDAO;
    }

    public static IngredientDataSource getInstance(IngredientDAO ingredientDAO){
        if(dbInstance==null){
            dbInstance = new IngredientDataSource(ingredientDAO);
        }
        return dbInstance;
    }

    @Override
    public Flowable<Ingredient> getIngredientById(int ingredientId){
        return ingredientDAO.getIngredientById(ingredientId);
    }

    @Override
    public Flowable<List<Ingredient>> getAllIngredients() {
        return ingredientDAO.getAllIngredients();
    }

    @Override
    public void insertIngredient(Ingredient... ingredients) {
        ingredientDAO.insertIngredient(ingredients);
    }

    @Override
    public void deleteIngredient(Ingredient... ingredients) {
        ingredientDAO.deleteIngredient(ingredients);
    }

    @Override
    public void updateIngredient(Ingredient... ingredients) {
        ingredientDAO.updateIngredient(ingredients);
    }

}
