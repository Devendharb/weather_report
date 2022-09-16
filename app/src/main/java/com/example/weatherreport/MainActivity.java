package com.example.weatherreport;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout home;
    private ProgressBar loading;
    private TextView citynametv,temperature,condition;
    private RecyclerView weather;
    private TextInputEditText cityedit;
    private ImageView background,icon,search;

    private ArrayList<Recyclerviewmodel> RecyclermodalArrayList;
    private RecyclerviewAdaptor RecyclerviewAdaptor;
    private LocationManager locationManager;
    private int PERMISSION_CODE=1;
    private String cityname;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        home=findViewById(R.id.home);
        loading=findViewById(R.id.loading);
        citynametv=findViewById(R.id.cityname);
        temperature=findViewById(R.id.temperature);
        condition=findViewById(R.id.condition);
        weather=findViewById(R.id.recyclerview);
        cityedit=findViewById(R.id.inputedittext);
        background=findViewById(R.id.backgroundimage);
        icon=findViewById(R.id.image);
        search=findViewById(R.id.searchicon);
        RecyclermodalArrayList=new ArrayList<>();
        RecyclerviewAdaptor=new RecyclerviewAdaptor(this,RecyclermodalArrayList);  //initlising adaptor
        weather.setAdapter(RecyclerviewAdaptor);    //setting this into recyclervew (recyclerview is weather)

        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
             ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
        }

        Location location =locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityname = getcity(location.getLongitude(),location.getLatitude());
        getweatherinfo(cityname);


         search.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                  String city = cityedit.getText().toString();
                 if(city.isEmpty()){
                     Toast.makeText(MainActivity.this,"please enter city name",Toast.LENGTH_SHORT).show();
                 }else{
                     citynametv.setText(cityname);
                     getweatherinfo(city);
                 }

             }
         });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission granted ",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Please provide permissions ",Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    private String getcity(double longitude, double latitude) {
        String cityname= "Not found";
        Geocoder gcd= new Geocoder(getBaseContext(), Locale.getDefault());
        try{
            List<Address> addresses = gcd.getFromLocation(latitude,longitude,10);
            for(Address adr: addresses){
                if(adr!=null){
                    String city =adr.getLocality();
                    if(city!=null && !city.equals(" ")){
                         cityname= city;
                     }
                    else{
                        Log.d("TAG", " CITY NOT FOUND");
                        Toast.makeText(this,"user city not found..",Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }
        return cityname;

    }

    private void getweatherinfo(String cityname){
         String url="http://api.weatherapi.com/v1/forecast.json?key=08938caf139943a8988101834221309&q="+ cityname + "&days=1&aqi=no&alerts=no";
         citynametv.setText(cityname);
         RequestQueue requestqueue= Volley.newRequestQueue(MainActivity.this);
         JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loading.setVisibility(View.GONE);
                home.setVisibility(View.VISIBLE);
                RecyclermodalArrayList.clear();

                try {
                    String temperature1 =response.getJSONObject("current").getString("temp_c");   // declare a varable for temperature and getresponce from the json object(which is api)
                     temperature.setText(temperature1+"°C");  //now set the temperature textview from the temperature1

                    int isday= response.getJSONObject("current").getInt("is day");      //  declaring varable for the day(in int because in json the day is in type int)(current is a key from json)
                    String condition1 = response.getJSONObject("current").getJSONObject("condition").getString("text");  //refer to json object (current-condition-text)
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon"); //refer to json object (current-condition-icon)
                    Picasso.get().load("http".concat(conditionIcon)).into(icon);  // now we have to change the url because it now having the http in json so concat that and load it into the imageview of the icon.
                    condition.setText(condition1);
                    if(isday==1){
                        //morning
                        Picasso.get().load("https://unsplash.com/s/photos/early-morning").into(background);   // we will check if the is day is 1 or not and set the background as day and night.

                        //night
                    }else{
                        Picasso.get().load("https://images.unsplash.com/photo-1568233823082-873ab0156ad7?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1887&q=80").into(background);
                    }
                    JSONObject forecastobj =response.getJSONObject("forecast");  // this is from the json file for the forecast
                    JSONObject forecasto =forecastobj.getJSONArray("forecastday").getJSONObject(0); //refer the json file in the forecaste there is a array of forecastday. for calling the first we will pass index 0.
                    JSONArray hourarray = forecasto.getJSONArray("hour");
                    for(int i=0;i<hourarray.length();i++){
                        JSONObject hourobj =hourarray.getJSONObject(i);
                        String time =hourobj.getString("time");
                        String temp =hourobj.getString("temp_c ");
                        String img =hourobj.getJSONObject("condition").getString("icon");
                        String wind =hourobj.getString("wind_kph");
                        RecyclermodalArrayList.add(new Recyclerviewmodel(time,temp,img,wind));  // add all the data to our array list.
                    }

                    RecyclerviewAdaptor.notifyDataSetChanged();   // to notofy the adaptor that the data has been changed
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"please enter valid city name ",Toast.LENGTH_SHORT).show();
            }
        });

          requestqueue.add(jsonObjectRequest);

    }
}