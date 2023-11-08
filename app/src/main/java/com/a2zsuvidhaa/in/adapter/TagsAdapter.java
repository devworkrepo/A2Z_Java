package com.a2zsuvidhaa.in.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.activity.MainActivity;

import java.util.ArrayList;

public class TagsAdapter extends BaseAdapter {

	private ArrayList<String> Items=new ArrayList<String>();
	private LayoutInflater mInflater;
	Context context;

	public TagsAdapter(Context context, ArrayList<String> Items) {
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
					R.layout.list_item_tags, parent,
					false);
		holder = new ViewHolder();


			//holder.icon = (TextView) convertView.findViewById(R.id.icon);
			holder.title = (TextView) convertView.findViewById(R.id.title);

			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String item = Items.get(position).toString();

		holder.title.setText(item);
	//	AllInfoFragment.spos=position;
		if(MainActivity.spos==position ) {
			holder.title.setBackground(context.getResources().getDrawable(R.drawable.bg_light_blue_5));
			holder.title.setTextColor(context.getResources().getColor(R.color.white));
		}
		else {
			holder.title.setTextColor(context.getResources().getColor(R.color.black));
			holder.title.setBackground(context.getResources().getDrawable(R.drawable.bg_white_5round));
		}



		return convertView;
		
	}

	private static class ViewHolder {
		public TextView title;
	}

	
}
