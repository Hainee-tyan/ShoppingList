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
    private String mName;
    private RealmList<ShoppingItem> mItems;
    private double mSum;
    private boolean mIsDeleted;

    public ShoppingList() {
        mItems = new RealmList<>();
        mName = "";
        mSum = 0d;
        mIsDeleted = false;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setCreatedTime(long time) {
        mCreatedTime = time;
    }

    public long getCreatedTime() {
        return mCreatedTime;
    }

    public String getStringCreatedTime() {
        return new SimpleDateFormat("dd MMM yyyy, kk:mm:ss", Locale.getDefault())
                .format(new Date(mCreatedTime));
    }

    public String getStringCreatedDate() {
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(new Date(mCreatedTime));
    }

    public String getStringCreatedDay() {
        return new SimpleDateFormat("EEEE kk:mm:ss", Locale.getDefault())
                .format(new Date(mCreatedTime));
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

    public void setDeleted(boolean isDeleted) {
        mIsDeleted = isDeleted;
    }

    public boolean isDeleted() {
        return mIsDeleted;
    }
}
