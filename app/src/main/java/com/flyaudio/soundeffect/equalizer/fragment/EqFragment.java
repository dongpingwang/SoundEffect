package com.flyaudio.soundeffect.equalizer.fragment;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.flyaudio.lib.base.BaseFragment;
import com.flyaudio.lib.toast.Toaster;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.equalizer.activity.EqAdjustActivity;
import com.flyaudio.soundeffect.equalizer.adapter.EqListAdapter;
import com.flyaudio.soundeffect.equalizer.bean.EqMode;
import com.flyaudio.soundeffect.equalizer.logic.EqManager;

import java.util.List;


/**
 * @author Dongping Wang
 * @date 20-4-24
 * email wangdongping@flyaudio.cn
 */
public class EqFragment extends BaseFragment {

    private static final int DEFAULT_SPAN_COUNT = 4;
    private static final int EQ_MODE_MAX_COUNT = 30;

    private TextView tvAdjust;
    private RecyclerView rvEqList;
    private EqListAdapter adapter;
    private EqManager eqManager = EqManager.getInstance();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_equalizer;
    }

    @Override
    protected void init() {
        initView();
        initAdapter();
    }

    private void initView() {
        tvAdjust = getView(R.id.tv_adjust);
        rvEqList = getView(R.id.rv_eq_list);
        tvAdjust.setOnClickListener(onClickListener);
    }

    private void initAdapter() {

        List<EqMode> eqList = eqManager.getEqList();
        adapter = new EqListAdapter(context(), eqList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), DEFAULT_SPAN_COUNT);
        gridLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        rvEqList.setLayoutManager(gridLayoutManager);
        rvEqList.setAdapter(adapter);
        // 更新上次的位置
        adapter.updateCheckedById(eqManager.getCurrentEq());

        adapter.setOnItemListener(new EqListAdapter.OnItemListener() {
            @Override
            public void onItemClick(int position) {
                adapter.updateChecked(position);
                eqManager.saveCurrentEq(adapter.getChecked().getId());
            }

            @Override
            public void onItemEdit(int position) {

            }

            @Override
            public void onCreateMode() {
                if (adapter.getItemViewCount() < EQ_MODE_MAX_COUNT + 1) {
                    int customEqCount = eqManager.getCustomEqCount();
                    int id = eqManager.getMaxEqId() + 1;
                    String name = ResUtils.getString(R.string.custom_, customEqCount + 1);
                    // 添加到倒数第二个
                    adapter.addItem(adapter.getItemViewCount() - 1, new EqMode(id, name));
                    adapter.clearChecked();
                    eqManager.saveEqList(adapter.getDatas());
                    eqManager.saveMaxEqId(id);
                }
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
}
