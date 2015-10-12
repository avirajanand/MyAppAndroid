package com.playtang.commonnavigation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.Profile;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.playtang.commonnavigation.login.LoadFacebookProfilePic;
import com.playtang.library.SliderLayout;
import com.playtang.library.SliderTypes.BaseSliderView;
import com.playtang.library.SliderTypes.TextSliderView;
import com.playtang.library.Tricks.InfiniteViewPager;
import com.playtang.library.Tricks.ViewPagerEx;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

public class BaseActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

    private static final String TAG = "BaseActivity" ;
    String TITLES[] = {"Home","Events","Mail","All Sports","Info"};
    int ICONS[] = { R.drawable.ic_action,
                    R.drawable.ic_event,
                    R.drawable.ic_mail,
                    R.drawable.ic_play,
                    R.drawable.ic_info};

    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view

    String NAME = "Abhishek.Anand";
    String EMAIL = "abhishek.anand@playtang.com";
    int PROFILE = R.drawable.avi;
    NavigationView mNavigationView;
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout
    CustomPagerAdapter mCustomPagerAdapter;
    ActionBarDrawerToggle mDrawerToggle;
    InfiniteViewPager mViewPager;
    private Menu menu;
    private Toolbar toolbar;                              // Declaring the Toolbar Object
    private LinearLayout leftArrow;
    private LinearLayout rightArrow;
    private Profile fbProfile;
    private SliderLayout mDemoSlider;
    private int mCurrentSelectedPosition = -1;
    private int mPrevSelectedPosition=-1;
    private int isItemOnTouch=0;
    private int currentPageselected;
    private ParseUser currentUser;
    private LoadFacebookProfilePic loadFacebookProfilePic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        leftArrow=(LinearLayout) findViewById(com.playtang.library.R.id.left_arrow);
        rightArrow=(LinearLayout) findViewById(com.playtang.library.R.id.right_arrow);
        currentUser=ParseUser.getCurrentUser();
        //setFBProfile();
        //setContentView(R.layout.activity_base);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        Toast.makeText(BaseActivity.this, "OnPostCreate", Toast.LENGTH_SHORT).show();
        leftArrow=(LinearLayout) findViewById(com.playtang.library.R.id.left_arrow);
        rightArrow=(LinearLayout) findViewById(com.playtang.library.R.id.right_arrow);
        leftArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(BaseActivity.this, "left arrow ontouch", Toast.LENGTH_SHORT).show();
                mViewPager.arrowScroll(View.FOCUS_LEFT);
                mViewPager.requestDisallowInterceptTouchEvent(true);
                return true;
            }
        });

        rightArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(BaseActivity.this, "right arrow ontouch", Toast.LENGTH_SHORT).show();
                mViewPager.arrowScroll(View.FOCUS_RIGHT);
                mViewPager.requestDisallowInterceptTouchEvent(true);
                return true;
            }
        });

        if(currentUser!=null){
            MenuItem logout = (MenuItem) findViewById(R.id.action_login);
            logout.setTitle("Logout");
        }

        //setFBProfile();
    }

    private void setFBProfile() {
        fbProfile= Profile.getCurrentProfile();
        if(fbProfile!=null){
            saveFbProfileinParse();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tool_bar, menu);
        this.menu=menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu){
        updateMenuTitles();
        //setFBProfile();
    return true;
    }

    private void updateMenuTitles() {
        MenuItem logoutItem = menu.findItem(R.id.action_login);
        if (ParseUser.getCurrentUser()!=null) {
            logoutItem.setTitle(R.string.action_logout);
        } else {
            logoutItem.setTitle(R.string.action_loggin);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_login:
                //View menuItem= findViewById(item.getItemId());
                //menuItem.setBackgroundColor(Color.argb(160, 24, 188, 156));
                openLogging();
                return true;
            case R.id.action_location:

                onGroupItemClick(item);

            case R.id.add_venue:
                addVenue();
/*
            case R.id.description_layout:
               LinearLayout descriptionLayout = (LinearLayout)findViewById(R.id.description_layout);
                descriptionLayout.setBackgroundColor(Color.argb(160,24, 188, 156));
*/


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void addVenue(){
       Intent intent = new Intent(this, PlayTangVenue.class);
        startActivity(intent);
    }
    /** Called when the user clicks the Login button */

    public void openLogging() {
        Intent intent;

        if(currentUser==null) {       // Do something in response to button

            intent = new Intent(this, SplasLoginActivity.class);
        }
        else {

             ParseUser.logOutInBackground(new LogOutCallback() {
             @Override
             public void done(ParseException e) {
                 currentUser=null;
             Toast.makeText(BaseActivity.this,"You have been successfully logged out",Toast.LENGTH_SHORT).show();
             }
         });
            intent = new Intent(this, Navigation.class);
        }
        startActivity(intent);
    }

    public void onGroupItemClick (MenuItem item) {
        if (item.isChecked()) {
            item.setChecked(false);}
        else {
            item.setChecked(true);
        }
    }

    public void onHeaderLinkClick(){

    }
    @Override
    public void setContentView(int layoutResID)
    {
        DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);

        super.setContentView(fullView);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        mDemoSlider = (SliderLayout)findViewById(R.id.slider);

        if (useToolbar())
        {
            toolbar.setLogo(R.mipmap.logo);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationIcon(R.drawable.ic_drawer);

            setTitle("Activity Title");
                if(isHomePage()) {
                        addSlider();

                   // TextView latestListing=(TextView) findViewById(R.id.latest_listing);
                    //String text= getResources(findViewById(R.drawable.horizontal_line))

                }


            mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

            mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

          /*if(Profile.getCurrentProfile()!=null){
                fbProfile=Profile.getCurrentProfile();

              Handler mainHandler = new Handler(Looper.getMainLooper());

              Runnable myRunnable = new Runnable() {
                  @Override
                  public void run() { // This is your code
                      saveFbProfileinParse();
                  }
              };
              mainHandler.post(myRunnable);
              //saveFbProfileinParse();
          }
*/

             if(ParseUser.getCurrentUser()!=null){

                    NAME=ParseUser.getCurrentUser().get("name").toString();
                    EMAIL=ParseUser.getCurrentUser().getEmail();
                    //PROFILE=ParseUser.getCurrentUser().get("profilePic")
                //Toast.makeText(getApplicationContext(),"Name :"+NAME+" EMail : "+EMAIL,Toast.LENGTH_SHORT).show();
            }
            mAdapter = new MyAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE,getApplicationContext(),mDrawerToggle);        // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
                                                                                                                    // And passing the titles,icons,header view name, header view email,
                                                                                                                    // and header view profile picture

            //final GestureDetector mGestureDetector = getGestureDetector();


            mRecyclerView.addOnItemTouchListener(getOnItemTouchListener());


            mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

            mRecyclerView.setLayoutManager(mLayoutManager);


            Drawer = (DrawerLayout) findViewById(R.id.activity_container);        // Drawer object Assigned to the view

            handleDrawerToggle();

            mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView
            Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle

            mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State



        }
        else
        {
            toolbar.setVisibility(View.GONE);
        }

    }

    private void handleDrawerToggle() {
        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar, R.string.openDrawer,R.string.closeDrawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //mAdapter.bindViewHolder();
                if (mPrevSelectedPosition > 0 && isItemOnTouch!=1){
                for(int i=1;i<=mAdapter.getItemCount();i++) {

                        if( i!=mPrevSelectedPosition)
                        mRecyclerView.getChildAt(mPrevSelectedPosition).setBackgroundResource(R.drawable.item_row_background);
                        else
                            mRecyclerView.getChildAt(i).setBackgroundResource(R.drawable.gradient_background);

                    }
                }
                invalidateOptionsMenu();
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
/*
                for(int i=1;i<mAdapter.getItemCount();i++) {

                    if (mCurrentSelectedPosition > 0 && i!=mCurrentSelectedPosition){
                        //mRecyclerView.getChildAt(i).setBackgroundResource(R.drawable.gradient_background);
                    }


                }*/
                invalidateOptionsMenu();
                // Code here will execute once drawer is closed

                mPrevSelectedPosition=mCurrentSelectedPosition;
            }


        }; // Drawer Toggle Object Made
    }

    @NonNull
    private RecyclerView.OnItemTouchListener getOnItemTouchListener() {
        return new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                return onNavigationItemClick(recyclerView, motionEvent);
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                isItemOnTouch=1;
                onTouchNavigationItem(recyclerView, motionEvent);
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean var1) {

            }
        };
    }

    @NonNull
    private GestureDetector getGestureDetector() {
        return new GestureDetector(BaseActivity.this, new GestureDetector.SimpleOnGestureListener() {

@Override public boolean onSingleTapUp(MotionEvent e) {
    return true;
}

@Override
public boolean onDown(MotionEvent e) {
    return super.onDown(e);
}


});
    }

    private void onTouchNavigationItem(RecyclerView recyclerView, MotionEvent motionEvent) {
        View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

            if(mRecyclerView.getChildAdapterPosition(child)!=0) {


                //Toast.makeText(BaseActivity.this, "Motion Event:  " + motionEvent.getAction(), Toast.LENGTH_SHORT).show();
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    Toast.makeText(BaseActivity.this, "OnTOuch :ACTION_UP, pos:" + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
                    Drawer.closeDrawers();
                    //child.setBackgroundResource(R.drawable.item_row_background);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
                    Toast.makeText(BaseActivity.this, "OnTOuch : ACTION_DOWN, pos:" + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
                    Drawer.closeDrawers();
                    //child.setBackgroundResource(R.drawable.gradient_background);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Toast.makeText(BaseActivity.this, "OnTOuch : ACTION_DOWN, pos:" + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
                    Drawer.closeDrawers();
                }
                Drawer.closeDrawers();
//                Toast.makeText(BaseActivity.this, "OnTOuch : The Item Clicked is: " + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
               }
    }

    private boolean onNavigationItemClick(RecyclerView recyclerView, MotionEvent motionEvent) {
        View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        int pos=mRecyclerView.getChildAdapterPosition(child);
        mCurrentSelectedPosition = pos;

        //Toast.makeText(BaseActivity.this, "Motion Event:  " +motionEvent.getAction() +", position:"+mCurrentSelectedPosition , Toast.LENGTH_SHORT).show();
        if (child != null && (pos!=0)) {
            //   child.setBackgroundColor(Color.argb(160, 24, 188, 156));
///                      Toast.makeText(BaseActivity.this, "Motion Event:  " +motionEvent.getAction() , Toast.LENGTH_SHORT).show();

            if(motionEvent.getAction()==MotionEvent.ACTION_MOVE || motionEvent.getAction()==MotionEvent.ACTION_CANCEL){
                Toast.makeText(BaseActivity.this, "ACTION_MOVE /ACTION_CANCEL :" + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
                Drawer.closeDrawers();
                return true;
            }

            if(motionEvent.getAction()==MotionEvent.ACTION_DOWN)
            {
                Toast.makeText(BaseActivity.this, "ACTION_DOWN" + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();

                //prevChild=child;
                //Drawer.closeDrawers();
                return true;
                //
            }

            if(motionEvent.getAction()==MotionEvent.ACTION_UP)
            { Toast.makeText(BaseActivity.this, "ACTION_UP" + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
                //child.setBackgroundResource(R.drawable.gradient_background);
                for(int i=1;i<=mAdapter.getItemCount();i++) {
                    if (mPrevSelectedPosition > 0){
                        if( i!=mPrevSelectedPosition)
                            mRecyclerView.getChildAt(mPrevSelectedPosition).setBackgroundResource(R.drawable.gradient_background);
                    }
                }

                child.setBackgroundResource(R.drawable.item_row_background);
                mPrevSelectedPosition=mCurrentSelectedPosition;
                Drawer.closeDrawers();
                return true;
                //child.setBackgroundResource(R.drawable.item_row_background);
            }



//          Toast.makeText(BaseActivity.this, "The Item Clicked is: " + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();

            return true;

        }
        return false;
    }

    protected boolean useToolbar()
    {
        return true;
    }

    protected boolean isHomePage()
    {
        return false;
    }

    public void addSlider(){
        leftArrow = new LinearLayout(this);
        rightArrow= new LinearLayout(this);


        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDemoSlider.movePrevPosition(true);
            }
        });


        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDemoSlider.moveNextPosition(true);
            }
        });



        Hashtable<String,Integer> file_maps = new Hashtable<String,Integer>();
        file_maps.put("Search",R.drawable.slider1);
        file_maps.put("Coming Soon",R.drawable.slider3);
        file_maps.put("Register Now",R.drawable.slider2);


        ArrayList<String> header_text = new ArrayList<>();
        header_text.add(0,"Welcome to Playtang");
        header_text.add(1, "Search for a Playground");
        header_text.add(2, "Book Your Time Slot");


        int i=0;

        for(String name : file_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .header(header_text.get(i))
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(this);
            i++;
            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("playtang", name);
            mDemoSlider.addSlider(textSliderView);

            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.FlipHorizontal);
            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            //mDemoSlider.setBackgroundColor(0b1000011100001000);
            //DescriptionAnimation descriptionAnimation = new DescriptionAnimation();
            mDemoSlider.setCustomAnimation(new ChildAnimationExample());
            //mDemoSlider.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
            //mDemoSlider.setCustomAnimation(new DescriptionAnimation());
            mDemoSlider.setDuration(4000);
            mViewPager=mDemoSlider.getmViewPager();
            //mDemoSlider.set
            //mDemoSlider.addOnPageChangeListener(this);
            //textSliderView.setOnSliderClickListener()


        }



    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        //mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this,slider.getBundle().get("playtang") + "",Toast.LENGTH_SHORT).show();
        if(slider.getView().getId()==R.id.left_arrow) {
          Toast.makeText(this,slider.getBundle().get("playtang") + "leftarrow",Toast.LENGTH_SHORT).show();
          //  mDemoSlider.
            //mDemoSlider.onInterceptTouchEvent());
        }
    }

    @Override
    public void onArrowClick(LinearLayout arrow) {
        Toast.makeText(this,"Arrow Click",Toast.LENGTH_SHORT).show();
        if(com.playtang.library.R.id.left_arrow==arrow.getId()){
            Toast.makeText(this,"Left Arrow Click",Toast.LENGTH_SHORT).show();
        }

        if(com.playtang.library.R.id.right_arrow==arrow.getId()){
            Toast.makeText(this,"Right Arrow Click",Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.linearLayout:
                finish();
                break;

            default:
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
       // Toast.makeText(BaseActivity.this, "BaseActivity :onPageScrolled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageSelected(int position) {
        //Toast.makeText(BaseActivity.this, "BaseActivity :onPageSelected : "+position, Toast.LENGTH_SHORT).show();
        currentPageselected=position;
        //Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //Toast.makeText(BaseActivity.this, "BaseActivity :onPageScrollStateChanged : "+state, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

    }

    public void saveFbProfileinParse(){
        try{
            String imageURLString= fbProfile.getProfilePictureUri(70,70).toString();
            URL imageURL= new URL(imageURLString);
            Toast.makeText(getApplicationContext(),"ImageUri :"+imageURLString,Toast.LENGTH_LONG);
            //String[] params = new String[1];
            //params[0] = imageURL;
            //ImageView imageView= findViewById(R.id.)
            Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] data = stream.toByteArray();
            String imageName = fbProfile.getFirstName() + "_image";
            ParseFile imageFile = new ParseFile(imageName,data);
            imageFile.saveInBackground();

            //loadFacebookProfilePic = new LoadFacebookProfilePic(getApplicationContext());
           //loadFacebookProfilePic.execute(params);

            // Bitmap bmpImage=
             //Bitmap bmpImage = loadFacebookProfilePic.get();

          //  Bitmap bmpImage = Picasso.with().load(imageURL).get();

            /*Picasso.with(getBaseContext()).load(imageURL).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    //sendImageDS(context, bitmap, imageIdentifier);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] data = stream.toByteArray();
                    String imageName = fbProfile.getFirstName() + "_image";
                    ParseFile imageFile = new ParseFile(imageName,data);
                    imageFile.saveInBackground();
                    Toast.makeText(getApplicationContext(),"Image Saved successfully",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.e(TAG, "Image was not obtained");
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            });*/
            //Drawable drawable = new BitmapDrawable(getResources(), bmpImage);
            // ((BitmapDrawable) drawable.getDrawable()).getBitmap();

            //ParseUser.getCurrentUser().put("profilePic", imageFile);

        }catch (Exception ex){
            ex.printStackTrace();
            Log.e("BaseActivity", ex.toString());
            Toast.makeText(getApplicationContext(), "Image Exception occured"+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
