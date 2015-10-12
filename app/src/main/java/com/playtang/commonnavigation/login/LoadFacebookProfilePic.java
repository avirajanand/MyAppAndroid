package com.playtang.commonnavigation.login;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by 310131737 on 9/30/2015.
 */

public class LoadFacebookProfilePic extends AsyncTask<String, Void, Bitmap> {

    private Context context;

    public LoadFacebookProfilePic(Context context) {
        this.context = context;
        Looper.prepare();
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap myBitmap = null;
/*if(Looper.myLooper()==null){
    Looper.prepare();
}*/
        try {

            URL url = new URL(params[0]);
            Toast.makeText(context, "URL :"+url, Toast.LENGTH_SHORT).show();
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(true);

            boolean redirect = false;
          //  HttpsURLConnection
            // normally, 3xx is redirect
            int status = connection.getResponseCode();
            if (status != HttpsURLConnection.HTTP_OK) {
                if (status == HttpsURLConnection.HTTP_MOVED_TEMP
                        || status == HttpsURLConnection.HTTP_MOVED_PERM
                        || status == HttpsURLConnection.HTTP_SEE_OTHER)
                    redirect = true;
            }

            // Handle redirect
            if (redirect) {
                String newUrl = connection.getHeaderField("Location");
                Toast.makeText(context, "newURL :"+newUrl, Toast.LENGTH_SHORT).show();
                connection = (HttpsURLConnection) new URL(newUrl).openConnection();
                InputStream input = connection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(input);
                myBitmap = BitmapFactory.decodeStream(bis);
                //myBitmap= Picasso.with(context).load(newUrl).get();
                //FileOutputStream fos = context.openFileOutput("test.png", Context.MODE_PRIVATE);
                //myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                //fos.close();
                bis.close();
                input.close();
            }
        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(context, "LoadFacebookUserPicture Exception occured", Toast.LENGTH_SHORT).show();
            Log.e("LoadFacebookUserPicture", e.getMessage());
        }
        Log.e("LoadFacebookProfilePic", "ok");
        return myBitmap;
    }


}