package com.juhani.thnibat.travelog;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.widget.Button;

public class WelcomeScreen extends AppCompatActivity implements View.OnClickListener{

    Button toSignupScreen; // declaring the sign up button
    Button toLoginScreen; // declaring the login button

    // after signup / login method

    public void toMapScreen(){

        Intent intent = new Intent(getApplicationContext(),MapScreen.class);
        startActivity(intent);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);


        // Enable Local Datastore.
        // caueses a crash replaced below on initliaze Parse.enableLocalDatastore(this);

        // Parse Server Info
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("a7e58edc8a4e66184fe7e5f5d01d54dd4aa54769")
                .clientKey("71f1ec2f5373af9f0844100ed5bd8fb8999fdc8c")
                .server("http://35.165.123.141:80/parse/")
                .enableLocalDataStore()
                .build()



        );

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);






            // linking the sign up button with its id and screen
            toSignupScreen = (Button) findViewById(R.id.wsignupButton);
            toSignupScreen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(WelcomeScreen.this, SignupScreen.class);

                    startActivity(intent);
                }
            });

            // linking the login button with its id and screen
            toLoginScreen = (Button) findViewById(R.id.wloginButton);
            toLoginScreen.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View login) {
                    Intent intent = new Intent(WelcomeScreen.this, LoginScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);


                }
            });



        // check if user logged in already

        if (ParseUser.getCurrentUser() != null) {

            toMapScreen();
            finish();

        }



    }

    @Override
    public void onClick(View v) {

    }

    public void imageClicked(View view) {
    }

    public void loginButton(View view) {
    }

    public void signUp(View view) {
    }
}

