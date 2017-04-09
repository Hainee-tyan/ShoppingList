package tyan.hainee.shoppinglist.ui.widget;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import tyan.hainee.shoppinglist.util.PriceFormatter;

public class PriceWatcher implements TextWatcher, View.OnFocusChangeListener {
    private final String TAG = "PriceWatcher";

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    @Override
    public void afterTextChanged(Editable priceText) {
        int indexOfPoint = priceText.toString().indexOf(".");

        //cut more than 2 symbols after dot
        if (indexOfPoint >= 0 && priceText.length() > indexOfPoint + 3) {
            priceText.delete(indexOfPoint + 3, priceText.length());
        }

        //cut more than 5 symbols before dot
        if (indexOfPoint < 0 && priceText.length() > 4) {
            priceText.delete(4, priceText.length());
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (!b) {
            EditText priceView = (EditText) view;
            Editable priceText = priceView.getText();

            priceView.removeTextChangedListener(this);
            priceText.replace(0, priceText.length(),
                    PriceFormatter.formatPrice(priceText.toString()));
            priceView.addTextChangedListener(this);
        }
    }
}