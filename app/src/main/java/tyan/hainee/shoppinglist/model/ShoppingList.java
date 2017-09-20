package tyan.hainee.shoppinglist.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class ShoppingList extends RealmObject {

    @PrimaryKey
    private long mCreatedTime;

    private long mShoppingTime = System.currentTimeMillis();
    private String mShopName = "";
    private String mName = "";
    private RealmList<ShoppingItem> mItems = new RealmList<>();
    private double mSum = 0d;
    private boolean mIsDeleted = false;

    public long getCreatedTime() {
        return mCreatedTime;
    }

//    public void setCreatedTime(long time) {
//        mCreatedTime = time;
//    }

    public long getShoppingTime() {
        return mShoppingTime;
    }

    public void setShoppingTime(long time) {
        mShoppingTime = time;
    }

    public String getShopName() {
        return mShopName;
    }

    public void setShopName(String name) {
        mShopName = name;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public RealmList<ShoppingItem> getItems() {
        return mItems;
    }

    public double getSum() {
        return mSum;
    }

    public void setSum(double sum) {
        mSum = sum;
    }

//    public boolean isDeleted() {
//        return mIsDeleted;
//    }

    public void setDeleted(boolean isDeleted) {
        mIsDeleted = isDeleted;
    }

    public String getStringCreatedTime() {
        return new SimpleDateFormat("dd MMM yyyy, kk:mm:ss", Locale.getDefault())
                .format(new Date(mCreatedTime));
    }

    public String getStringShoppingDate() {
        return new SimpleDateFormat("dd MMM yyyy, EEEE", Locale.getDefault())
                .format(new Date(mShoppingTime));
    }

    public String getStringShoppintDateShort() {
        return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                .format(new Date(mShoppingTime));
    }

    public String getStringCreatedDay() {
        return new SimpleDateFormat("EEEE kk:mm:ss", Locale.getDefault())
                .format(new Date(mCreatedTime));
    }
}
