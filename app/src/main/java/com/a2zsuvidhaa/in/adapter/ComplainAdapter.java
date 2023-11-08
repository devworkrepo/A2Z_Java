package com.a2zsuvidhaa.in.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a2zsuvidhaa.in.model.Complain;
import com.a2zsuvidhaa.in.R;

import java.util.ArrayList;
import java.util.List;


public class ComplainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<Complain> complainList;
    private Context context;

    private boolean canClick = true;
    private boolean isLoadingAdded = false;

    public ComplainAdapter(Context context) {
        this.context = context;
        complainList = new ArrayList<>();
    }

    public List<Complain> getComplainList() {
        return complainList;
    }

    public void setComplainList(List<Complain> complainList) {
        this.complainList = complainList;
    }

    @NonNull
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
        View v1 = inflater.inflate(R.layout.list_view_complain, parent, false);
        viewHolder = new ViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Complain complain = complainList.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                ViewHolder viewHolder = (ViewHolder) holder;


                viewHolder.tv_issueDate.setText(String.valueOf(complain.getCreatedAt()));
                viewHolder.tv_txnId.setText(String.valueOf(complain.getTxnId()));
                viewHolder.tv_issueType.setText(String.valueOf(complain.getIssueType()));
                viewHolder.tv_complainId.setText(complain.getId());
                viewHolder.tv_remark.setText(complain.getRemark());
                viewHolder.tv_status.setText(complain.getStatus());
                viewHolder.tv_currentStatusRemark.setText(complain.getCurrentStatusRemark());

                viewHolder.tv_itemCount1.setText((position + 1) + "");
                viewHolder.tv_itemCount2.setText((position + 1) + "");

                if(complain.getStatus().equalsIgnoreCase("Reject"))
                    viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.red));
                else if(complain.getStatus().equalsIgnoreCase("Resolved"))
                    viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.success));
                else if(complain.getStatus().equalsIgnoreCase("Pending"))
                    viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.yellow_dark));



                viewHolder.ll_main_layout.setOnClickListener(view -> {
                    viewHideShow(viewHolder, complain);
                });
                if (complainList.get(viewHolder.getAdapterPosition()).isHide()) {
                    viewHolder.ll_arrow_hideable.setVisibility(View.VISIBLE);
                    viewHolder.ll_hideable.setVisibility(View.GONE);
                } else {
                    viewHolder.ll_arrow_hideable.setVisibility(View.GONE);
                    viewHolder.ll_hideable.setVisibility(View.VISIBLE);
                }



                break;
            case LOADING:
                break;

        }

    }


    private void viewHideShow(@NonNull ComplainAdapter.ViewHolder viewHolder, Complain member) {
        if (member.isHide()) {
            viewHolder.ll_arrow_hideable.setVisibility(View.GONE);
            viewHolder.ll_hideable.setVisibility(View.VISIBLE);
            complainList.get(viewHolder.getAdapterPosition()).setHide(false);
        } else {
            viewHolder.ll_arrow_hideable.setVisibility(View.VISIBLE);
            viewHolder.ll_hideable.setVisibility(View.GONE);
            complainList.get(viewHolder.getAdapterPosition()).setHide(true);
        }
    }

    @Override
    public int getItemCount() {
        return complainList == null ? 0 : complainList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == complainList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void add(Complain member) {
        complainList.add(member);
        notifyItemInserted(complainList.size() - 1);
    }

    public void addAll(List<Complain> reportList) {
        for (Complain member : reportList) {
            add(member);
        }
    }

    public void remove(Complain member) {
        int position = complainList.indexOf(member);
        if (position > -1) {
            complainList.remove(position);
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

        add(new Complain());
        isLoadingAdded = true;

    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = complainList.size() - 1;
        Complain item = null;
        if (position > 0)
            item = getItem(position);

        if (item != null) {
            complainList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Complain getItem(int position) {
        return complainList.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_issueDate;
        private TextView tv_txnId;
        private TextView tv_issueType;
        private TextView tv_status;
        private TextView tv_complainId;
        private TextView tv_remark;
        private TextView tv_currentStatusRemark;
        private TextView tv_itemCount1;
        private TextView tv_itemCount2;

        LinearLayout ll_hideable;
        private LinearLayout ll_main_layout;
        LinearLayout ll_arrow_hideable;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_issueDate = itemView.findViewById(R.id.tv_issueDate);
            tv_txnId = itemView.findViewById(R.id.tv_txnId);
            tv_issueType = itemView.findViewById(R.id.tv_issueType);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_complainId = itemView.findViewById(R.id.tv_complainId);
            tv_remark = itemView.findViewById(R.id.tv_remark);
            tv_currentStatusRemark = itemView.findViewById(R.id.tv_currentStatusRemark);

            tv_itemCount1 = itemView.findViewById(R.id.tv_itemCount);
            tv_itemCount2 = itemView.findViewById(R.id.tv_itemCount2);

            ll_hideable = itemView.findViewById(R.id.ll_hideable);
            ll_main_layout = itemView.findViewById(R.id.ll_main_layout);
            ll_arrow_hideable = itemView.findViewById(R.id.ll_arrow_hideable);


        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }


}
