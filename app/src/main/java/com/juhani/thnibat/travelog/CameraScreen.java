package com.juhani.thnibat.travelog;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class CameraScreen extends AppCompatActivity {

    ImageView imageView;
    ImageButton takePictureButton;
    String mCurrentPhotoPath;
    String clicked;



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureButton.setEnabled(true);
            }
        }
    }


    File createImageFile() throws IOException {
        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_screen);
        // use this for devlopment so app doesnt crash until u fix it with fileprovider it doesnt crash on ur phone marshmello
        //StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        //StrictMode.setVmPolicy(builder.build());

        ImageButton shareButton = (ImageButton) findViewById(R.id.sharebtn);
        ImageButton camButton = (ImageButton) findViewById(R.id.button_image);
        ImageButton onlymeButton = (ImageButton) findViewById(R.id.onlyme);
        ImageButton galleryButton = (ImageButton) findViewById(R.id.imageShare);

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view
        config.setMaskColor(getResources().getColor(R.color.colorAccent));
        // add ,SHOWCASEID FOR SINGLE TIME USE
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "useonce");

        sequence.setConfig(config);

        sequence.addSequenceItem(camButton,
                "Use this to take a photo of your current place.", "GOT IT");

        sequence.addSequenceItem(galleryButton,
                "Or you can simply choose a photo from your gallery.", "GOT IT");

        sequence.addSequenceItem(onlymeButton,
                "Use this if you would like your image to be private.", "GOT IT");

        sequence.addSequenceItem(shareButton,
                "After you took a photo or picked one from your gallery, you want to share it right? you guessed it thats the button for that!", "GOT IT");

        sequence.start();

        clicked = "false";
        takePictureButton = (ImageButton) findViewById(R.id.button_image);
        imageView = (ImageView) findViewById(R.id.imageView);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }


        final ImageButton onlyme = (ImageButton) findViewById(R.id.onlyme);
        onlyme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clicked ="true";
                Toasty.warning(CameraScreen.this, "Image visibility is set to only me", Toast.LENGTH_SHORT, true).show();
                onlyme.setImageResource(R.mipmap.lockiconclosed2);
                /// object.put("visibility", "onlyme" );

            }
        });

    }


    // import picture from gallery

    public void importPicture(View view){

        Intent pickimge = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickimge,200);


    }

    // take picture from camera

    public void takePicture(View view) {


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // new file path method
        String auth = getApplicationContext().getPackageName() + ".fileprovider";
        Uri imageUri = FileProvider.getUriForFile(this, auth, photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        // old creating file path method
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

        startActivityForResult(intent, 100);

    }


    // sharing images function called in taken camera photo and imported photo from gallery

    public  void imageShare(Bitmap photo){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        photo.compress(Bitmap.CompressFormat.JPEG, 50, stream);

        byte[] byteArray = stream.toByteArray();

        ParseFile file = new ParseFile("image.png", byteArray);

        final ParseObject object = new ParseObject("Image");

        object.put("image", file);
        object.put("visibility", "public" );
        object.put("username", ParseUser.getCurrentUser().getUsername());
        object.put("location", new ParseGeoPoint(ParseUser.getCurrentUser().getParseGeoPoint("location")));
        if (clicked=="true") {
            object.put("visibility", "onlyme" );
        }

        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {
                    Toasty.success(CameraScreen.this, "Image shared!", Toast.LENGTH_SHORT, true).show();
                    finish();

                } else {
                    Toasty.error(CameraScreen.this, "Uploading failed!", Toast.LENGTH_SHORT, true).show();
                }


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){


            // in case of capturing image using camera
            case (100):
            {
                if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

                    // reduce image size so it fits inside imageview

                    int targetImageViewWidth = imageView.getWidth();
                    int targetImageViewHeight =imageView.getHeight();

                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(mCurrentPhotoPath,bmOptions);
                    int cameraImageWidth = bmOptions.outWidth;
                    int cameraImageHeight = bmOptions.outHeight;

                    int scaleFactor= Math.min(cameraImageWidth/targetImageViewWidth,cameraImageHeight/targetImageViewHeight);
                    bmOptions.inSampleSize = scaleFactor;
                    bmOptions.inJustDecodeBounds = false;

                    // replacing the imageview with taken picture after scaling it down

                    final Bitmap photo = BitmapFactory.decodeFile(mCurrentPhotoPath,bmOptions);
                    imageView.setImageBitmap(photo);

                    ImageButton shareButton = (ImageButton) findViewById(R.id.sharebtn);
                    shareButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imageShare(photo);

                        }
                    });

                    }
                            }
            break;


            // in case of picking image from gallery
            case (200):
            {
                if (requestCode == 200 && resultCode == RESULT_OK && data != null) {


                    Uri selectedimage = data.getData();
                    try {
                        final Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimage);
                        ImageView imageView = (ImageView) findViewById(R.id.imageView);
                        imageView.setImageBitmap(bitmap);


                        ImageButton shareButton = (ImageButton) findViewById(R.id.sharebtn);
                        shareButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                imageShare(bitmap);

                            }


                        });


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }}
            break;
        }





    }

}
