package com.android.baking.ui.widget;

import java.util.List;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;

import android.appwidget.AppWidgetManager;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import retrofit2.Response;

import com.android.baking.R;

import com.android.baking.service.ApiCaller;
import com.android.baking.service.RequestCompletionListener;

import com.android.baking.db.Database;

import com.android.baking.model.Ingredient;
import com.android.baking.model.Recipe;
import com.android.baking.model.Widget;

public class WidgetActivity extends Activity implements RequestCompletionListener {

    int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private ArrayList<Recipe> recipes;

    private Spinner spinner;

    private ProgressDialog progressDialog;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {

        public void onClick(View view) {

            Recipe recipe = recipes.get(spinner.getSelectedItemPosition());
            Widget widget = new Widget(recipe.getName(), (ArrayList<Ingredient>) recipe.getIngredients());

            Context context = WidgetActivity.this;
            Database database = new Database(context);
            database.insertIngredients(widget, appWidgetId);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RecipeWidget.updateAppWidget(context, appWidgetManager, appWidgetId);

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();

        }

    };

    @Override
    public void onCreate(Bundle icicle) {

        super.onCreate(icicle);
        setResult(RESULT_CANCELED);

        setContentView(R.layout.baking_widget_configuration);

        spinner = findViewById(R.id.spinner);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();

        findViewById(R.id.add_widget).setOnClickListener(mOnClickListener);

        //Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        //If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        } else {
            ApiCaller.loadRecipes(this);
        }

    }

    @Override
    public void onFailure(String message) {
        progressDialog.dismiss();
        Toast.makeText(this, "There is a problem try again later ! or make sure you are connected to internet"
                , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(Response<List<Recipe>> response) {
        progressDialog.dismiss();
        recipes = (ArrayList<Recipe>) response.body();

        String[]values= new  String [recipes.size()];
        for(int i=0; i < recipes.size();i++)
        {
            values[i]=recipes.get(i).getName();
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }
}

