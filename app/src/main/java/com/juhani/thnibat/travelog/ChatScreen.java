package com.juhani.thnibat.travelog;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ChatScreen extends AppCompatActivity {

    String activeUser = "";

    ArrayList<String> messages = new ArrayList<>();

    ArrayAdapter arrayAdapter;


    public void sendChat(View view) {

        final EditText chatEditText = (EditText) findViewById(R.id.chatEditText);

        ParseObject message = new ParseObject("Message");

        final String messageContent = chatEditText.getText().toString();

        message.put("sender", ParseUser.getCurrentUser().getUsername());
        message.put("recipient", activeUser);
        message.put("message", messageContent);

        chatEditText.setText("");

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {

                    messages.add(messageContent);

                    arrayAdapter.notifyDataSetChanged();


                }

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Intent intent = getIntent();

        activeUser = intent.getStringExtra("username");

        setTitle("Chat with " + activeUser);




        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, messages);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {



                ListView chatListView = (ListView) findViewById(R.id.chatListView);


                chatListView.setAdapter(arrayAdapter);

                ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Message");

                query1.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
                query1.whereEqualTo("recipient", activeUser);

                ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Message");

                query2.whereEqualTo("recipient", ParseUser.getCurrentUser().getUsername());
                query2.whereEqualTo("sender", activeUser);

                List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();

                queries.add(query1);
                queries.add(query2);

                ParseQuery<ParseObject> query = ParseQuery.or(queries);

                query.orderByAscending("createdAt");

                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        if (e == null) {

                            if (objects.size() > 0) {

                                messages.clear();

                                for (ParseObject message : objects) {

                                    String messageContent = message.getString("message");

                                    if (!message.getString("sender").equals(ParseUser.getCurrentUser().getUsername())) {


                                        messageContent = activeUser+": " + messageContent;

                                    }

                                    if (message.getString("sender").equals(ParseUser.getCurrentUser().getUsername())) {

                                        messageContent = "you: " + messageContent;

                                    }

                                    Log.i("Info", messageContent);

                                    messages.add(messageContent);

                                }

                                arrayAdapter.notifyDataSetChanged();

                            }

                        }

                    }
                });




                Log.i("Info", "Handler working");


                handler.postDelayed(this,5000);
            }
        },5000);




    }
}
