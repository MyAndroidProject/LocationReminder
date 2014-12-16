package com.aml.locationreminder.extras;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class AddressAndLocations {

	String uri;
	String returnValue = null;
	
	public String getLatLongFromGivenAddress(String youraddress) {
		uri = "http://maps.google.com/maps/api/geocode/json?address="
				+ youraddress + "&sensor=false";
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpGet httpGet;
				HttpClient client;
				HttpResponse response;
				StringBuilder stringBuilder = new StringBuilder();
				try {
					httpGet = new HttpGet(uri.replaceAll(" ", "%20"));
					client = new DefaultHttpClient();
					response = client.execute(httpGet);
					HttpEntity entity = response.getEntity();
					InputStream stream = entity.getContent();
					int b;
					while ((b = stream.read()) != -1) {
						stringBuilder.append((char) b);
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					// TODO: handle exception
				}

				JSONObject jsonObject;

				try {
					jsonObject = new JSONObject(stringBuilder.toString());

					JSONObject jsonTemp = jsonObject.optJSONArray("results")
							.optJSONObject(0).optJSONObject("geometry")
							.getJSONObject("location");

					double latitude = jsonTemp.getDouble("lat");
					double longitude = jsonTemp.getDouble("lng");
					String fullAddress = jsonObject.optJSONArray("results").optJSONObject(0).optString("formatted_address");
					returnValue = latitude+"-"+longitude+"-"+fullAddress;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		
		try {
			t.start();
			if (t.isAlive()) {
				t.join();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return returnValue;
	}

}
