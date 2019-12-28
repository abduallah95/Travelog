package com.juhani.thnibat.travelog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

import static android.view.View.VISIBLE;

public class FullimageScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullimage_screen);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.6));

        Bundle extras = getIntent().getExtras();
        byte[] b = extras.getByteArray("picture");
        String username = extras.getString("username");
        final String objectid = extras.getString("objectid");
        String currentusername= ParseUser.getCurrentUser().getUsername();
        final TextView placetaken = (TextView) findViewById(R.id.placetaken);


        // geocoder for place of the taken photo
        final ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Image");
        query2.whereEqualTo("objectId",objectid);
        ParseGeoPoint geoPointLocation = new ParseGeoPoint();
        query2.whereNear("location", geoPointLocation);
        query2.setLimit(1);
        query2.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() > 0) {
                    for (final ParseObject object : objects) {
                        final ParseGeoPoint point = object.getParseGeoPoint("location");
                        Double lat = point.getLatitude();
                        Double lng = point.getLongitude();

                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                        try {
                            List<Address> listAddresses = geocoder.getFromLocation(lat, lng, 1);

                            if (listAddresses != null && listAddresses.size() > 0) {

                                String country = listAddresses.get(0).getCountryName();
                                String city = listAddresses.get(0).getLocality();

                                placetaken.setText(city + ", " + country);


                            }

                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                    }

                }
            }
        });


        if (b!= null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
            ImageView image = (ImageView) findViewById(R.id.imageView1);


            image.setImageBitmap(bmp);


            TextView usernameid = (TextView) findViewById(R.id.usernameID);

            usernameid.setText("@"+username);

            Log.i("passedusername", username);
            Log.i("currentusername", currentusername);
            Log.i("objectid", objectid);

            // if its the user image show him a buttton to change image visibility
            if (username.equals(currentusername)) {
                final ImageButton makepublic = (ImageButton) findViewById(R.id.makepublic);
                makepublic.setVisibility(VISIBLE);
                makepublic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
                        query.getInBackground(objectid, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if (object.get("visibility").equals("onlyme")) {
                                    makepublic.setImageResource(R.mipmap.lockiconclosed2);
                                    object.put("visibility", "public");
                                    object.saveInBackground();
                                    makepublic.setImageResource(R.mipmap.lockicon2);
                                    Toasty.warning(FullimageScreen.this, "Image visibility is set to public now!", Toast.LENGTH_SHORT, true).show();
                                } else {
                                    makepublic.setImageResource(R.mipmap.lockiconclosed2);
                                    object.put("visibility", "onlyme");
                                    object.saveInBackground();
                                    Toasty.warning(FullimageScreen.this, "Image visibility is set to onlyme now!", Toast.LENGTH_SHORT, true).show();
                                }
                            }
                        });
                    }
                });

            }

        }else {

            getWindow().setLayout((int)(width*.3),(int)(height*.0));


            ImageView image = (ImageView) findViewById(R.id.imageView1);

            TextView usernameid = (TextView) findViewById(R.id.usernameID);
            usernameid.setText(username);
            usernameid.setTextColor(Color.BLACK);



            //usernameid.setText("Thats not an image :(");

            image.setImageResource(R.drawable.image);
            image.setBackgroundColor(Color.WHITE);

            TextView report = (TextView) findViewById(R.id.report);

            report.setVisibility(View.INVISIBLE);

        }

        TextView report = (TextView) findViewById(R.id.report);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ParseObject object = new ParseObject("Reports");

                object.put("imageid", objectid);
                object.put("username",ParseUser.getCurrentUser().getUsername());
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        if (e == null) {
                            Toasty.success(FullimageScreen.this, "Report have been sent.", Toast.LENGTH_SHORT).show();

                        } else {
                            Toasty.error(FullimageScreen.this, "Something wrong happened!", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
            }
        });



    }
}
