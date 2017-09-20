package tyan.hainee.shoppinglist.ui.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import java.util.List;

import tyan.hainee.shoppinglist.model.ShoppingItem;
import tyan.hainee.shoppinglist.ui.widget.CheckedItemListener;
import tyan.hainee.shoppinglist.ui.widget.PriceWatcher;
import tyan.hainee.shoppinglist.ui.widget.ShoppingItemView;
import tyan.hainee.shoppinglist.ui.widget.SwipeView;

public class ShoppingListAdapter {
    private final String TAG = "ShoppingListAdapter";
    private Context mContext;
    private ViewGroup mParent;
    private List<ShoppingItem> mItems;
    private CheckedItemListener mCheckedItemListener = new CheckedItemListener();
    private PriceWatcher mPriceWatcher = new PriceWatcher();
    private TextWatcher mPriceChangeWatcher;
    private TextView.OnEditorActionListener mImeActionListener;
    private SwipeView.OnViewCopyListener mOnViewCopyListener;
    private SwipeView.OnViewDismissListener mOnViewDismissListener;
    public ShoppingListAdapter(Context context, List<ShoppingItem> items) {
        mContext = context;
        mItems = items;
    }

    public void bind(ViewGroup parent) {
        mParent = parent;
        for (ShoppingItem item : mItems) {
            drawShoppingItem(item, -1, false, false);
        }
    }

    private void drawShoppingItem(ShoppingItem item, int position, boolean showKeyboard, boolean animate) {
        ShoppingItemView view = new ShoppingItemView(mContext);
        view.setImeActionListener(mImeActionListener);
        view.setCheckedItemListener(mCheckedItemListener);
        view.setPriceWatcher(mPriceWatcher);

        view.setShoppingItem(item);

        view.setPriceChangeWatcher(mPriceChangeWatcher);
        view.setOnViewDismissListener(mOnViewDismissListener);
        view.setOnViewCopyListener(mOnViewCopyListener);
        mParent.addView(view, position);

        if (animate) {
            view.animateAppearance();
        }

        if (showKeyboard) {
            view.showKeyboard();
        }
    }

    public void restoreView(int position, ShoppingItemView view) {
        mParent.addView(view, position);
        view.restore();
    }

    public void updateList() {
        for (int i = 0; i < mParent.getChildCount(); i++) {
            ((ShoppingItemView) mParent.getChildAt(i)).updateShoppingItem();
        }
    }

    public void notifyItemInserted(int position) {
        drawShoppingItem(mItems.get(position), position, true, true);
    }

    public void notifyItemRemoved(int position) {
    }

    public void notifyDataSetChanged() {
        mParent.removeAllViews();
        for (ShoppingItem item : mItems) {
            drawShoppingItem(item, -1, false, false);
        }
    }

    public void notifyItemChanged(int position) {

    }

    public void notifyItemMoved(int fromPosition, int toPosition) {

    }

    public void setImeActionListener(final ImeActionListener listener) {
        mImeActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    listener.action(mParent.indexOfChild(mParent.getFocusedChild()));
                    return true;
                }
                return false;
            }
        };
    }

    public void setPriceChangeWatcher(final PriceChangeWatcher watcher) {
        mPriceChangeWatcher = new TextWatcher() {
            private double previousPrice;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    previousPrice = Double.parseDouble(charSequence.toString());
                }
                catch (Exception e) {
                    previousPrice = 0;
                }
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                double newPrice = 0;
                try {
                    newPrice = Double.parseDouble(charSequence.toString());
                }
                catch (Exception e) {
                    newPrice = 0;
                }
                finally {
                    watcher.onPriceChanged(previousPrice, newPrice);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        };
    }

    public void setOnCopyListener(final OnCopyListener listener) {
        mOnViewCopyListener = new SwipeView.OnViewCopyListener() {
            @Override
            public void onCopy(View view) {
                listener.onCopy(mParent.indexOfChild(view), (ShoppingItemView) view);
            }
        };
    }

    public void setOnDismissListener(final OnDismissListener listener) {
        mOnViewDismissListener = new SwipeView.OnViewDismissListener() {
            @Override
            public void onDismiss(View view) {
                int deletedPosition = mParent.indexOfChild(view);
                mParent.removeViewAt(deletedPosition);

                listener.onDismiss(deletedPosition, (ShoppingItemView) view);
            }
        };
    }

    public interface ImeActionListener {
        public void action(int position);
    }

    public interface PriceChangeWatcher {
        public void onPriceChanged(double previousPrice, double newPrice);
    }

    public interface OnCopyListener {
        public void onCopy(int position, ShoppingItemView initialItemView);
    }

    public interface OnDismissListener {
        public void onDismiss(int position, ShoppingItemView initialItemView);
    }
}
