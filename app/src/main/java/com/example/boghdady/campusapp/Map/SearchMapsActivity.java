package com.example.boghdady.campusapp.Map;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.boghdady.campusapp.R;
import com.example.boghdady.campusapp.Retrofit.Interfaces;
import com.example.boghdady.campusapp.Retrofit.Models;
import com.example.boghdady.campusapp.Retrofit.Responses;
import com.example.boghdady.campusapp.helper.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    public Polyline SCU_Boundary;
    //  SCU latitude and longitude coordinates
    LatLng SCU_Location = new LatLng(30.623109, 32.2729409);

    final int MY_LOCATION_REQUEST_CODE = 25;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker marker;
    private LatLng searchedPlace;
    ProgressDialog pDialog;
    ArrayList<LatLng> MarkerPoints;
    private Map<String, LatLng> places;
    private AutoCompleteTextView textView;
    private LatLng originPlace;
    private String destinationName;


    private static boolean foundLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        textView = (AutoCompleteTextView) findViewById(R.id.autocomplete_country);
        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView t = (TextView) view;
                if (originPlace != null) {

                    destinationName = t.getText().toString();
                    searchedPlace = getIdFromPlaces(t.getText());
                    drawRoute(originPlace, searchedPlace, destinationName);
                }
                //Toast.makeText(SearchMapsActivity.this, getIdFromPlaces(t.getText()) + "", Toast.LENGTH_SHORT).show();
            }
        });
        GetLocationPlaceDetailsRetrofit();

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    //    here to request the missing permissions, and then overriding
                    //    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
            }
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        //add style to the map using json
//        boolean success = mMap.setMapStyle(new MapStyleOptions(getResources()
//                .getString(R.string.style_json)));
//        if (!success) {
//
//            Log.e(TAG, "Style parsing failed.");
//
//        }
        //make Zoom (In/Out) Buttons appear
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //  SCU latitude and longitude coordinates
        //  LatLng SCU_Location = new LatLng(30.623109, 32.2729409);

        moveCameraPosition(SCU_Location);
        //Put SCU Boundary on Google map
        drawSCU_Boundary();
        // put func at dynamic zoom level detected
        handleZoom_GroundOverlay_Boundary();
// handle Zoom level between roundOverlay and Boundary when << Camera change >>
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition cameraPosition) {


                handleZoom_GroundOverlay_Boundary();

            }
        });


        //After Click on GroundOverlay
        // Zoom in SCU and remove GroundOverlay
        mMap.setOnGroundOverlayClickListener(new GoogleMap.OnGroundOverlayClickListener() {
            @Override
            public void onGroundOverlayClick(GroundOverlay groundOverlay) {
                moveCameraIn();
                /*need twice click to remove GroundOverlay
                  groundOverlay.remove(); */
                //cleared the complete set of overlays using
                mMap.clear();


                drawSCU_Boundary();
            }

        });
/*
//------old at onMaoReady

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_LOCATION_REQUEST_CODE);
            // Show rationale and request permission.
        }

    }
}*/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //mMap.setMyLocationEnabled(true);

    }


//-----------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;


        //remove previous current location Marker
        if (marker != null) {
            marker.remove();
        }

        double dLatitude = mLastLocation.getLatitude();
        double dLongitude = mLastLocation.getLongitude();
        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(dLatitude, dLongitude))
                .title("My Location").icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        originPlace = new LatLng(dLatitude, dLongitude);
        if (searchedPlace != null) {


            drawRoute(originPlace, searchedPlace, destinationName);
        }
        if (!foundLocation) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dLatitude, dLongitude), 14));
            foundLocation = true;
        }


    }



    private void drawRoute(LatLng origin, LatLng dest , String destName) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(origin).title("My Location"));
        mMap.addMarker(new MarkerOptions().position(dest).title(destName));
        // Getting URL to the Google Directions API
        String url = getUrl(origin, dest);
        Log.d("onMapClick", url.toString());
        FetchUrl FetchUrl = new FetchUrl();

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
    }



