package com.flyaudio.soundeffect.equalizer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.equalizer.bean.EqDataBean;

import java.util.Arrays;

/**
 * @author Dongping Wang
 * @date 20-5-16
 * email wangdongping@flyaudio.cn
 */
public class EqDataDetailDialog extends Dialog {

    public EqDataDetailDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_eq_data_detail);
    }

    public void updateMsg(EqDataBean dataBean) {
        ((TextView) findViewById(R.id.tv_eq_frequencies)).setText(Arrays.toString(dataBean.frequencies));
        ((TextView) findViewById(R.id.tv_eq_gains)).setText(Arrays.toString(dataBean.gains));
        ((TextView) findViewById(R.id.tv_eq_q_values)).setText(Arrays.toString(dataBean.qValues));
    }
}
