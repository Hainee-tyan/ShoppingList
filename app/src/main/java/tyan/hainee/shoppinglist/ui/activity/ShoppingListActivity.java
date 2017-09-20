package tyan.hainee.shoppinglist.ui.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tyan.hainee.shoppinglist.R;
import tyan.hainee.shoppinglist.ui.presenter.ShoppingListPresenter;
import tyan.hainee.shoppinglist.ui.widget.ListInfoDialog;
import tyan.hainee.shoppinglist.ui.widget.ListSortDialog;

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

    //Utils
    private ListInfoDialog mDialogInfo;
    private ListSortDialog mDialogSort;
    private ShoppingListPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        ButterKnife.bind(this);

        mPresenter = new ShoppingListPresenter(this);
        mPresenter.init();
        mPresenter.getAdapter().bind(mShoppingListView);

        mNameView.setText(mPresenter.getName());
        mSumView.setText(mPresenter.getSum());

        mDialogInfo = new ListInfoDialog(this);
        mDialogInfo.setOnConfirmListener(mPresenter.getDialogInfoListener());

        mDialogSort = new ListSortDialog(this);
        mDialogSort.setOnConfirmListener(mPresenter.getDialogSortListener());

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialogInfo.show(mPresenter.getShopName(), mPresenter.getShoppingDate());
                }
            });
            setSupportActionBar(toolbar);
        }
        updateToolbar();
    }

    public void updateToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mPresenter.getShopName());
            actionBar.setSubtitle(mPresenter.getStringShoppingDate());
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
        mPresenter.updateShoppingList();
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (getCurrentFocus() != null) {
            getCurrentFocus().clearFocus();
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
                mPresenter.addItem(0);
                return true;
            }
            case R.id.action_sort: {
                mDialogSort.show();
                return true;
            }
            default : {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void changeSum(String sum) {
        mSumView.setText(sum);
    }

    public String getNameViewText() {
        return mNameView.getText().toString();
    }

    public void showDeleteSnackBar(String snackBarText, View.OnClickListener snackBarListener,
                                   Snackbar.Callback callback) {
        Snackbar
                .make(mMainView, snackBarText, Snackbar.LENGTH_LONG)
                .setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setAction(R.string.delete_snackbar_action, snackBarListener)
                .addCallback(callback)
                .show();
    }
}