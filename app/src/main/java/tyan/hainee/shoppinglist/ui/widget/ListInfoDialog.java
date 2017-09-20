package tyan.hainee.shoppinglist.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tyan.hainee.shoppinglist.R;

public class ListInfoDialog  {

    private final String TAG = "ListInfoDialog";
    @BindView(R.id.dialog_button_ok)
    TextView mOkButton;
    @BindView(R.id.dialog_button_cancel)
    TextView mCancelButton;
    @BindView(R.id.dialog_info_today_button)
    TextView mTodayButton;
    @BindView(R.id.dialog_info_edittext_shopname)
    EditText mEditTextShopName;
    @BindView(R.id.dialog_info_datepicker)
    DatePicker mDatePicker;
    private Context mContext;
    private DateTime mToday = new DateTime();

    private AlertDialog mDialog;
    private OnConfirmListener mListener;

    public ListInfoDialog(Context context) {
        mContext = context;
        View dialogView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_list_info, null, false);

        ButterKnife.bind(this, dialogView);
        mDatePicker.requestFocus();
        mDialog = new AlertDialog.Builder(context, R.style.Dialog)
                .setView(dialogView)
                .create();

        setDividerColor();
    }

    @OnClick(R.id.dialog_info_today_button)
    public void todayClick() {
        mDatePicker.updateDate(mToday.getYear(),
                mToday.getMonthOfYear() - 1,
                mToday.getDayOfMonth());
    }

    @OnClick(R.id.dialog_button_cancel)
    public void cancelClick() {
        mDialog.cancel();
        mDatePicker.requestFocus();
    }

    @OnClick(R.id.dialog_button_ok)
    public void okClick() {
        if (mListener != null) {
            DateTime dateTime = new DateTime(mDatePicker.getYear(),
                    mDatePicker.getMonth() + 1,
                    mDatePicker.getDayOfMonth(), 0, 0);
            mListener.onConfirm(mEditTextShopName.getText().toString(),
                    dateTime.getMillis());
        }
        mDialog.cancel();
        mDatePicker.requestFocus();
    }

    private void setDividerColor() {
        try {
            Object parentObject = mDatePicker;
            Field[] fields = mDatePicker.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if ("mDelegate".equals(fields[i].getName())) {
                    fields[i].setAccessible(true);
                    parentObject = fields[i].get(mDatePicker);
                    fields = parentObject.getClass().getDeclaredFields();
                    i = 0;
                    continue;
                }
                if ("mSpinners".equals(fields[i].getName())) {
                    fields[i].setAccessible(true);
                    LinearLayout mSpinners = (LinearLayout) fields[i].get(parentObject);
                    Field[] numberPickerFields = NumberPicker.class.getDeclaredFields();
                    Drawable divider = mContext.getResources().getDrawable(R.drawable.datepicker_divider);
                    for (Field field : numberPickerFields) {
                        if (field.getName().equals("mSelectionDivider")) {
                            field.setAccessible(true);
                            for (int j = 0; j < 3; j++)
                                field.set(mSpinners.getChildAt(j), divider);
                            break;
                        }
                    }
                    break;
                }
            }
        } catch (Exception ex) {
            Log.v(TAG, "Unable to change date dialog style.");
        }
    }

    public void show(String shopName, long date) {
        mEditTextShopName.setText(shopName);
        DateTime dateTime = new DateTime(date);
        mDatePicker.updateDate(dateTime.getYear(), dateTime.getMonthOfYear() - 1, dateTime.getDayOfMonth());
        mDialog.show();
    }

    public void setOnConfirmListener(OnConfirmListener listener) {
        mListener = listener;
    }

    public interface OnConfirmListener {
        void onConfirm(String shopName, long date);
    }
}
