package com.playtang.commonnavigation;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Navigation extends BaseActivity  {

    Bitmap bitmap;
    private class DownloadWebpageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Looper.prepare();
            // params comes from the execute() call: params[0] is the url.
            try {
               // saveFbProfileinParse();
              saveFbImage();
             //   return downloadUrl(urls[0]);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),"Image couldnt be saved : "+e.getMessage(),Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;

            }
            return bitmap;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Bitmap result) {
            final ImageView imageView = (ImageView) findViewById(R.id.circleView);
            if(imageView!=null){
                Toast.makeText(getApplicationContext(),"Imageview not null",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"Imageview is null",Toast.LENGTH_SHORT).show();
            }
            ParseFile imageFile =(ParseFile)ParseUser.getCurrentUser().get("profilePic");


            imageFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        imageView.setImageBitmap(bmp);
                    } else {
                        Toast.makeText(getApplicationContext(), "Error showing profilepic :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


            if(imageFile!=null){
            Toast.makeText(getApplicationContext(),"sucessfully saved",Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow()
        setContentView(R.layout.activity_navigation);

        getPackageInfo();
        if(ParseUser.getCurrentUser()!=null)
        new DownloadWebpageTask().execute(Profile.getCurrentProfile().getProfilePictureUri(70,70).toString());
        //saveFbImage();


    }

    private void getPackageInfo() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.playtang.commonnavigation",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    private void saveFbImage() throws Exception{
        try{
                if(Profile.getCurrentProfile()!=null) {

                String imageURLString= Profile.getCurrentProfile().getProfilePictureUri(70, 70).toString();

                URL imageURL= new URL(imageURLString);
                Toast.makeText(getApplicationContext(), "ImageUri :" + imageURLString, Toast.LENGTH_LONG).show();
                //String[] params = new String[1];
                //params[0] = imageURL;
                //ImageView imageView= findViewById(R.id.)
                bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] data = stream.toByteArray();
                String imageName = Profile.getCurrentProfile().getFirstName() + "_image.jpeg";
                ParseFile imageFile = new ParseFile(imageName,data);
                imageFile.saveInBackground();
                    ParseUser.getCurrentUser().put("profilePic", imageFile);
                    ParseUser.getCurrentUser().saveInBackground();

                }
        }catch(Exception pe){
            Log.e("Navigation", "Image cant be opnened" + pe.getMessage());
            pe.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_navigation, menu);
        //onCreateOptionsMenu(menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean isHomePage() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }




}
