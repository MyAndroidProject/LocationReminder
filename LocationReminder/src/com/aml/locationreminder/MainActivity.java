package com.aml.locationreminder;

import com.aml.locationreminder.R.id;
import com.aml.locationreminder.databasecomponent.LocationReminderDatabaseAccessUtility;
import com.aml.locationreminder.servicecomponent.LocationReminderService;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

	Button serviceButton, addButton, removeButton;
	Intent serviceIntent;
	RelativeLayout mainLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mainLayout = (RelativeLayout) findViewById(id.mainActivity_Layout);
		mainLayout.setBackgroundResource(R.drawable.locationreminderbackground);
		serviceButton = (Button) findViewById(id.service_button);
		addButton = (Button) findViewById(id.add_button);
		removeButton = (Button) findViewById(id.remove_button);

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

				PopupMenu popup = new PopupMenu(MainActivity.this, removeButton);

				popup.getMenuInflater().inflate(R.menu.remove_popup_menu,
						popup.getMenu());

				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {

						if (item.getItemId() == id.remove_all) {
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
		getMenuInflater().inflate(R.menu.main, menu);
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
