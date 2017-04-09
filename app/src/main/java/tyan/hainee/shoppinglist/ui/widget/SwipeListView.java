package tyan.hainee.shoppinglist.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;


public class SwipeListView extends ListView {

    public final String TAG = "SwipeListView";

    public SwipeListView(Context context) {
        super(context);
    }

    public SwipeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        getFocusedChild();
//        long eventTime = motionEvent.getEventTime() - motionEvent.getDownTime();
//        motionEvent.getX();
//        float viewX = view.getX();
//
//        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
//            case MotionEvent.ACTION_DOWN:
//                Log.d(TAG, "ACTION DOWN. mStartX: " + mStartX + ", viewX: " + viewX);
//                mStartX = motionEvent.getRawX();
////                return false;
//                break;
//
//            case MotionEvent.ACTION_UP:
//                Log.d(TAG, "ACTION UP. mStartX: " + mStartX + ", viewX: " + viewX + ", eventTime: " + eventTime);
//                if (eventTime < TIME_THRESHOLD) {
//                    Log.d(TAG, "EventTime is less than time_threshold");
////                    view.callOnClick();
////                    view.performClick();
//                    view.onTouchEvent(motionEvent);
////                    view.invalidate();
//                    return false;
////                    break;
//                }
//
//                if (viewX == 0 || viewX == - mSwipeLength) {
//                    break;
//                }
//
//                if (viewX > -(mSwipeLength / 2)) {
//                    view.animate()
//                            .x(0)
//                            .setDuration(500)
//                            .start();
//                }
//                else {
//                    view.animate()
//                            .x(- mSwipeLength)
//                            .setDuration(500)
//                            .start();
//                }
//                break;
//            case MotionEvent.ACTION_MOVE:
//                Log.d(TAG, "ACTION MOVE. mStartX: " + mStartX + ", viewX: " + viewX);
//
//                if (eventTime >= TIME_THRESHOLD && mStartX - motionEvent.getRawX() < LENGTH_THRESHOLD) {
//                    longClick(view);
//                    break;
//                }
//                else {
//                    click(view);
//                    break;
//                }
//
////                float animateX = viewX - (mStartX - motionEvent.getRawX());
////                if (animateX > 0) animateX = 0;
////                if (animateX < -mSwipeLeng3th) animateX = -mSwipeLength;
////
////                view.animate()
////                        .x(animateX)
////                        .setDuration(0)
////                        .start();
////
////                mStartX = motionEvent.getRawX();
////                break;
//            case MotionEvent.ACTION_CANCEL:
//                Log.d(TAG, "ACTION CANCEL");
//                return false;
//        }
//
//        view.invalidate();
//        return true;
        return super.onTouchEvent(motionEvent);
    }
}
