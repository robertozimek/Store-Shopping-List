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

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {
    private Context mContext;
    private final List<Store> mStoreList;
    private final me.robertozimek.android.storeshoppinglist.StoreListFragment.OnStoreFragmentInteractionListener mListener;

    public StoreAdapter(List<Store> items, me.robertozimek.android.storeshoppinglist.StoreListFragment.OnStoreFragmentInteractionListener listener, Context context) {
        mStoreList = items;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)  {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mStore = mStoreList.get(position);
        holder.storeNameTextView.setText(holder.mStore.getStoreName());
        holder.streetTextView.setText(holder.mStore.getStreetAddress());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onStoreFragmentInteraction(holder.mStore);
                }
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.deleteAlert.setTitle("Attention");
                holder.deleteAlert.setMessage("Are you sure you want to delete store?");
                holder.deleteAlert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteStore(holder.mStore, position);
                    }
                });
                holder.deleteAlert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Wait until clicked
                    }
                });

                AlertDialog dialog = holder.deleteAlert.create();
                dialog.show();
                return true;
            }
        });
    }

    public void deleteStore(Store store, int position) {
        if(StoreUtil.removeStore(mContext, store)) {
            mStoreList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
        }
    }


    @Override
    public int getItemCount() {
        return mStoreList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView storeNameTextView;
        public final TextView streetTextView;
        public Store mStore;
        public AlertDialog.Builder deleteAlert;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            storeNameTextView = (TextView) view.findViewById(R.id.store_textview);
            streetTextView = (TextView) view.findViewById(R.id.street_textview);
            deleteAlert = new AlertDialog.Builder(view.getContext());
        }
    }
}
