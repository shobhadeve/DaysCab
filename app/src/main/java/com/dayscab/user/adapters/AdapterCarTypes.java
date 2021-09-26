package com.dayscab.user.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.dayscab.R;
import com.dayscab.databinding.ItemRideBookBinding;
import com.dayscab.databinding.ItemRideHistoryBinding;

public class AdapterCarTypes extends RecyclerView.Adapter<AdapterCarTypes.MyRideHolder> {

    Context mContext;

    public AdapterCarTypes(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AdapterCarTypes.MyRideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRideBookBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.item_ride_book, parent, false);
        return new AdapterCarTypes.MyRideHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCarTypes.MyRideHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class MyRideHolder extends RecyclerView.ViewHolder {

        ItemRideBookBinding binding;

        public MyRideHolder(@NonNull ItemRideBookBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
