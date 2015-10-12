package com.playtang.library.SliderTypes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.playtang.library.R;

/**
 * This is a slider with a description TextView.
 */
public class TextSliderView extends BaseSliderView{
    private static final String TAG = "TextSliderView";

    public TextSliderView(Context context) {
        super(context);
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.render_type_text,null);
        ImageView target = (ImageView)v.findViewById(R.id.playtang_slider_image);
        Button description = (Button)v.findViewById(R.id.description);
        TextView header = (TextView)v.findViewById(R.id.silder_text);
      final  LinearLayout leftArrow = (LinearLayout) v.findViewById(R.id.left_arrow);
      final  LinearLayout rightArrow = (LinearLayout) v.findViewById(R.id.right_arrow);
        //Toast.makeText(getContext(), "TextSliderView : left_arrow"+leftArrow, Toast.LENGTH_SHORT).show();
       // final LinearLayout linearLayout = (LinearLayout)v.findViewById((R.id.linearLayout));
        setLeftArrow(leftArrow);
        setRightArrow(rightArrow);


        description.setText(getDescription());
        //leftArrow.setBackground();
        header.setText(getHeader());



        /*linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                Toast.makeText(getContext(), "TextSliderView", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "OnTouch");
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "ACTION_DOWN");
                        linearLayout.setBackgroundColor(Color.argb(160, 24, 188, 156));
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "ACTION_UP");
                        //set color back to default
                        linearLayout.setBackgroundColor(Color.argb(160, 57, 96, 145));
                        break;
                }
                return true;
            }
        });*/
        bindEventAndShow(v, target);
        return v;
    }
}
