package tyan.hainee.shoppinglist.ui.widget;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import tyan.hainee.shoppinglist.R;
import tyan.hainee.shoppinglist.model.ShoppingItem;
import tyan.hainee.shoppinglist.util.PriceFormatter;

public class ShoppingItemView extends SwipeView {
    public final String TAG = "ShoppingItemView";

    private ShoppingItem mShoppingItem;
    private CheckBox mIsChecked;
    private EditText mName;
    private EditText mPrice;

    public ShoppingItemView(Context context) {
        this(context, null, 0);
    }

    public ShoppingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initComponent();
    }

    public ShoppingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initComponent();
    }

    private void initComponent() {
//        setOrientation(LinearLayout.HORIZONTAL);
//        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//
//        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        inflater.inflate(R.layout.shopping_item, this, true);
        setSwipingView(R.layout.shopping_item, R.id.shopping_item);
        mIsChecked = (CheckBox) findViewById(R.id.item_checkbox);
        mName = (EditText) findViewById(R.id.item_name);
        mPrice = (EditText) findViewById(R.id.item_price);
    }

    public void setChecked(boolean isChecked) {
        mIsChecked.setChecked(isChecked);
    }

    public void setName(String name) {
        mName.setText(name);
    }

    public void setPrice(double price) {
        mPrice.setText(PriceFormatter.formatPrice(String.valueOf(price)));
    }

    public double getPrice() {
        return PriceFormatter.priceToDouble(mPrice.getText().toString());
    }

    public void setShoppingItem(ShoppingItem shoppingItem) {
        mShoppingItem = shoppingItem;
        updateFieldsByItem();
    }

    public ShoppingItem getShoppingItem() {
//        updateShoppingItem();
        return mShoppingItem;
    }

    public boolean updateShoppingItem() {
        if (mShoppingItem == null) {
            return false;
        }
        mShoppingItem.setChecked(mIsChecked.isChecked());
        mShoppingItem.setName(mName.getText().toString());
        mShoppingItem.setPrice(PriceFormatter.priceToDouble(mPrice.getText().toString()));
        return true;
    }

    private void updateFieldsByItem() {
        setPrice(mShoppingItem.getPrice());
        setName(mShoppingItem.getName());
        setChecked(mShoppingItem.isChecked());
    }

    public void setImeActionListener(TextView.OnEditorActionListener listener) {
        mName.setOnEditorActionListener(listener);
        mPrice.setOnEditorActionListener(listener);
    }

    public void setFocus() {
        mName.requestFocus();
    }

    public void setPriceChangeWatcher(TextWatcher priceChangeWatcher) {
        mPrice.addTextChangedListener(priceChangeWatcher);
    }

    public void setPriceWatcher(PriceWatcher watcher) {
        mPrice.addTextChangedListener(watcher);
        mPrice.setOnFocusChangeListener(watcher);
    }

    public void setCheckedItemListener(CheckedItemListener listener) {
        mIsChecked.setOnCheckedChangeListener(listener);
    }

    public void setOnDeleteActionListener(OnKeyListener listener) {
        mName.setOnKeyListener(listener);
    }
}