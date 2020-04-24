package com.flyaudio.soundeffect.comm.view.comm;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;


public class SoundAdjustSeekBar extends FrameLayout {

	protected TextView adjustTitle;
	protected ImageView adjustIcon;
	protected SeekBar adjustSeek;
	protected TextView adjustDetail;

	private SeekBarChangeListener seekBarChangeListener;

	private int min;
	private int step;

	public SoundAdjustSeekBar(Context context) {
		this(context, null);
	}

	public SoundAdjustSeekBar(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SoundAdjustSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		bindView(context, attrs);
		init(context, attrs);
	}

	protected void bindView(Context context, AttributeSet attrs){
		View.inflate(context, R.layout.view_sound_adjust_seek_bar, this);
		adjustTitle = getView(R.id.sound_adjust_seek_bar_title);
		adjustIcon = getView(R.id.sound_adjust_seek_bar_icon);
		adjustSeek = getView(R.id.sound_adjust_seek_bar);
		adjustDetail = getView(R.id.sound_adjust_seek_bar_detail);
	}

	private <T extends View> T getView(@IdRes int id){
		return (T)findViewById(id);
	}

	protected void init(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SoundAdjustSeekBar);
		min = typedArray.getInteger(R.styleable.SoundAdjustSeekBar_seek_bar_min, 0);
		step = typedArray.getInteger(R.styleable.SoundAdjustSeekBar_seek_bar_step, 1);
		setMax(typedArray.getInteger(R.styleable.SoundAdjustSeekBar_seek_bar_max, 100));
		setProgress(typedArray.getInteger(R.styleable.SoundAdjustSeekBar_seek_bar_progress, min));
		setTitle(typedArray.getString(R.styleable.SoundAdjustSeekBar_seek_bar_title));
		setIcon(typedArray.getDrawable(R.styleable.SoundAdjustSeekBar_seek_bar_icon));
		setValueVisibility(typedArray.getInt(R.styleable.SoundAdjustSeekBar_seek_bar_value_visibility, View.VISIBLE));
		setTitleVisibility(typedArray.getInt(R.styleable.SoundAdjustSeekBar_seek_bar_title_visibility, View.VISIBLE));
		setIconVisibility(typedArray.getInt(R.styleable.SoundAdjustSeekBar_seek_bar_icon_visibility, View.GONE));
		setEnabled(typedArray.getBoolean(R.styleable.SoundAdjustSeekBar_seek_bar_enable, true));
		typedArray.recycle();
		adjustDetail.setText(String.valueOf(min + adjustSeek.getProgress() * step));
		adjustSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				adjustDetail.setText(String.valueOf(min + i * step));
				if(seekBarChangeListener != null)
					seekBarChangeListener.onProgressChanged(SoundAdjustSeekBar.this, min + i * step, b);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				if(seekBarChangeListener != null)
					seekBarChangeListener.onStartTrackingTouch(SoundAdjustSeekBar.this);
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if(seekBarChangeListener != null)
					seekBarChangeListener.onStopTrackingTouch(SoundAdjustSeekBar.this);
			}
		});
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		adjustTitle.setEnabled(enabled);
		adjustSeek.setEnabled(enabled);
		adjustDetail.setEnabled(enabled);
		if(adjustIcon != null)
			adjustIcon.setEnabled(enabled);
	}

	public void setValueVisibility(int visibility){
		adjustDetail.setVisibility(visibility);
	}
	public void setValueDetail(int value){
		String valueStr=(value>0?"+"+String.valueOf(value):String.valueOf(value))+" "+ ResUtils.getString(R.string.db_unit).toLowerCase();
		adjustDetail.setText(valueStr);
	}
	public void setValueDetailHighlight(int value){
		adjustDetail.setTextColor(ResUtils.getColor(R.color.theme_color));
		setValueDetail(value);
	}
	public void setValueDetailNormal(){
		adjustDetail.setTextColor(ResUtils.getColor(R.color.text_color_white));
	}

	public void setTitleVisibility(int visibility){
		adjustTitle.setVisibility(visibility);
	}

	public void setIconVisibility(int visibility){
		if(adjustIcon != null)
			adjustIcon.setVisibility(visibility);
	}

	public int getValueVisibility(){
		return adjustDetail.getVisibility();
	}

	public int getTitleVisibility(){
		return adjustTitle.getVisibility();
	}

	public int getIconVisibility(){
		if(adjustIcon != null)
			return adjustIcon.getVisibility();
		return View.GONE;
	}

	public int getProgress(){
		return min + adjustSeek.getProgress() * step;
	}

	public void setProgress(int progress){
		adjustSeek.setProgress((progress - min) / step);
	}

	public String getTitle(){
		return (String) adjustTitle.getText();
	}

	public void setTitle(String title){
		adjustTitle.setText(title);
	}

	public void setTitle(@StringRes int titleRes){
		setTitle(ResUtils.getString(titleRes));
	}

	public void setIcon(@DrawableRes int iconRes){
		if(adjustIcon != null)
			adjustIcon.setImageDrawable(ResUtils.getDrawable(iconRes));
	}

	public void setIcon(Drawable icon){
		if(adjustIcon != null)
			adjustIcon.setImageDrawable(icon);
	}

	public void setStep(int step){
		this.step = step;
	}

	public int getStep(){
		return this.step;
	}

	public void setMin(int min){
		int progress = getProgress();
		this.min = min;
		if(progress < this.min)
			setProgress(this.min);
	}

	public int getMin(){
		return this.min;
	}

	public void setMax(int max){
		int progress = getProgress();
		adjustSeek.setMax((max - min) / step);
		if(progress > max)
			setProgress(max);
	}

	public int getMax(){
		return adjustSeek.getMax() * step + min;
	}

	public void setSeekBarChangeListener(SeekBarChangeListener seekBarChangeListener) {
		this.seekBarChangeListener = seekBarChangeListener;
	}

	public interface SeekBarChangeListener{
		void onProgressChanged(SoundAdjustSeekBar seekBar, int progress, boolean byTouch);
		void onStartTrackingTouch(SoundAdjustSeekBar seekBar);
		void onStopTrackingTouch(SoundAdjustSeekBar seekBar);
	}
}
