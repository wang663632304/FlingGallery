package com.huneng.flinggallery;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

public class FlingGalleryAnimation extends Animation{
	private int mAnimationDuration = 250;
	private boolean mIsAnimationInProgress;
	private int mRelativeViewNumber;
	private int mInitialOffset;
	private int mTargetOffset;
	private int mTargetDistance;
	private FlingGalleryView mViews[];
	private FlingGallery gallery;
	private Interpolator mDecelerateInterpolater;
	public FlingGalleryAnimation(FlingGallery gallery, Context context, FlingGalleryView[] views){
		this.gallery =gallery; 
		mViews = views;
		mIsAnimationInProgress = false;
		mRelativeViewNumber = 0;
		mInitialOffset = 0;
		mTargetOffset = 0;
		mTargetDistance = 0;;
		mDecelerateInterpolater = AnimationUtils.loadInterpolator(context,
				android.R.anim.decelerate_interpolator);
	}
	public void prepareAnimation(int relativeViewNumber){
		// If we are animating relative to a new view
		if (mRelativeViewNumber != relativeViewNumber) {
			if (mIsAnimationInProgress == true) {
				// We only have three views so if requested again to animate
				// in same direction we must snap
				int newDirection = (relativeViewNumber == gallery.getPrevViewNumber(mRelativeViewNumber)) ? 1
						: -1;
				int animDirection = (mTargetDistance < 0) ? 1 : -1;

				// If animation in same direction
				if (animDirection == newDirection) {
					// Ran out of time to animate so snap to the target
					// offset
					mViews[0].setOffset(mTargetOffset, 0,
							mRelativeViewNumber);
					mViews[1].setOffset(mTargetOffset, 0,
							mRelativeViewNumber);
					mViews[2].setOffset(mTargetOffset, 0,
							mRelativeViewNumber);
				}
			}

			// Set relative view number for animation
			mRelativeViewNumber = relativeViewNumber;
		}

		// Note: In this implementation the targetOffset will always be zero
		// as we are centering the view; but we include the calculations of
		// targetOffset and targetDistance for use in future implementations

		mInitialOffset = mViews[mRelativeViewNumber].getCurrentOffset();
		mTargetOffset = gallery.getViewOffset(mRelativeViewNumber,
				mRelativeViewNumber);
		mTargetDistance = mTargetOffset - mInitialOffset;

		// Configure base animation properties
		this.setDuration(mAnimationDuration);
		this.setInterpolator(mDecelerateInterpolater);

		// Start/continued animation
		mIsAnimationInProgress = true;
	}
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		super.applyTransformation(interpolatedTime, t);
		// Ensure interpolatedTime does not over-shoot then calculate new
		// offset
		interpolatedTime = (interpolatedTime > 1.0f) ? 1.0f
				: interpolatedTime;
		int offset = mInitialOffset
				+ (int) (mTargetDistance * interpolatedTime);

		for (int viewNumber = 0; viewNumber < 3; viewNumber++) {
			// Only need to animate the visible views as the other view will
			// always be off-screen
			if ((mTargetDistance > 0 && viewNumber != gallery.getNextViewNumber(mRelativeViewNumber))
					|| (mTargetDistance < 0 && viewNumber != gallery.getPrevViewNumber(mRelativeViewNumber))) {
				mViews[viewNumber]
						.setOffset(offset, 0, mRelativeViewNumber);
			}
		}
	}
	@Override
	public boolean getTransformation(long currentTime,
			Transformation outTransformation) {if (super.getTransformation(currentTime, outTransformation) == false) {
				mViews[0].setOffset(mTargetOffset, 0, mRelativeViewNumber);
				mViews[1].setOffset(mTargetOffset, 0, mRelativeViewNumber);
				mViews[2].setOffset(mTargetOffset, 0, mRelativeViewNumber);
				mIsAnimationInProgress = false;

				return false;
			}

			if (gallery.mIsTouched || gallery.mIsDragging) {
				return false;
			}
			return true;
	}
}
