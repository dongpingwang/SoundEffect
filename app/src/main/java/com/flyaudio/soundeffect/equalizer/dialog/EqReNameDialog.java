package com.flyaudio.soundeffect.equalizer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import com.flyaudio.soundeffect.R;

/**
 * @author Dongping Wang
 * @date 20-5-8
 * email wangdongping@flyaudio.cn
 */
public class EqReNameDialog extends Dialog implements View.OnClickListener {

    private EditText etEqName;
    private EqModeEditListener listener;

    public EqReNameDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_eq_rename);
        init();
    }

    private void init() {
        etEqName = (EditText) findViewById(R.id.et_name);
        findViewById(R.id.tv_del).setOnClickListener(this);
        findViewById(R.id.tv_save).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_del) {
            if (listener != null) {
                listener.onDelete();
            }
        } else if (v.getId() == R.id.tv_save) {
            if (listener != null) {
                listener.onRename(etEqName.getText().toString());
            }
        } else {
            if (listener != null) {
                listener.onCancel();
            }
        }
    }

    public void updateEqName(String name) {
        etEqName.setText(name);
    }

    public void setListener(EqModeEditListener listener) {
        this.listener = listener;
    }

    public interface EqModeEditListener {
        /**
         * 重命名eq模式名称
         *
         * @param name eq模式名称
         */
        void onRename(String name);

        /**
         * 删除eq模式
         */
        void onDelete();

        /**
         * 关闭弹框
         */
        void onCancel();
    }
}
