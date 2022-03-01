package com.dayscab.user.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.dayscab.R;
import com.dayscab.databinding.ItemRideHistoryBinding;
import com.dayscab.driver.models.ModelHistory;
import com.dayscab.user.activities.RideDetailsAct;

import java.util.ArrayList;

public class AdapterRideHistory extends RecyclerView.Adapter<AdapterRideHistory.MyRideHolder> {

    Context mContext;
    ArrayList<ModelHistory.Result> historyList;
    String type;

    public AdapterRideHistory(Context mContext, ArrayList<ModelHistory.Result> historyList, String type) {
        this.mContext = mContext;
        this.historyList = historyList;
        this.type = type;
    }

    @NonNull
    @Override
    public MyRideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRideHistoryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.item_ride_history, parent, false);
        return new MyRideHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRideHolder holder, int position) {

        ModelHistory.Result data = historyList.get(position);

        holder.binding.tvFrom.setText(data.getPicuplocation());
        holder.binding.etDestination.setText(data.getDropofflocation());

        if (data.getBooktype().equals("NOW")) {
            holder.binding.tvDateTime.setText(data.getAccept_time());
        } else {
            holder.binding.tvDateTime.setText(data.getPicklaterdate() + " " + data.getPicklatertime());
        }

        holder.binding.tvStatus.setText(data.getStatus());

        if (data.getStatus().equals("Finish")) {
            holder.binding.rlPick.setVisibility(View.VISIBLE);
            holder.binding.rlDrop.setVisibility(View.VISIBLE);
            holder.binding.tvPickupTime.setText(data.getStart_time().split(" ")[1]);
            holder.binding.tvEndTime.setText(data.getEnd_time().split(" ")[1]);
        } else {
            holder.binding.rlPick.setVisibility(View.GONE);
            holder.binding.rlDrop.setVisibility(View.GONE);
        }

        holder.binding.getRoot().setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, RideDetailsAct.class)
                    .putExtra("data", data)
                    .putExtra("type", type)
            );
        });

    }

    @Override
    public int getItemCount() {
        return historyList == null ? 0 : historyList.size();
    }

    public class MyRideHolder extends RecyclerView.ViewHolder {

        ItemRideHistoryBinding binding;

        public MyRideHolder(@NonNull ItemRideHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
