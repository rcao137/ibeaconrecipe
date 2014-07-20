package com.codepath.beacon;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Application;
import android.util.Log;

import com.codepath.beacon.models.Recipe;
import com.codepath.beacon.models.TriggerAction;
import com.codepath.beacon.scan.BleDeviceInfo;
import com.parse.Parse;
import com.parse.ParseObject;

public class BeaconApplication extends Application {
  private static final String LOG_TAG = BeaconApplication.class.getSimpleName();

  private static final String APP_ID = "KopNnh31P28DZMDp9njtWRDpgUkn2qwrMBNZ53VJ";

  private static final String CLIENT_KEY = "MI66awL0XWsXnNrTn6KKjo27vOsCE9jwYsyk2b95";
  
  private final Set<Recipe> myRecipes = new HashSet<Recipe>();

  private static BeaconApplication beaconApplication;

  public static BeaconApplication getApplication() {
    return beaconApplication;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d(LOG_TAG, "onCreate(). Initializing Parse");
    ParseObject.registerSubclass(Recipe.class);
    // TODO: Parse.enableLocalDatastore(this);
    ParseObject.registerSubclass(BleDeviceInfo.class);
    ParseObject.registerSubclass(TriggerAction.class);
    Parse.initialize(this, APP_ID, CLIENT_KEY);
    Recipe.isInitialized = true;
    BleDeviceInfo.isInitialized = true;
    TriggerAction.isInitialized = true;
    beaconApplication = this;

  }
  
  public boolean addNewRecipe(Recipe recipe) {
	  if (myRecipes.contains(recipe)) {
		  return false;
	  } else {
		  myRecipes.add(recipe);
		  return true;
	  }
  }
  
  public void deleteRecipe(Recipe recipe) {
	  myRecipes.remove(recipe);
  }
  
  public void addAllRecipes(List<Recipe> recipes) {
	  myRecipes.addAll(recipes);
  }
  
  public boolean recipeExists(Recipe recipe) {
	  if (myRecipes.contains(recipe)) {
		  return true;
	  } else {
		  return false;
	  }
  }
}
