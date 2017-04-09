package tyan.hainee.shoppinglist.ui.widget;

import android.text.Editable;
import android.text.Spannable;
import android.text.style.StrikethroughSpan;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import tyan.hainee.shoppinglist.R;

public class CheckedItemListener implements CompoundButton.OnCheckedChangeListener {

    private StrikethroughSpan span = new StrikethroughSpan();

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        EditText name = (EditText) ((LinearLayout) compoundButton.getParent()).findViewById(R.id.item_name);
        Editable text = name.getText();
        int selectionStart = name.getSelectionStart();
        int selectionEnd = name.getSelectionEnd();

        if (checked) {
            text.setSpan(span, 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        else {
            text.removeSpan(span);
        }
        name.setText(text);
        name.setSelection(selectionStart, selectionEnd);
    }
}
