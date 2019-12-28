package com.juhani.thnibat.travelog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.List;

import es.dmoral.toasty.Toasty;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static android.view.View.VISIBLE;

public class MapScreen extends FragmentActivity implements OnMapReadyCallback {

    ProgressDialog progressDialog;

    private GoogleMap mMap;


    Bitmap bitmap;
    LocationManager locationManager;
    LocationListener locationListener;


    // search for specified user public images
    public void search(View view){

        final EditText searchFilter = (EditText) findViewById(R.id.searchFilter);

        final String searchvalue = searchFilter.getText().toString();

        if (searchFilter.getText().toString().equals("")){

            Toasty.error(this, "You need to enter a username!", Toast.LENGTH_LONG,true).show();

        }else {

        mMap.clear();
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
        ParseGeoPoint geoPointLocation = new ParseGeoPoint();
        query.whereNear("location", geoPointLocation);
        query.whereEqualTo("username",searchvalue);
        query.whereNotEqualTo("visibility", "onlyme"); // which equals where visibility is public
        query.setLimit(100);
        Log.i("imageslocation", "limit set to 100 image on map");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.i("imageslocation", "no errors");
                }

                if (objects.size() > 0) {
                    for (final ParseObject object : objects) {
                        //This is the loop function to get all images from parseserver
                        final ParseGeoPoint point = object.getParseGeoPoint("location");
                        ParseFile file = (ParseFile) object.get("image");
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null && data != null) ;
                                final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);


                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                                final byte[] b = baos.toByteArray();


                                // thumbnail that will replace the marker icon
                                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(getCroppedBitmap(bitmap), 200, 200);

                                Double lat = point.getLatitude();
                                Double lng = point.getLongitude();

                                LatLng marker = new LatLng(lat, lng);
                                Log.i("imageslocation", "Latitude :" + lat + " Longitude: " + lng);


                                Marker newMarker = mMap.addMarker(new MarkerOptions()
                                        .title(String.valueOf(object.get("username")))
                                        .icon(BitmapDescriptorFactory.fromBitmap(thumbnail))
                                        .snippet(object.getObjectId())
                                        .position(marker));
                                newMarker.setTag(b);

                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        Intent intent = new Intent(MapScreen.this, FullimageScreen.class);
                                        intent.putExtra("picture", (byte[]) marker.getTag());
                                        intent.putExtra("username", marker.getTitle());
                                        intent.putExtra("objectid", marker.getSnippet());
                                        startActivity(intent);

                                        return true;



                                    }


                                });



                            }


                        });


                    }


                }



            }
        });

            Toasty.info(MapScreen.this, searchvalue +" images", Toast.LENGTH_LONG,true).show();


            // hides searchbar and keyboard
            RelativeLayout searchbarlayout = (RelativeLayout) findViewById(R.id.search_bar);
            searchbarlayout.setVisibility(View.GONE);
            searchFilter.setText("");

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchFilter.getWindowToken(), 0);

        }



    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    {

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 11, 11, locationListener);

                        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (lastKnownLocation != null) {

                            updateMap(lastKnownLocation);

                        }
                    }

                }

            }

        }

    }

    /*
    // if logout is successful go to welcome screen

    public void logOutSuccessful() {

        Intent intent = new Intent(MapScreen.this, WelcomeScreen.class);
        startActivity(intent);

    }

    // parse logout function

    public void logOut(View view) {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {
                    logOutSuccessful();
                } else {
                    Log.i("Logout", "logout failed");
                }


            }
        });

    }
    */

    // function to add and center map on user location
    public void updateMap(Location location) {

        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        Marker mMarker;
        mMarker = mMap.addMarker(new MarkerOptions().position(userLocation).title("you!").icon(BitmapDescriptorFactory.fromResource(R.mipmap.userlocation)));
        CameraUpdate userloc = CameraUpdateFactory.newLatLngZoom(userLocation, 15);
        mMap.animateCamera(userloc);
        getPublicImages();



    }


    // starts the camera screen by the camera button
    public void camera(View view) {


        Intent intent = new Intent(this, CameraScreen.class);
        startActivityForResult(intent, 50);


    }

    // start profile screen
    public void profile(View view){

        Intent intent = new Intent(this, ProfileScreen.class);
        startActivityForResult(intent, 324);



    }

    // start chat activity
    public void chat(View view) {


        Intent intent = new Intent(this, FriendsScreen.class);
        startActivityForResult(intent, 322);


    }

    // starts nearbyplaces Intent
    public void placesNearby(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(MapScreen.this), 999);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    // shows the searchbar for user images
    public void showSearchBar(View view){

        RelativeLayout searchbarlayout = (RelativeLayout) findViewById(R.id.search_bar);
        searchbarlayout.setVisibility(VISIBLE);

    }

    // show all app users activity
    //public void viewuserslist(View view) {


    //    Intent intent = new Intent(this, UsersScreen.class);
    //    startActivityForResult(intent, 323);


    //}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            // refresh the map after image capture
            case (50): {
                if (requestCode == 50) {
                    Intent refresh = new Intent(this, MapScreen.class);
                    startActivity(refresh);
                    //this.finish();
                }
            }
            break;

            // in case of selecting a nearby place
            case (999): {
                if (requestCode == 999) {

                    if (resultCode == RESULT_OK) {
                        Place place = PlacePicker.getPlace(this, data);
                        String toastMsg = String.format(String.valueOf(place.getName()));
                        Toasty.info(this, toastMsg, Toast.LENGTH_LONG,false).show();
                         Marker placemarker = mMap.addMarker(new MarkerOptions()
                                .title((String) place.getName())
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.placeslocation))
                                .snippet("Rating: " + String.valueOf(place.getRating()) + " Phone: " + place.getPhoneNumber())
                                .position(place.getLatLng()));


                        //move the camera to the picked location
                        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15);
                        mMap.animateCamera(location);
                        placemarker.showInfoWindow();



                    }
                }

            }
            break;

        }
    }


    // crop thumbnails into circle shape
    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    // get the taken public images with it location from parse server and mark them on the map when its ready
    public void getPublicImages() {

        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading images...");
        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        //progressDialog.dismiss();


        /*new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                progressDialog.dismiss();
            }
        }.start();*/



        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
        ParseGeoPoint geoPointLocation = new ParseGeoPoint();
        query.whereNear("location", geoPointLocation);
        query.whereNotEqualTo("visibility", "onlyme"); // which equals where visibility is public
        query.setLimit(100);
        Log.i("imageslocation", "limit set to 100 image on map");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.i("imageslocation", "no errors");
                }

                if (objects.size() > 0) {
                    for (final ParseObject object : objects) {
                        //This is the loop function to get all images from parseserver
                        final ParseGeoPoint point = object.getParseGeoPoint("location");
                        ParseFile file = (ParseFile) object.get("image");
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null && data != null) ;
                                final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);


                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                                final byte[] b = baos.toByteArray();


                                // thumbnail that will replace the marker icon
                                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(getCroppedBitmap(bitmap), 200, 200);

                                Double lat = point.getLatitude();
                                Double lng = point.getLongitude();

                                LatLng marker = new LatLng(lat, lng);
                                Log.i("imageslocation", "Latitude :" + lat + " Longitude: " + lng);


                                Marker newMarker = mMap.addMarker(new MarkerOptions()
                                        .title(String.valueOf(object.get("username")))
                                        .icon(BitmapDescriptorFactory.fromBitmap(thumbnail))
                                        .snippet(object.getObjectId())
                                        .position(marker));
                                         newMarker.setTag(b);

                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        Intent intent = new Intent(MapScreen.this, FullimageScreen.class);
                                        intent.putExtra("picture", (byte[]) marker.getTag());
                                        intent.putExtra("username", marker.getTitle());
                                        intent.putExtra("objectid", marker.getSnippet());
                                        startActivity(intent);

                                        return true;



                                    }


                                });


