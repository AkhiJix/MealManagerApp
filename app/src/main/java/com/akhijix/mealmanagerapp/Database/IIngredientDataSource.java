package com.akhijix.mealmanagerapp.Database;

import java.util.List;

import com.akhijix.mealmanagerapp.Model.Ingredient;
import io.reactivex.Flowable;

public interface IIngredientDataSource {
    Flowable<Ingredient> getIngredientById(int ingredientId);
    Flowable<List<Ingredient>> getAllIngredients();
    void insertIngredient(Ingredient... ingredients);
    void deleteIngredient(Ingredient... ingredients);
    void updateIngredient(Ingredient... ingredients);
}
