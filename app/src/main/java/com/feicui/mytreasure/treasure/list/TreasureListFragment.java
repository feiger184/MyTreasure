package com.feicui.mytreasure.treasure.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feicui.mytreasure.R;
import com.feicui.mytreasure.treasure.TreasureRepo;

/**
 * 宝藏列表
 */

public class TreasureListFragment extends Fragment {

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        recyclerView = new RecyclerView(container.getContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setBackgroundResource(R.mipmap.screen_bg);

        return recyclerView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TreasureListAdapter treasureListAdapter = new TreasureListAdapter();
        recyclerView.setAdapter(treasureListAdapter);

        treasureListAdapter.addItemData(TreasureRepo.getInstance().getTreasure());
    }
}
