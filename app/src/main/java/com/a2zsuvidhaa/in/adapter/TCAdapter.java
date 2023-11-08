package com.a2zsuvidhaa.in.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.a2zsuvidhaa.in.R;

import java.util.ArrayList;

public class TCAdapter extends BaseAdapter {

	private ArrayList<String> Items=new ArrayList<String>();
	private LayoutInflater mInflater;
	Context context;

	public TCAdapter(Context context, ArrayList<String> Items) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context=context;
		this.Items=Items;

		//mDrawerItems = DummyContent.getTravelDummyList();
	}

	@Override
	public int getCount() {
		return Items.size();
	}

	@Override
	public Object getItem(int position) {
		return Items.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.list_item_transafer, parent,
					false);
		holder = new ViewHolder();


			//holder.icon = (TextView) convertView.findViewById(R.id.icon);
			holder.title = (TextView) convertView.findViewById(R.id.title);

			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String item = Items.get(position).toString();

		Log.e("item","="+item);
		holder.title.setText(item);




		return convertView;
		
	}

	private static class ViewHolder {
		public TextView title;
	}

	
}
