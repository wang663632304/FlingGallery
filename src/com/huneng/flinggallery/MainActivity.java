package com.huneng.flinggallery;


import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.app.Activity;

public class MainActivity extends Activity {

	private final String[] mLabelArray = { "View1", "View2", "View3", "View4",
			"View5" };
	private FlingGallery mGallery;
	CheckBox mCheckBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGallery = new FlingGallery(this);
		mGallery.setPaddingWidth(5);
		MyAdapter adapter = new MyAdapter(this,
				android.R.layout.simple_list_item_1, mLabelArray);
		adapter.gallery = mGallery;

		mGallery.setAdapter(adapter);
		LinearLayout layout = new LinearLayout(getApplicationContext());
		layout.setOrientation(LinearLayout.VERTICAL);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		layoutParams.setMargins(10, 10, 10, 10);
		layoutParams.weight = 1.0f;

		layout.addView(mGallery, layoutParams);

		mCheckBox = new CheckBox(getApplicationContext());
		mCheckBox.setText("Gallery is Circular");
		mCheckBox.setText("Gallery is Circular");
		mCheckBox.setPadding(50, 10, 0, 10);
		mCheckBox.setTextSize(30);
		mCheckBox.setChecked(true);
		mCheckBox.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				mGallery.setIsGalleryCircular(mCheckBox.isChecked());
			}
		});

		layout.addView(mCheckBox, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		setContentView(layout);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGallery.onGalleryTouchEvent(event);
		return super.onTouchEvent(event);
	}

}
