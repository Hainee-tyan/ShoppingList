package tyan.hainee.shoppinglist;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import tyan.hainee.shoppinglist.model.ShoppingList;
import tyan.hainee.shoppinglist.util.Constants;

public class ShoppingListApplication extends Application {

    private Realm mRealm;
    private RealmConfiguration mConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        mConfig = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        mRealm = Realm.getInstance(mConfig);

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm
                        .where(ShoppingList.class)
                        .equalTo(Constants.SHOPPING_LIST_IS_DELETED_NAME, true)
                        .findAll().deleteAllFromRealm();
            }
        });
    }

    public Realm getRealm() {
        if (mRealm.isClosed()) {
            mRealm = Realm.getInstance(mConfig);
        }
        return mRealm;
    }
}
