package com.huneng.flinggallery;

import android.content.Context;
import android.view.View;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.FrameLayout.LayoutParams;

public class FlingGalleryView {
	private int mViewNumber;
	private FrameLayout mParentLayout;
	private FrameLayout mInvalidLayout = null;
	private LinearLayout mInternalLayout = null;
	private View mExternalView = null;

	public MyAdapter adapter;
	private FlingGallery gallery;

	public FlingGalleryView(int viewNumber, FrameLayout parentLayout,
			Context context, FlingGallery gallery) {
		this.gallery = gallery;
		mViewNumber = viewNumber;
		mParentLayout = parentLayout;

		mInvalidLayout = new FrameLayout(context);
		mInvalidLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		mInternalLayout = new LinearLayout(context);
		mInternalLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		mParentLayout.addView(mInternalLayout);
	}

	public void recycleView(int newPosition) {
		if (mExternalView != null) {
			mInternalLayout.removeView(mExternalView);
		}

		if (adapter != null) {
			if (newPosition >= gallery.getFirstPosition()
					&& newPosition <= gallery.getLastPosition()) {
				mExternalView = adapter.getView(newPosition, mExternalView,
						mInternalLayout);
			} else {
				mExternalView = mInvalidLayout;
			}
		}

		if (mExternalView != null) {
			mInternalLayout.addView(mExternalView,
					new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT));
		}
	}

	public void setOffset(int xOffset, int yOffset, int relativeViewNumber) {
		mInternalLayout.scrollTo(
				gallery.getViewOffset(mViewNumber, relativeViewNumber)
						+ xOffset, yOffset);
	}

	public int getCurrentOffset() {
		return mInternalLayout.getScrollX();
	}

	public void requestFocus() {

		mInternalLayout.requestFocus();
	}

}
