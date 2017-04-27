package tyan.hainee.shoppinglist.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import tyan.hainee.shoppinglist.R;
import tyan.hainee.shoppinglist.ShoppingListApplication;
import tyan.hainee.shoppinglist.model.ShoppingItem;
import tyan.hainee.shoppinglist.model.ShoppingList;
import tyan.hainee.shoppinglist.ui.widget.CheckedItemListener;
import tyan.hainee.shoppinglist.ui.widget.PriceWatcher;
import tyan.hainee.shoppinglist.ui.widget.ShoppingItemView;
import tyan.hainee.shoppinglist.ui.widget.SwipeView;
import tyan.hainee.shoppinglist.util.Constants;

public class ShoppingListActivity extends AppCompatActivity {
    private final String TAG = "ShoppingListActivity";

    //Views
    @BindView(R.id.shopping_list)
    LinearLayout mShoppingListView;
    @BindView(R.id.shopping_list_sum)
    TextView mSumView;
    @BindView(R.id.shopping_list_name)
    EditText mNameView;
    @BindView(R.id.shopping_list_activity)
    View mMainView;

    //Shopping list information
    private ShoppingList mShoppingList;
    private RealmList<ShoppingItem> mItems;
    private long mCreatedTime;
    private double mSum;

    //Utils
    private Realm mRealm;
    private DecimalFormat mDecimalFormat;
    private Snackbar mSnackBar;

    //Listeners and watchers
    private AddViewActionListener mAddViewActionListener;
    private PriceWatcher mPriceWatcher;
    private CheckedItemListener mCheckedItemListener;
    private PriceChangeWatcher mPriceChangeWatcher;
    private OnDismissListener mOnDismissListener;
    private ShoppingItemView mDeletedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCreatedTime = System.currentTimeMillis();
        if (intent != null) {
            mCreatedTime = intent.getLongExtra(Constants.SHOPPING_LIST_CREATED_TIME_EXTRA, mCreatedTime);
        }

        mRealm = ((ShoppingListApplication) getApplication()).getRealm();
        mShoppingList = mRealm
                .where(ShoppingList.class)
                .equalTo(Constants.SHOPPING_LIST_CREATED_TIME_NAME, mCreatedTime)
                .findFirst();

        if (mShoppingList == null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    mShoppingList = realm.createObject(ShoppingList.class, System.currentTimeMillis());
                }
            });
        }

        initToolbar();

        mDecimalFormat = new DecimalFormat();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        mDecimalFormat.setGroupingUsed(false);
        mDecimalFormat.setDecimalFormatSymbols(dfs);
        mDecimalFormat.setMaximumFractionDigits(2);

        mSnackBar = Snackbar
                .make(mMainView, "", Snackbar.LENGTH_LONG)
                .setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setAction(R.string.delete_snackbar_action, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mDeletedView != null) {
                            mDeletedView.restore();
                            changeSum(mDeletedView.getPrice(), 0);
                            mDeletedView = null;
                        }
                    }
                });

        String name = mShoppingList.getName().isEmpty() ? "" : mShoppingList.getName();
        mNameView.setText(name);
        mSum = mShoppingList.getSum();
        mSumView.setText(mDecimalFormat.format(mSum));

        mItems = mShoppingList.getItems();

        mAddViewActionListener = new AddViewActionListener();
        mCheckedItemListener = new CheckedItemListener();
        mPriceWatcher = new PriceWatcher();
        mPriceChangeWatcher = new PriceChangeWatcher();
        mOnDismissListener = new OnDismissListener();

        for (ShoppingItem item : mItems) {
            drawShoppingItem(item, -1, false);
        }

        if (mItems.isEmpty()) {
            addShoppingItem(0);
        }
    }

    private void initToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mShoppingList.getStringCreatedDate());
            actionBar.setSubtitle(mShoppingList.getStringCreatedDay());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }

    @Override
    protected void onPause() {
        updateShoppingList();
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (getCurrentFocus() != null) {
            getCurrentFocus().clearFocus();
        }
    }

    public void addShoppingItem(final int position) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mItems.add(position, mRealm.createObject(ShoppingItem.class));
            }
        });
        drawShoppingItem(mItems.get(position), position, true);
    }

    private void drawShoppingItem(ShoppingItem item, int position, boolean setFocus) {
        ShoppingItemView view = new ShoppingItemView(this);
        view.setImeActionListener(mAddViewActionListener);
        view.setCheckedItemListener(mCheckedItemListener);
        view.setPriceWatcher(mPriceWatcher);

        view.setShoppingItem(item);
        if (setFocus) {
            view.setFocus();
        }

        view.setPriceChangeWatcher(mPriceChangeWatcher);
        view.setOnViewDismissListener(mOnDismissListener);
        mShoppingListView.addView(view, position);
    }


    private void updateShoppingList() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (int i = 0; i < mShoppingListView.getChildCount(); i++) {
                    ShoppingItemView itemView = (ShoppingItemView) mShoppingListView.getChildAt(i);
                    if (itemView.getVisibility() == View.GONE) {
                        mItems.remove(itemView.getShoppingItem());
                    }
                    else {
                        itemView.updateShoppingItem();
                    }
                }
                mShoppingList.setName(mNameView.getText().toString());
                mShoppingList.setSum(mItems.sum("mPrice").doubleValue());
            }
        });
    }

    private void changeSum(double addend, double subtrahend) {
        mSum = mSum - subtrahend + addend;
        mSumView.setText(mDecimalFormat.format(mSum));
    }

    private class OnDismissListener implements SwipeView.OnViewDismissListener {
        @Override
        public void onDismiss(View view) {
            if (mShoppingListView.getChildCount() <= 1) {
            }
            changeSum(0, ((ShoppingItemView) view).getPrice());
            mDeletedView = (ShoppingItemView) view;
            mSnackBar.setText("1 item deleted").show();
        }
    }

    private class PriceChangeWatcher implements TextWatcher {
        private final String TAG = "PriceChangeWatcher";

        private double previousAddend;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            try {
                previousAddend = Double.parseDouble(charSequence.toString());
            }
            catch (Exception e) {
                previousAddend = 0;
            }
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            double addend = 0;
            try {
                addend = Double.parseDouble(charSequence.toString());
            }
            catch (Exception e) {
                addend = 0;
            }
            finally {
                changeSum(addend, previousAddend);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    }

    private class AddViewActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView textView, int keyCode, KeyEvent keyEvent) {
            if (keyCode == EditorInfo.IME_ACTION_NEXT) {
                int position = mShoppingListView.indexOfChild(mShoppingListView.getFocusedChild());
                addShoppingItem(position + 1);
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shopping_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_item: {
                addShoppingItem(mShoppingListView.getChildCount());
                return true;
            }
            default : {
                return super.onOptionsItemSelected(item);
            }

        }
    }
}