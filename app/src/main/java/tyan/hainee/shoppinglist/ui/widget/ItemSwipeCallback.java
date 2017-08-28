package tyan.hainee.shoppinglist.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import tyan.hainee.shoppinglist.R;

public class ItemSwipeCallback extends ItemTouchHelper.Callback {

    private final String TAG = "ItemSwipeCallback";

    private int mDeletionColor;
    private int mNonDeletionColor;

    private RecyclerView.ViewHolder mViewHolder;
    private View mSwipingView;
    private View mDeleteView;
    private View mDeleteIcon;
    private int mDeleteIconWidth;
    private float mDeleteThreshold;
    private OnSwipeListener mListener;


    public ItemSwipeCallback(Context context, OnSwipeListener listener) {
        mDeletionColor = ContextCompat.getColor(context, R.color.swipeDismissColor);
        mNonDeletionColor = ContextCompat.getColor(context, R.color.swipeBackgroundColor);

        mListener = listener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = 0;
        int swipeFlags = ItemTouchHelper.LEFT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mListener.onSwiped(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (mViewHolder != viewHolder) {
            mViewHolder = viewHolder;
            mSwipingView = viewHolder.itemView.findViewById(R.id.shopping_list_item);
            mDeleteView = viewHolder.itemView.findViewById(R.id.shopping_list_delete_view);
            mDeleteIcon = viewHolder.itemView.findViewById(R.id.shopping_list_delete_icon);
            mDeleteIconWidth = mDeleteIcon.getMeasuredWidth();

            mDeleteThreshold = mSwipingView.getWidth() * getSwipeThreshold(viewHolder);
        }

        if (dX < -mDeleteThreshold) {
            mDeleteView.setBackgroundColor(mDeletionColor);
        }
        else {
            mDeleteView.setBackgroundColor(mNonDeletionColor);
        }

        mSwipingView.setTranslationX(dX);
        mDeleteIcon.setTranslationX(mDeleteIconWidth + dX);
    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return .25f;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    public interface OnSwipeListener {
        void onSwiped(int position);
    }
}