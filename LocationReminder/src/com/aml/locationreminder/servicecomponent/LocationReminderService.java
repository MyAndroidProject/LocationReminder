package com.aml.locationreminder.servicecomponent;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.Toast;

import com.aml.locationreminder.LocationUpdateActivity;
import com.aml.locationreminder.databasecomponent.LocationReminderDatabaseAccessUtility;

@SuppressLint("HandlerLeak")
public class LocationReminderService extends Service {

	public static int SERVICE_COUNT;
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	private ArrayList<String> placesList = new ArrayList<String>();
	private LocationManager mlocManager = null;
	private LocationListener mlocListener;
	private ArrayList<Double> latitudeList = new ArrayList<Double>();
	private ArrayList<Double> longitudeList = new ArrayList<Double>();

	private final class ServiceHandler extends Handler {

		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {

			mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			mlocListener = new MyLocationListener();
			mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					1000, 10, mlocListener);

		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		HandlerThread thread = new HandlerThread("ServiceStartArguments",
				Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
		SERVICE_COUNT = 1;

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mlocManager != null) {
			mlocManager.removeUpdates(mlocListener);
		}

		SERVICE_COUNT = 0;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		if (getContentResolver() != null) {
			Cursor c = getContentResolver().query(
					LocationReminderDatabaseAccessUtility.CONTENT_URI, null,
					null, null, null);
			placesList.clear();
			if (c.moveToFirst()) {

				do {
					placesList.add(c.getString(0));
					latitudeList.add(c.getDouble(1));
					longitudeList.add(c.getDouble(2));
				} while (c.moveToNext());
			}
		}
		if (placesList.isEmpty()) {
			Toast.makeText(getApplicationContext(),
					"Location Reminder Service Stoped", Toast.LENGTH_SHORT)
					.show();
			stopSelf();
		}

		if (1 == SERVICE_COUNT) {

			Message msg = mServiceHandler.obtainMessage();
			msg.arg1 = startId;
			mServiceHandler.sendMessage(msg);
		}
		SERVICE_COUNT++;

		return Service.START_NOT_STICKY;

	}

	public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {

			double currentLatitude = loc.getLatitude();
			double currentLongitude = loc.getLongitude();
			JSONObject ret = getLocationInfo(currentLatitude,currentLongitude); 
			JSONObject location;
			String location_string = null;
			try {
			    location = ret.getJSONArray("results").getJSONObject(0);
			    location_string = location.getString("formatted_address");
			} catch (JSONException e1) {
			    e1.printStackTrace();
			} catch (Exception e) {
				// TODO: handle exception
			}
			if (location_string == null) {
				location_string ="no_place_found";
			}
			
			for (String tempName : placesList) {
				String arr[] = tempName.split(",", 2);
				String firstWord = arr[0].toLowerCase();
				if (location_string.toLowerCase().contains(firstWord)) {
					Intent new_Activity = new Intent(
							getApplicationContext(),
							LocationUpdateActivity.class);
					new_Activity.putExtra("placeToDelete", tempName);
					new_Activity
							.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(new_Activity);
				}
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(getApplicationContext(), "Gps Disabled",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(getApplicationContext(), "Gps Enabled",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}
	}
	
	public JSONObject getLocationInfo(double lat, double lng) {

        HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng="+lat+","+lng+"&sensor=true");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
			// TODO: handle exception
		}
        return jsonObject;
    }

}