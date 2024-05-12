package com.example.passengerapp.tools;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.passengerapp.adapters.PlaceAdapter;
import com.example.passengerapp.models.Place;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Goong {

    public static void searchPlace(Context context, String keyWord, String api_key, List<Place> listPlace, PlaceAdapter placeAdapter) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = Uri.parse("https://rsapi.goong.io/Place/AutoComplete")
                .buildUpon()
                .appendQueryParameter("api_key", api_key)
                .appendQueryParameter("input", keyWord)
                .toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    listPlace.clear();
                    JSONArray listPredictions = response.getJSONArray("predictions");
                    for (int i = 0; i < listPredictions.length(); i++) {
                        JSONObject prediction = listPredictions.getJSONObject(i);
                        String placeId = prediction.getString("place_id");
                        JSONObject structured_formatting = prediction.getJSONObject("structured_formatting");
                        String main_text = structured_formatting.getString("main_text");
                        String secondary_text = structured_formatting.getString("secondary_text");

                        Place place = new Place(placeId, main_text, secondary_text);
                        listPlace.add(place);
                    }
                    placeAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        RetryPolicy retryPolicy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(jsonObjectRequest);
    }

    public static void getPlaceLatLng(Context context, Place place, String api_key) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = Uri.parse("https://rsapi.goong.io/Place/Detail")
                .buildUpon()
                .appendQueryParameter("place_id", place.getPlaceId())
                .appendQueryParameter("api_key", api_key)
                .toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject result = response.getJSONObject("result");
                    String placeName = result.getString("formatted_address");
                    JSONObject geometry = result.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    String lat = location.getString("lat");
                    String lng = location.getString("lng");

                    LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                    place.setLatLng(latLng);
                    place.setPlaceName(placeName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        RetryPolicy retryPolicy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(jsonObjectRequest);
    }

}
