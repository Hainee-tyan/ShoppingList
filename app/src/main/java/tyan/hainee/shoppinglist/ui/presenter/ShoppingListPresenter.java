package tyan.hainee.shoppinglist.ui.presenter;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;
import tyan.hainee.shoppinglist.R;
import tyan.hainee.shoppinglist.ShoppingListApplication;
import tyan.hainee.shoppinglist.model.ShoppingItem;
import tyan.hainee.shoppinglist.model.ShoppingList;
import tyan.hainee.shoppinglist.ui.activity.ShoppingListActivity;
import tyan.hainee.shoppinglist.ui.adapter.ShoppingListAdapter;
import tyan.hainee.shoppinglist.ui.widget.ListInfoDialog;
import tyan.hainee.shoppinglist.ui.widget.ListSortDialog;
import tyan.hainee.shoppinglist.ui.widget.ShoppingItemView;
import tyan.hainee.shoppinglist.util.Constants;

public class ShoppingListPresenter {
    private final String TAG = "ShoppingListPresenter";

    private ShoppingListActivity mActivity;
    private Realm mRealm;
    private long mCreatedTime = System.currentTimeMillis();
    private ShoppingList mList;
    private RealmList<ShoppingItem> mItems;
    private ShoppingListAdapter mAdapter;

    private ListInfoDialog.OnConfirmListener mDialogInfoListener;
    private ListSortDialog.OnConfirmListener mDialogSortListener;
    private DecimalFormat mDF;

    private double mSum;

    public ShoppingListPresenter(ShoppingListActivity activity) {
        mActivity = activity;
    }

    public void init() {
        Intent intent = mActivity.getIntent();
        if (intent != null) {
            mCreatedTime = intent.getLongExtra(Constants.SHOPPING_LIST_CREATED_TIME_EXTRA, mCreatedTime);
        }

        mRealm = ((ShoppingListApplication) mActivity.getApplication()).getRealm();
        mList = mRealm
                .where(ShoppingList.class)
                .equalTo(Constants.SHOPPING_LIST_CREATED_TIME_NAME, mCreatedTime)
                .findFirst();

        if (mList == null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
//                    mCreatedTime = System.currentTimeMillis();
                    mList = mRealm.createObject(ShoppingList.class, mCreatedTime);
                }
            });
        }

        mItems = mList.getItems();
        if (mItems.isEmpty()) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    mItems.add(0, mRealm.createObject(ShoppingItem.class));
                }
            });
        }

        mSum = mList.getSum();

        mDF = new DecimalFormat();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        mDF.setGroupingUsed(false);
        mDF.setDecimalFormatSymbols(dfs);
        mDF.setMaximumFractionDigits(2);

        mDialogInfoListener = new ListInfoDialog.OnConfirmListener() {
            @Override
            public void onConfirm(final String shopName, final long date) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        mList.setShopName(shopName);
                        mList.setShoppingTime(date);
                    }
                });
                mActivity.updateToolbar();
            }
        };

        //TODO make comparator partly
        mDialogSortListener = new ListSortDialog.OnConfirmListener() {
            @Override
            public void onConfirm(final boolean ascendingSort, final String sortBy) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        mAdapter.updateList();
                        Collections.sort(mItems, new Comparator<ShoppingItem>() {
                            @Override
                            public int compare(ShoppingItem o1, ShoppingItem o2) {
                                int compare = 0;
                                switch (sortBy) {
                                    case Constants.SORTING_BY_CHECKING: {
                                        compare = (o1.isChecked() == o2.isChecked()) ? 0 :
                                                (o1.isChecked() ? 1 : -1);
                                        break;
                                    }
                                    case Constants.SORTING_BY_NAME: {
                                        compare = o1.getName().compareTo(o2.getName());
                                        break;
                                    }
                                    case Constants.SORTING_BY_PRICE: {
                                        compare = (o1.getPrice() == o2.getPrice()) ? 0 :
                                                (o1.getPrice() > o2.getPrice() ? 1 : -1);
                                        break;
                                    }
                                }

                                if (!ascendingSort) compare *= -1;
                                return compare;
                            }
                        });
                    }
                });
                mAdapter.notifyDataSetChanged();
            }
        };

        mAdapter = new ShoppingListAdapter(mActivity, mItems);
        mAdapter.setImeActionListener(new ShoppingListAdapter.ImeActionListener() {
            @Override
            public void action(final int position) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        mItems.add(position + 1, mRealm.createObject(ShoppingItem.class));
                        mAdapter.notifyItemInserted(position + 1);
                    }
                });
            }
        });

        mAdapter.setPriceChangeWatcher(new ShoppingListAdapter.PriceChangeWatcher() {
            @Override
            public void onPriceChanged(double previousPrice, double newPrice) {
                mSum = mSum - previousPrice + newPrice;
                mActivity.changeSum(getSum());
            }
        });

        mAdapter.setOnCopyListener(new ShoppingListAdapter.OnCopyListener() {
            @Override
            public void onCopy(final int position, final ShoppingItemView initialItemView) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        ShoppingItem item = mRealm.createObject(ShoppingItem.class);
                        item.setChecked(initialItemView.isItemChecked());
                        item.setName(initialItemView.getItemName());
                        item.setPrice(initialItemView.getItemPrice());

                        mItems.add(position, item);
                    }
                });
                mSum = mSum + mItems.get(position).getPrice();
                mActivity.changeSum(getSum());
                mAdapter.notifyItemInserted(position);
            }
        });

        //TODO: external callback and listener objects
        mAdapter.setOnDismissListener(new ShoppingListAdapter.OnDismissListener() {
            @Override
            public void onDismiss(final int position, final ShoppingItemView initialItemView) {
                mSum = mSum - initialItemView.getItemPrice();
                mActivity.changeSum(getSum());

                String snackBarText = String.format(Locale.getDefault(),
                        mActivity.getResources().getString(R.string.delete_item_snackbar_text),
                        initialItemView.getItemName());
                mActivity.showDeleteSnackBar(snackBarText,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mSum = mSum + initialItemView.getItemPrice();
                                mActivity.changeSum(getSum());
                                mAdapter.restoreView(position, initialItemView);
                            }
                        },
                        new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                super.onDismissed(transientBottomBar, event);
                                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                                    mRealm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            mItems.remove(position);
                                        }
                                    });
                                }
                            }
                        });
            }
        });
    }

    public ShoppingListAdapter getAdapter() {
        return mAdapter;
    }

    public void updateShoppingList() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mAdapter.updateList();
                mList.setName(mActivity.getNameViewText());
                mList.setSum(mItems.sum("mPrice").doubleValue());
            }
        });
    }

    private void addItem(final int position, final ShoppingItem item) {
                mItems.add(position, item);
        mAdapter.notifyItemInserted(position);
    }

    public void addItem(final int position) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                addItem(position, mRealm.createObject(ShoppingItem.class));
            }
        });
    }

    public String getName() {
        return mList.getName().isEmpty() ? "" : mList.getName();
    }

    public String getShopName() {
        return mList.getShopName().isEmpty() ?
                mActivity.getResources().getString(R.string.default_shop_name) : mList.getShopName();
    }

    public String getStringShoppingDate() {
        return mList.getStringShoppingDate();
    }

    public long getShoppingDate() {
        return mList.getShoppingTime();
    }

    public String getSum() {
        return mDF.format(mSum);
    }

    public ListInfoDialog.OnConfirmListener getDialogInfoListener() {
        return mDialogInfoListener;
    }

    public ListSortDialog.OnConfirmListener getDialogSortListener() {
        return mDialogSortListener;
    }
}
