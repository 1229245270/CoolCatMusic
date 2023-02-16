/*******************************************************************************
 * The MIT License (MIT)
 * 
 * Copyright (c) 2013 Triggertrap Ltd
 * Author Neil Davies
 * 
 * Permission is hereby granted, free of charge, to any person obtaining app copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.hzc.coolcatmusic.ui.costom;

import static com.hzc.coolcatmusic.service.MusicConnection.musicInterface;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.hzc.coolcatmusic.app.SPUtilsConfig;

import com.hzc.coolcatmusic.R;

import skin.support.utils.SkinPreference;
import skin.support.widget.SkinCompatTextHelper;
import skin.support.widget.SkinCompatView;

/**
 * 
 * SeekArc.java
 * 
 * This is app class that functions much like app SeekBar but
 * follows app circle path instead of app straight line.
 * 
 * @author Neil Davies
 * 
 */
public class SeekArc extends SkinCompatView {

	private SkinCompatTextHelper mTextHelper;

	private static final String TAG = SeekArc.class.getSimpleName();
	private static int INVALID_PROGRESS_VALUE = -1;
	// The initial rotational offset -90 means we start at 12 o'clock
	private final int mAngleOffset = -90;

	/**
	 * The Drawable for the seek arc thumbnail
	 */
	private Drawable mThumb;
	
	/**
	 * The Maximum value that this SeekArc can be set to
	 */
	private int mMax = 100;
	
	/**
	 * The Current value that the SeekArc is set to
	 */
	private int mProgress = 0;
		
	/**
	 * The width of the progress line for this SeekArc
	 */
	private int mProgressWidth = 4;
	
	/**
	 * The Width of the background arc for the SeekArc 
	 */
	private int mArcWidth = 2;
	
	/**
	 * The Angle to start drawing this Arc from
	 */
	private int mStartAngle = 0;
	
	/**
	 * The Angle through which to draw the arc (Max is 360)
	 */
	private int mSweepAngle = 360;
	
	/**
	 * The rotation of the SeekArc- 0 is twelve o'clock
	 */
	private int mRotation = 0;
	
	/**
	 * Give the SeekArc rounded edges
	 */
	private boolean mRoundedEdges = false;
	
	/**
	 * Enable touch inside the SeekArc
	 */
	private boolean mTouchInside = true;
	
	/**
	 * Will the progress increase clockwise or anti-clockwise
	 */
	private boolean mClockwise = true;


	/**
	 * is the control enabled/touchable
 	 */
	private boolean mEnabled = true;

	// Internal variables
	private int mArcRadius = 0;
	private float mProgressSweep = 0;
	private RectF mArcRect = new RectF();
	private Paint mArcPaint;
	private Paint mProgressPaint;
	private int mTranslateX;
	private int mTranslateY;
	private int mThumbXPos;
	private int mThumbYPos;
	private double mTouchAngle;
	private float mTouchIgnoreRadius;
	private OnSeekArcChangeListener mOnSeekArcChangeListener;
	private double radiu = 0;

	public interface OnSeekArcChangeListener {

		void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser);

		void onStartTrackingTouch(SeekArc seekArc);

