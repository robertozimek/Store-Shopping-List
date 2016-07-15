package me.robertozimek.android.storeshoppinglist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder> {
    private Context mContext;
    private final List<ShoppingItem> mShoppingItems;
    private final ShoppingListFragment.OnShoppingListFragmentInteractionListener mListener;

    public ShoppingItemAdapter(List<ShoppingItem> items, ShoppingListFragment.OnShoppingListFragmentInteractionListener listener, Context context) {
        mShoppingItems = items;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopping_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mShoppingItems.get(position);
        holder.itemNameTextView.setText(holder.mItem.getItemName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onShoppingItemFragmentInteraction(holder.mItem);
                }
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Opens alert dialog asking if the user wants to delete or update item
                String[] options = {"Delete", "Update"};

                holder.deleteAlert.setTitle("Pick Option");
                holder.deleteAlert.setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                deleteItem(holder.mItem, position);
                                break;
                            case 1:
                                updateItem(holder.mItem);
                                break;
                        }
                    }
                });
                holder.deleteAlert.create();
                holder.deleteAlert.show();

                return true;
            }
        });
    }

    public void deleteItem(ShoppingItem item, int position) {
        if(ShoppingItemUtil.removeItem(item, mContext)) {
            mShoppingItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
        }
    }

    public void updateItem(ShoppingItem item) {
        mListener.openUpdateItemFragment(item);
    }


    @Override
    public int getItemCount() {
        return mShoppingItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView itemNameTextView;
        public ShoppingItem mItem;
        public AlertDialog.Builder deleteAlert;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            itemNameTextView = (TextView) view.findViewById(R.id.item_textview);
            deleteAlert = new AlertDialog.Builder(view.getContext());
        }
    }
}

