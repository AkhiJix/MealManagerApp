package com.akhijix.mealmanagerapp.Database;

import java.util.List;

import com.akhijix.mealmanagerapp.Model.Ingredient;
import io.reactivex.Flowable;

public class IngredientRepository implements IIngredientDataSource {

    private IIngredientDataSource mLocalDataSource;
    private static IngredientRepository mInstance;

    public IngredientRepository (IIngredientDataSource mLocalDataSource) {
        this.mLocalDataSource = mLocalDataSource;
    }

    public static IngredientRepository getmInstance(IIngredientDataSource mLocalDataSource){
        if(mInstance == null){
            mInstance = new IngredientRepository(mLocalDataSource);
        }
        return mInstance;
    }

    @Override
    public Flowable<Ingredient> getIngredientById(int ingredientId) {
        return mLocalDataSource.getIngredientById(ingredientId);
    }

    @Override
    public Flowable<List<Ingredient>> getAllIngredients() {
        return mLocalDataSource.getAllIngredients();
    }

    @Override
    public void insertIngredient(Ingredient... ingredients) {
        mLocalDataSource.insertIngredient(ingredients);
    }

    @Override
    public void deleteIngredient(Ingredient... ingredients) {
        mLocalDataSource.deleteIngredient(ingredients);
    }

    @Override
    public void updateIngredient(Ingredient... ingredients) {
        mLocalDataSource.updateIngredient(ingredients);
    }
}
