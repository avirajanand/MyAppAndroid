package com.playtang.library.Animations;

import android.view.View;

/**
 * A demo class to show how to use {link com.daimajia.slider.library.Animations.BaseAnimationInterface}
 * to make  your custom animation in {link com.daimajia.slider.library.Tricks.ViewPagerEx.PageTransformer} action.
 */
public class DescriptionAnimation implements BaseAnimationInterface {

    @Override
    public void onPrepareCurrentItemLeaveScreen(View current) {
       // View descriptionLayout = current.findViewById(com.playtang.library.R.id.linearLayout);
        //if(descriptionLayout!=null){
          //  current.findViewById(R.id.linearLayout).setVisibility(View.INVISIBLE);
        //}
    }

    /**
     * When next item is coming to show, let's hide the description layout.
     * @param next
     */
    @Override
    public void onPrepareNextItemShowInScreen(View next) {
        /*View descriptionLayout = next.findViewById(R.id.linearLayout);
        if(descriptionLayout!=null){
            next.findViewById(R.id.linearLayout).setVisibility(View.INVISIBLE);
        }*/
    }


    @Override
    public void onCurrentItemDisappear(View view) {

    }

    /**
     * When next item show in ViewPagerEx, let's make an animation to show the
     * description layout.
     * @param view
     */
    @Override
    public void onNextItemAppear(View view) {
/*
        View descriptionLayout = view.findViewById(R.id.linearLayout);
        if(descriptionLayout!=null){
            float layoutY = ViewHelper.getY(descriptionLayout);
            view.findViewById(R.id.linearLayout).setVisibility(View.VISIBLE);
            ValueAnimator animator = ObjectAnimator.ofFloat(
                    descriptionLayout,"y",layoutY + descriptionLayout.getHeight(),
                    layoutY).setDuration(500);
            animator.start();
        }*/

    }
}
