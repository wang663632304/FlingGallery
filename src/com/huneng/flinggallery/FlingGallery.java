package com.huneng.flinggallery;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Adapter;
import android.widget.FrameLayout;

public class FlingGallery extends FrameLayout {
	Context context;
	public MyAdapter adapter;
	private FlingGalleryView mViews[];
	private FlingGalleryAnimation mAnimation;
	private GestureDetector mGestureDetector;
	private int mViewPaddingWidth;
	private float mSnapBorderRatio = 0.5f;
	private boolean mIsGalleryCircular;
	public int mCurrentViewNumber;
	public int mGalleryWidth;
	private int mCurrentPosition;
	public int mFlingDirection ;
	public boolean mIsTouched, mIsDragging;
	public FlingGallery(Context context) {
		super(context);
		this.context = context;
		adapter = null;

		mViews = new FlingGalleryView[3];
		mViews[0] = new FlingGalleryView(0, this, context, this);
		mViews[1] = new FlingGalleryView(1, this, context, this);
		mViews[2] = new FlingGalleryView(2, this, context, this);

		mAnimation = new FlingGalleryAnimation(this, context, mViews);
		mGestureDetector = new GestureDetector(new FlingGalleryDetector(this,
				mViews));
	}

	public void setPaddingWidth(int viewPaddingWidth) {
		mViewPaddingWidth = viewPaddingWidth;
	}

	public void setSnapBorderRatio(float snapBorderRatio) {
		mSnapBorderRatio = snapBorderRatio;
	}

	public void setIsGalleryCircular(boolean isGalleryCircular) {
		if (mIsGalleryCircular != isGalleryCircular) {
			mIsGalleryCircular = isGalleryCircular;

			if (mCurrentPosition == getFirstPosition()) {
				// We need to reload the view immediately to the left to change
				// it to circular view or blank
				mViews[getPrevViewNumber(mCurrentViewNumber)]
						.recycleView(getPrevPosition(mCurrentPosition));
			}

			if (mCurrentPosition == getLastPosition()) {
				// We need to reload the view immediately to the right to change
				// it to circular view or blank
				mViews[getNextViewNumber(mCurrentViewNumber)]
						.recycleView(getNextPosition(mCurrentPosition));
			}
		}
	}

	public int getGalleryCount() {
		return (adapter == null ? 0 : adapter.getCount());
	}

	public int getPrevPosition(int relativePosition) {
		int prevPosition = relativePosition - 1;

		if (prevPosition < getFirstPosition()) {
			prevPosition = getFirstPosition() - 1;

			if (mIsGalleryCircular == true) {
				prevPosition = getLastPosition();
			}
		}
		return prevPosition;
	}

	private int getNextPosition(int relativePosition) {
		int nextPosition = relativePosition + 1;

		if (nextPosition > getLastPosition()) {
			nextPosition = getLastPosition() + 1;

			if (mIsGalleryCircular == true) {
				nextPosition = getFirstPosition();
			}
		}

		return nextPosition;
	}

	public int getFirstPosition() {
		return 0;
	}

	public int getLastPosition() {
		return (getGalleryCount() == 0) ? 0 : getGalleryCount() - 1;
	}

	public int getPrevViewNumber(int relativeViewNumber) {
		return (relativeViewNumber == 0) ? 2 : relativeViewNumber - 1;
	}

	public int getNextViewNumber(int relativeViewNumber) {
		return (relativeViewNumber == 2) ? 0 : relativeViewNumber + 1;
	}



	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		// Calculate our view width
		mGalleryWidth = right - left;

		if (changed == true) {
			// Position views at correct starting offsets
			mViews[0].setOffset(0, 0, mCurrentViewNumber);
			mViews[1].setOffset(0, 0, mCurrentViewNumber);
			mViews[2].setOffset(0, 0, mCurrentViewNumber);
		}
	}

	public void setAdapter(MyAdapter adapter) {
		this.adapter = adapter;
		mCurrentPosition = 0;
		mCurrentViewNumber = 0;

		mViews[0].adapter = adapter;
		mViews[1].adapter = adapter;
		mViews[2].adapter = adapter;
		
		mViews[0].recycleView(mCurrentPosition);
		mViews[1].recycleView(getNextPosition(mCurrentPosition));
		mViews[2].recycleView(getPrevPosition(mCurrentPosition));

		mViews[0].setOffset(0, 0, mCurrentViewNumber);
		mViews[1].setOffset(0, 0, mCurrentViewNumber);
		mViews[2].setOffset(0, 0, mCurrentViewNumber);
	}

	public int getViewOffset(int viewNumber, int relativeViewNumber) {

		int offsetWidth = mGalleryWidth + mViewPaddingWidth;

		if (viewNumber == getPrevViewNumber(relativeViewNumber)) {
			return offsetWidth;
		}

		if (viewNumber == getNextViewNumber(relativeViewNumber)) {
			return offsetWidth * -1;
		}

		return 0;
	}

	void movePrevious() {
		mFlingDirection = 1;
		processGesture();
	}


	void moveNext() {
		mFlingDirection = -1;
		processGesture();
	}
	
	public boolean onGalleryTouchEvent(MotionEvent event) {
		boolean consumed = mGestureDetector.onTouchEvent(event);

		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (mIsTouched || mIsDragging) {
				processScrollSnap();
				processGesture();
			}
		}

		return consumed;
	}
	void processGesture() {
		int newViewNumber = mCurrentViewNumber;
		int reloadViewNumber = 0;
		int reloadPosition = 0;

		mIsTouched = false;
		mIsDragging = false;

		if (mFlingDirection > 0) {
			if (mCurrentPosition > getFirstPosition()
					|| mIsGalleryCircular == true) {

				newViewNumber = getPrevViewNumber(mCurrentViewNumber);
				mCurrentPosition = getPrevPosition(mCurrentPosition);
				reloadViewNumber = getNextViewNumber(mCurrentViewNumber);
				reloadPosition = getPrevPosition(mCurrentPosition);
			}
		}

		if (mFlingDirection < 0) {
			if (mCurrentPosition < getLastPosition()
					|| mIsGalleryCircular == true) {

				newViewNumber = getNextViewNumber(mCurrentViewNumber);
				mCurrentPosition = getNextPosition(mCurrentPosition);
				reloadViewNumber = getPrevViewNumber(mCurrentViewNumber);
				reloadPosition = getNextPosition(mCurrentPosition);
			}
		}

		if (newViewNumber != mCurrentViewNumber) {
			mCurrentViewNumber = newViewNumber;

			mViews[reloadViewNumber].recycleView(reloadPosition);
		}

		mViews[mCurrentViewNumber].requestFocus();

		mAnimation.prepareAnimation(mCurrentViewNumber);
		this.startAnimation(mAnimation);

		mFlingDirection = 0;
	}

	void processScrollSnap() {
		float rollEdgeWidth = mGalleryWidth * mSnapBorderRatio;
		int rollOffset = mGalleryWidth - (int) rollEdgeWidth;
		int currentOffset = mViews[mCurrentViewNumber].getCurrentOffset();

		if (currentOffset <= rollOffset * -1) {
			mFlingDirection = 1;
		}

		if (currentOffset >= rollOffset) {
			mFlingDirection = -1;
		}
	}


}
