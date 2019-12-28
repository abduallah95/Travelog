package com.juhani.thnibat.travelog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;

public class PassrestScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passrest_screen);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.6));

        Bundle extras = getIntent().getExtras();
        byte[] b = extras.getByteArray("picture");


        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        ImageView image = (ImageView) findViewById(R.id.imageView1);

        image.setImageBitmap(bmp);


    }
}
