package com.flyaudio.soundeffect.comm.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.flyaudio.soundeffect.R;

/**
 * @author Dongping Wang
 * date 2020/5/9  21:39
 * email wangdongping@flyaudio.cn
 */
public class ResetDialog extends Dialog implements View.OnClickListener {

    private TextView tvMsg;
    private ResetListener listener;

    public ResetDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_reset);
        init();
    }

    private void init() {
        tvMsg = (TextView) findViewById(R.id.tv_msg);
        findViewById(R.id.tv_confirm).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_confirm) {
            if (listener != null) {
                listener.onReset();
            }
        } else {
            if (listener != null) {
                listener.onCancel();
            }
        }
    }


    public void setMsg(String msg) {
        tvMsg.setText(msg);
    }

    public void setListener(ResetListener listener) {
        this.listener = listener;
    }

    public interface ResetListener {
        /**
         * 重置
         */
        void onReset();

        /**
         * 取消
         */
        void onCancel();
    }
}
