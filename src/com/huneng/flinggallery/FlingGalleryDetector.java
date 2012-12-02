package com.huneng.flinggallery;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class FlingGalleryDetector extends
		GestureDetector.SimpleOnGestureListener {
	private FlingGalleryView mViews[];
	private long mScrollTimestamp;
	private final int swipe_min_distance = 120;
	private final int swipe_max_off_path = 250;
	private final int swipe_threshold_veloicty = 400;
	private int mAnimationDuration = 250;
	private FlingGallery gallery;

	private float mCurrentOffset = 0.0f;
	public FlingGalleryDetector(FlingGallery gallery, FlingGalleryView[] views) {
		mViews = views;
		this.gallery = gallery;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		gallery.mIsTouched = true;

		gallery.mFlingDirection = 0;
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if (e2.getAction() == MotionEvent.ACTION_MOVE) {
			if (gallery.mIsDragging == false) {
				// Stop animation
				gallery.mIsTouched = true;

				// Reconfigure scroll
				gallery.mIsDragging = true;
				gallery.mFlingDirection = 0;
				mScrollTimestamp = System.currentTimeMillis();
				mCurrentOffset = mViews[gallery.mCurrentViewNumber].getCurrentOffset();
			}

			float maxVelocity = gallery.mGalleryWidth / (mAnimationDuration / 1000.0f);
			long timestampDelta = System.currentTimeMillis() - mScrollTimestamp;
			float maxScrollDelta = maxVelocity * (timestampDelta / 1000.0f);
			float currentScrollDelta = e1.getX() - e2.getX();

			if (currentScrollDelta < maxScrollDelta * -1)
				currentScrollDelta = maxScrollDelta * -1;
			if (currentScrollDelta > maxScrollDelta)
				currentScrollDelta = maxScrollDelta;
			int scrollOffset = Math.round(mCurrentOffset + currentScrollDelta);

			// We can't scroll more than the width of our own frame layout
			if (scrollOffset >= gallery.mGalleryWidth)
				scrollOffset = gallery.mGalleryWidth;
			if (scrollOffset <= gallery.mGalleryWidth * -1)
				scrollOffset = gallery.mGalleryWidth * -1;

			mViews[0].setOffset(scrollOffset, 0, gallery.mCurrentViewNumber);
			mViews[1].setOffset(scrollOffset, 0, gallery.mCurrentViewNumber);
			mViews[2].setOffset(scrollOffset, 0, gallery.mCurrentViewNumber);
		}

		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (Math.abs(e1.getY() - e2.getY()) <= swipe_max_off_path) {
			if (e2.getX() - e1.getX() > swipe_min_distance
					&& Math.abs(velocityX) > swipe_threshold_veloicty) {
				gallery.movePrevious();
			}

			if (e1.getX() - e2.getX() > swipe_min_distance
					&& Math.abs(velocityX) > swipe_threshold_veloicty) {
				gallery.moveNext();
			}
		}

		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		gallery.mFlingDirection = 0;
		gallery.processGesture();
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// Reset fling state
		gallery.mFlingDirection = 0;
		return false;
	}
}