//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {

            buildGoogleApiClient();
            mGoogleApiClient.connect();

        }
    }

    protected synchronized void buildGoogleApiClient() {
        Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //mLocationRequest.setSmallestDisplacement(0.1F);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }



//------------------------------------------------------------------------------------------------------------------------------

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }




        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


//--------------------------------------------------------------------------------------------------------------------------------------------------

    void GetLocationPlaceDetailsRetrofit(){

                    pDialog = new ProgressDialog(SearchMapsActivity.this);
                    pDialog.setMessage(getString(R.string.please_wait));
                    pDialog.setCancelable(false);
                    pDialog.show();

                    Gson gson = new GsonBuilder()
                            .setLenient()
                            .create();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(Constants.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();

                    Interfaces.GetPlacesLocation getPlaceLocation=retrofit.create(Interfaces.GetPlacesLocation.class);
                    Call<Responses.LocationPlacesResponse> call=getPlaceLocation.getPlacesLoaction();
                    call.enqueue(new Callback<Responses.LocationPlacesResponse>() {
                        @Override
                        public void onResponse(Call<Responses.LocationPlacesResponse> call, Response<Responses.LocationPlacesResponse> response) {

                            try {
                                if (response.body().getSuccess()==1){


                                    List<Models.LocationModel> placeLocation = response.body().getLocation();

                                    places = new HashMap<String, LatLng>();
                                    String[] autoCompleteList = new String[placeLocation.size()];

                                    for (int i = 0; i < placeLocation.size(); i++){
                                        places.put(placeLocation.get(i).getFaculty_Name(), new LatLng(placeLocation.get(i).getLatitude(), placeLocation.get(i).getLongitude()));
                                        autoCompleteList[i] = placeLocation.get(i).getFaculty_Name();
                                    }
                                    initAutoComplete(autoCompleteList);


                                }
                                if (response.body().getSuccess()==0){
                                    Toast.makeText(SearchMapsActivity.this,R.string.wrong , Toast.LENGTH_LONG).show();
                                    pDialog.dismiss();
                                }
                                pDialog.dismiss();
                            }catch (Exception e){
                                e.getMessage();
                                pDialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<Responses.LocationPlacesResponse> call, Throwable t) {
                            try {
                                Toast.makeText(SearchMapsActivity.this, R.string.check_your_internet, Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            }catch (Exception e){
                                pDialog.dismiss();
                            }
                        }
                    });
    }

//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    void initAutoComplete(String[] places){

    //    String[] countries = getResources().getStringArray(R.array.countries_array);

        ArrayAdapter adapter =
                new ArrayAdapter(this, android.R.layout.simple_list_item_1, places);

        textView.setAdapter(adapter);
    }


    // method to get Lat and lon from using faculty name
    private LatLng getIdFromPlaces(CharSequence text) {

        return places.get(text.toString());
    }
    //------------- last ------------
       /* Draw Boundary of SCU
          get latitude and longitude coordinates of pointes
          https://developers.google.com/maps/documentation/utilities/polylineutility*/
    public void drawSCU_Boundary() {
        SCU_Boundary = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(30.617601379370974, 32.25452899932861), new LatLng(30.615727045601805, 32.25506544113159))
                .add(new LatLng(30.62100832664296, 32.28353977203369), new LatLng(30.615727045601805, 32.25506544113159))
                .add(new LatLng(30.62100832664296, 32.28353977203369), new LatLng(30.623944298832274, 32.28301137685776))
                .add(new LatLng(30.623944298832274, 32.28301137685776), new LatLng(30.627055593547315, 32.28206992149353))
                .add(new LatLng(30.627055593547315, 32.28206992149353), new LatLng(30.62797880715714, 32.280235290527344))
                .add(new LatLng(30.62797880715714, 32.280235290527344), new LatLng(30.62765568, 32.27997779))
                .add(new LatLng(30.62765568, 32.27997779), new LatLng(30.62802, 32.27558))
                .add(new LatLng(30.62802, 32.27558), new LatLng(30.62801, 32.27007))
                .add(new LatLng(30.62801, 32.27007), new LatLng(30.62832, 32.26871))
                .add(new LatLng(30.62832, 32.26871), new LatLng(30.62775, 32.26836))
                .add(new LatLng(30.62775, 32.26836), new LatLng(30.62778, 32.26513))
                .add(new LatLng(30.62778, 32.26513), new LatLng(30.62457, 32.26561))
                .add(new LatLng(30.62457, 32.26561), new LatLng(30.62108, 32.26666))
                .add(new LatLng(30.62108, 32.26666), new LatLng(30.61874, 32.25556))
                .add(new LatLng(30.61874, 32.25556), new LatLng(30.617601379370974, 32.25452899932861))
                .visible(true)
                .width(5)
                .color(Color.RED));
        draw_polygon();
    }
    // .add(new LatLng(,),new LatLng(,))

    //        //hide Boundary of SCU
    public void hideSCU_Boundary() {
        SCU_Boundary.setVisible(false);
    }


    /* Start app Camera ..
    then move the camera to the place and Zoom =15f
     bearing : Direction that the camera is pointing in, in degrees clockwise from north.
     tilt : The angle, in degrees, of the camera angle from the nadir (directly facing the Earth).".tilt(65.5f)"*/
    public void moveCameraPosition(LatLng location) {

        CameraPosition currentPlace = new CameraPosition.Builder()
                .target(location)
                .bearing(270f).zoom(14f).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(currentPlace));
    }

    // move camera to focus on SCU more zoom in
    public void moveCameraIn() {

        CameraPosition currentPlace = new CameraPosition.Builder()
                .target(SCU_Location)
                .bearing(270f).zoom(15f).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(currentPlace));
    }


    // Add an overlay to the map, retaining a handle to the GroundOverlay object.
    public void add_or_remove_GroundOverlay(LatLng location, Boolean condition) {
        GroundOverlayOptions newarkMap = new GroundOverlayOptions()
                .clickable(true)
                .transparency(.7f)


                .image(BitmapDescriptorFactory.fromResource(R.drawable.logo))
                //position(LatLng location, float width, float height)
                .position(location, 2500f, 2500f);
        GroundOverlay imageOverlay = mMap.addGroundOverlay(newarkMap);
        if (condition == Boolean.FALSE) {
            // remove a ground overlay
            imageOverlay.remove();

        } else {
            mMap.addGroundOverlay(newarkMap);
        }


    }

    public void handleZoom_GroundOverlay_Boundary() {


        float zoom = mMap.getCameraPosition().zoom;
        if (zoom < 15f) {
            add_or_remove_GroundOverlay(SCU_Location, Boolean.TRUE);

            if (
                    SCU_Boundary.isVisible()) {
                hideSCU_Boundary();
            }

        } else {
            add_or_remove_GroundOverlay(SCU_Location, Boolean.FALSE);
            mMap.clear();
            drawSCU_Boundary();

        }
    }

    // polygon and marker on fci
    public void draw_polygon() {
        Polygon polygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(30.62141, 32.26829), new LatLng(30.62104, 32.26882), new LatLng(30.62087, 32.26883), new LatLng(30.62098, 32.26832), new LatLng(30.62137, 32.26808))
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE)
                .visible(true));
        //
        LatLng fci_p = new LatLng(30.62108, 32.26843);
        GroundOverlayOptions newarkMap2 = new GroundOverlayOptions()




                .image(BitmapDescriptorFactory.fromResource(R.drawable.logo))
                //position(LatLng location, float width, float height)
                .position(fci_p, 25f, 25f);
        GroundOverlay imageOverlay2 = mMap.addGroundOverlay(newarkMap2);
    }
}
