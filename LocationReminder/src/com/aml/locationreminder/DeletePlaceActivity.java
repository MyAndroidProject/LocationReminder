package com.aml.locationreminder;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.aml.locationreminder.databasecomponent.LocationReminderDatabaseAccessUtility;
import com.aml.locationreminder.databasecomponent.LocationReminderDatabaseHandler;
import com.aml.locationreminder.servicecomponent.LocationReminderService;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DeletePlaceActivity extends ListActivity implements
		LoaderCallbacks<Cursor> {

	SimpleCursorAdapter mAdapter;
	LoaderManager loadermanager;
	CursorLoader cursorLoader;
	@SuppressWarnings("rawtypes")
	LoaderCallbacks callback;
	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadermanager = getLoaderManager();
		String[] uiBindFrom = { LocationReminderDatabaseHandler.UserTable.id };

		int[] uiBindTo = { R.id.textViewItem };
		mAdapter = new SimpleCursorAdapter(this, R.layout.list_view_row_item,
				null, uiBindFrom, uiBindTo, 0);
		setListAdapter(mAdapter);
		callback = this;
		loadermanager.initLoader(1, null, callback);

		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				TextView textViewItem = ((TextView) view
						.findViewById(R.id.textViewItem));

				String listItemText = textViewItem.getText().toString().trim();
				String[] deletearg = { listItemText };
				getContentResolver()
						.delete(LocationReminderDatabaseAccessUtility.CONTENT_URI,
								LocationReminderDatabaseHandler.UserTable.id,
								deletearg);
				loadermanager.restartLoader(1, null, callback);
				makeChangeInService();
			}
		});
	}

	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {

		String[] projection = { LocationReminderDatabaseHandler.UserTable.id,
				LocationReminderDatabaseHandler.UserTable.latitude,
				LocationReminderDatabaseHandler.UserTable.longitude };

		cursorLoader = new CursorLoader(this,
				LocationReminderDatabaseAccessUtility.CONTENT_URI, projection,
				null, null, null);
		return cursorLoader;

	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

		if (mAdapter != null && cursor != null)
			mAdapter.swapCursor(cursor);
		if (cursor.getCount() == 0) {
			Toast.makeText(getApplicationContext(), "No places to delete", Toast.LENGTH_SHORT).show();
		}
	}

	public void onLoaderReset(Loader<Cursor> arg0) {

		if (mAdapter != null)
			mAdapter.swapCursor(null);
	}
	
	private void makeChangeInService(){
		
		if (LocationReminderService.SERVICE_COUNT >= 1) {
			startService(new Intent(getApplicationContext(),
					LocationReminderService.class));
		}
	}

}
