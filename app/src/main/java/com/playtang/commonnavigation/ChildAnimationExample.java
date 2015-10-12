package com.playtang.commonnavigation;

import android.util.Log;
import android.view.View;

import com.daimajia.androidanimations.library.attention.StandUpAnimator;
import com.daimajia.androidanimations.library.bouncing_entrances.BounceInAnimator;
import com.playtang.library.Animations.BaseAnimationInterface;

import static com.playtang.library.R.id.linearLayout;

public class ChildAnimationExample implements BaseAnimationInterface {

    private final static String TAG = "ChildAnimationExample";
     View descriptionLayout;
    View mHeaderLayout;


    @Override
    public void onPrepareCurrentItemLeaveScreen(View current) {
        descriptionLayout = current.findViewById(linearLayout);

        if(descriptionLayout!=null){
            //current.findViewById(com.playtang.commonnavigation.R.id.linearLayout).setVisibility(View.INVISIBLE);
           // current.findViewById(R.id.description).setBackgroundColor(Color.argb(160, 24, 188, 156));
        }
        Log.e(TAG, "onPrepareCurrentItemLeaveScreen called");
    }

    @Override
    public void onPrepareNextItemShowInScreen(View next) {
        mHeaderLayout = next.findViewById(com.playtang.library.R.id.silder_text);
        descriptionLayout = next.findViewById(com.playtang.library.R.id.linearLayout);
        if(descriptionLayout!=null){
            next.findViewById(com.playtang.library.R.id.linearLayout).setVisibility(View.INVISIBLE);
            mHeaderLayout.setVisibility(View.INVISIBLE);
            //next.findViewById(com.playtang.library.R.id.description_layout).setBackgroundColor(Color.argb(160, 24, 188, 156));
            Log.e(TAG, "onPrepareNextItemShowInScreen descriptionLayout not null");

        }


        Log.e(TAG, "onPrepareNextItemShowInScreen called");
    }

    @Override
    public void onCurrentItemDisappear(View view) {
//        view.findViewById(R.id.description).setBackgroundColor(Color.argb(160, 24, 188, 156));
        Log.e(TAG, "onCurrentItemDisappear called");
    }

    @Override
    public void onNextItemAppear(View view) {
        mHeaderLayout = view.findViewById(com.playtang.library.R.id.silder_text);
       descriptionLayout = view.findViewById(com.playtang.library.R.id.linearLayout);
      //  Button b=(Button)view.findViewById(com.playtang.commonnavigation.R.id.description);
        if(descriptionLayout!=null){
            view.findViewById(com.playtang.commonnavigation.R.id.linearLayout).setVisibility(View.VISIBLE);

            mHeaderLayout.setVisibility(View.VISIBLE);

            //view.findViewById(com.playtang.library.R.id.description_layout).setBackgroundColor(Color.argb(160, 24, 188, 156));
         //   ValueAnimator animator = ObjectAnimator.ofFloat(
           //         descriptionLayout, "y", -descriptionLayout.getHeight(),
             //       0).setDuration(500);
            //animator.start();


            new StandUpAnimator().animate(mHeaderLayout);
            //new RubberBandAnimator().animate(mHeaderLayout);
           // new WobbleAnimator().animate(mHeaderLayout);
            //new HingeAnimator().animate(mHeaderLayout);
            //new RollInAnimator().animate(mHeaderLayout);
            new BounceInAnimator().animate(descriptionLayout);
           // descriptionLayout = (LinearLayout)findViewById(com.playtang.library.R.id.description_layout);
            // descriptionLayout.setBackgroundColor(Color.argb(160, 24, 188, 156));

        }


        Log.e(TAG, "onCurrentItemDisappear called");
    }

}
