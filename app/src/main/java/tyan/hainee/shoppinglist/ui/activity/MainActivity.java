package tyan.hainee.shoppinglist.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tyan.hainee.shoppinglist.R;
import tyan.hainee.shoppinglist.ui.presenter.MainPresenter;
import tyan.hainee.shoppinglist.ui.widget.ItemSwipeCallback;
import tyan.hainee.shoppinglist.util.Constants;
import tyan.hainee.shoppinglist.util.ItemClickSupport;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    @BindView(R.id.activity_main_view)
    View mMainView;
    @BindView(R.id.list)
    RecyclerView mListView;
    @BindView(R.id.empty_list_view)
    TextView mEmptyView;

    private MainPresenter mPresenter;
    private Snackbar mSnackBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPresenter = new MainPresenter(this);
        mPresenter.init();
        initRecyclerView();

        mSnackBar = Snackbar
                .make(mMainView, "", Snackbar.LENGTH_LONG)
                .setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                    }
                })
                .setAction(R.string.delete_snackbar_action, mPresenter.getOnRestoreListener());
        findViewById(R.id.fab).setOnClickListener(mPresenter.getOnAddListListener());
    }

    private void initRecyclerView() {
        mListView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.horizontal_divider));
        mListView.addItemDecoration(dividerItemDecoration);
        mListView.setAdapter(mPresenter.getAdapter());

        mListView.setItemAnimator(new DefaultItemAnimator() {
            @Override
            public void onAnimationFinished(RecyclerView.ViewHolder viewHolder) {
                checkIfEmpty();
            }
        });

        ItemClickSupport.addTo(mListView).setOnItemClickListener(mPresenter.getOnItemClickListener());
        (new ItemTouchHelper(new ItemSwipeCallback(this, mPresenter.getOnSwipeListener()))).attachToRecyclerView(mListView);

        checkIfEmpty();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == Constants.SHOPPING_LIST_REQUEST_CODE ) {
            mPresenter.getAdapter().notifyDataSetChanged();
            checkIfEmpty();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
    }

    public void startShoppingListActivity(long createdTime) {
        Intent intent = new Intent(getApplicationContext(), ShoppingListActivity.class);
        intent.putExtra(Constants.SHOPPING_LIST_CREATED_TIME_EXTRA, createdTime);
        startActivityForResult(intent, Constants.SHOPPING_LIST_REQUEST_CODE);
    }

    public void checkIfEmpty() {
        boolean isEmpty = mPresenter.isEmpty();
        mListView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        mEmptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    public void showSnackBar(String text) {
        mSnackBar.setText(text).show();
    }
}