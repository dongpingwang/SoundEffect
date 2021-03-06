package com.flyaudio.soundeffect.equalizer.fragment;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.flyaudio.lib.toast.Toaster;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;

import com.flyaudio.soundeffect.comm.base.AbstractFragment;
import com.flyaudio.soundeffect.dsp.service.EffectManager;
import com.flyaudio.soundeffect.equalizer.activity.EqAdjustActivity;
import com.flyaudio.soundeffect.equalizer.adapter.EqListAdapter;
import com.flyaudio.soundeffect.equalizer.bean.EqMode;
import com.flyaudio.soundeffect.equalizer.dialog.EqDeleteDialog;
import com.flyaudio.soundeffect.equalizer.dialog.EqReNameDialog;
import com.flyaudio.soundeffect.equalizer.logic.EqManager;
import com.flyaudio.soundeffect.main.event.Event;

import java.util.List;


/**
 * @author Dongping Wang
 * @date 20-4-24
 * email wangdongping@flyaudio.cn
 */
public class EqFragment extends AbstractFragment {

    private static final int DEFAULT_SPAN_COUNT = 4;
    private static final int EQ_MODE_MAX_COUNT = 30;

    private TextView tvAdjust;
    private RecyclerView rvEqList;
    private EqListAdapter adapter;
    private EqReNameDialog eqEditDialog;
    private EqDeleteDialog eqDeleteDialog;
    private static EqManager eqManager = EqManager.getInstance();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_equalizer;
    }

    @Override
    protected void onInit() {
        initView();
        initAdapter();
    }

    private void initView() {
        tvAdjust = getView(R.id.tv_adjust);
        rvEqList = getView(R.id.rv_eq_list);
        tvAdjust.setOnClickListener(onClickListener);
    }

    private void initAdapter() {
        final List<EqMode> eqList = eqManager.getEqList();
        adapter = new EqListAdapter(context(), eqList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context(), DEFAULT_SPAN_COUNT);
        gridLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        rvEqList.setLayoutManager(gridLayoutManager);
        rvEqList.setAdapter(adapter);
        // 更新上次的位置
        adapter.updateCheckedById(eqManager.getCurrentEq());

        adapter.setOnItemListener(new EqListAdapter.OnItemListener() {
            @Override
            public void onItemClick(int position) {
                if (position != adapter.getCurrent()) {
                    adapter.updateCurrent(position);
                    eqManager.saveCurrentEq(adapter.getData(position).getId());
                    EffectManager.getInstance().setAllEq();
                }
            }

            @Override
            public void onItemEdit(int position) {
                editEqMode(position);
            }

            @Override
            public void onCreateMode() {
                if (adapter.getItemViewCount() < EQ_MODE_MAX_COUNT + 1) {
                    int customEqCount = eqManager.getCustomEqCount();
                    int id = eqManager.getMaxEqId() + 1;
                    String name = ResUtils.getString(R.string.custom_, customEqCount + 1);
                    adapter.addEqMode(new EqMode(id, name));
                    eqManager.saveCurrentEq(-1);
                    eqManager.saveEqList(adapter.getDatas());
                    eqManager.saveMaxEqId(id);
                }
            }
        });
    }

    private void editEqMode(final int position) {
        if (position < 0 || position > adapter.getDatas().size()) {
            return;
        }
        if (eqEditDialog == null) {
            eqEditDialog = new EqReNameDialog(context());
        }
        eqEditDialog.show();
        final EqMode data = adapter.getData(position);
        final String oldName = data.getName();
        eqEditDialog.updateEqName(oldName);
        eqEditDialog.setListener(new EqReNameDialog.EqModeEditListener() {
            @Override
            public void onRename(String name) {
                if (TextUtils.isEmpty(name)) {
                    Toaster.show(ResUtils.getString(R.string.eq_name_not_empty));
                } else if (TextUtils.equals(name, oldName)) {
                    Toaster.show(ResUtils.getString(R.string.eq_name_be_new));
                } else {
                    data.setName(name);
                    adapter.updateItem(position, data);
                    eqManager.saveEqList(adapter.getDatas());
                    onCancel();
                }
            }

            @Override
            public void onDelete() {
                if (eqDeleteDialog == null) {
                    eqDeleteDialog = new EqDeleteDialog(context());
                }
                eqDeleteDialog.show();
                eqEditDialog.hide();
                eqDeleteDialog.setListener(new EqDeleteDialog.EqDeleteListener() {
                    @Override
                    public void onDelete() {
                        eqManager.clearEqDataWhenDelete(adapter.getData(position).getId());
                        adapter.deleteEqMode(position);
                        eqManager.saveEqList(adapter.getDatas());
                        eqManager.saveCurrentEq(-1);
                        eqDeleteDialog.cancel();
                        eqEditDialog.cancel();
                    }

                    @Override
                    public void onCancel() {
                        eqDeleteDialog.cancel();
                        eqEditDialog.show();
                    }
                });
            }

            @Override
            public void onCancel() {
                eqEditDialog.cancel();
            }
        });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tv_adjust) {
                EqMode eqMode = adapter.getChecked();
                if (eqMode != null) {
                    Intent intent = new Intent(context(), EqAdjustActivity.class);
                    intent.putExtra(EqAdjustActivity.INTENT_MODE_NAME, eqMode);
                    startActivity(intent);
                } else {
                    Toaster.show(ResUtils.getString(R.string.please_check_one_mode));
                }
            }
        }
    };

    @Override
    public void onEvent(String event) {
        if (TextUtils.equals(event, Event.RESTORE_DATA)) {
            adapter.updateAdapter(eqManager.getEqList());
            adapter.updateCheckedById(eqManager.getCurrentEq());
        }
    }
}
