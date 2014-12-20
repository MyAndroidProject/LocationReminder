package com.teamNIKaml.locationReminder.activity;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


import com.teamNIKaml.locationReminder.databasecomponent.LocationReminderDatabaseAccessUtility;
import com.teamNIKaml.locationReminder.databasecomponent.LocationReminderDatabaseHandler;
import com.teamNIKaml.locationReminder.service.LocationReminderService;

public class LocationUpdateActivity extends Activity {

	TextView placeTextView;
	Button okButton,cancelButton;
	String placeToDelete = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_location_update);
		placeTextView = (TextView) findViewById(R.id.placeNmae_textView);
		okButton = (Button) findViewById(R.id.updateOk_button);
		cancelButton = (Button) findViewById(R.id.updateCancel_button);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			placeToDelete = extras.getString("placeToDelete");
			placeTextView.setText(placeToDelete);
		}

		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (placeToDelete != null) {
					String[] deletearg = { placeToDelete };
					getContentResolver()
							.delete(LocationReminderDatabaseAccessUtility.CONTENT_URI,
									LocationReminderDatabaseHandler.UserTable.id,
									deletearg);
				}
				makeChangeInService();
				finish();
			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(1000);
	}
	
	private void makeChangeInService(){
		
		if (LocationReminderService.SERVICE_COUNT >= 1) {
			startService(new Intent(getApplicationContext(),
					LocationReminderService.class));
		}
	}



}