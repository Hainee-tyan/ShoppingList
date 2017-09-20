package tyan.hainee.shoppinglist.ui.widget;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
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

    public boolean isItemChecked() {
        return mIsChecked.isChecked();
    }

    public void setItemChecked(boolean isChecked) {
        mIsChecked.setChecked(isChecked);
    }

    public String getItemName() {
        return mName.getText().toString();
    }

    public void setItemName(String name) {
        mName.setText(name);
    }

    public double getItemPrice() {
        return PriceFormatter.priceToDouble(mPrice.getText().toString());
    }

    public void setItemPrice(double price) {
        mPrice.setText(PriceFormatter.formatPrice(String.valueOf(price)));
    }

    public ShoppingItem getShoppingItem() {
        return mShoppingItem;
    }

    public void setShoppingItem(ShoppingItem shoppingItem) {
        mShoppingItem = shoppingItem;
        updateFieldsByItem();
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
        setItemPrice(mShoppingItem.getPrice());
        setItemName(mShoppingItem.getName());
        setItemChecked(mShoppingItem.isChecked());
    }

    public void setImeActionListener(TextView.OnEditorActionListener listener) {
        mName.setOnEditorActionListener(listener);
        mPrice.setOnEditorActionListener(listener);
    }

    public void showKeyboard() {
        mName.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mName, InputMethodManager.SHOW_IMPLICIT);
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