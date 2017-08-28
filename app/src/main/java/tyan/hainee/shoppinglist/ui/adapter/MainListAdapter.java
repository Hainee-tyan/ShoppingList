package tyan.hainee.shoppinglist.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tyan.hainee.shoppinglist.R;
import tyan.hainee.shoppinglist.model.ShoppingList;

public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.ViewHolder> {

    private final String TAG = "MainListAdapter";

    private LayoutInflater mInflater;
    private List<ShoppingList> mList;
    private DecimalFormat mDF;
    private Context mContext;

    public MainListAdapter(Context context, List<ShoppingList> list) {
        mContext = context;
        mList = list;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mDF = new DecimalFormat("0.00");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        mDF.setGroupingUsed(true);
        mDF.setDecimalFormatSymbols(dfs);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.shopping_list_item)
        View shoppingListItem;

        @BindView(R.id.shopping_list_name)
        TextView nameTextView;
        @BindView(R.id.shopping_list_date)
        TextView dateTextView;
        @BindView(R.id.shopping_list_sum)
        TextView sumTextView;

        @BindView(R.id.shopping_list_delete_view)
        View deleteView;
        @BindView(R.id.shopping_list_delete_icon)
        View deleteIcon;
        @BindView(R.id.shopping_list_selectable_view)
        View selectableView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.shopping_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ShoppingList shoppingList = getList(position);
        String listName = shoppingList.getName();

        holder.shoppingListItem.setX(0f);

        holder.nameTextView.setText((listName.isEmpty() || listName.equals("")) ?
                mContext.getResources().getString(R.string.default_list_name) : listName);
        holder.dateTextView.setText(shoppingList.getStringCreatedTime());
        holder.sumTextView.setText(mDF.format(shoppingList.getSum()));

        holder.shoppingListItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int measuredHeight = holder.shoppingListItem.getMeasuredHeight();

        holder.deleteView.getLayoutParams().height = measuredHeight;
        holder.deleteIcon.getLayoutParams().height = measuredHeight;
        holder.deleteIcon.getLayoutParams().width = measuredHeight;
        holder.deleteIcon.setTranslationX(measuredHeight);
        holder.selectableView.getLayoutParams().height = measuredHeight;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public ShoppingList getList(int position) {
        return mList.get(position);
    }
}

