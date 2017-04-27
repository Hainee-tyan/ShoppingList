package tyan.hainee.shoppinglist.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import tyan.hainee.shoppinglist.R;

public class SwipeView extends RelativeLayout {
    private final String TAG = "SwipeView";

    private LayoutInflater mInflater;

    private int mDeletionColor;
    private int mNonDeletionColor;

    private View mDeletionView;
    private View mDeleteIcon;
    private int mDeleteIconWidth;

    private float mDownX;
    private float mDownY;
    private boolean mSwiping;
    private float mTranslationX;
    private VelocityTracker mVelocityTracker;
    private OnViewDismissListener mListener;

    private View mSwipingView;
    private int mViewWidth = 1;

    private int mSlop;
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;
    private long mAnimationTime;

    public SwipeView(Context context) {
        super(context);
    }

    public SwipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSwipe();
    }

    public SwipeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSwipe();
    }

    private void initSwipe() {
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.swipe_view, this, true);

        mDeletionView = findViewById(R.id.delete_view);
        mDeleteIcon = findViewById(R.id.delete_icon);

        mDeletionColor = ContextCompat.getColor(getContext(), R.color.deletion);
        mNonDeletionColor = ContextCompat.getColor(getContext(), R.color.transparentDeletion);

        ViewConfiguration vc = ViewConfiguration.get(getContext());
        mSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity() * 16;
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mAnimationTime = getContext().getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        mSwipingView = this;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ev.offsetLocation(mTranslationX, 0);

        if (mViewWidth < 2) {
            mViewWidth = mSwipingView.getWidth();
        }

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                mDownX = ev.getRawX();
                mDownY = ev.getRawY();

                mVelocityTracker = VelocityTracker.obtain();
                mVelocityTracker.addMovement(ev);

                return false;
            }

            case MotionEvent.ACTION_UP: {
                if (mVelocityTracker == null) {
                    break;
                }

                mVelocityTracker.addMovement(ev);
                mVelocityTracker.computeCurrentVelocity(1000);
                float velocityX = mVelocityTracker.getXVelocity();
                float absVelocityX = Math.abs(velocityX);
                float absVelocityY = Math.abs(mVelocityTracker.getYVelocity());

                float deltaX = ev.getRawX() - mDownX;
                boolean dismiss = false;

                if (mSwiping && -deltaX > mViewWidth * getSwipeThreshold()) {
                    dismiss = true;
//                } else if (mSwiping
//                        && mMinFlingVelocity <= absVelocityX
//                        && absVelocityX <= mMaxFlingVelocity
//                        && absVelocityY < absVelocityX) {
//                    dismiss = (velocityX < 0) == (deltaX < 0);
                }

                if (dismiss) {
                    mSwipingView.animate()
                            .translationX(-mViewWidth)
                            .setDuration(mAnimationTime)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    performDismiss();
                                }
                            });
                    mDeleteIcon.animate()
                            .translationX(mDeleteIconWidth - mViewWidth)
                            .setDuration(mAnimationTime);
                } else if (mSwiping) {
                    mSwipingView.animate()
                            .translationX(0)
                            .setDuration(mAnimationTime)
                            .setListener(null);
                    mDeleteIcon.animate()
                            .translationX(mDeleteIconWidth)
                            .setDuration(mAnimationTime);
                }
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                mTranslationX = 0;
                mDownX = 0;
                mDownY = 0;
                mSwiping = false;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (mVelocityTracker == null) {
                    break;
                }

                mVelocityTracker.addMovement(ev);
                float deltaX = ev.getRawX() - mDownX;
                float deltaY = ev.getRawY() - mDownY;
                if (-deltaX > mSlop && Math.abs(deltaY) < Math.abs(deltaX) / 2) {
                    mSwiping = true;
                    getParent().requestDisallowInterceptTouchEvent(true);

                    //THIS BLOCK IS EXPERIMENTAL
                    MotionEvent cancelEvent = MotionEvent.obtain(ev);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
                            (ev.getActionIndex() <<
                                    MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    mSwipingView.onTouchEvent(cancelEvent);
                    cancelEvent.recycle();
                    //THIS BLOCK IS EXPERIMENTAL
                }

                if (mSwiping) {
                    mTranslationX = deltaX;
                    float translationX = deltaX + mSlop;
                    if (translationX > 0)
                        translationX = 0;
                    mSwipingView.setTranslationX(translationX);
                    mDeleteIcon.setTranslationX(mDeleteIconWidth + translationX);
                    if (-deltaX > mViewWidth * getSwipeThreshold()) {
                        mDeletionView.setBackgroundColor(mDeletionColor);
                    }
                    else {
                        mDeletionView.setBackgroundColor(mNonDeletionColor);
                    }
                    return false;
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                if (mVelocityTracker == null) {
                    break;
                }

                mSwipingView.animate()
                        .translationX(0)
                        .setDuration(mAnimationTime)
                        .setListener(null);
                mDeleteIcon.animate()
                        .translationX(mDeleteIconWidth)
                        .setDuration(mAnimationTime);
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                mTranslationX = 0;
                mDownX = 0;
                mDownY = 0;
                mSwiping = false;
                break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    private float getSwipeThreshold() {
        return .25f;
    }

    public void setSwipingView(int layoutID, int viewID) {
        mInflater.inflate(layoutID, this, true);
        mSwipingView = findViewById(viewID);

        if (mSwipingView == null) {
            mSwipingView = this;
        }

        mSwipingView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mSwipingView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                int measuredHeight = mSwipingView.getMeasuredHeight();
                if (measuredHeight != mDeletionView.getMeasuredHeight()) {
                    ViewGroup.LayoutParams lp = mDeletionView.getLayoutParams();
                    lp.height = measuredHeight;
                    mDeletionView.setLayoutParams(lp);
                }
            }
        });

        int measuredHeight = mSwipingView.getMeasuredHeight();

        mDeletionView.getLayoutParams().height = measuredHeight;
        mDeleteIcon.getLayoutParams().height = measuredHeight;
        mDeleteIcon.getLayoutParams().width = measuredHeight;
        mDeleteIcon.setTranslationX(measuredHeight);
        mDeleteIconWidth = measuredHeight;
    }

    public interface OnViewDismissListener {
        void onDismiss(View view);
    }

    public void setOnViewDismissListener(OnViewDismissListener listener) {
        mListener = listener;
    }

    public void restore() {
        setVisibility(View.VISIBLE);
        mSwipingView.setTranslationX(0);

        final ViewGroup.LayoutParams lp = getLayoutParams();
        measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final int height = getMeasuredHeight();

        ValueAnimator animator = ValueAnimator.ofInt(0, height).setDuration(mAnimationTime);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lp.height = (Integer) valueAnimator.getAnimatedValue();
                setLayoutParams(lp);
            }
        });

        animator.start();
    }

    private void performDismiss() {
        final ViewGroup.LayoutParams lp = getLayoutParams();
        final int originalHeight = getHeight();
        final View view = this;

        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0).setDuration(mAnimationTime);

//        if (mListener != null) {
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    setVisibility(View.GONE);
                    if (mListener != null) {
                        mListener.onDismiss(view);
                    }
//                mCallbacks.onDismiss(mView, mToken);
                    // Reset view presentation
//                setTranslationX(0);
//                lp.height = originalHeight;
//                setLayoutParams(lp);
                }
            });
//        }

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lp.height = (Integer) valueAnimator.getAnimatedValue();
                setLayoutParams(lp);
            }
        });

        animator.start();
    }
}
