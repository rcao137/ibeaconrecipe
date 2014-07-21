package com.codepath.beacon.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.codepath.beacon.BeaconApplication;
import com.codepath.beacon.R;
import com.codepath.beacon.contracts.RecipeContracts;
import com.codepath.beacon.contracts.RecipeContracts.TRIGGERS;
import com.codepath.beacon.fragments.RecipeAlertDialog;
import com.codepath.beacon.models.Recipe;
import com.codepath.beacon.models.TriggerAction;
import com.codepath.beacon.models.TriggerAction.NOTIFICATION_TYPE;
import com.codepath.beacon.scan.BeaconListener;
import com.codepath.beacon.scan.BeaconManager;
import com.codepath.beacon.scan.BleActivity;
import com.codepath.beacon.scan.BleDeviceInfo;
import com.codepath.beacon.scan.BleService.State;
import com.codepath.beacon.ui.RecipeActionActivity1;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class RecipeDetailActivity extends Activity implements BeaconListener {
  private static final String LOG_TAG = RecipeDetailActivity.class
      .getSimpleName();

  private Recipe recipe;

  private Recipe oldRecipe;

  BeaconManager beaconManager;

  private boolean createFlag = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recipe_detail);
    ActionBar ab = getActionBar();
    ab.setDisplayHomeAsUpEnabled(true);
    recipe = new Recipe();
    populateRecipeDetail();
    beaconManager = new BeaconManager(this, null);
  }

  @Override
  protected void onStop() {
    beaconManager.stopListenening();
    super.onStop();
  }

  @Override
  protected void onStart() {
    super.onStart();
    beaconManager.startListening();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    default:
      return super.onOptionsItemSelected(item);
    }
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.recipe_detail, menu);
    return true;
  }

  private void populateRecipeDetail() {
    oldRecipe = getIntent().getParcelableExtra("recipe");
    recipe = new Recipe();
    if (oldRecipe != null) {
      recipe.setBeacon(oldRecipe.getBeacon());
      recipe.setDisplayName(oldRecipe.getDisplayName());
      recipe.setTriggerAction(oldRecipe.getTriggerAction());
      recipe.setTriggerActionDisplayName(oldRecipe
          .getTriggerActionDisplayName());
      recipe.setActivationDate(oldRecipe.getActivationDate());
      recipe.setTrigger(oldRecipe.getTrigger());
      recipe.setStatus(oldRecipe.isStatus());
      Log.e("RecipeDetailActivity",
          "User ID for old recipe:" + oldRecipe.getUserID());
      recipe.setUserID(oldRecipe.getUserID());
      // recipe.setTriggeredCount(oldRecipe.getTriggeredCount());
      recipe.setObjectId(oldRecipe.getObjectId());
      Log.d(LOG_TAG, "Recipe object id:" + recipe.getObjectId());

      Log.d("RecipeDetailActivity", "populateRecipeDetail():Old recipe:"
          + oldRecipe.getBeacon().getName());
      Log.d("RecipeDetailActivity", "populateRecipeDetail():New recipe:"
          + recipe.getBeacon().getName());
    } else {
      createFlag = true;
    }
    showRecipe();
    // findRecipeInBackground(objID);
  }

  private void showRecipe() {
    TextView tvActivationDate = (TextView) findViewById(R.id.tvActivationDate);
    if (recipe.getActivationDate() != null) {
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
      String reportDate = sdf.format(recipe.getActivationDate());
      tvActivationDate.setText(reportDate);
    }

    //if(recipe.isStatus()){
    // miToggleSwitch.setIcon(R.drawable.ic_action_switch_on);
    // }else{
    // miToggleSwitch.setIcon(R.drawable.ic_action_switch_off);
    //}
    
    TextView tvSelectedBeacon = (TextView) findViewById(R.id.tvSelectedBeacon);
    TextView tvSelectedAction = (TextView) findViewById(R.id.tvSelectedAction);
    ImageButton ibPlus1 = (ImageButton) findViewById(R.id.btn_beacon);
    ImageButton ibPlus2 = (ImageButton) findViewById(R.id.btn_notification);
    TextView tvActivationDate_lab = (TextView) findViewById(R.id.tvActivationDatelab);
    TextView tvTriggerandNotification = (TextView) findViewById(R.id.tvTriggerandNotification);

    // TextView tvBeaconnameandUUID = (TextView)
    // findViewById(R.id.tvBeaconnameandUUID);

    if (!createFlag) {
      if (recipe.getActivationDate() != null) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
        String reportDate = sdf.format(recipe.getActivationDate());
        tvActivationDate.setText(reportDate);
      }
      // if(recipe.isStatus()){
      // miToggleSwitch.setIcon(R.drawable.ic_action_switch_on);
      // }else{
      // miToggleSwitch.setIcon(R.drawable.ic_action_switch_off);
      // }
    } else {
      tvActivationDate.setVisibility(View.GONE);
      tvActivationDate_lab.setVisibility(View.GONE);
    }

    // Image button control
    if (createFlag) {
      if (recipe.getBeacon() == null) {
        ibPlus1.setImageResource(R.drawable.blueplus);
        ibPlus2.setImageResource(R.drawable.ic_grayplus);
      } else {
        ibPlus1.setImageResource(R.drawable.ic_beacon);
        if (recipe.getTriggerAction() == null)
          ibPlus2.setImageResource(R.drawable.redplus);
        else
          ibPlus2.setImageResource(R.drawable.ic_notification);
      }

    } else {
      ibPlus1.setImageResource(R.drawable.ic_beacon);
      ibPlus2.setImageResource(R.drawable.ic_notification);
    }

    if (recipe.getBeacon() != null && recipe.getBeacon().getName() != null)
      tvSelectedBeacon.setText(recipe.getBeacon().getName());

    if (recipe.getTriggerAction() != null && recipe.getTrigger() != null)
      tvSelectedAction.setText(recipe.getTriggerActionDisplayName() + " on "
          + recipe.getTrigger());

    if (recipe.getDisplayName() != null
        && recipe.getTriggerActionDisplayName() != null)
      tvTriggerandNotification
          .setText(recipe.getDisplayName() + " receive "
              + recipe.getTriggerActionDisplayName() + " on "
              + recipe.getTrigger());

    // TODO: Change image depends on beacon UUID/MajorID/MonorID and on SMS/Push
    // notification
    // TODO: Need to call 3rd party lib to get distance or other beacon related
    // information
    // TextView tvBeaconDistance = (TextView)
    // findViewById(R.id.tvBeaconDistance);
  }

  public void onScanBeacon(View view) {
    Intent scanIntent = new Intent(this, BleActivity.class);
    startActivityForResult(scanIntent, 0);
  }

  public void findRecipeInBackground(final String recipeID) {
    ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
    query.getInBackground(recipeID, new GetCallback<Recipe>() {
      public void done(final Recipe recipe, ParseException e) {
        if (e == null) {
          recipe.getParseObject(RecipeContracts.BEACON)
              .fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                  if (e == null) {
                    recipe.setBeacon((BleDeviceInfo) object);
                  } else {
                    Log.e(LOG_TAG, "ParseException", e);
                  }
                }
              });
          recipe.getParseObject(RecipeContracts.RECIPE_ACTION)
              .fetchIfNeededInBackground(new GetCallback<ParseObject>() {

                @Override
                public void done(ParseObject noticationObject,
                    ParseException done) {
                  if (done == null) {
                    recipe.setTriggerAction((TriggerAction) noticationObject);
                  } else {
                    Log.e(LOG_TAG, "ParseException", done);
                  }
                }
              });
          showRecipe();

        } else {
          // something went wrong
        }
      }
    });
  }

  public void onSaveAction(MenuItem mi) {
    if (BeaconApplication.getApplication().recipeExists(recipe)
        && recipe.isBeingEdited()) {
      RecipeAlertDialog alert = new RecipeAlertDialog();
      Bundle args = new Bundle();
      args.putString(
          "message",
          "Recipe on that beacon already exists. Create recipe with another beacon or add more actions to existing");
      alert.setArguments(args);
      alert.show(getFragmentManager(), null);
      return;
    }
    if (recipe.getBeacon() == null || recipe.getTrigger() == null || recipe.getTriggerAction() == null) {
    	RecipeAlertDialog alert = new RecipeAlertDialog();
        Bundle args = new Bundle();
        args.putString(
            "message",
            "Recipe does not have beacon, trigger or trigger action defined. Please check and try again");
        alert.setArguments(args);
        alert.show(getFragmentManager(), null);
        return;
    }
    if (createFlag) {
      String userID = ParseUser.getCurrentUser().getObjectId();
      recipe.setUserID(userID);
      // set default values for recipe
      recipe.setStatus(true);
      recipe.setActivationDate(new Date());
      recipe.setTriggeredCount(0);
    }

    Log.d(LOG_TAG, "Saving an existing recipe:" + recipe.getObjectId());
    recipe.saveInBackground(new SaveCallback() {
      @Override
      public void done(ParseException exception) {
        if (exception == null) {
          Log.d("Recipe", "Recipe saved successfully");
          if (!createFlag)
            BeaconApplication.getApplication().deleteRecipe(oldRecipe);
          BeaconApplication.getApplication().addNewRecipe(recipe);
          if (TRIGGERS.APPROACHING.name().equalsIgnoreCase(recipe.getTrigger())) {
            beaconManager.monitorDeviceEntry(recipe.getBeacon(), recipe.getTriggerAction());
          } else if (TRIGGERS.LEAVING.name().equalsIgnoreCase(
              recipe.getTrigger())) {
            beaconManager.monitorDeviceExit(recipe.getBeacon(), recipe.getTriggerAction());
          }
          returnToMyRecipe(RecipeContracts.RECIPE_ACTION_UPDATE);

        } else {
          Log.e("Recipe", "ParseException on save", exception);
        }
      }
    });
  }

  public void onSetAction(View view) {
    Intent scanIntent = new Intent(this, RecipeActionActivity1.class);
    scanIntent.putExtra(RecipeContracts.RECIPE_ACTION,
        recipe.getTriggerAction());
    scanIntent.putExtra(RecipeContracts.TRIGGER, recipe.getTrigger());
    startActivityForResult(scanIntent, 1);
  }

  public void onDeleteAction(MenuItem mi) {
    recipe.deleteInBackground(new DeleteCallback() {
      @Override
      public void done(ParseException exception) {
        if (exception == null) {
          Log.d("Recipe", "Recipe saved successfully");
          returnToMyRecipe(RecipeContracts.RECIPE_ACTION_DELETE);
        } else {
          Log.e("Recipe", "ParseException on save", exception);
        }
      }
    });
    BeaconApplication.getApplication().deleteRecipe(recipe);
  }

  public void returnToMyRecipe(String recipeAction) {
    Intent data = new Intent();
    data.putExtra("recipe", recipe);
    data.putExtra("oldRecipe", oldRecipe);
    data.putExtra(RecipeContracts.RECIPE_ACTION, recipeAction);
    setResult(RESULT_OK, data);
    finish();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 0) {
      if (resultCode == RESULT_OK) {
        BleDeviceInfo deviceInfo = (BleDeviceInfo) data
            .getParcelableExtra("beacon");
        if (oldRecipe != null
            && !oldRecipe.getBeacon().equals(recipe.getBeacon())) {
          recipe.setEditState(true);
          ;
        }
        recipe.setBeacon(deviceInfo);
        recipe.setDisplayName(deviceInfo.getName());
        deviceInfo.setEditState(false);
        showRecipe();
      }
    } else if (requestCode == 1) {
      if (resultCode == RESULT_OK) {
        String trigger = data.getStringExtra("trigger");
        String message = data.getStringExtra("message");
        Boolean isSms = data.getBooleanExtra("isSms", false);
        Boolean isNotification = data.getBooleanExtra("isPush", true);
        String phn = null;
        if (isSms) {
          phn = data.getStringExtra("phone");
        }
        TriggerAction notification = new TriggerAction();
        if (isSms) {
          notification.setType(NOTIFICATION_TYPE.SMS.name());
        } else {
          notification.setType(NOTIFICATION_TYPE.NOTIFICATION.name());
        }
        notification.setMessage(message);
        if (phn != null) {
          notification.setExtra(phn);
        }
        recipe.setTriggerAction(notification);
        recipe.setTriggerActionDisplayName(notification.getType());
        recipe.setTrigger(trigger);
        if (oldRecipe != null
            && !oldRecipe.getTrigger().equals(recipe.getTrigger())) {
          recipe.setEditState(true);
        }
        showRecipe();
//        if (TRIGGERS.APPROACHING.name().equalsIgnoreCase(recipe.getTrigger())) {
//          beaconManager.monitorDeviceEntry(recipe.getBeacon());
//        } else if (TRIGGERS.LEAVING.name()
//            .equalsIgnoreCase(recipe.getTrigger())) {
//          beaconManager.monitorDeviceExit(recipe.getBeacon());
//        }
      }
    } else {
      Log.e("RecipeDetailActivity", "Invalid request code:" + requestCode);
    }
  }

  @Override
  public void onStateChanged(State newState) {
  }

  @Override
  public void onNewDeviceDiscovered(BleDeviceInfo[] devices) {
  }

  @Override
  public void onDeviceLost(BleDeviceInfo[] device) {
//    Toast.makeText(BeaconApplication.getApplication(),
//        "Lost a device..." + device[0], Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onDeviceFound(BleDeviceInfo[] device) {
//    Toast.makeText(BeaconApplication.getApplication(),
//        "Found a device..." + device[0], Toast.LENGTH_SHORT).show();
  }
}
