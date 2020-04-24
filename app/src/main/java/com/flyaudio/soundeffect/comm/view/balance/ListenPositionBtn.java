package com.flyaudio.soundeffect.comm.view.balance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.flyaudio.lib.utils.ConvertUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.util.SkinUtils;
import com.flyaudio.soundeffect.logic.comm.ListenPositionLogic;

/**
 * @author Dongping Wang
 * @date 2019.12.25
 */
public class ListenPositionBtn extends FrameLayout {

    private RadioButton[] mBtns = new RadioButton[ListenPositionLogic.LISTEN_POSITIONS.length];
    private IListenPositionListener mListener;

    public ListenPositionBtn(@NonNull Context context) {
        this(context, null);
    }

    public ListenPositionBtn(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListenPositionBtn(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ListenPositionBtn(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_listen_position, this, true);
        mBtns[0] = (RadioButton) findViewById(R.id.position_FL_btn);
        mBtns[1] = (RadioButton) findViewById(R.id.position_FR_btn);
        mBtns[2] = (RadioButton) findViewById(R.id.position_F_btn);
        mBtns[3] = (RadioButton) findViewById(R.id.position_R_btn);
        mBtns[4] = (RadioButton) findViewById(R.id.position_ALL_btn);
        mBtns[5] = (RadioButton) findViewById(R.id.position_OFF_btn);
        for (RadioButton btn : mBtns) {
            if (btn != null) {
                SkinUtils.setBackgroundEffect(R.integer.bg_listen_position_btn, btn, R.drawable.sf_position_btn_bg, R.drawable.sf_position_btn_bg_pic);
                btn.setOnClickListener(mClickListener);
            }
        }
    }

    public void setChecked(@ListenPositionLogic.ListenPositionType int position) {
        int i = ListenPositionLogic.indexOfListenPosition(position);
        mBtns[i].setChecked(true);
    }

    public void setRearVisibility(boolean visibility) {
        mBtns[2].setVisibility(visibility ? VISIBLE : GONE);
        mBtns[3].setVisibility(visibility ? VISIBLE : GONE);
    }

    public String getText(@ListenPositionLogic.ListenPositionType int position) {
        int i = ListenPositionLogic.indexOfListenPosition(position);
        return mBtns[i].getText().toString();
    }

    public void setText(@ListenPositionLogic.ListenPositionType int position, String text) {
        int i = ListenPositionLogic.indexOfListenPosition(position);
        mBtns[i].setText(text);
    }

    @Deprecated
    public void updateMargin(boolean reared) {
        RadioGroup.LayoutParams param = (RadioGroup.LayoutParams) mBtns[4].getLayoutParams();
        if (reared){
            param.topMargin = ConvertUtils.dp2px( 19.46F);
        }else {
            param.topMargin = ConvertUtils.dp2px(48.65F);
        }
        mBtns[4].setLayoutParams(param);
    }

    private OnClickListener mClickListener = new OnClickListener() {
        @SuppressWarnings({"UnnecessaryReturnStatement", "ConstantConditions"})
        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            int index = 0;
            if (viewId == R.id.position_FL_btn) {
                index = 0;
            } else if (viewId == R.id.position_FR_btn) {
                index = 1;
            } else if (viewId == R.id.position_F_btn) {
                index = 2;
            } else if (viewId == R.id.position_R_btn) {
                index = 3;
            } else if (viewId == R.id.position_ALL_btn) {
                index = 4;
            } else if (viewId == R.id.position_OFF_btn) {
                index = 5;
            } else {
                index = -1;
                return;
            }
            if (mListener != null) {
                mListener.onChange(ListenPositionLogic.LISTEN_POSITIONS[index]);
            }
        }
    };

    public interface IListenPositionListener {
        void onChange(@ListenPositionLogic.ListenPositionType int position);
    }

    public void setListenPositionListener(IListenPositionListener listener) {
        this.mListener = listener;
    }
}
