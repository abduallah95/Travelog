package com.juhani.thnibat.travelog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ProfileScreen extends AppCompatActivity {

    // if logout is successful go to welcome screen
    public void logOutSuccessful() {

        Intent intent = new Intent(ProfileScreen.this, WelcomeScreen.class);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        /*//linearLayout2
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout2);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Image");

        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.orderByAscending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {

                    if (objects.size() > 0) {

                        for (ParseObject object : objects) {

                            ParseFile file = (ParseFile) object.get("image");

                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {

                                    if (e == null && data != null) {

                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                        ImageView imageView = new ImageView(getApplicationContext());

                                        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT
                                        ));

                                        imageView.setImageBitmap(bitmap);

                                        linearLayout.addView(imageView);

                                    }


                                }
                            });

                        }

                    }

                }

            }
        });*/


        // geocoder to get the user current location and display it on profile city,country
        ParseQuery<ParseUser> query2 = ParseUser.getQuery();
        query2.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());
        ParseGeoPoint parseGeoPoint = new ParseGeoPoint();
        query2.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                ParseGeoPoint point = ParseUser.getCurrentUser().getParseGeoPoint("location");

                Double lat = point.getLatitude();
                Double lng = point.getLongitude();

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {
                    List<Address> listAddresses = geocoder.getFromLocation(lat,lng,1);

                    if (listAddresses !=null && listAddresses.size() > 0){

                        String country = listAddresses.get(0).getCountryName();
                        String city = listAddresses.get(0).getLocality();

                        TextView currentlocation = (TextView) findViewById(R.id.currentloc);
                        currentlocation.setText(city+", "+country);


                    }

                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });


        // generate color for profile picture background
        ColorGenerator generator = ColorGenerator.MATERIAL;

        // get the first character from the user firstname in parse and use it for image
        TextDrawable profilepic = TextDrawable.builder()
                .buildRound(String.valueOf(String.valueOf(ParseUser.getCurrentUser().get("name")).charAt(0)), generator.getRandomColor());

        ImageView image = (ImageView) findViewById(R.id.profile_pic);
        image.setImageDrawable(profilepic);

        // get users full name
        TextView Fullname = (TextView) findViewById(R.id.fullname);
        Fullname.setText((String) ParseUser.getCurrentUser().get("name"));

        // get username
        TextView Username = (TextView) findViewById(R.id.username);
        Username.setText("@"+(String) ParseUser.getCurrentUser().getUsername());

        // get email address
        TextView Email = (TextView) findViewById(R.id.useremail);
        Email.setText((String) ParseUser.getCurrentUser().getEmail());



    }
}
