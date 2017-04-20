package tyan.hainee.shoppinglist.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import tyan.hainee.shoppinglist.R;
import tyan.hainee.shoppinglist.ShoppingListApplication;
import tyan.hainee.shoppinglist.model.ShoppingList;
import tyan.hainee.shoppinglist.ui.adapter.RecyclerViewAdapter;
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

    private RecyclerViewAdapter mAdapter;
    private Realm mRealm;
    private RealmResults<ShoppingList> mListArray;

    private ShoppingList mDeletedList;

    private Snackbar mSnackBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mRealm = ((ShoppingListApplication) getApplication()).getRealm();
        mListArray = mRealm.where(ShoppingList.class)
                .equalTo(Constants.SHOPPING_LIST_IS_DELETED_NAME, false)
                .findAllSorted(Constants.SHOPPING_LIST_CREATED_TIME_NAME, Sort.DESCENDING);

        mListView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.horizontal_divider));
        mListView.addItemDecoration(dividerItemDecoration);

        mAdapter = new RecyclerViewAdapter(this, mListArray);
        mListView.setAdapter(mAdapter);

        ItemClickSupport.addTo(mListView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                startShoppingListActivity(mAdapter.getList(position).getCreatedTime());
            }
        });

        (new ItemTouchHelper(new ItemSwipeCallback(this))).attachToRecyclerView(mListView);

        mSnackBar = Snackbar
                .make(mMainView, "", Snackbar.LENGTH_LONG)
                .setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setAction(R.string.delete_snackbar_action, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        restoreList();
                    }
                });
        checkIfEmpty();
    }

    @OnClick(R.id.fab)
    void addNewList() {
        final long createdTime = System.currentTimeMillis();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.createObject(ShoppingList.class, createdTime);
            }
        });
        startShoppingListActivity(createdTime);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == Constants.SHOPPING_LIST_REQUEST_CODE ) {
            mAdapter.notifyDataSetChanged();
            checkIfEmpty();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    private void startShoppingListActivity(long createdTime) {
        Intent intent = new Intent(getApplicationContext(), ShoppingListActivity.class);
        intent.putExtra(Constants.SHOPPING_LIST_CREATED_TIME_EXTRA, createdTime);
        startActivityForResult(intent, Constants.SHOPPING_LIST_REQUEST_CODE);
    }

    private void checkIfEmpty() {
        if (mListArray == null || mListArray.isEmpty()) {
            mListView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
        else {
            mListView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    public void deleteList(int position) {
        mDeletedList = mListArray.get(position);

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mDeletedList.setDeleted(true);
            }
        });
        mAdapter.notifyItemRemoved(position);
        checkIfEmpty();

        String listName = mDeletedList.getName();
        listName = (listName.isEmpty() || listName.equals("")) ?
                getResources().getString(R.string.default_list_name) : listName;

        String snackBarText = String.format(Locale.getDefault(),
                getResources().getString(R.string.delete_snackbar_text),
                listName);

        mSnackBar.setText(snackBarText).show();
    }

    private void restoreList() {
        if (mDeletedList == null) return;

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mDeletedList.setDeleted(false);
            }
        });
        mAdapter.notifyItemInserted(mListArray.indexOf(mDeletedList));
        checkIfEmpty();
        mDeletedList = null;
    }
}