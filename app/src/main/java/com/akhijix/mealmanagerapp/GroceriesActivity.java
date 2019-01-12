package com.akhijix.mealmanagerapp;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.akhijix.mealmanagerapp.Database.IngredientRepository;
import com.akhijix.mealmanagerapp.Database.RecipeRepository;
import com.akhijix.mealmanagerapp.Local.IngredientDataSource;
import com.akhijix.mealmanagerapp.Local.IngredientDatabase;
import com.akhijix.mealmanagerapp.Local.RecipeDataSource;
import com.akhijix.mealmanagerapp.Local.RecipeDatabase;
import com.akhijix.mealmanagerapp.Model.Ingredient;
import com.akhijix.mealmanagerapp.Model.Recipe;
import io.reactivex.Flowable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class GroceriesActivity extends AppCompatActivity {
    private CompositeDisposable compositeDisposable1;
    private IngredientRepository ingredientRepository;
    List<Ingredient> ingredList = new ArrayList<>();
    ArrayList<String> ingrednames = new ArrayList<>();
    ArrayAdapter adapter;
    SwipeMenuListView listView;
    String itemAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groceries);

        //Init
        compositeDisposable1 = new CompositeDisposable();

        //Init view
        listView = (SwipeMenuListView) findViewById(R.id.listView);

        //Ingred DB
        IngredientDatabase ingredientDatabase = IngredientDatabase.getDbInstance(this);
        ingredientRepository = IngredientRepository.getmInstance(IngredientDataSource.getInstance(ingredientDatabase.iDAO()));
        loadIngredientData();

        //Create Swipe menu to deal with swiping interactions
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem addToQuantity = new SwipeMenuItem(getApplicationContext());
                addToQuantity.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                addToQuantity.setWidth(170);
                addToQuantity.setTitle("+");
                addToQuantity.setTitleSize(18);
                addToQuantity.setTitleColor(Color.WHITE);
                menu.addMenuItem(addToQuantity);

                SwipeMenuItem deductFromQuantity = new SwipeMenuItem(getApplicationContext());
                deductFromQuantity.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,0xCE)));
                deductFromQuantity.setWidth(170);
                deductFromQuantity.setTitle("-");
                deductFromQuantity.setTitleSize(18);
                deductFromQuantity.setTitleColor(Color.WHITE);
                menu.addMenuItem(deductFromQuantity);
            }
        };
        listView.setMenuCreator(creator);

        //Adapter to hold ingredients list
        adapter = new ArrayAdapter(this,R.layout.groceries_listview, ingredList);
        listView.setAdapter(adapter);

        // Handle clicks on plus minus buttons for every ingredient
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                itemAt = listView.getItemAtPosition(position).toString();
                if(!itemAt.equals("")){
                    switch (index)
                    {
                        case 0:
                            io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {
                                @Override
                                public void subscribe(ObservableEmitter<Object> e) throws Exception
                                {
                                    String ingrname = itemAt.split("\\s+")[0];
                                    if (ingrednames.contains(ingrname)) {
                                        for (Ingredient in : ingredList) {
                                            if (in.getIngredient_name().equals(ingrname)) {
                                                in.setQty(in.getQty() + 1);
                                                ingredientRepository.updateIngredient(in);
                                            }
                                        }
                                    }
                                    loadIngredientData();
                                    e.onComplete();
                                }
                            })
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new Consumer() {
                                        @Override
                                        public void accept(Object o) throws Exception {
                                            Toast.makeText(GroceriesActivity.this, "Value modified in database!",Toast.LENGTH_SHORT).show();
                                        }
                                    }, new Consumer<Throwable>(){
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            Toast.makeText(GroceriesActivity.this, ""+throwable.getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            break;

                        case 1:
                            io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {
                                @Override
                                public void subscribe(ObservableEmitter<Object> e) throws Exception
                                {
                                    String ingrname = itemAt.split("\\s+")[0];
                                    if (ingrednames.contains(ingrname)) {
                                        for (Ingredient in : ingredList) {
                                            if (in.getIngredient_name().equals(ingrname)) {
                                                int tmp = in.getQty();
                                                if(tmp>0)   in.setQty(tmp - 1);
                                                ingredientRepository.updateIngredient(in);
                                            }
                                        }
                                    }
                                    loadIngredientData();
                                    e.onComplete();
                                }
                            })
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new Consumer() {
                                        @Override
                                        public void accept(Object o) throws Exception {
                                            Toast.makeText(GroceriesActivity.this, "Value modified in database!",Toast.LENGTH_SHORT).show();
                                        }
                                    }, new Consumer<Throwable>(){
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            Toast.makeText(GroceriesActivity.this, ""+throwable.getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            break;
                    }
                }
                else{
                    return false; //closes menu
                }
                return true; // keeps menu open
            }
        });

    }

    // Load all Recipes from the Database to check for existing IngredientNames
    private void loadIngredientData() {
        Disposable disposable = ingredientRepository.getAllIngredients()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Ingredient>>() {
                    @Override
                    public void accept(List<Ingredient> ingredients) throws Exception {
                        ingredList.clear();
                        ingredList.addAll(ingredients);
                        adapter.notifyDataSetChanged();
                        for (Ingredient in : ingredList){
                            ingrednames.add(in.getIngredient_name());
                        }
                    }
                }, new Consumer<Throwable>(){
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(GroceriesActivity.this,""+throwable.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
        compositeDisposable1.add(disposable);
    }
}

