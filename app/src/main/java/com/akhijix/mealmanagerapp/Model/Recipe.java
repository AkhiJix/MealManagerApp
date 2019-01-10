package com.akhijix.mealmanagerapp.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import io.reactivex.annotations.NonNull;


@Entity(tableName = "Recipes")
public class Recipe {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ColumnInfo(name= "RecipeName")
    private String name;

    @ColumnInfo(name = "Instructions")
    private String instr;

    @ColumnInfo(name = "Ingredients")
    private String ingr;


    public Recipe(String name, String instr, String ingr){
        this.name = name;
        this.instr = instr;
        this.ingr = ingr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstr() {
        return instr;
    }

    public void setInstr(String instr) {
        this.instr = instr;
    }

    public String getIngr() {
        return ingr;
    }

    public void setIngr(String ingr) {
        this.ingr = ingr;
    }

    @Override
    public String toString() {
        return new StringBuilder(name).append("\n").append(instr).toString();
    }
}