/*
                                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                                    // Use default InfoWindow frame
                                    @Override
                                    public View getInfoWindow(Marker arg0) {
                                        return null;
                                    }

                                    // Defines the contents of the InfoWindow
                                    @Override
                                    public View getInfoContents(Marker arg0) {
                                        View v = null;
                                        try {

                                            // Getting view from the layout file info_window_layout
                                            v = getLayoutInflater().inflate(R.layout.custom_infowindow, null);

                                            // Getting reference to the TextView to set latitude
                                            TextView addressTxt = (TextView) v.findViewById(R.id.addressTxt);
                                            addressTxt.setText(arg0.getTitle());
                                            ImageView image = (ImageView) v.findViewById(R.id.markerimageview);
                                            image.setImageBitmap(bitmap);

                                        } catch (Exception ev) {
                                            System.out.print(ev.getMessage());
                                        }

                                        return v;
                                    }
                                });
*/
                            }


                        });


                    }


                }


            }
        });


                        progressDialog.dismiss();


                    }
                }, 200);



    }


    // user private images method
    public void getPrivateImages() {

        mMap.clear();
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
        ParseGeoPoint geoPointLocation = new ParseGeoPoint();
        query.whereNear("location", geoPointLocation);
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("visibility", "onlyme") ;
        query.setLimit(100);
        Log.i("imageslocation", "limit set to 100 image on map");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.i("imageslocation", "no errors");
                }

                if (objects.size() > 0) {
                    for (final ParseObject object : objects) {
                        //This is the loop function to get all images from parseserver
                        final ParseGeoPoint point = object.getParseGeoPoint("location");
                        ParseFile file = (ParseFile) object.get("image");
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null && data != null) ;
                                final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);


                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                                final byte[] b = baos.toByteArray();


                                // thumbnail that will replace the marker icon
                                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(getCroppedBitmap(bitmap), 200, 200);

                                Double lat = point.getLatitude();
                                Double lng = point.getLongitude();

                                LatLng marker = new LatLng(lat, lng);
                                Log.i("imageslocation", "Latitude :" + lat + " Longitude: " + lng);

                                Marker newMarker = mMap.addMarker(new MarkerOptions()
                                        .title(String.valueOf(object.get("username")))
                                        .icon(BitmapDescriptorFactory.fromBitmap(thumbnail))
                                        .snippet(object.getObjectId())
                                        .position(marker));
                                        newMarker.setTag(b);

                                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(Marker marker) {

                                            Intent intent = new Intent(MapScreen.this, FullimageScreen.class);
                                            intent.putExtra("picture", (byte[]) marker.getTag());
                                            intent.putExtra("username", marker.getTitle());
                                            intent.putExtra("objectid", marker.getSnippet());
                                            startActivity(intent);

                                            return true;


                                        }


                                    });

                            }


                        });

                    }


                }


            }
        });


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        

        // quick user guide
        ImageButton profileButton = (ImageButton) findViewById(R.id.profile);
        ImageButton cameraButton = (ImageButton) findViewById(R.id.camButton);
        ImageButton chatButton = (ImageButton) findViewById(R.id.chatList);
        ImageButton searchButton = (ImageButton) findViewById(R.id.search);
        Switch switchButton = (Switch) findViewById(R.id.mapSwitch);
        ImageButton nearbyButton = (ImageButton) findViewById(R.id.nearbyplaces);

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view
        config.setMaskColor(getResources().getColor(R.color.colorAccent));

        // add ,SHOWCASEID after this FOR SINGLE TIME USE
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this,"onetime");

        sequence.setConfig(config);

        sequence.addSequenceItem(profileButton,
                "Hello thank you for using travelog here is a quick starter guide, This button is your profile page.", "GOT IT");

        sequence.addSequenceItem(chatButton,
                "This is where you can chat with other users.", "GOT IT");

        sequence.addSequenceItem(cameraButton,
                "Use this to snap a photo.", "GOT IT");

        sequence.addSequenceItem(switchButton,
                "Use this to switch views between public images or your own private images.", "GOT IT");

        sequence.addSequenceItem(searchButton,
                "You can use this button to search for a specific user images.", "GOT IT");

        sequence.addSequenceItem(nearbyButton,
                "Finally you are ready to get started :), you can use this button to look for nearby places.", "GET STARTED");

        sequence.start();

    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle_vibrance));

            if (!success) {
                Log.i("msg", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.i("msg", "Can't find style. Error: ", e);
        }








        final Switch mapSwitch = (Switch) findViewById(R.id.mapSwitch);
        mapSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mapSwitch.isChecked()){

                    try {
                        // Customise the styling of the base map using a JSON object defined
                        // in a raw resource file.
                        boolean success = googleMap.setMapStyle(
                                MapStyleOptions.loadRawResourceStyle(
                                        MapScreen.this, R.raw.mapstyle_dark));

                        if (!success) {
                            Log.i("msg", "Style parsing failed.");
                        }
                    } catch (Resources.NotFoundException e) {
                        Log.i("msg", "Can't find style. Error: ", e);
                    }

                    getPrivateImages();

                    //Private travelog!
                    Toasty.info(MapScreen.this, "Someone turned the light off!", Toast.LENGTH_SHORT, true).show();

                } else {

                    try {
                        // Customise the styling of the base map using a JSON object defined
                        // in a raw resource file.
                        boolean success = googleMap.setMapStyle(
                                MapStyleOptions.loadRawResourceStyle(
                                        MapScreen.this, R.raw.mapstyle_vibrance));

                        if (!success) {
                            Log.i("msg", "Style parsing failed.");
                        }
                    } catch (Resources.NotFoundException e) {
                        Log.i("msg", "Can't find style. Error: ", e);
                    }

                    getPublicImages();


                    Toasty.info(MapScreen.this, "Public travelog!", Toast.LENGTH_SHORT,true).show();

                }

            }
        });

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {


                updateMap(location);


                // uploads the user location to the user class in parse so it can be used later as an image location
                ParseUser.getCurrentUser().put("location", new ParseGeoPoint(location.getLatitude(),location.getLongitude()));

                ParseUser.getCurrentUser().saveInBackground();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        // check for user permission to use gps

        if (Build.VERSION.SDK_INT < 23) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 11, 11, locationListener);

        } else {


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 11, 11, locationListener);

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


            if (lastKnownLocation != null) {

                updateMap(lastKnownLocation);

            }


            }
        }

    }

}
