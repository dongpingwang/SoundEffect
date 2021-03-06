package com.flyaudio.soundeffect.comm.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.flyaudio.soundeffect.R;

import java.util.Arrays;
import java.util.List;

/**
 * @author Dongping Wang
 * date 2019.12.25
 * email wangdongping@flyaudio.cn
 */
public class ListenPositionButtons extends FrameLayout implements RadioGroup.OnCheckedChangeListener {

    private List<RadioButton> buttons;
    private ListenPositionCheckedListener listener;


    public ListenPositionButtons(@NonNull Context context) {
        this(context, null);
    }

    public ListenPositionButtons(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListenPositionButtons(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_btn_listen_position, this);
        buttons = Arrays.asList(
                (RadioButton) findViewById(R.id.btn_front_left),
                (RadioButton) findViewById(R.id.btn_front_right),
                (RadioButton) findViewById(R.id.btn_front_row),
                (RadioButton) findViewById(R.id.btn_back_row),
                (RadioButton) findViewById(R.id.btn_all),
                (RadioButton) findViewById(R.id.btn_off)
        );
        ((RadioGroup) findViewById(R.id.btn_group)).setOnCheckedChangeListener(this);

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        int position = buttons.indexOf((RadioButton) radioGroup.findViewById(id));
        if (listener != null) {
            listener.onCheckedChanged(position);
        }
    }

    public void setChecked(int position) {
        if (position >= 0 && position < buttons.size()) {
            buttons.get(position).setChecked(true);
        }
    }

    public String getPositionName() {
        String name = buttons.get(0).getText().toString();
        for (RadioButton button : buttons) {
            if (button.isChecked()) {
                name = button.getText().toString();
                break;
            }
        }
        return name;
    }


    public void displayIfBackRowOff(boolean on) {
        int visibility = on ? VISIBLE : GONE;
        RadioGroup.LayoutParams param = (RadioGroup.LayoutParams) buttons.get(4).getLayoutParams();
        if (on) {
            param.topMargin = getResources().getDimensionPixelSize(R.dimen.btn_listen_position_margin_top);
        } else {
            param.topMargin = getResources().getDimensionPixelSize(R.dimen.btn_listen_position_margin_top_no_rear);
        }
        buttons.get(4).setLayoutParams(param);
        buttons.get(2).setVisibility(visibility);
        buttons.get(3).setVisibility(visibility);
    }

    public interface ListenPositionCheckedListener {
        /**
         * 收听位置发生改变
         *
         * @param position 收听位置，值为0-5
         */
        void onCheckedChanged(int position);
    }

    public void setListenPositionCheckedListener(ListenPositionCheckedListener listener) {
        this.listener = listener;
    }
}
