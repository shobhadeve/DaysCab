package com.dayscab.common.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.dayscab.R;
import com.dayscab.common.models.AccountModel;
import com.dayscab.databinding.AdapterAccountsOptionsBinding;
import com.dayscab.utils.MyApplication;

import java.util.ArrayList;

public class AdapterAccounts extends BaseAdapter {

    Context mContext;
    ArrayList<AccountModel.Result> accountList;

    public AdapterAccounts(Context mContext, ArrayList<AccountModel.Result> accountList) {
        this.mContext = mContext;
        this.accountList = accountList;
    }

    @Override
    public int getCount() {
        return accountList == null ? 0 : accountList.size();
    }

    @Override
    public Object getItem(int position) {
        return accountList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AdapterAccountsOptionsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.adapter_accounts_options, parent, false);

        binding.tvTitle.setText(accountList.get(position).getQuestion());

        binding.ivDetails.setOnClickListener(v -> {
            MyApplication.showAlert(mContext, accountList.get(position).getAnswer());
        });

        return binding.getRoot();

    }


}
