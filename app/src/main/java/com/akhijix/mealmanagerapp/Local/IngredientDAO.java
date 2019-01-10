package edu.sjsu.akhilesh.dinner.Local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import edu.sjsu.akhilesh.dinner.Model.Ingredient;
import io.reactivex.Flowable;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface IngredientDAO {
    @Query("SELECT * from Ingredients WHERE id=:ingredientId")
    Flowable<Ingredient> getIngredientById(int ingredientId);

    @Query("SELECT * FROM Ingredients")
    Flowable<List<Ingredient>> getAllIngredients();

    @Insert
    void insertIngredient(Ingredient... ingredients);

    @Delete
    void deleteIngredient(Ingredient... ingredients);

    @Update(onConflict = REPLACE)
    void updateIngredient(Ingredient... ingredients);

}
