package com.a2zsuvidhaa.in.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.a2zsuvidhaa.in.listener.FundTransferListener;
import com.a2zsuvidhaa.in.model.FundTransfer;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.AppPreference;


import java.util.ArrayList;
import java.util.List;


public class FundTransferAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final int TRANSFER =1;
    public static final int RETURN=2;

    private List<FundTransfer> fundTransferList;
    private Context context;

    private boolean isLoadingAdded = false;

    public FundTransferAdapter(Context context) {
        this.context = context;
        fundTransferList = new ArrayList<>();
    }

    public List<FundTransfer> getFundTransferList() {
        return fundTransferList;
    }

    public void setFundTransferList(List<FundTransfer> fundTransferList) {
        this.fundTransferList = fundTransferList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.list_fund_transfer, parent, false);
        viewHolder = new ViewHolder(v1);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        FundTransfer fundTransfer = fundTransferList.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                ViewHolder viewHolder = (ViewHolder) holder;

                viewHolder.tv_idName.setText(fundTransfer.getId() +"\n"+fundTransfer.getName());
                viewHolder.tv_shopName.setText(fundTransfer.getShopName());
                viewHolder.tv_mobile.setText(fundTransfer.getMobile());
                viewHolder.tv_moneyAmount.setText(fundTransfer.getMoneyBalance());
                viewHolder.tv_itemCount.setText(String.valueOf((position+1)));
                viewHolder.btn_fundTransfer.setOnClickListener(view-> listener.onFundTransfer(fundTransfer,TRANSFER));
                viewHolder.btn_fundReturn.setOnClickListener(view->listener.onFundTransfer(fundTransfer,RETURN));

                if(AppPreference.getInstance(context).getRollId() ==1)
                    viewHolder.btn_fundReturn.setVisibility(View.VISIBLE);
                else viewHolder.btn_fundReturn.setVisibility(View.GONE);





                break;
            case LOADING:
//                Do nothing
                break;

        }

    }


    @Override
    public int getItemCount() {
        return fundTransferList == null ? 0 : fundTransferList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == fundTransferList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void add(FundTransfer report) {
        fundTransferList.add(report);
        notifyItemInserted(fundTransferList.size() - 1);
    }

    public void addAll(List<FundTransfer> reportList) {
        for (FundTransfer report : reportList) {
            add(report);
        }
    }

    public void remove(FundTransfer report) {
        int position = fundTransferList.indexOf(report);
        if (position > -1) {
            fundTransferList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {

            add(new FundTransfer());
            isLoadingAdded = true;
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = fundTransferList.size() - 1;
        FundTransfer item = null;
        if(position>0)
        item = getItem(position);

        if (item != null) {
            fundTransferList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public FundTransfer getItem(int position) {
        return fundTransferList.get(position);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView tv_idName;
        private TextView tv_shopName;
        private TextView tv_mobile;
        private TextView tv_moneyAmount;
        private TextView tv_itemCount;
        private Button btn_fundTransfer;
        private Button btn_fundReturn;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_idName = itemView.findViewById(R.id.tv_idName);
            tv_shopName = itemView.findViewById(R.id.tv_shopName);
            tv_mobile = itemView.findViewById(R.id.tv_mobile);
            tv_moneyAmount = itemView.findViewById(R.id.tv_amount);
            tv_itemCount = itemView.findViewById(R.id.tv_itemCount);
            btn_fundTransfer = itemView.findViewById(R.id.btn_fundTransfer);
            btn_fundReturn = itemView.findViewById(R.id.btn_fundReturn);


        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }


    private FundTransferListener listener;
    public void setupFundTransferListener(FundTransferListener listener){
        this.listener = listener;
    }

}
