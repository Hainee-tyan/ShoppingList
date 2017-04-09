package tyan.hainee.shoppinglist;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

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
    }

    public Realm getRealm() {
        if (mRealm.isClosed()) {
            mRealm = Realm.getInstance(mConfig);
        }
        return mRealm;
    }
}