		void onStopTrackingTouch(SeekArc seekArc);
	}

	public SeekArc(Context context) {
		super(context);
		init(context, null, 0);
	}

	public SeekArc(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, R.attr.seekArcStyle);
	}

	public SeekArc(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}

	private void init(Context context, AttributeSet attrs, int defStyle) {

		Log.d(TAG, "Initialising SeekArc");
		final Resources res = getResources();
		float density = context.getResources().getDisplayMetrics().density;

		// Defaults, may need to link this into theme settings
		int arcColor = ContextCompat.getColor(getContext(),R.color.progress_gray);
		int progressColor = ContextCompat.getColor(getContext(),R.color.default_blue_light);
		int thumbHalfheight = 0;
		int thumbHalfWidth = 0;
		mThumb = ResourcesCompat.getDrawable(res,R.drawable.seek_arc_control_selector,null);
		// Convert progress width to pixels for current density
		mProgressWidth = (int) (mProgressWidth * density);

		
		if (attrs != null) {
			// Attribute initialization
			final TypedArray a = context.obtainStyledAttributes(attrs,
					R.styleable.SeekArc, defStyle, 0);

			Drawable thumb = a.getDrawable(R.styleable.SeekArc_thumb);
			if (thumb != null) {
				mThumb = thumb;
			}

			
			
			thumbHalfheight = (int) mThumb.getIntrinsicHeight() / 2;
			thumbHalfWidth = (int) mThumb.getIntrinsicWidth() / 2;
			mThumb.setBounds(-thumbHalfWidth, -thumbHalfheight, thumbHalfWidth,
					thumbHalfheight);

			mMax = a.getInteger(R.styleable.SeekArc_max, mMax);
			mProgress = a.getInteger(R.styleable.SeekArc_progress, mProgress);
			mProgressWidth = (int) a.getDimension(
					R.styleable.SeekArc_progressWidth, mProgressWidth);
			mArcWidth = (int) a.getDimension(R.styleable.SeekArc_arcWidth,
					mArcWidth);
			mStartAngle = a.getInt(R.styleable.SeekArc_startAngle, mStartAngle);
			mSweepAngle = a.getInt(R.styleable.SeekArc_sweepAngle, mSweepAngle);
			mRotation = a.getInt(R.styleable.SeekArc_rotation, mRotation);
			mRoundedEdges = a.getBoolean(R.styleable.SeekArc_roundEdges,
					mRoundedEdges);
			mTouchInside = a.getBoolean(R.styleable.SeekArc_touchInside,
					mTouchInside);
			mClockwise = a.getBoolean(R.styleable.SeekArc_clockwise,
					mClockwise);
			mEnabled = a.getBoolean(R.styleable.SeekArc_enabled, mEnabled);

			arcColor = a.getColor(R.styleable.SeekArc_arcColor, arcColor);
			progressColor = a.getColor(R.styleable.SeekArc_progressColor,
					progressColor);

			a.recycle();
		}

		mProgress = Math.min(mProgress, mMax);
		mProgress = Math.max(mProgress, 0);

		mSweepAngle = Math.min(mSweepAngle, 360);
		mSweepAngle = Math.max(mSweepAngle, 0);

		mProgressSweep = (float) mProgress / mMax * mSweepAngle;

		mStartAngle = (mStartAngle > 360) ? 0 : mStartAngle;
		mStartAngle = Math.max(mStartAngle, 0);

		mArcPaint = new Paint();
		mArcPaint.setColor(arcColor);
		mArcPaint.setAntiAlias(true);
		mArcPaint.setStyle(Paint.Style.STROKE);
		mArcPaint.setStrokeWidth(mArcWidth);
		//mArcPaint.setAlpha(45);

		mProgressPaint = new Paint();
		mProgressPaint.setColor(progressColor);
		mProgressPaint.setAntiAlias(true);
		mProgressPaint.setStyle(Paint.Style.STROKE);
		mProgressPaint.setStrokeWidth(mProgressWidth);

		if (mRoundedEdges) {
			mArcPaint.setStrokeCap(Paint.Cap.ROUND);
			mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
		}
	}


	@Override
	protected void onDraw(Canvas canvas) {
		if(!mClockwise) {
			canvas.scale(-1, 1, mArcRect.centerX(), mArcRect.centerY() );
		}
		
		// Draw the arcs
		final int arcStart = mStartAngle + mAngleOffset + mRotation;
		final int arcSweep = mSweepAngle;
		canvas.drawArc(mArcRect, arcStart, arcSweep, false, mArcPaint);
		canvas.drawArc(mArcRect, arcStart, mProgressSweep, false, mProgressPaint);

		if(mEnabled) {
			// Draw the thumb nail
			canvas.translate(mTranslateX - mThumbXPos, mTranslateY - mThumbYPos);
			mThumb.draw(canvas);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		final int height = getDefaultSize(getSuggestedMinimumHeight(),
				heightMeasureSpec);
		final int width = getDefaultSize(getSuggestedMinimumWidth(),
				widthMeasureSpec);
		final int min = Math.min(width, height);
		float top = 0;
		float left = 0;
		int arcDiameter = 0;

		mTranslateX = (int) (width * 0.5f);
		mTranslateY = (int) (height * 0.5f);

		radiu = min / 2.0;
		arcDiameter = min - getPaddingLeft();
		mArcRadius = arcDiameter / 2;
		top = height / 2 - (arcDiameter / 2);
		left = width / 2 - (arcDiameter / 2);
		mArcRect.set(left, top, left + arcDiameter, top + arcDiameter);
	
		int arcStart = (int)mProgressSweep + mStartAngle  + mRotation + 90;
		mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(arcStart)));
		mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(arcStart)));
		
		setTouchInSide(mTouchInside);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	private boolean isProgress = false;

	@Override
	public boolean performClick() {
		return super.performClick();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		return super.dispatchTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mEnabled) {
			//this.getParent().requestDisallowInterceptTouchEvent(false);
			float x = event.getX();
			float y = event.getY();
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					onLongClick.actionDown();
					double downR = Math.sqrt(Math.pow((radiu - x),2) + Math.pow((radiu - y),2));
					//进度条控制
					if((radiu - downR) <= getResources().getDimension(R.dimen.seekArc_width) + getResources().getDimension(R.dimen.seekArc_padding)){
						onStartTrackingTouch();
						updateOnTouch(event);
						isProgress = true;
					}else{
						showPlayButton();
						isProgress = false;
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if(isProgress){
						updateOnTouch(event);
					}else{
						longClickActionMove(x,y);
					}
					break;
				case MotionEvent.ACTION_UP:
					onLongClick.actionUp();
					if(isProgress){
						onStopTrackingTouch();
						setPressed(false);
						//this.getParent().requestDisallowInterceptTouchEvent(false);
					}else{
						longClickActionUp(x,y);
					}
					closePlayButton();
					performClick();
					break;
			}
			return true;
		}
		return false;
	}

	public interface OnLongClick{
		void actionDown();
		void actionUp();
		void upSong();
		void play();
		void nextSong();
	}


	private OnLongClick onLongClick;
	public void setOnLongClick(OnLongClick onLongClick){
		this.onLongClick = onLongClick;
	}

	public void setPlayImage(int resource){
		if(popupWindow != null){
			CircleImage playButton = popupWindow.getContentView().findViewById(R.id.playButton);
			playButton.setImageResource(resource);
		}
	}

	private void longClickActionUp(float x,float y){
		playButtonCallback(x, y, new ActionListener() {
			@Override
			public void onPlayBefore(CircleImage image, boolean auto) {
				if(auto){
					onLongClick.upSong();
				}
			}

			@Override
			public void onPlayButton(CircleImage image, boolean auto) {
				if(auto){
					onLongClick.play();
				}
			}

			@Override
			public void onPlayNext(CircleImage image, boolean auto) {
				if(auto){
					onLongClick.nextSong();
				}
			}
		});
	}

	//减少cpu和内存使用，防止反复重绘
	private boolean playBeforeCheck = false;
	private boolean playBeforeUnCheck = false;
	private boolean playingCheck = false;
	private boolean playingUnCheck = false;
	private boolean playNextCheck = false;
	private boolean playNextUnCheck = false;
	private void initCheck(){
		playBeforeCheck = false;
		playBeforeUnCheck = false;
		playingCheck = false;
		playingUnCheck = false;
		playNextCheck = false;
		playNextUnCheck = false;
	}
	private void longClickActionMove(float x,float y){
		playButtonCallback(x, y, new ActionListener() {
			@Override
			public void onPlayBefore(CircleImage image, boolean auto) {
				if(auto){
					playBeforeUnCheck = false;
					if(!playBeforeCheck){
						image.setImageResource(R.drawable.home_music_upsong_check);
						playBeforeCheck = true;
					}
				}else{
					playBeforeCheck = false;
					if(!playBeforeUnCheck){
						image.setImageResource(R.drawable.home_music_upsong_uncheck);
						playBeforeUnCheck = true;
					}
				}
			}

			@Override
			public void onPlayButton(CircleImage image, boolean auto) {
				if(auto){
					playingUnCheck = false;
					if(!playingCheck){
						if(musicInterface.isPlaying()){
							image.setImageResource(R.drawable.home_music_stop_check);
						}else{
							image.setImageResource(R.drawable.home_music_play_check);
						}
						playingCheck = true;
					}
				}else{
					playingCheck = false;
					if(!playingUnCheck){
						if(musicInterface.isPlaying()){
							image.setImageResource(R.drawable.home_music_stop_uncheck);
						}else{
							image.setImageResource(R.drawable.home_music_play_uncheck);
						}
						playingUnCheck = true;
					}

				}
			}

			@Override
			public void onPlayNext(CircleImage image, boolean auto) {
				if(auto){
					playNextUnCheck = false;
					if(!playNextCheck){
						image.setImageResource(R.drawable.home_music_nextsong_check);
						playNextCheck = true;
					}
				}else{
					playNextCheck = false;
					if(!playNextUnCheck){
						image.setImageResource(R.drawable.home_music_nextsong_uncheck);
						playNextUnCheck = true;
					}
				}
			}
		});
	}

	private interface ActionListener{
		void onPlayBefore(CircleImage image,boolean auto);
		void onPlayButton(CircleImage image,boolean auto);
		void onPlayNext(CircleImage image,boolean auto);
	}

	//按钮范围判断
	private void playButtonCallback(float x,float y,ActionListener actionListener){

		CircleImage playBefore = popupWindow.getContentView().findViewById(R.id.playBefore);
		CircleImage playButton = popupWindow.getContentView().findViewById(R.id.playButton);
		CircleImage playNext = popupWindow.getContentView().findViewById(R.id.playNext);

		float buttonWidth = getResources().getDimension(R.dimen.seekArc_popup_item);
		float buttonHeight = getResources().getDimension(R.dimen.seekArc_popup_item);
		float marginBottom = getResources().getDimension(R.dimen.seekArc_popup_marginBottom);
		float marginLeft = getResources().getDimension(R.dimen.seekArc_popup_marginLeft);
		float marginRight = getResources().getDimension(R.dimen.seekArc_popup_marginRight);
		boolean autoCenterY = y < 0 && y > - buttonHeight;
		boolean autoY = y < marginBottom && y > - buttonHeight + marginBottom;
		int width = 3 * (int)(getResources().getDimension(R.dimen.seekArc_popup_item)) + (int)marginLeft + (int)marginRight;
		int xOff = (width - (int)(getResources().getDimension(R.dimen.seekArc_item))) / 2;
		actionListener.onPlayBefore(playBefore,x > - xOff && x < - xOff + buttonWidth && autoY);
		actionListener.onPlayButton(playButton,x > - xOff + marginLeft + buttonWidth  && x < - xOff + marginLeft + 2 * buttonWidth && autoCenterY);
		actionListener.onPlayNext(playNext,x > - xOff + marginLeft + marginRight + 2 * buttonWidth && x  < - xOff + marginLeft + marginRight + 3 * buttonWidth && autoY);
	}

	//弹窗
	private PopupWindow popupWindow;
	private void showPlayButton(){
		int marginLeft = (int) getResources().getDimension(R.dimen.seekArc_popup_marginLeft);
		int marginRight = (int) getResources().getDimension(R.dimen.seekArc_popup_marginRight);
		int width = 3 * (int)(getResources().getDimension(R.dimen.seekArc_popup_item)) + marginLeft + marginRight;
		int height = (int)(getResources().getDimension(R.dimen.seekArc_popup_item)) + (int)(getResources().getDimension(R.dimen.seekArc_popup_marginBottom));
		if(popupWindow == null){
			View view = View.inflate(getContext(),R.layout.play_button_popupwindow,null);
			popupWindow = new PopupWindow(view,width,height);//参数为1.View 2.宽度 3.高度
			//popupWindow.setOutsideTouchable(true);//设置点击外部区域可以取消popupWindow
			popupWindow.setAnimationStyle(R.style.play_button_anim);
			CircleImage playBefore = popupWindow.getContentView().findViewById(R.id.playBefore);
			CircleImage playButton = popupWindow.getContentView().findViewById(R.id.playButton);
			CircleImage playNext = popupWindow.getContentView().findViewById(R.id.playNext);
			playBefore.setImageResource(R.drawable.home_music_upsong_uncheck);
			if(musicInterface.isPlaying()){
				playButton.setImageResource(R.drawable.home_music_stop_uncheck);
			}else{
				playButton.setImageResource(R.drawable.home_music_play_uncheck);
			}
			playNext.setImageResource(R.drawable.home_music_nextsong_uncheck);
		}
		if(!popupWindow.isShowing()){
			int xOff = ((int)(getResources().getDimension(R.dimen.seekArc_item)) - width) / 2;
			int yOff = (int)(getResources().getDimension(R.dimen.seekArc_popup_marginBottom));
			initCheck();
			popupWindow.showAsDropDown(this,xOff,yOff);
		}
	}

	private void closePlayButton(){
		if(popupWindow != null && popupWindow.isShowing()){
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		if (mThumb != null && mThumb.isStateful()) {
			int[] state = getDrawableState();
			mThumb.setState(state);
		}
		invalidate();
	}

	private void onStartTrackingTouch() {
		if (mOnSeekArcChangeListener != null) {
			mOnSeekArcChangeListener.onStartTrackingTouch(this);
		}
	}

	private void onStopTrackingTouch() {
		if (mOnSeekArcChangeListener != null) {
			mOnSeekArcChangeListener.onStopTrackingTouch(this);
		}
	}

	private void updateOnTouch(MotionEvent event) {
		boolean ignoreTouch = ignoreTouch(event.getX(), event.getY());
		if (ignoreTouch) {
			return;
		}
		setPressed(true);
		mTouchAngle = getTouchDegrees(event.getX(), event.getY());
		int progress = getProgressForAngle(mTouchAngle);
		onProgressRefresh(progress, true);
	}

	private boolean ignoreTouch(float xPos, float yPos) {
		boolean ignore = false;
		float x = xPos - mTranslateX;
		float y = yPos - mTranslateY;

		float touchRadius = (float) Math.sqrt(((x * x) + (y * y)));
		if (touchRadius < mTouchIgnoreRadius) {
			ignore = true;
		}
		return ignore;
	}

	private double getTouchDegrees(float xPos, float yPos) {
		float x = xPos - mTranslateX;
		float y = yPos - mTranslateY;
		//invert the x-coord if we are rotating anti-clockwise
		x= (mClockwise) ? x:-x;
		// convert to arc Angle
		double angle = Math.toDegrees(Math.atan2(y, x) + (Math.PI / 2)
				- Math.toRadians(mRotation));
		if (angle < 0) {
			angle = 360 + angle;
		}
		angle -= mStartAngle;
		return angle;
	}

	private int getProgressForAngle(double angle) {
		int touchProgress = (int) Math.round(valuePerDegree() * angle);

		touchProgress = (touchProgress < 0) ? INVALID_PROGRESS_VALUE
				: touchProgress;
		touchProgress = (touchProgress > mMax) ? INVALID_PROGRESS_VALUE
				: touchProgress;
		return touchProgress;
	}

	private float valuePerDegree() {
		return (float) mMax / mSweepAngle;
	}

	private void onProgressRefresh(int progress, boolean fromUser) {
		updateProgress(progress, fromUser);
	}

	private void updateThumbPosition() {
		int thumbAngle = (int) (mStartAngle + mProgressSweep + mRotation + 90);
		mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(thumbAngle)));
		mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(thumbAngle)));
	}
	
	private void updateProgress(int progress, boolean fromUser) {

		if (progress == INVALID_PROGRESS_VALUE) {
			return;
		}

		progress = Math.min(progress, mMax);
		progress = Math.max(progress, 0);
		mProgress = progress;

		if (mOnSeekArcChangeListener != null) {
			mOnSeekArcChangeListener
					.onProgressChanged(this, progress, fromUser);
		}

		mProgressSweep = (float) progress / mMax * mSweepAngle;

		updateThumbPosition();

		invalidate();
	}

	public void setOnSeekArcChangeListener(OnSeekArcChangeListener l) {
		mOnSeekArcChangeListener = l;
	}

	public void setProgress(int progress) {
		updateProgress(progress, false);
	}

	public int getProgress() {
		return mProgress;
	}

	public int getProgressWidth() {
		return mProgressWidth;
	}

	public void setProgressWidth(int mProgressWidth) {
		this.mProgressWidth = mProgressWidth;
		mProgressPaint.setStrokeWidth(mProgressWidth);
	}
	
	public int getArcWidth() {
		return mArcWidth;
	}

	public void setArcWidth(int mArcWidth) {
		this.mArcWidth = mArcWidth;
		mArcPaint.setStrokeWidth(mArcWidth);
	}
	public int getArcRotation() {
		return mRotation;
	}

	public void setArcRotation(int mRotation) {
		this.mRotation = mRotation;
		updateThumbPosition();
	}

	public int getStartAngle() {
		return mStartAngle;
	}

	public void setStartAngle(int mStartAngle) {
		this.mStartAngle = mStartAngle;
		updateThumbPosition();
	}

	public int getSweepAngle() {
		return mSweepAngle;
	}

	public void setSweepAngle(int mSweepAngle) {
		this.mSweepAngle = mSweepAngle;
		updateThumbPosition();
	}
	
	public void setRoundedEdges(boolean isEnabled) {
		mRoundedEdges = isEnabled;
		if (mRoundedEdges) {
			mArcPaint.setStrokeCap(Paint.Cap.ROUND);
			mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
		} else {
			mArcPaint.setStrokeCap(Paint.Cap.SQUARE);
			mProgressPaint.setStrokeCap(Paint.Cap.SQUARE);
		}
	}
	
	public void setTouchInSide(boolean isEnabled) {
		int thumbHalfheight = (int) mThumb.getIntrinsicHeight() / 2;
		int thumbHalfWidth = (int) mThumb.getIntrinsicWidth() / 2;
		mTouchInside = isEnabled;
		if (mTouchInside) {
			mTouchIgnoreRadius = (float) mArcRadius / 4;
		} else {
			// Don't use the exact radius makes interaction too tricky
			mTouchIgnoreRadius = mArcRadius
					- Math.min(thumbHalfWidth, thumbHalfheight);
		}
	}
	
	public void setClockwise(boolean isClockwise) {
		mClockwise = isClockwise;
	}

	public boolean isClockwise() {
		return mClockwise;
	}

	public boolean isEnabled() {
		return mEnabled;
	}

	public void setEnabled(boolean enabled) {
		this.mEnabled = enabled;
	}

	public int getProgressColor() {
		return mProgressPaint.getColor();
	}

	public void setProgressColor(int color) {
		mProgressPaint.setColor(color);
		invalidate();
	}

	public int getArcColor() {
		return mArcPaint.getColor();
	}

	public void setArcColor(int color) {
		mArcPaint.setColor(color);
		invalidate();
	}

	public int getMax() {
		return mMax;
	}

	public void setMax(int mMax) {
		this.mMax = mMax;
	}

	@Override
	public void applySkin() {
		super.applySkin();
		switch (SkinPreference.getInstance().getSkinName()){
			case SPUtilsConfig.THEME_MODE_NIGHT:
				int arcColor = ContextCompat.getColor(getContext(),R.color.progress_gray_night_mode);
				int progressColor = ContextCompat.getColor(getContext(),R.color.default_blue_light_night_mode);
				if(mArcPaint != null){
					mArcPaint.setColor(arcColor);
				}
				if(mProgressPaint != null){
					mProgressPaint.setColor(progressColor);
				}
				break;
			case SPUtilsConfig.THEME_MODE_LIGHT:

				break;
		}
	}
}
