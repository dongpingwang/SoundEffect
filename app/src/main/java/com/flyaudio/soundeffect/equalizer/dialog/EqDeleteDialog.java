package com.flyaudio.soundeffect.equalizer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.flyaudio.soundeffect.R;

/**
 * @author Dongping Wang
 * @date 20-5-8
 * email wangdongping@flyaudio.cn
 */
public class EqDeleteDialog extends Dialog implements View.OnClickListener {

    private EqDeleteListener listener;

    public EqDeleteDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_eq_delete);
        findViewById(R.id.tv_del).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_del) {
            if (listener != null) {
                listener.onDelete();
            }
        } else {
            if (listener != null) {
                listener.onCancel();
            }
        }
    }


    public void setListener(EqDeleteListener listener) {
        this.listener = listener;
    }


    public interface EqDeleteListener {
        void onDelete();

        void onCancel();
    }
}
