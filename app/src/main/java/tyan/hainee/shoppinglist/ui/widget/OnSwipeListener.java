package tyan.hainee.shoppinglist.ui.widget;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class OnSwipeListener implements View.OnTouchListener {

    private final static String TAG = "OnSwipeListener";

    private float mStartX;
    private float mSwipeLength;
    private float mVelocity;
    private final int TIME_THRESHOLD = 500;
    private final int LENGTH_THRESHOLD = 100;

    public OnSwipeListener(float swipeLength, float velocity) {
        mSwipeLength = swipeLength;
        mVelocity = velocity;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        Log.d(TAG, "OnTouch, viewX: " + view.getX() + ", event.getX: " + motionEvent.getX());
        Log.d(TAG, view.getClass().toString() + " " + view.getParent().getClass().toString());
        Log.d(TAG, "ID: " + ((ListView) view.getParent()).getSelectedItemId());
        Log.d(TAG, "index of child" + ((ListView) view.getParent()).indexOfChild(view));

        long eventTime = motionEvent.getEventTime() - motionEvent.getDownTime();
        float viewX = view.getX();

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "ACTION DOWN. mStartX: " + mStartX + ", viewX: " + viewX);
                mStartX = motionEvent.getRawX();
//                return false;
                break;

            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION UP. mStartX: " + mStartX + ", viewX: " + viewX + ", eventTime: " + eventTime);
                if (eventTime < TIME_THRESHOLD) {
                    Log.d(TAG, "EventTime is less than time_threshold");
                    int id = ((ListView) view.getParent()).indexOfChild(view);
                    ListView lv = (ListView) view.getParent();
//                    lv.performClick();
//                    lv.setChoiceMode(ListView.CHOICE_MODE_NONE);
                    motionEvent.setAction(MotionEvent.ACTION_CANCEL);
//                    lv.performItemClick(lv.getAdapter().getView(id, null, null), id, lv.getItemIdAtPosition(id));
                    return false;
//                    break;
                }

                if (viewX == 0 || viewX == - mSwipeLength) {
                    break;
                }

                if (viewX > -(mSwipeLength / 2)) {
                    view.animate()
                            .x(0)
                            .setDuration(500)
                            .start();
                }
                else {
                    view.animate()
                            .x(- mSwipeLength)
                            .setDuration(500)
                            .start();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "ACTION MOVE. mStartX: " + mStartX + ", viewX: " + viewX);

                if (eventTime >= TIME_THRESHOLD && mStartX - motionEvent.getRawX() < LENGTH_THRESHOLD) {
                    longClick(view);
                    break;
                }
                else {
                    click(view);
                    break;
                }

//                float animateX = viewX - (mStartX - motionEvent.getRawX());
//                if (animateX > 0) animateX = 0;
//                if (animateX < -mSwipeLeng3th) animateX = -mSwipeLength;
//
//                view.animate()
//                        .x(animateX)
//                        .setDuration(0)
//                        .start();
//
//                mStartX = motionEvent.getRawX();
//                break;
        }

        view.invalidate();
        return true;
    }

    public void click(View view) {
        Log.d(TAG, view.getClass() + " is clicked");
    }

    public void longClick(View view) {
        Log.d(TAG, view.getClass() + " is long clicked");
    }
}
