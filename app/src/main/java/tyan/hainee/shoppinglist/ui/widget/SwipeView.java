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
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import tyan.hainee.shoppinglist.R;

public class SwipeView extends RelativeLayout {
    private final String TAG = "SwipeView";

    private LayoutInflater mInflater;

    private int mDeletionColor;
    private int mBackgroundColor;
    private int mCopyColor;

    private View mBackgroundView;
    private View mDeleteIcon;
    private View mCopyIcon;
    private int mIconWidth;

    private float mDownX;
    private float mDownY;
    private boolean mSwiping;
    private float mTranslationX;
    private VelocityTracker mVelocityTracker;
    private OnViewDismissListener mDismissListener;

    private View mSwipingView;
    private int mViewWidth = 1;

    private int mSlop;
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;
    private long mAnimationTime;
    private OnViewCopyListener mCopyListener;

    public SwipeView(Context context) {
        super(context);
    }

    public SwipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.swipe_view, this, true);

        mBackgroundView = findViewById(R.id.background_view);
        mDeleteIcon = findViewById(R.id.delete_icon);
        mCopyIcon = findViewById(R.id.copy_icon);

        mDeletionColor = ContextCompat.getColor(getContext(), R.color.swipeDismissColor);
        mCopyColor = ContextCompat.getColor(getContext(), R.color.swipeCopyColor);
        mBackgroundColor = ContextCompat.getColor(getContext(), R.color.swipeBackgroundColor);

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
                boolean copy = false;

                if (mSwiping) {
                    if (Math.abs(deltaX) > mViewWidth * getSwipeThreshold()) {
                        dismiss = deltaX < 0;
                        copy = deltaX > 0;
                    } else if (absVelocityX >= mMinFlingVelocity
                            && absVelocityX <= mMaxFlingVelocity
                            && absVelocityX > absVelocityY) {
                        dismiss = (velocityX < 0) && (deltaX < 0);
                        copy = (velocityX > 0) && (deltaX > 0);
                    }

                }

                if (dismiss) {
                    mBackgroundView.setBackgroundColor(mDeletionColor);
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
                            .translationX(mIconWidth - mViewWidth)
                            .setDuration(mAnimationTime);
                } else if (mSwiping) {
                    mSwipingView.animate()
                            .translationX(0)
                            .setDuration(mAnimationTime);
                    mDeleteIcon.animate()
                            .translationX(mIconWidth)
                            .setDuration(mAnimationTime);
                    mCopyIcon.animate()
                            .translationX(-mIconWidth)
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
                if (Math.abs(deltaX) > mSlop && Math.abs(deltaY) < Math.abs(deltaX) / 2) {
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

                    mSwipingView.setTranslationX(translationX);
                    mDeleteIcon.setTranslationX(mIconWidth + translationX);
                    mCopyIcon.setTranslationX(-mIconWidth + translationX);
                    if (-deltaX > mViewWidth * getSwipeThreshold()) {
                        mBackgroundView.setBackgroundColor(mDeletionColor);
                    } else if (deltaX > mViewWidth * getSwipeThreshold()) {
                        mBackgroundView.setBackgroundColor(mCopyColor);
                    } else {
                        mBackgroundView.setBackgroundColor(mBackgroundColor);
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
                        .setDuration(mAnimationTime);
                mDeleteIcon.animate()
                        .translationX(mIconWidth)
                        .setDuration(mAnimationTime);
                mCopyIcon.animate()
                        .translationX(-mIconWidth)
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
                if (measuredHeight != mBackgroundView.getMeasuredHeight()) {
                    ViewGroup.LayoutParams lp = mBackgroundView.getLayoutParams();
                    lp.height = measuredHeight;
                    mBackgroundView.setLayoutParams(lp);
                }
            }
        });

        int measuredHeight = mSwipingView.getMeasuredHeight();

        mBackgroundView.getLayoutParams().height = measuredHeight;
        mDeleteIcon.getLayoutParams().height = measuredHeight;
        mDeleteIcon.getLayoutParams().width = measuredHeight;
        mDeleteIcon.setTranslationX(measuredHeight);
        mCopyIcon.getLayoutParams().height = measuredHeight;
        mCopyIcon.getLayoutParams().width = measuredHeight;
        mCopyIcon.setTranslationX(-measuredHeight);
        mIconWidth = measuredHeight;
    }

    public interface OnViewDismissListener {
        void onDismiss(View view);
    }

    public interface OnViewCopyListener {
        void onCopy(View view);
    }

    public void setOnViewDismissListener(OnViewDismissListener listener) {
        mDismissListener = listener;
    }

    public void setOnViewCopyListener(OnViewCopyListener listener) {
        mCopyListener = listener;
    }

    public void restore() {
        setVisibility(View.VISIBLE);
        mSwipingView.setTranslationX(0);

        animateAppearance();
    }

    public void animateAppearance() {
        final ViewGroup.LayoutParams lp = getLayoutParams();
        measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
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

        if (hasFocus()) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        }

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(View.GONE);
                if (mDismissListener != null) {
                    mDismissListener.onDismiss(view);
                }
            }
        });

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
