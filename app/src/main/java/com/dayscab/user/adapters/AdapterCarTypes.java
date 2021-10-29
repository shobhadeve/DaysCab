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
import com.dayscab.user.models.ModelCar;

import java.util.ArrayList;

public class AdapterCarTypes extends RecyclerView.Adapter<AdapterCarTypes.MyRideHolder> {

    Context mContext;
    ArrayList<ModelCar> arrayList;
    private onCarSelectListener listener;

    public interface onCarSelectListener {
        void onCarSelected(ModelCar car);
    }

    public AdapterCarTypes(Context context, ArrayList<ModelCar> arrayList) {
        this.mContext = context;
        this.arrayList = arrayList;
    }

    public AdapterCarTypes Callback(onCarSelectListener listener) {
        this.listener = listener;
        return this;
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
        holder.binding.setCar(arrayList.get(position));
        holder.binding.executePendingBindings();

        holder.binding.tvTotal.setText(arrayList.get(position).getTotal() +"/Km");

        holder.binding.getRoot().setOnClickListener(v -> {
            for (int i = 0; i < arrayList.size(); i++) {
                arrayList.get(i).setSelected(false);
            }
            arrayList.get(position).setSelected(true);
            listener.onCarSelected(arrayList.get(position));
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return arrayList == null ? 0 : arrayList.size();
    }

    public class MyRideHolder extends RecyclerView.ViewHolder {

        ItemRideBookBinding binding;

        public MyRideHolder(@NonNull ItemRideBookBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
