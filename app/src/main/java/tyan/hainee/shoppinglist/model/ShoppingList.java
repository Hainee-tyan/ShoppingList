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
    private int mID;
    private String mName;
    private long mCreatedTime;
    private RealmList<ShoppingItem> mItems;
    private double mSum;

    public ShoppingList() {
        mItems = new RealmList<>();
        mCreatedTime = System.currentTimeMillis();
        mName = "";
        mSum = 0d;
    }

    public int getID() {
        return mID;
    }

    public void setID(int id) {
        mID = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
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
}
