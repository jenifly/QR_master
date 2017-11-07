package com.jenifly.qr_master.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.jenifly.qr_master.R;
import com.jenifly.qr_master.helper.Item;
import com.jenifly.qr_master.helper.ItemTouchHelperAdapter;
import com.jenifly.qr_master.helper.ItemTouchHelperViewHolder;
import com.jenifly.qr_master.helper.JyConstants;
import com.jenifly.qr_master.interfaces.OnItemClickListener;
import com.jenifly.qr_master.interfaces.OnStartDragListener;
import com.jenifly.qr_master.tool.FileTool;
import com.jenifly.qr_master.view.dialog.JyDialogSure;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> implements ItemTouchHelperAdapter {

    private final Context context;
    private ArrayList<Item> itemList;
    private TextView tvNumber;
    private boolean isDelete = true;
    private final OnStartDragListener dragStartListener;
    private OnItemClickListener onItemClickListener;

    public ItemAdapter(Context context, OnStartDragListener dragStartListener, TextView tvNumber) {
        this.context = context;
        this.dragStartListener = dragStartListener;
        this.tvNumber = tvNumber;
    }

    public void setItemList(ArrayList<Item> itemList) {
        this.itemList = itemList;
        this.tvNumber.setText(String.valueOf(itemList.size()));
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onItemDismiss(final int position) {
        final Item item = itemList.get(position);
        notifyItemRemoved(position);
        itemList.remove(position);
        notifyItemRangeChanged(0, getItemCount());
        tvNumber.setText(String.valueOf(itemList.size()));
        final Snackbar snackbar =  Snackbar
                .make(tvNumber,context.getResources().getString(R.string.item_deleted), Snackbar.LENGTH_LONG)
                .setActionTextColor(ContextCompat.getColor(context, R.color.white))
                .setAction(context.getResources().getString(R.string.item_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       itemList.add(position, item);
                        notifyItemInserted(position);
                        isDelete = false;
                        tvNumber.setText(String.valueOf(itemList.size()));
                    }
                });
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.mi_green));
        TextView tvSnack = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tvSnack.setTextColor(Color.WHITE);
        snackbar.show();
        Runnable runnableUndo = new Runnable() {
            @Override
            public void run() {
                snackbar.dismiss();
                if(isDelete){
                    tvNumber.setText(String.valueOf(itemList.size()));
                    JyConstants.sqlBaseHelper.deleteById(item.getItemId());
                    new File(FileTool.getCecheFolder() + File.separator + item.getItemName()).delete();
                }
            }
        };
        Handler handlerUndo = new Handler();
        handlerUndo.postDelayed(runnableUndo,2500);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(itemList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(itemList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder itemViewHolder, int position) {

        final Item item = itemList.get(position);
        itemViewHolder.tvItemName.setText(item.getItemContent());
        itemViewHolder.item_iv.setImageURI(Uri.fromFile(new File(JyConstants.CachePath + item.getItemName())));
        itemViewHolder.relativeReorder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) ==
                        MotionEvent.ACTION_DOWN) {
                    dragStartListener.onStartDrag(itemViewHolder);
                }
                return false;
            }
        });
        itemViewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener != null)
                    onItemClickListener.onItemClick(item);
            }
        });
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_adapter, viewGroup, false);
        return new ItemViewHolder(itemView);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder,View.OnClickListener {

        protected RelativeLayout container;
        protected TextView tvItemName;
        protected ImageView ivReorder;
        protected ImageView item_iv;
        protected RelativeLayout relativeReorder;

        public ItemViewHolder(final View v) {
            super(v);
            container = v.findViewById(R.id.container);
            tvItemName = v.findViewById(R.id.tvItemName);
            ivReorder = v.findViewById(R.id.ivReorder);
            relativeReorder = v.findViewById(R.id.relativeReorder);
            item_iv = v.findViewById(R.id.item_iv);
        }

        @Override
        public void onClick(View view) {
        }

        @Override
        public void onItemSelected(Context context) {
            itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.mi_green));
            tvItemName.setTextColor(ContextCompat.getColor(context, R.color.white));
            ivReorder.setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN);
        }

        @Override
        public void onItemClear(Context context) {
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
            itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            ivReorder.setColorFilter(ContextCompat.getColor(context, R.color.textlight), PorterDuff.Mode.SRC_IN);
            tvItemName.setTextColor(ContextCompat.getColor(context, R.color.textlight));
        }
    }
}
