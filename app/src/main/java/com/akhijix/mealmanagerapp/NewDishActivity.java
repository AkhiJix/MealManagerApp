package com.akhijix.mealmanagerapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.Observable;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class NewDishActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;

    // UI Elements
    private ListView listview;
    private Button addToListBtn;
    private EditText recipeName;
    private AutoCompleteTextView newIngred;
    private EditText ingredqty;
    private EditText ingredunit;
    private String ListElements[] = new String[]{};
    private ImageView recimg;
    private EditText inst;

    // check counter for number of ingredients > 10
    private int list_count;

    //Adapter
    List<Recipe> recipeList = new ArrayList<>();
    List<Ingredient> ingredList = new ArrayList<>();
    List<Ingredient> existingingredList = new ArrayList<>();
    ArrayAdapter adapter;
    ArrayList<String> recipeNamesList;
    ArrayList<String> ingredientNamesList;
    List<String> pullDownList;

    //Database - Recipes
    private CompositeDisposable compositeDisposable;
    private RecipeRepository recipeRepository;

    //Database - Ingredients
    private CompositeDisposable compositeDisposable1;
    private CompositeDisposable compositeDisposable2;
    private IngredientRepository ingredientRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dish);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        list_count = 0;

        //Init
        compositeDisposable = new CompositeDisposable();
        compositeDisposable1 = new CompositeDisposable();
        compositeDisposable2 = new CompositeDisposable();

        //Init view
        listview = (ListView) findViewById(R.id.listIngred);
        addToListBtn = (Button) findViewById(R.id.addToListBtn);
        newIngred = (AutoCompleteTextView) findViewById(R.id.newIngred);
        ingredqty = (EditText) findViewById(R.id.quantity);
        ingredunit = (EditText) findViewById(R.id.unit_qty);
        recimg = (ImageView) findViewById(R.id.recipeImg);
        recipeName = (EditText) findViewById(R.id.recipe_name);
        inst = (EditText) findViewById(R.id.instructions);


        // focus removed from recipeName, check for existing recipe
        recipeName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && !recipeName.getText().toString().equals("")) {
                    if(recipeNamesList.contains(recipeName.getText().toString())){
                        Toast.makeText(NewDishActivity.this, "Recipe Already Exists!",Toast.LENGTH_SHORT).show();
                        recipeName.setText("");
                    }
                }
            }
        });

        // focus removed from Ingredient Name, check for existing ingredient: set unit
        newIngred.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && !newIngred.getText().toString().equals("")) {
                    newIngred.showDropDown();
                    if(ingredientNamesList.contains(newIngred.getText().toString()))
                    {
                        for (Ingredient in : existingingredList)
                        {
                            if (in.getIngredient_name().equals(newIngred.getText().toString())){
                                String u = in.getUnit();
                                ingredunit.setText(u);
                                ingredunit.setEnabled(false);
                            }
                        }
                    }
                }
                if(newIngred.getText().toString().equals("" )) {
                    ingredunit.setEnabled(true);
                    ingredunit.setText("");
                }
            }
        });

        //RecipeDB
        RecipeDatabase recipeDatabase = RecipeDatabase.getDbInstance(this);
        recipeRepository = RecipeRepository.getmInstance(RecipeDataSource.getInstance(recipeDatabase.rDAO()));
        loadRecipeData();

        //Ingred DB
        IngredientDatabase ingredientDatabase = IngredientDatabase.getDbInstance(this);
        ingredientRepository = IngredientRepository.getmInstance(IngredientDataSource.getInstance(ingredientDatabase.iDAO()));
        loadIngredientData();

        // Local list of ingredients visible ot the User, saves new ingredients to the database after checking for existing recipes
        final List<String> ListElementsArrayList = new ArrayList<String>(Arrays.asList(ListElements));
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewDishActivity.this, android.R.layout.simple_list_item_1, ListElementsArrayList);
        listview.setAdapter(adapter);

        //List adapter to connect to pull down list when adding new ingredient
        pullDownList = new ArrayList<String>();

        final ArrayAdapter<String> adapterPullDownList = new ArrayAdapter<String>(NewDishActivity.this,android.R.layout.simple_list_item_1,pullDownList);
        newIngred.setAdapter(adapterPullDownList);
        newIngred.setThreshold(0);

        // onclick - add to list button - adds ingredient to list and to ingredient database
        addToListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if there are less than 10 items in the list
                if (list_count<10) {
                    // if ingrname, qty and unit are not empty, add in the visible list, then add in DB
                    if(!newIngred.getText().toString().isEmpty() && !ingredunit.getText().toString().isEmpty() && !ingredqty.getText().toString().isEmpty()){
                        try{
                            ListElementsArrayList.add(newIngred.getText().toString() + " - " + ingredqty.getText() + " " + ingredunit.getText().toString());
                            adapter.notifyDataSetChanged();
                            list_count++;
                        }
                        catch (Exception e){
                            Toast.makeText(NewDishActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                        Disposable disposable = io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {
                            @Override
                            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                                int prevqty = 0;
                                if(ingredientNamesList.contains(newIngred.getText().toString()))
                                {
                                    for (Ingredient in : existingingredList){
                                        if (in.getIngredient_name().equals(newIngred.getText().toString())){
                                            prevqty = in.getQty();
                                            in.setQty(prevqty + Integer.parseInt(ingredqty.getText().toString()));
                                            ingredientRepository.updateIngredient(in);
                                            adapterPullDownList.notifyDataSetChanged();
                                        }
                                    }
                                }
                                else{
                                    ingredientRepository.insertIngredient(new Ingredient(newIngred.getText().toString(), prevqty + Integer.parseInt(ingredqty.getText().toString()), ingredunit.getText().toString()));
                                    adapterPullDownList.notifyDataSetChanged();
                                }
                                newIngred.setText("");
                                ingredqty.setText("");
                                ingredunit.setText("");
                                ingredunit.setEnabled(true);
                                e.onComplete();
                            }
                        })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Consumer() {
                                    @Override
                                    public void accept(Object o) throws Exception {
                                        Toast.makeText(NewDishActivity.this, "Ingredient Added!",Toast.LENGTH_SHORT).show();
                                    }
                                }, new Consumer<Throwable>(){
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Toast.makeText(NewDishActivity.this, ""+throwable.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else Toast.makeText(NewDishActivity.this,"problem",Toast.LENGTH_SHORT).show();
                }
                //if there are more than 10 items in the list
                else{
                    Toast.makeText(NewDishActivity.this,"Only 10 ingredients allowed!",Toast.LENGTH_SHORT).show();
                    newIngred.setText("");
                    ingredqty.setText("");
                    ingredunit.setText("");
                }
            }
        });

        // onclick - floatingbuttion - save recipe to recipe database and go back to main_activity screen
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.saveRecipeBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Disposable disposable = io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {
                    @Override
                    public void subscribe(ObservableEmitter<Object> e) throws Exception {
                        Recipe recipe =  new Recipe(recipeName.getText().toString(),inst.getText().toString(),ingredList.toString());
                        recipeRepository.insertRecipe(recipe);
                        e.onComplete();
                    }
                })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer() {
                            @Override
                            public void accept(Object o) throws Exception {
                                Toast.makeText(NewDishActivity.this, "Recipe Added!",Toast.LENGTH_SHORT).show();
                            }
                        }, new Consumer<Throwable>(){
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(NewDishActivity.this, ""+throwable.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                Toast.makeText(NewDishActivity.this, "Recipe Added!",Toast.LENGTH_SHORT).show();
                finish(); //go back to main_activity screen
            }});
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Load all Recipes from the Database to check for existing RecipeNames
    private void loadRecipeData(){
        Disposable disposable = recipeRepository.getAllRecipes()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Recipe>>() {
                    @Override
                    public void accept(List<Recipe> recipes) throws Exception {
                        recipeList.clear();
                        recipeList.addAll(recipes);
                        recipeNamesList = new ArrayList<>();
                        for(Recipe recipe : recipeList){
                            recipeNamesList.add(recipe.getName());
                        }
                    }
                }, new Consumer<Throwable>(){
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(NewDishActivity.this,""+throwable.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
        compositeDisposable1.add(disposable);
    }

    // Load all Recipes from the Database to check for existing RecipeNames
    private void loadIngredientData(){
        Disposable disposable = ingredientRepository.getAllIngredients()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Ingredient>>() {
                    @Override
                    public void accept(List<Ingredient> ingredients) throws Exception {
                        existingingredList.clear();
                        existingingredList.addAll(ingredients);
                        ingredientNamesList = new ArrayList<>();
                        for(Ingredient ingredient : existingingredList){
                            ingredientNamesList.add(ingredient.getIngredient_name());
                        }
                        pullDownList.addAll(ingredientNamesList);
                    }
                }, new Consumer<Throwable>(){
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(NewDishActivity.this,""+throwable.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
        compositeDisposable2.add(disposable);
    }

    // Select Image from Gallery
    public void addRecipeImage(View view) {
        checkPermissions();
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        startActivityForResult(pickIntent,PICK_IMAGE);
    }

    // Gives Result code and Data after coming back from Gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null!= data){
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            recimg.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    // Check for Storage Permission for Gallery
    private void checkPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    1052);

        }
    }

    // Request Storage Permission for Gallery Access
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1052: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted.

                } else {
                    Toast.makeText(NewDishActivity.this, "Give Permissions!",Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}