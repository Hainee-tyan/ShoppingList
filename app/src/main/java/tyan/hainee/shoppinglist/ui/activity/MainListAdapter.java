package tyan.hainee.shoppinglist.ui.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tyan.hainee.shoppinglist.R;
import tyan.hainee.shoppinglist.model.ShoppingList;
import tyan.hainee.shoppinglist.ui.widget.OnSwipeListener;

class MainListAdapter extends BaseAdapter {

    private final String TAG = "MainListAdapter";

    private LayoutInflater mInflater;
    private List<ShoppingList> mList;
    private DecimalFormat mDF;
    private Context mContext;

    MainListAdapter(Context context, List<ShoppingList> list) {
        mContext = context;
        mList = list;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mDF = new DecimalFormat("0.00");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        mDF.setGroupingUsed(true);
        mDF.setDecimalFormatSymbols(dfs);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.shopping_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        ShoppingList shoppingList = getList(position);
        String listName = shoppingList.getName();

        holder.nameTextView.setText((listName.isEmpty() || listName.equals("")) ?
                mContext.getResources().getString(R.string.default_list_name) : listName);
        holder.dateTextView.setText(shoppingList.getStringCreatedTime());
        holder.sumTextView.setText(mDF.format(shoppingList.getSum()));

        convertView.setOnTouchListener(new OnSwipeListener(100, 100));

        return convertView;
    }

    ShoppingList getList(int position) {
        return ((ShoppingList) getItem(position));
    }

    class ViewHolder {
        @BindView(R.id.shopping_list_name)
        TextView nameTextView;

        @BindView(R.id.shopping_list_date)
        TextView dateTextView;

        @BindView(R.id.shopping_list_sum)
        TextView sumTextView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
