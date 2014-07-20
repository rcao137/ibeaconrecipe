package com.codepath.beacon.adapter;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.beacon.R;
import com.codepath.beacon.activity.MyRecipeActivity;
import com.codepath.beacon.activity.RecipeDetailActivity;
import com.codepath.beacon.contracts.RecipeContracts.TRIGGERS;
import com.codepath.beacon.models.Recipe;
import com.codepath.beacon.models.TriggerAction;

public class RecipeArrayAdapter extends ArrayAdapter<Recipe> {
	private List<Recipe> mRecipes;
	public RecipeArrayAdapter(Context context, List<Recipe> recipes) {
		super(context, R.layout.recipe_item, recipes);
		mRecipes = recipes;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Recipe recipe = (Recipe) getItem(position);
		View v;

		if (convertView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			v = inflator.inflate(R.layout.recipe_item, parent, false);
		} else {
			v = convertView;
		}

		// find the views within template
		TextView tvBeaconName = (TextView) v.findViewById(R.id.tvbeaconname);
		
		tvBeaconName.setText(recipe.getDisplayName().toUpperCase());
		
		ImageView ivAction = (ImageView) v.findViewById(R.id.ivAction);
		if (TriggerAction.NOTIFICATION_TYPE.NOTIFICATION.name().equalsIgnoreCase(recipe.getTriggerAction().getType())) {
			ivAction.setImageResource(R.drawable.notification);
		} else {
			ivAction.setImageResource(R.drawable.sms);
		}
		
		TextView tvRecipeDesc = (TextView) v.findViewById(R.id.tvRecipeDesc);
		StringBuilder desc = new StringBuilder("Send ");
		desc.append(recipe.getTriggerActionDisplayName().toLowerCase());
		desc.append(" when ");
		desc.append(recipe.getTrigger().toLowerCase());
		desc.append(" ");
		desc.append(recipe.getDisplayName());
		tvRecipeDesc.setText(desc.toString());
		// pass recipe to activity view
		v.setTag(recipe);

		v.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Date activationDate = null;
				
				Recipe recipe = (Recipe) v.getTag();
				String objID = recipe.getObjectId();
                String fn = recipe.getDisplayName();
				activationDate = recipe.getActivationDate();
				if (activationDate == null)
					activationDate = new Date();
				int triggerCount = recipe.getTriggeredCount();
				boolean status = recipe.isStatus();
				
				Intent i = new Intent(getContext(), RecipeDetailActivity.class);
				i.putExtra("recipe", recipe);
				((Activity)getContext()).startActivityForResult(i,MyRecipeActivity.EDIT_REQUEST_CODE);
			}
		});
		return v;
	}
}
