package com.flyaudio.soundeffect.comm.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyaudio.soundeffect.R;

/**
 * Created by x8 on 17-9-26.
 */

@SuppressWarnings("all")
public class BtnSelectLayout extends LinearLayout implements View.OnClickListener {

    private Context mContext;
    private BtnSelectedListener btnSelectedListener;
    private int itemPadding;
    private int itemMargin;
    private Drawable itemSelectedBackground;
    private Drawable itemUnselectedBackground;
    private float itemTextSize;

    public BtnSelectLayout(Context context) {
        this(context, null);
    }

    public BtnSelectLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BtnSelectLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        setGravity(Gravity.CENTER);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BtnSelectLayout);
        itemPadding = (int) (typedArray.getDimension(R.styleable.BtnSelectLayout_item_padding, getResources().getDimension(R.dimen.btn_select_layout_padding)) + 0.5f);
        itemMargin = (int) (typedArray.getDimension(R.styleable.BtnSelectLayout_item_margin, getResources().getDimension(R.dimen.btn_select_layout_margin)) + 0.5f);
        itemSelectedBackground = typedArray.getDrawable(R.styleable.BtnSelectLayout_item_selected_background);
        itemUnselectedBackground = typedArray.getDrawable(R.styleable.BtnSelectLayout_item_unselected_background);
        itemTextSize = typedArray.getDimension(R.styleable.BtnSelectLayout_item_text_size, getResources().getDimension(R.dimen.font_16));
        typedArray.recycle();
        if (itemSelectedBackground == null)
            itemSelectedBackground = getResources().getDrawable(R.color.theme_color);
        if (itemUnselectedBackground == null)
            itemUnselectedBackground = getResources().getDrawable(R.color.btn_uncheck_bg);
    }

    public void addButtons(String... texts) {
        for (String text : texts)
            addButton(text);
    }

    public void addButtons(@StringRes int... stringReses) {
        for (int stringRes : stringReses)
            addButton(stringRes);
    }

    public void addButton(@StringRes int stringRes) {
        addButton(getResources().getString(stringRes));
    }

    public void addButton(String text) {
        TextView button = new TextView(mContext);
        button.setMinWidth(0);
        button.setMinHeight(0);
        button.setText(text);
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemTextSize);
        button.setTextColor(getResources().getColor(R.color.text_color_normal));
        button.setGravity(Gravity.CENTER);


        button.setBackground(itemUnselectedBackground);
        button.setForeground(getResources().getDrawable(R.drawable.comm_ripple));
        int orientation = getOrientation();
        LayoutParams layoutParams;
        if (orientation == HORIZONTAL)
            layoutParams = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
        else
            layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
        button.setPadding(itemPadding, itemPadding, itemPadding, itemPadding);
        layoutParams.setMargins(itemMargin, itemMargin, itemMargin, itemMargin);
        button.setLayoutParams(layoutParams);
        button.setOnClickListener(this);
        super.addView(button);
    }

    @Override
    public void onClick(View view) {
        View preSelectBtn = (View) getTag();
        if (preSelectBtn != view) {
            if (preSelectBtn != null) {
                preSelectBtn.setSelected(false);
                preSelectBtn.setBackground(itemUnselectedBackground);
            }
            if (view != null) {
                view.setSelected(true);
                view.setBackground(itemSelectedBackground);
            }
            setTag(view);
        }
        if (btnSelectedListener != null)
            btnSelectedListener.onBtnSelected(indexOfChild(preSelectBtn), indexOfChild(view));
    }

    public int getSelectIndex() {
        return indexOfChild((View) getTag());
    }

    public void setSelectIndex(int index) {
        onClick(getChildAt(index));
    }

    public int getBtnCount() {
        return getChildCount();
    }

    public void setBtnSelectedListener(BtnSelectedListener btnSelectedListener) {
        this.btnSelectedListener = btnSelectedListener;
    }

    public interface BtnSelectedListener {
        void onBtnSelected(int preIndex, int selectedIndex);
    }
}
