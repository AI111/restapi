package com.example.sasha.resyapi;

import android.support.v7.widget.RecyclerView;

/**
 * Created by sasha on 10.10.15.
 */
public interface OnItemRemovedListener {
    void onDismiss(RecyclerView recyclerView,RecyclerView.ViewHolder viewHolder,int position);
}
