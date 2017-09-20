package tyan.hainee.shoppinglist.model;

import java.util.Locale;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.RealmClass;

@RealmClass
public class ShoppingItem extends RealmObject {

    @LinkingObjects("mItems")
    private final RealmResults<ShoppingList> mParent = null;
    private boolean mChecked = false;
    private String mName = "";
    private double mPrice = 0d;
    private boolean mBlackListed = false;

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public double getPrice() {
        return mPrice;
    }

    public void setPrice(double price) {
        mPrice = price;
    }

    public boolean isBlackListed() {
        return mBlackListed;
    }

    public void setBlackListed(boolean black) {
        mBlackListed = black;
    }

    public String toString() {
        return String.format(Locale.getDefault(),
                "Shopping item:\nname - %1$s\nprice - %2$f\nchecked - %3$b",
                mName, mPrice, mChecked);
    }
}