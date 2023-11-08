package com.a2zsuvidhaa.in.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.model.GridModel;
import com.a2zsuvidhaa.in.R;

import java.util.ArrayList;

public class CommissionAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<GridModel> list;

    public CommissionAdapter(Context c, ArrayList<GridModel> list) {
        mContext = c;
        this.list = list;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            GridModel model = list.get(position);

            grid = inflater.inflate(R.layout.list_commission, null, false);
            TextView tv_heading = grid.findViewById(R.id.tv_heading);
            TextView tv_totalSale = grid.findViewById(R.id.tv_totalSale);
            TextView tv_commission = grid.findViewById(R.id.tv_commission);
            TextView tv_charge = grid.findViewById(R.id.tv_charge);
            TextView tv_txnCount = grid.findViewById(R.id.tv_txnCount);

            tv_heading.setText(model.getHeading());
            tv_totalSale.setText(model.getTotalSale());
            tv_commission.setText(model.getCommission());
            if (AppPreference.getInstance(mContext).getRollId() != 4) {
                tv_charge.setText(model.getCharge());
            }
            tv_txnCount.setText(model.getTxnCount());



            if (model.getHeading().equalsIgnoreCase("Pending")) {
                tv_heading.setTextColor(mContext.getResources().getColor(R.color.yellow_dark));
            } else if (model.getHeading().equalsIgnoreCase("Success")) {
                tv_heading.setTextColor(mContext.getResources().getColor(R.color.success));
            } else if (model.getHeading().equalsIgnoreCase("Failure")) {
                tv_heading.setTextColor(mContext.getResources().getColor(R.color.red));
            } else if (model.getHeading().equalsIgnoreCase("PtxnCredit")) {
                tv_heading.setTextColor(mContext.getResources().getColor(R.color.text_color_1));
            } else if (model.getHeading().equalsIgnoreCase("RefundSuc")) {
                tv_heading.setTextColor(mContext.getResources().getColor(R.color.success));
            } else if (model.getHeading().equalsIgnoreCase("RefundAvailable")) {
                tv_heading.setTextColor(mContext.getResources().getColor(R.color.text_color_2));
            } else if (model.getHeading().equalsIgnoreCase("Refunded")) {
                tv_heading.setTextColor(mContext.getResources().getColor(R.color.success));
            }


        } else {
            grid = convertView;
        }

        return grid;
    }
}