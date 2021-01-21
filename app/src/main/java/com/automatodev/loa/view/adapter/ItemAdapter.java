package com.automatodev.loa.view.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.automatodev.loa.R;
import com.automatodev.loa.model.entity.ItemEntity;
import com.automatodev.loa.view.extras.FormatTools;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.DataHandler> {

    private List<ItemEntity> itemEntityList;
    private OnClickListener listener;
    private OnItemLongClickListener mListener;
    private FormatTools formatTools;
    private int typeList;
    private RelativeLayout relativeResources;
    private Activity context;
    private boolean myList;

    public interface OnClickListener {

        void onDeleteClick(int position);
        void onItemClick(int position);

    }

    public interface OnItemLongClickListener {

        void onItemLongClick(int position);

    }

    public void setItemEntityList(List<ItemEntity> itemEntityList){
        this.itemEntityList = itemEntityList;
    }
    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public void setOnLongItemClickListener(OnItemLongClickListener mListener) {
        this.mListener = mListener;
    }

    public ItemAdapter(Activity context, List<ItemEntity> itemEntityList, RelativeLayout relativeResources, boolean myList,int typeList) {
        this.context = context;
        this.itemEntityList = itemEntityList;
        this.relativeResources = relativeResources;
        this.myList = myList;
        this.typeList = typeList;
    }

    public ItemAdapter(Activity context, List<ItemEntity> itemEntityList, int typeList, RelativeLayout relativeResources) {
        this.itemEntityList = itemEntityList;
        this.context = context;
        this.typeList = typeList;
        this.relativeResources = relativeResources;
        formatTools = new FormatTools();

    }

    @NonNull
    @Override
    public DataHandler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(!myList ? R.layout.layout_itens_global : R.layout.layout_itens_profile, parent, false);
        return new DataHandler(view, listener, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DataHandler holder, int position) {
        if (!myList) {
            if (typeList == 1) {
                holder.btnDelete_layout.setVisibility(View.VISIBLE);
            }
            holder.txtTitleItem_layout.setText(itemEntityList.get(position).getTitle());
            holder.txtUf_layout.setText(itemEntityList.get(position).getUf());
            holder.txtDate_layout.setText(formatTools.dateFormatnoHour(itemEntityList.get(position).getDateCad()));
            holder.txtPrice_layout.setText(formatTools.decimalFormat(itemEntityList.get(position).getPrice()));
            if (itemEntityList.get(position).getImages().get(0).getUrlImage() != null)
                Glide.with(context).load(itemEntityList.get(position).getImages().get(0).getUrlImage()).transition(new DrawableTransitionOptions().crossFade())
                        .into(holder.imgItem_layout);
        } else {
            holder.txtTitleItem_layout_profile.setText(itemEntityList.get(position).getTitle());
            if (itemEntityList.get(position).getImages().get(0).getUrlImage() != null)
                Glide.with(context).load(itemEntityList.get(position).getImages().get(0).getUrlImage()).transition(new DrawableTransitionOptions().crossFade())

                        .into(holder.imgItem_layout_profile);
        }

    }

    @Override
    public int getItemCount() {
        if (typeList != 0)
            if (itemEntityList.size() == 0)
                relativeResources.setVisibility(View.VISIBLE);
            else
                relativeResources.setVisibility(View.GONE);
        return itemEntityList.size();
    }

    public class DataHandler extends RecyclerView.ViewHolder {

        private TextView txtTitleItem_layout;
        private TextView txtUf_layout;
        private TextView txtDate_layout;
        private TextView txtPrice_layout;
        private ImageView imgItem_layout;
        private ImageButton btnDelete_layout;
        private TextView txtViews_layout;
        private ImageView imgItem_layout_profile;
        private TextView txtTitleItem_layout_profile;

        public DataHandler(@NonNull View itemView, final OnClickListener listener, final OnItemLongClickListener mListener) {
            super(itemView);
            txtTitleItem_layout = itemView.findViewById(R.id.txtTitleItem_layout);
            txtUf_layout = itemView.findViewById(R.id.txtUf_layout);
            txtDate_layout = itemView.findViewById(R.id.txtDate_layout);
            txtPrice_layout = itemView.findViewById(R.id.txtPrice_layout);
            imgItem_layout = itemView.findViewById(R.id.imgItem_layout);
            btnDelete_layout = itemView.findViewById(R.id.btnDelete_layout);
            txtViews_layout = itemView.findViewById(R.id.txtViews_layout);
            txtTitleItem_layout_profile = itemView.findViewById(R.id.txtTitleItem_layout_profile);
            imgItem_layout_profile = itemView.findViewById(R.id.imgItem_layout_profile);
            if (listener != null) {
                btnDelete_layout.setOnClickListener(v -> {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            listener.onDeleteClick(position);
                    }
                });
                itemView.setOnClickListener(view -> {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            listener.onItemClick(position);
                    }
                });
            }
            if (mListener != null) {
                itemView.setOnLongClickListener(v -> {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            mListener.onItemLongClick(position);
                    }
                    return true;
                });
            }

        }

    }

}
