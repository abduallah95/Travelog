package com.juhani.thnibat.travelog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class LoginScreen extends AppCompatActivity implements View.OnKeyListener {

    EditText passwordLogin;

    // after signup / login method

    public void toMapScreen(){

        Intent intent = new Intent(getApplicationContext(),MapScreen.class);
        startActivity(intent);

    }

   /* public void restPW(View view){


        ParseUser.requestPasswordResetInBackground("myemail@example.com",
                new RequestPasswordResetCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(LoginScreen.this, "Email has been sent.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginScreen.this, "Something went wrong.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }*/

    // makes keyobard enter button clicks signup
    @Override
        public boolean onKey(View v, int keyCode, KeyEvent KeyEvent) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && KeyEvent.getAction() == KeyEvent.ACTION_DOWN){
            loginButton(v);

        }

        return false;
    }


    public void loginButton(View view) {

        EditText usernameLogin = (EditText) findViewById(R.id.usernameLogin);
        EditText passwordLogin = (EditText) findViewById(R.id.passwordLogin);

        if (usernameLogin.getText().toString().matches("") || passwordLogin.getText().toString().matches("")) {
            Toast.makeText(this, "A username and password are required.", Toast.LENGTH_SHORT).show();
        } else {

            ParseUser.logInInBackground(usernameLogin.getText().toString(), passwordLogin.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        Log.i("login","login successful");

                        toMapScreen();
                        finish();

                    } else {
                        Toast.makeText(LoginScreen.this, e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        passwordLogin = (EditText) findViewById(R.id.passwordLogin);
        passwordLogin.setOnKeyListener(this);

        // check if user logged in already

        if (ParseUser.getCurrentUser() != null) {

            toMapScreen();
            finish();

        }


    }
}
