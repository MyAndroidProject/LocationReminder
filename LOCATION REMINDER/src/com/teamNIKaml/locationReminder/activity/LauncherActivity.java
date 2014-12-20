package com.teamNIKaml.locationReminder.activity;

import com.teamNIKaml.locationReminder.activity.AddLocationActivity;
import com.teamNIKaml.locationReminder.activity.DeletePlaceActivity;
import com.teamNIKaml.locationReminder.activity.LauncherActivity;
import com.teamNIKaml.locationReminder.databasecomponent.LocationReminderDatabaseAccessUtility;
import com.teamNIKaml.locationReminder.service.LocationReminderService;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;


public class LauncherActivity extends Activity {
	
	private Button serviceButton, addButton, removeButton;
	private Intent serviceIntent;
	private RelativeLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        mainLayout = (RelativeLayout) findViewById(R.id.activity_launcher);
		mainLayout.setBackgroundResource(R.drawable.locationreminderbackground);
		serviceButton = (Button) findViewById(R.id.service_button);
		addButton = (Button) findViewById(R.id.add_button);
		removeButton = (Button) findViewById(R.id.remove_button);

		serviceIntent = new Intent(getApplicationContext(),
				LocationReminderService.class);

		serviceButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (LocationReminderService.SERVICE_COUNT >= 1) {
					stopService(serviceIntent);
					serviceButton.setText("Start Service");
				} else {
					startService(serviceIntent);
					if (getContentResolver() != null) {
						Cursor c = getContentResolver()
								.query(LocationReminderDatabaseAccessUtility.CONTENT_URI,
										null, null, null, null);
						if (c.getCount()>0) {
							serviceButton.setText("Stop Service");
						}
					}
				}
			}
		});

		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getApplicationContext(),
						AddLocationActivity.class));

			}
		});


		removeButton.setOnClickListener(new OnClickListener() {

			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			public void onClick(View v) {

				PopupMenu popup = new PopupMenu(LauncherActivity.this, removeButton);

				popup.getMenuInflater().inflate(R.menu.remove_popup_menu,
						popup.getMenu());

				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {

						if (item.getItemId() == R.id.remove_all) {
							getContentResolver()
									.delete(LocationReminderDatabaseAccessUtility.CONTENT_URI,
											null, null);
							makeChangeInService();
							serviceButton.setText("Start Service");
						} else {
							startActivity(new Intent(getApplicationContext(),
									DeletePlaceActivity.class));
						}
						return true;
					}
				});

				popup.show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.launcher, menu);
		return true;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setServiceButtonTittle();
	}

	private void setServiceButtonTittle() {
		// TODO Auto-generated method stub
		if (LocationReminderService.SERVICE_COUNT >= 1) {
			serviceButton.setText("Stop Service");
		} else {
			serviceButton.setText("Start Service");
		}
	}


	private void makeChangeInService() {

		if (LocationReminderService.SERVICE_COUNT >= 1) {
			startService(new Intent(getApplicationContext(),
					LocationReminderService.class));
		}
	}

}