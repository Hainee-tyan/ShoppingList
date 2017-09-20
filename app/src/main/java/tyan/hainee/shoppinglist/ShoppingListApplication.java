package tyan.hainee.shoppinglist;

import android.app.Application;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import tyan.hainee.shoppinglist.model.ShoppingItem;
import tyan.hainee.shoppinglist.model.ShoppingList;
import tyan.hainee.shoppinglist.util.Constants;

public class ShoppingListApplication extends Application {

    private final String TAG = getClass().getName();
    private Realm mRealm;
    private RealmConfiguration mConfig;

    @Override
    public void onCreate() {
        super.onCreate();

        //STETHO
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());

        //REALM
        Realm.init(this);
        mConfig = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        mRealm = Realm.getInstance(mConfig);

        //TODO: async thread
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(TAG, "Start deleting items, " + System.currentTimeMillis());
                mRealm
                        .where(ShoppingItem.class)
                        .equalTo(Constants.SHOPPING_ITEM_IS_DELETED_NAME, true)
                        .findAll().deleteAllFromRealm();

                Log.d(TAG, "End deleting items, " + System.currentTimeMillis());

                Log.d(TAG, "Start deleting lists, " + System.currentTimeMillis());
                mRealm
                        .where(ShoppingList.class)
                        .equalTo(Constants.SHOPPING_LIST_IS_DELETED_NAME, true)
                        .findAll().deleteAllFromRealm();

                Log.d(TAG, "End deleting items, " + System.currentTimeMillis());
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
