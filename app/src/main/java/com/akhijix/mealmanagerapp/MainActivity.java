package com.akhijix.mealmanagerapp;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createNewDish(View view) {
        Intent intent = new Intent(getApplicationContext(),NewDishActivity.class);
        startActivity(intent);
    }

    public void createGroceryList(View view) {
        Intent intent = new Intent(getApplicationContext(),GroceriesActivity.class);
        startActivity(intent);
    }

    public void createRecipe(View view) {
        //Intent intent = new Intent(getApplicationContext(),Recipe_main.class);
        //startActivity(intent);
    }

    public void showMeals(View view){
        //Intent intent = new Intent(getApplicationContext(),MealScreen.class);
        //startActivity(intent);
    }

}
