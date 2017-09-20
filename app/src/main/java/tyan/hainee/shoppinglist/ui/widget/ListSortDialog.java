package tyan.hainee.shoppinglist.ui.widget;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tyan.hainee.shoppinglist.R;
import tyan.hainee.shoppinglist.util.Constants;

public class ListSortDialog  {

    private final String TAG = "ListSortDialog";
    @BindView(R.id.dialog_button_ok)
    TextView mOkButton;
    @BindView(R.id.dialog_button_cancel)
    TextView mCancelButton;
    @BindView(R.id.dialog_sort_ascending)
    RadioButton mAscendingButton;
    @BindView(R.id.dialog_sort_descending)
    RadioButton mDescendingButton;
    @BindView(R.id.dialog_sort_byChecking)
    RadioButton mByCheckingButton;
    @BindView(R.id.dialog_sort_byName)
    RadioButton mByNameButton;
    @BindView(R.id.dialog_sort_byPrice)
    RadioButton mByPriceButton;
    private Context mContext;
    private AlertDialog mDialog;
    private OnConfirmListener mListener;

    public ListSortDialog(Context context) {
        mContext = context;
        View dialogView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_sorting, null, false);

        ButterKnife.bind(this, dialogView);

        mDialog = new AlertDialog.Builder(context, R.style.Dialog)
                .setView(dialogView)
                .create();
    }

    @OnClick(R.id.dialog_button_cancel)
    public void cancelClick() {
        mDialog.cancel();
    }

    @OnClick(R.id.dialog_button_ok)
    public void okClick() {
        if (mListener != null) {
            String sortBy = null;
            if (mByCheckingButton.isChecked())
                sortBy = Constants.SORTING_BY_CHECKING;
            else if (mByNameButton.isChecked())
                sortBy = Constants.SORTING_BY_NAME;
            else if (mByPriceButton.isChecked())
                sortBy = Constants.SORTING_BY_PRICE;
            mListener.onConfirm(mAscendingButton.isChecked(), sortBy);
        }
        mDialog.cancel();
    }

    public void show() {
        mAscendingButton.setChecked(true);
        mByCheckingButton.setChecked(true);
        mDialog.show();
    }

    public void setOnConfirmListener(OnConfirmListener listener) {
        mListener = listener;
    }

    public interface OnConfirmListener {
        void onConfirm(boolean ascendingSort, String sortBy);
    }
}
