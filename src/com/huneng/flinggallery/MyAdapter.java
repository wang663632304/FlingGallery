package com.huneng.flinggallery;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class MyAdapter extends ArrayAdapter<String>{
	Context context;
	public FlingGallery gallery;
	public MyAdapter(Context context, int textViewResourceId, Object[] objects) {
		super(context, textViewResourceId);
		this.context = context;
	
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return new GalleryViewItem(context, position, gallery);
	}
	@Override
	public int getCount() {
		return 5;
	}
}
