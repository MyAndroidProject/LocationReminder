package com.teamNIKaml.locationReminder.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;
import com.teamNIKaml.locationReminder.autocomplete.PlaceJSONParser;
import com.teamNIKaml.locationReminder.databasecomponent.LocationReminderDatabaseAccessUtility;
import com.teamNIKaml.locationReminder.databasecomponent.LocationReminderDatabaseHandler;
import com.teamNIKaml.locationReminder.location.AddressAndLocations;
import com.teamNIKaml.locationReminder.service.LocationReminderService;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class AddLocationActivity extends Activity {

	private Button okButton, cancelButton;
	private AutoCompleteTextView atvPlaces;
	private PlacesTask placesTask;
	private ParserTask parserTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_location);

		okButton = (Button) findViewById(R.id.ok_button);
		cancelButton = (Button) findViewById(R.id.cancel_button);
		atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);

		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (atvPlaces.getText().toString().length() > 0) {
					String placeAddress = atvPlaces.getText().toString().trim();
					@SuppressWarnings("unused")
					String[] placeAddr = placeAddress.split(",");
					String latAndLong = new AddressAndLocations()
							.getLatLongFromGivenAddress(placeAddress);
					if (latAndLong != null) {
						String[] lat_long = latAndLong.split("-");
						double lat = Double.valueOf(lat_long[0].trim());
						double lon = Double.valueOf(lat_long[1].trim());
						String fullAddress = String.valueOf(lat_long[2].trim());
						addLocation(fullAddress, lat, lon);
						Toast.makeText(getApplicationContext(),
								"Location Is Added", Toast.LENGTH_SHORT).show();
					} else {
						addLocation(placeAddress, 0, 0);
						Toast.makeText(getApplicationContext(),
								"Sorry Location Not Found", Toast.LENGTH_SHORT)
								.show();
					}
					finish();
				} else {
					Toast.makeText(getApplicationContext(), "Enter input",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		atvPlaces.setThreshold(2);
		atvPlaces.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				placesTask = new PlacesTask();
				placesTask.execute(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_location, menu);
		return true;
	}

	private void makeChangeInService() {

		if (LocationReminderService.SERVICE_COUNT >= 1) {
			startService(new Intent(getApplicationContext(),
					LocationReminderService.class));
		}
	}

	private void addLocation(String place, double latitude, double longitude) {

		ContentValues cv = new ContentValues();
		cv.put(LocationReminderDatabaseHandler.UserTable.id, place);
		cv.put(LocationReminderDatabaseHandler.UserTable.latitude, latitude);
		cv.put(LocationReminderDatabaseHandler.UserTable.longitude, longitude);
		getContentResolver().insert(
				LocationReminderDatabaseAccessUtility.CONTENT_URI, cv);
		makeChangeInService();

	}

	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			urlConnection = (HttpURLConnection) url.openConnection();

			urlConnection.connect();

			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	private class PlacesTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... place) {
			String data = "";

			String key = "key=AIzaSyArzuM6Bw81D4XYMr18fiuqGEz9QnvTSiY";

			String input = "";

			try {
				input = "input=" + URLEncoder.encode(place[0], "utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			String types = "types=geocode";

			String sensor = "sensor=false";

			String parameters = input + "&" + types + "&" + sensor + "&" + key;

			String output = "json";

			String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
					+ output + "?" + parameters;

			try {
				data = downloadUrl(url);
			} catch (Exception e) {
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			parserTask = new ParserTask();

			parserTask.execute(result);
		}
	}

	private class ParserTask extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {

		JSONObject jObject;

		@Override
		protected List<HashMap<String, String>> doInBackground(
				String... jsonData) {

			List<HashMap<String, String>> places = null;

			PlaceJSONParser placeJsonParser = new PlaceJSONParser();

			try {
				jObject = new JSONObject(jsonData[0]);

				places = placeJsonParser.parse(jObject);

			} catch (Exception e) {
			}
			return places;
		}

		@Override
		protected void onPostExecute(List<HashMap<String, String>> result) {

			String[] from = new String[] { "description" };
			int[] uiBindTo = { R.id.textViewItem };

			SimpleAdapter adapter1 = new SimpleAdapter(getApplicationContext(),
				result, R.layout.list_view_row_item, from, uiBindTo);
			atvPlaces.setAdapter(adapter1);
		}
	}

}
