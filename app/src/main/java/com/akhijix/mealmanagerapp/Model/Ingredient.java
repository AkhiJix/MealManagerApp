package com.akhijix.mealmanagerapp.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import io.reactivex.annotations.NonNull;

@Entity(tableName = "Ingredients")
public class Ingredient {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "Ingredient_Name")
    private String ingredient_name;

    @ColumnInfo(name = "Quantity")
    private int qty;

    @ColumnInfo(name = "Unit")
    private String unit;

    public Ingredient(String ingredient_name, int qty, String unit) {
        this.ingredient_name = ingredient_name;
        this.qty = qty;
        this.unit = unit;
    }

    public String getIngredient_name() {
        return ingredient_name;
    }

    public void setIngredient_name(String ingredient_name) {
        this.ingredient_name = ingredient_name;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return new StringBuilder(ingredient_name).append("\n").append(qty).append(unit).toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
