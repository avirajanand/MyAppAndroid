/*
 *  Copyright (c) 2014, Parse, LLC. All rights reserved.
 *
 *  You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 *  copy, modify, and distribute this software in source code or binary form for use
 *  in connection with the web services and APIs provided by Parse.
 *
 *  As with any software that integrates with the Parse platform, your use of
 *  this software is subject to the Parse Terms of Service
 *  [https://www.parse.com/about/terms]. This copyright notice shall be
 *  included in all copies or substantial portions of the software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 *  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.playtang.commonnavigation;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseTwitterUtils;

import java.util.ArrayList;
import java.util.List;

public class PlayTangApp extends Application {
    public ArrayList<String> sportsArray;
    private boolean dataFetched = false;
    @Override
    public void onCreate() {
        super.onCreate();


        //      Create an InitializerBuilder
        Stetho.InitializerBuilder initializerBuilder =
                Stetho.newInitializerBuilder(this);

        // Enable Chrome DevTools
        initializerBuilder.enableWebKitInspector(
                Stetho.defaultInspectorModulesProvider(this)
        );

        // Enable command line interface
        initializerBuilder.enableDumpapp(
                Stetho.defaultDumperPluginsProvider(getApplicationContext())
        );

        // Use the InitializerBuilder to generate an Initializer
        Stetho.Initializer initializer = initializerBuilder.build();

        // Initialize Stetho with the Initializer
        Stetho.initialize(initializer);

        Parse.enableLocalDatastore(this);
        // Required - Initialize the Parse SDK
        Parse.initialize(this);

        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        ParseFacebookUtils.initialize(this);

        // Optional - If you don't want to allow Twitter login, you can
        // remove this line (and other related ParseTwitterUtils calls)
        ParseTwitterUtils.initialize(getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));

        if (isOnline()) {
            Toast.makeText(getApplicationContext(), "Taking you to Login Page", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        sportsArray = new ArrayList<>();

        getSportsAvailable();


    }

    private void getSportsAvailable() {
        //Toast.makeText(getApplicationContext(), "[PlaytangApp ] Inside getSportsAvailable:", Toast.LENGTH_LONG).show();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Sports");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> sports, ParseException e) {
               final List<ParseObject> SportsAvailable=sports;
                if(e!=null){
                    Toast.makeText(getApplicationContext(), "query error :" + e.toString(), Toast.LENGTH_LONG).show();
                    return;
                }

                ParseObject.unpinAllInBackground("Sports", SportsAvailable, new DeleteCallback() {
                    public void done(ParseException e) {
                        if (e != null) {
                            // There was some error.
                            return;
                        }

                        // Add the latest results for this query to the cache.
                        ParseObject.pinAllInBackground("SportsAvailable",SportsAvailable);
                    }
                });


            }
        });

    }


    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConn = networkInfo.isConnected();
        Log.d("PlayTangApp", "Wifi connected: " + isWifiConn);
        Log.d("PlayTanApp", "Mobile connected: " + isMobileConn);

        if (isMobileConn) {
            Toast.makeText(getApplicationContext(), "Mobile Network detected", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isWifiConn) {
            Toast.makeText(getApplicationContext(), "Wifi Detected", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(getApplicationContext(), "No Internet connection", Toast.LENGTH_SHORT).show();
            return false;
        }


    }

}