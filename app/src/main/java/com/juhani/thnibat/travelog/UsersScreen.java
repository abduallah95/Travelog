package com.juhani.thnibat.travelog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UsersScreen extends AppCompatActivity {

    ArrayList<String> users = new ArrayList<>();

    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_screen);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        setTitle("Users List");

        if (ParseUser.getCurrentUser().get("isFollowing")== null){

            List<String> emptyList = new ArrayList<>();

            ParseUser.getCurrentUser().put("isFollowing", emptyList);
        }

        final ListView userListView = (ListView) findViewById(R.id.userListView);

        userListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked, users);

        userListView.setAdapter(arrayAdapter);

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckedTextView checkedTextView = (CheckedTextView) view;
                if (checkedTextView.isChecked()) {

                    Log.i("Info","User Checked");
                    ParseUser.getCurrentUser().getList("isFollowing").add(users.get(i));
                    ParseUser.getCurrentUser().saveInBackground();

                } else {

                    Log.i("Info","User Unchecked");
                    ParseUser.getCurrentUser().getList("isFollowing").remove(users.get(i));
                    ParseUser.getCurrentUser().saveInBackground();



                }
            }
        });


        users.clear();

        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                if (e == null) {

                    if (objects.size() > 0) {

                        for (ParseUser user : objects) {

                            users.add(user.getUsername());

                        }

                        arrayAdapter.notifyDataSetChanged();

                        for (String username : users) {

                            if (ParseUser.getCurrentUser().getList("isFollowing").contains(username)) {

                                userListView.setItemChecked(users.indexOf(username), true);

                            }

                        }

                    }

                }

            }
        });


    }



    }

