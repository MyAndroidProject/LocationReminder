package com.aml.locationreminder.autocomplete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
 
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
public class PlaceJSONParser {
 
    public List<HashMap<String,String>> parse(JSONObject jObject){
 
        JSONArray jPlaces = null;
        try {
            jPlaces = jObject.getJSONArray("predictions");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPlaces(jPlaces);
    }
 
    private List<HashMap<String, String>> getPlaces(JSONArray jPlaces){
        int placesCount = jPlaces.length();
        List<HashMap<String, String>> placesList = new ArrayList<HashMap<String,String>>();
        HashMap<String, String> place = null;
 
        for(int i=0; i<placesCount;i++){
            try {
                place = getPlace((JSONObject)jPlaces.get(i));
                placesList.add(place);
 
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
 
        return placesList;
    }
 
    private HashMap<String, String> getPlace(JSONObject jPlace){
 
        HashMap<String, String> place = new HashMap<String, String>();
 
        try {
 
            place.put("description", jPlace.getString("description"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return place;
    }
}