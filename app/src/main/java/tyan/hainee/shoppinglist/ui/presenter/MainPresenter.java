package tyan.hainee.shoppinglist.ui.presenter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import tyan.hainee.shoppinglist.R;
import tyan.hainee.shoppinglist.ShoppingListApplication;
import tyan.hainee.shoppinglist.model.ShoppingList;
import tyan.hainee.shoppinglist.ui.activity.MainActivity;
import tyan.hainee.shoppinglist.ui.adapter.MainListAdapter;
import tyan.hainee.shoppinglist.ui.widget.ItemSwipeCallback;
import tyan.hainee.shoppinglist.util.Constants;
import tyan.hainee.shoppinglist.util.ItemClickSupport;

public class MainPresenter {
    private final String TAG = "MainPresenter";

    private MainActivity mActivity;
    private Realm mRealm;
    private MainListAdapter mAdapter;
    private int mDeletedListPosition = -1;
    private ShoppingList mDeletedList;

    private ItemClickSupport.OnItemClickListener mOnItemClickListener = new ItemClickSupport.OnItemClickListener() {
        @Override
        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
            mActivity.startShoppingListActivity(mAdapter.getList(position).getCreatedTime());
        }
    };
    private ItemSwipeCallback.OnSwipeListener mOnSwipeListener = new ItemSwipeCallback.OnSwipeListener() {
        @Override
        public void onSwiped(final int position) {
            mDeletedListPosition = position;
            mDeletedList = mAdapter.getList(position);

            String listName = mAdapter.getList(position).getName();
            listName = (listName.isEmpty() || listName.equals("")) ?
                    mActivity.getResources().getString(R.string.default_list_name) : listName;

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    mDeletedList.setDeleted(true);
                }
            });
            mAdapter.notifyItemRemoved(position);

            String snackBarText = String.format(Locale.getDefault(),
                    mActivity.getResources().getString(R.string.delete_snackbar_text),
                    listName);
            mActivity.showSnackBar(snackBarText);
        }
    };
    private View.OnClickListener mOnRestoreListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mDeletedListPosition < 0 || mDeletedList == null) return;

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    mDeletedList.setDeleted(false);
                }
            });

            mActivity.checkIfEmpty();
            mAdapter.notifyItemInserted(mDeletedListPosition);

            mDeletedListPosition = -1;
            mDeletedList = null;
        }
    };
    private View.OnClickListener mOnAddListListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final long createdTime = System.currentTimeMillis();
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.createObject(ShoppingList.class, createdTime);
                }
            });
            mActivity.startShoppingListActivity(createdTime);
        }
    };

    public MainPresenter(MainActivity activity) {
        mActivity = activity;
    }

    public void init() {
        mRealm = ((ShoppingListApplication) mActivity.getApplication()).getRealm();
        RealmResults<ShoppingList> listArray = mRealm.where(ShoppingList.class)
                .equalTo(Constants.SHOPPING_LIST_IS_DELETED_NAME, false)
                .findAllSorted(Constants.SHOPPING_LIST_CREATED_TIME_NAME, Sort.DESCENDING);
        mAdapter = new MainListAdapter(mActivity, listArray);
    }

    public void detach() {
        mRealm.close();
    }

    public boolean isEmpty() {
        return (mAdapter == null || mAdapter.getItemCount() < 1);
    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    public ItemClickSupport.OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public ItemSwipeCallback.OnSwipeListener getOnSwipeListener() {
        return mOnSwipeListener;
    }

    public View.OnClickListener getOnRestoreListener() {
        return mOnRestoreListener;
    }

    public View.OnClickListener getOnAddListListener() {
        return mOnAddListListener;
    }
}