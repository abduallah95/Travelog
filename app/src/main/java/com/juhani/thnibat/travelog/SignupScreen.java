package com.juhani.thnibat.travelog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupScreen extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {

    Button toProfilepicScreen;
    EditText passwordSignup;


    // after signup / login method

    public void toMapScreen(){

        Intent intent = new Intent(getApplicationContext(),MapScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }



    /*public  void laststepScreen() {


        Intent intent = new Intent(getApplicationContext(),ProfilepicScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);

    }*/


    // makes keyobard enter button clicks signup
    @Override
        public boolean onKey(View v, int keyCode, KeyEvent KeyEvent) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && KeyEvent.getAction() == KeyEvent.ACTION_DOWN){
            signUp(v);
        }

        return false;
    }


    public void signUp(View view) {

        EditText emailSignup = (EditText) findViewById(R.id.emailAdd);
        EditText nameSignup = (EditText) findViewById(R.id.nameSignup);
        EditText usernameSignup = (EditText) findViewById(R.id.usernameSignup);
        EditText passwordSignup = (EditText) findViewById(R.id.passwordSignup);



        // eliminate empty fields

            if (usernameSignup.getText().toString().matches("") || passwordSignup.getText().toString().matches("") || emailSignup.getText().toString().matches("") || nameSignup.getText().toString().matches("")) {
            Toast.makeText(this,"Required fields cannot be empty.",Toast.LENGTH_SHORT).show();
        } else {

            ParseUser user = new ParseUser();


            // signup info

            user.setEmail(emailSignup.getText().toString());
            user.put("name",nameSignup.getText().toString());
            user.setUsername(usernameSignup.getText().toString());
            user.setPassword(passwordSignup.getText().toString());




            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){
                        Log.i("signup","successful");

                        toMapScreen();
                        finish();

                    } else {
                        Toast.makeText(SignupScreen.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen);

        passwordSignup = (EditText) findViewById(R.id.passwordSignup);
        passwordSignup.setOnKeyListener(this);


        // check if user logged in already

        /*if (ParseUser.getCurrentUser() != null) {

            laststepScreen();
            finish();

        }*/


        // linking the im ready button with its id and screen
       /* causes crash and uncrash for next scrren toProfilepicScreen = (Button) findViewById(R.id.signupButton);
        toProfilepicScreen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View propic) {
                Intent intent = new Intent(SignupScreen.this, ProfilepicScreen.class);

                startActivity(intent);

            }
        });*/

    }




    @Override
    public void onClick(View v) {

    }
}
