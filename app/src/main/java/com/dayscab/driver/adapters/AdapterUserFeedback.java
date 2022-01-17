package com.dayscab.driver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dayscab.R;
import com.dayscab.databinding.AdapterUsersFeedbackBinding;
import com.dayscab.driver.models.ModelReviews;

import java.util.ArrayList;

public class AdapterUserFeedback extends RecyclerView.Adapter<AdapterUserFeedback.MyRideHolder> {

    Context mContext;
    ArrayList<ModelReviews.Result> reviewsList;

    public AdapterUserFeedback(Context mContext, ArrayList<ModelReviews.Result> reviewsList) {
        this.mContext = mContext;
        this.reviewsList = reviewsList;
    }

    @NonNull
    @Override
    public AdapterUserFeedback.MyRideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterUsersFeedbackBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.adapter_users_feedback, parent, false);
        return new AdapterUserFeedback.MyRideHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUserFeedback.MyRideHolder holder, int position) {

        ModelReviews.Result data = reviewsList.get(position);

        Glide.with(mContext).load(data.getImage())
                .placeholder(R.drawable.user_ic)
                .error(R.drawable.user_ic)
                .into(holder.binding.ivUserPic);

        holder.binding.tvComment.setText(data.getReview());
        holder.binding.tvName.setText(data.getUser_name());

        try {
            holder.binding.tvRating.setRating(Float.parseFloat(data.getRating()));
        } catch (Exception e) {}

    }

    @Override
    public int getItemCount() {
        return reviewsList == null ? 0 : reviewsList.size();
    }

    public class MyRideHolder extends RecyclerView.ViewHolder {

        AdapterUsersFeedbackBinding binding;

        public MyRideHolder(@NonNull AdapterUsersFeedbackBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
