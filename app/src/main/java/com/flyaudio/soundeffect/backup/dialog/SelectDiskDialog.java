package com.flyaudio.soundeffect.backup.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.flyaudio.lib.adapter.RecyclerViewAdapter;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.backup.adapter.DiskListAdapter;
import com.flyaudio.soundeffect.backup.bean.Device;

import java.util.List;

/**
 * @author Dongping Wang
 * date 2020/5/23  23:06
 * email wangdongping@flyaudio.cn
 */
public class SelectDiskDialog extends Dialog implements View.OnClickListener,
        RecyclerViewAdapter.OnItemClickListener {

    private List<Device> disks;
    private RecyclerView rvDiskList;
    private DiskListAdapter adapter;
    private SelectDiskListener listener;

    public SelectDiskDialog(@NonNull Context context, List<Device> disks) {
        super(context);
        this.disks = disks;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_disk);
        rvDiskList = findViewById(R.id.rv_disk_list);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
        initAdapter();
    }

    private void initAdapter() {
        adapter = new DiskListAdapter(getContext(), disks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvDiskList.setAdapter(adapter);
        rvDiskList.setLayoutManager(layoutManager);
        adapter.setOnItemClickListener(this);
    }

    public void updateAdapter(List<Device> disks) {
        this.disks = disks;
        this.adapter.refreshAdapter();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_cancel) {
            dismiss();
        }
    }

    @Override
    public void onItemClick(int position) {
        if (listener != null) {
            adapter.updateSelect(position);
            dismiss();
            listener.onSelectDisk(adapter.getData(position));
        }
    }

    public interface SelectDiskListener {
        /**
         * 点击选中保存音效文件的磁盘
         * @param device 磁盘
         */
        void onSelectDisk(Device device);
    }

    public void setListener(SelectDiskListener listener) {
        this.listener = listener;
    }
}
