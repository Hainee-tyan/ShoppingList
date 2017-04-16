package tyan.hainee.shoppinglist.model;

import java.util.Locale;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class ShoppingItem extends RealmObject {

    private boolean mChecked;
    private String mName;
    private double mPrice;

    public ShoppingItem() {
        this.mChecked = false;
        this.mName = "";
        this.mPrice = 0;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        this.mChecked = checked;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public double getPrice() {
        return mPrice;
    }

    public void setPrice(double price) {
        this.mPrice = price;
    }

    public String toString() {
        return String.format(Locale.getDefault(),
                "Shopping item:\nname - %1$s\nprice - %2$f\nchecked - %3$b",
                mName, mPrice, mChecked);
    }
}