package com.a2zsuvidhaa.in.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.util.dialogs.Dialogs;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.model.Member;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.MakeToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class MemberAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<Member> memberList;
    private Context context;

    private boolean canClick = true;
    private boolean isLoadingAdded = false;

    public MemberAdapter(Context context) {
        this.context = context;
        memberList = new ArrayList<>();
    }

    public List<Member> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
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
        View v1 = inflater.inflate(R.layout.list_member, parent, false);
        viewHolder = new ViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Member member = memberList.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                ViewHolder viewHolder = (ViewHolder) holder;


                viewHolder.tv_name.setText(String.valueOf(member.getName()));
                viewHolder.tv_balance.setText(String.valueOf(member.getBalance()));
                viewHolder.tv_email.setText(String.valueOf(member.getEmail()));
                viewHolder.tv_id.setText(member.getPrefix() + " : " + member.getId());
                viewHolder.tv_shopName.setText(String.valueOf(member.getShopName()));
                viewHolder.tv_status.setText(String.valueOf(member.getStatus()));
                viewHolder.tv_mobile.setText(String.valueOf(member.getMobile()));
                viewHolder.tv_parent.setText(member.getParentDetails());

                viewHolder.tv_itemCount1.setText((position + 1) + "");
                viewHolder.tv_itemCount2.setText((position + 1) + "");




                viewHolder.ll_main_layout.setOnClickListener(view -> {
                    viewHideShow(viewHolder, member);
                });
                if (memberList.get(viewHolder.getAdapterPosition()).isHide()) {
                    viewHolder.ll_arrow_hideable.setVisibility(View.VISIBLE);
                    viewHolder.ll_hideable.setVisibility(View.GONE);
                } else {
                    viewHolder.ll_arrow_hideable.setVisibility(View.GONE);
                    viewHolder.ll_hideable.setVisibility(View.VISIBLE);
                }


                break;
            case LOADING:
//                Do nothing
                break;

        }

    }

    private void viewHideShow(@NonNull MemberAdapter.ViewHolder viewHolder, Member member) {
        if (member.isHide()) {
            viewHolder.ll_arrow_hideable.setVisibility(View.GONE);
            viewHolder.ll_hideable.setVisibility(View.VISIBLE);
            memberList.get(viewHolder.getAdapterPosition()).setHide(false);
        } else {
            viewHolder.ll_arrow_hideable.setVisibility(View.VISIBLE);
            viewHolder.ll_hideable.setVisibility(View.GONE);
            memberList.get(viewHolder.getAdapterPosition()).setHide(true);
        }
    }

    @Override
    public int getItemCount() {
        return memberList == null ? 0 : memberList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == memberList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void add(Member member) {
        memberList.add(member);
        notifyItemInserted(memberList.size() - 1);
    }

    public void addAll(List<Member> reportList) {
        for (Member member : reportList) {
            add(member);
        }
    }

    public void remove(Member member) {
        int position = memberList.indexOf(member);
        if (position > -1) {
            memberList.remove(position);
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

        add(new Member());
        isLoadingAdded = true;

    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = memberList.size() - 1;
        Member item = null;
        if (position > 0)
            item = getItem(position);

        if (item != null) {
            memberList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Member getItem(int position) {
        return memberList.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView tv_name;
        private TextView tv_mobile;
        private TextView tv_balance;
        private TextView tv_status;
        private TextView tv_id;
        private TextView tv_parent;
        private TextView tv_shopName;
        private TextView tv_email;
        private TextView tv_itemCount1;
        private TextView tv_itemCount2;

        private LinearLayout ll_hideable;
        private LinearLayout ll_main_layout;
        private LinearLayout ll_arrow_hideable;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_mobile = itemView.findViewById(R.id.tv_mobile);
            tv_balance = itemView.findViewById(R.id.tv_balance);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_id = itemView.findViewById(R.id.tv_id);
            tv_shopName = itemView.findViewById(R.id.tv_shopName);
            tv_parent = itemView.findViewById(R.id.tv_parent);
            tv_email = itemView.findViewById(R.id.tv_email);

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
