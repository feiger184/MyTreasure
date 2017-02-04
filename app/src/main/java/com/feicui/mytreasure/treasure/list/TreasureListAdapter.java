package com.feicui.mytreasure.treasure.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.feicui.mytreasure.custom.TreasureView;
import com.feicui.mytreasure.treasure.Treasure;
import com.feicui.mytreasure.treasure.detail.TreasureDetailActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * RecyclerView 的适配器
 */

public class TreasureListAdapter extends RecyclerView.Adapter<TreasureListAdapter.MyViewHolder> {

    private ArrayList<Treasure> data = new ArrayList<>();

    public void addItemData(List<Treasure> list){
        data.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TreasureView treasureView = new TreasureView(parent.getContext());
        return new MyViewHolder(treasureView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final Treasure treasure = data.get(position);
        holder.treasureView.bindTreasure(treasure);

        holder.treasureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreasureDetailActivity.open(v.getContext(), treasure);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TreasureView treasureView;
        public MyViewHolder(TreasureView itemView) {
            super(itemView);
            treasureView = itemView;
        }
    }

}
