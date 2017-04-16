package tyan.hainee.shoppinglist.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import tyan.hainee.shoppinglist.R;
import tyan.hainee.shoppinglist.ShoppingListApplication;
import tyan.hainee.shoppinglist.model.ShoppingList;
import tyan.hainee.shoppinglist.util.Constants;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    @BindView(R.id.list)
    ListView mListView;

    private MainListAdapter mAdapter;
    private int mMaxID = -1;
    private Realm mRealm;
    private RealmResults<ShoppingList> mListArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mRealm = ((ShoppingListApplication) getApplication()).getRealm();
        mListArray = mRealm.where(ShoppingList.class)
                .findAllSorted(Constants.SHOPPING_LIST_CREATED_TIME_NAME, Sort.DESCENDING);

        if (mListArray != null && mListArray.size() > 0 && mListArray.first() != null) {
            mMaxID = mListArray.first().getID();
        }

        mAdapter = new MainListAdapter(this, mListArray);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(findViewById(R.id.empty_list_view));
    }

    @OnItemClick(R.id.list)
    void onItemClick(AdapterView<?> adapterView, int position) {
        startShoppingListActivity(mAdapter.getList(position).getID());
    }

    @OnClick(R.id.fab)
    void addNewList() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.createObject(ShoppingList.class, ++mMaxID);
            }
        });
        startShoppingListActivity(mMaxID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == Constants.SHOPPING_LIST_REQUEST_CODE ) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    private void startShoppingListActivity(int id) {
        Intent intent = new Intent(getApplicationContext(), ShoppingListActivity.class);
        intent.putExtra(Constants.SHOPPING_LIST_ID_EXTRA, id);
        startActivityForResult(intent, Constants.SHOPPING_LIST_REQUEST_CODE);
    }
}
