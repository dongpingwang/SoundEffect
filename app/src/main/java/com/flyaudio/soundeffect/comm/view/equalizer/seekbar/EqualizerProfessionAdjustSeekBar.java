package com.flyaudio.soundeffect.comm.view.equalizer.seekbar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.view.comm.SoundAdjustSeekBar;

public class EqualizerProfessionAdjustSeekBar extends SoundAdjustSeekBar {
	public EqualizerProfessionAdjustSeekBar(Context context) {
		super(context);
	}

	public EqualizerProfessionAdjustSeekBar(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public EqualizerProfessionAdjustSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void bindView(Context context, AttributeSet attrs){
		View.inflate(context, R.layout.view_equalizer_adjust_seek_bar, this);
		adjustTitle = (TextView)findViewById(R.id.equalizer_adjust_seek_bar_title);
		adjustSeek = (SeekBar)findViewById(R.id.equalizer_adjust_seek_bar);
		adjustDetail = (TextView)findViewById(R.id.equalizer_adjust_seek_bar_detail);
		adjustSeek.setThumbOffset((int) (ResUtils.getDimension(R.dimen.equalizer_seekbar_humb_offset) + 0.5f));
	}
}
