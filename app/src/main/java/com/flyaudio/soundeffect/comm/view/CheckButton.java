package com.flyaudio.soundeffect.comm.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.flyaudio.soundeffect.R;

/**
 * @author Dongping Wang
 * date 2019.12.25
 * email wangdongping@flyaudio.cn
 */
public class CheckButton extends FrameLayout {

    private View inflateView;
    private TextView remindTv;
    private TextView showTv;

    private Drawable onRemindIcon;
    private Drawable offRemindIcon;
    private String onRemindText;
    private String offRemindText;
    private String onText;
    private String offText;
    private boolean isChecked;
    private OnCheckedChangeListener onCheckedChangeListener;

    public CheckButton(@NonNull Context context) {
        this(context, null);
    }

    public CheckButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflateView = inflate(context, R.layout.view_check_btn, this);
        remindTv = (TextView) findViewById(R.id.comm_check_btn_remind_tv);
        showTv = (TextView) findViewById(R.id.comm_check_btn_show_tv);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheckButton);
        setOnRemindIcon(typedArray.getDrawable(R.styleable.CheckButton_on_remind_icon));
        setOffRemindIcon(typedArray.getDrawable(R.styleable.CheckButton_off_remind_icon));
        setOnRemindText(typedArray.getString(R.styleable.CheckButton_on_remind_text));
        setOffRemindText(typedArray.getString(R.styleable.CheckButton_off_remind_text));
        setOnText(typedArray.getString(R.styleable.CheckButton_on_text));
        setOffText(typedArray.getString(R.styleable.CheckButton_off_text));
        setChecked(typedArray.getBoolean(R.styleable.CheckButton_checked, false));
        setCheckable(typedArray.getBoolean(R.styleable.CheckButton_checkable, true));
        typedArray.recycle();
        setBackgroundResource(R.drawable.comm_check_button_bg);
        inflateView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setChecked(!isChecked());
            }
        });
    }

    public void setChecked(boolean checked) {
        remindTv.setCompoundDrawablesWithIntrinsicBounds(checked ? onRemindIcon : offRemindIcon,
                null, null, null);
        remindTv.setText(checked ? onRemindText : offRemindText);
        if (remindTv.getText().length() == 0) {
            for (Drawable drawable : remindTv.getCompoundDrawables()) {
                if (drawable != null) {
                    remindTv.setVisibility(VISIBLE);
                    break;
                }
                if (remindTv.getVisibility() != GONE) {
                    remindTv.setVisibility(GONE);
                }
            }
        } else {
            remindTv.setVisibility(VISIBLE);
        }
        showTv.setText(checked ? onText : offText);
        showTv.setTextColor(getResources().getColor(checked ? R.color.theme_color : R.color.text_color_normal));

        if (isChecked != checked) {
            isChecked = checked;
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(this, isChecked);
            }
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setCheckable(boolean checkable) {
        inflateView.setEnabled(checkable);
    }

    public boolean isCheckable() {
        return inflateView.isEnabled();
    }

    public void setOnRemindIcon(@DrawableRes int iconRes) {
        setOnRemindIcon(getResources().getDrawable(iconRes, null));
    }

    public void setOnRemindIcon(Drawable icon) {
        onRemindIcon = icon;
        if (isChecked() && onRemindIcon != null) {
            remindTv.setCompoundDrawablesWithIntrinsicBounds(onRemindIcon, null, null, null);
            remindTv.setVisibility(VISIBLE);
        }
    }

    public void setOffRemindIcon(@DrawableRes int iconRes) {
        setOffRemindIcon(getResources().getDrawable(iconRes, null));
    }

    public void setOffRemindIcon(Drawable icon) {
        offRemindIcon = icon;
        if (!isChecked() && offRemindIcon != null) {
            remindTv.setCompoundDrawablesWithIntrinsicBounds(onRemindIcon, null, null, null);
            remindTv.setVisibility(VISIBLE);
        }
    }

    public void setOnRemindText(@StringRes int remindTextRes) {
        setOnRemindText(getResources().getString(remindTextRes));
    }

    public void setOnRemindText(String remindText) {
        onRemindText = remindText;
        if (isChecked() && onRemindText != null) {
            remindTv.setText(onRemindText);
            remindTv.setVisibility(VISIBLE);
        }
    }

    public void setOffRemindText(@StringRes int remindTextRes) {
        setOffRemindText(getResources().getString(remindTextRes));
    }

    public void setOffRemindText(String remindText) {
        offRemindText = remindText;
        if (!isChecked() && offRemindText != null) {
            remindTv.setText(offRemindText);
            remindTv.setVisibility(VISIBLE);
        }
    }

    public void setOnText(@StringRes int onTextRes) {
        setOnText(getResources().getString(onTextRes));
    }

    public void setOnText(String onText) {
        this.onText = onText;
        if (isChecked()) {
            showTv.setText(this.onText);
        }
    }

    public void setOffText(@StringRes int offTextRes) {
        setOffText(getResources().getString(offTextRes));
    }

    public void setOffText(String offText) {
        this.offText = offText;
        if (!isChecked()) {
            showTv.setText(this.offText);
        }
    }

    public Drawable getOnRemindIcon() {
        return onRemindIcon;
    }

    public Drawable getOffRemindIcon() {
        return offRemindIcon;
    }

    public String getOnRemindText() {
        return onRemindText;
    }

    public String getOffRemindText() {
        return offRemindText;
    }

    public String getOnText() {
        return onText;
    }

    public String getOffText() {
        return offText;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(CheckButton checkBtn, boolean isChecked);
    }
}
