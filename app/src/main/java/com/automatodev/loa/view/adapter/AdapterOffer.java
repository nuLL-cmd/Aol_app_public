package com.automatodev.loa.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.automatodev.loa.R;
import com.automatodev.loa.model.entity.OfferEntity;
import com.automatodev.loa.view.extras.FormatTools;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;

//Marco Aurelio 03/09
//Classe Adapter responsavel por distribuir os dados nas listas de oferta tanto para usuario
//quanto para anunciante
public class AdapterOffer extends RecyclerView.Adapter<AdapterOffer.DataHandler> {

    private List<OfferEntity> offers;
    private String viewSide;
    private Context context;
    private OnItemClickListener listener;
    private FormatTools formatTools;
    private RelativeLayout relativeResources_dialogOffer;

    public interface OnItemClickListener {

        void onItemClick(int position);
        void onItemDeleteClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public AdapterOffer(List<OfferEntity> offers, String viewSide, Context context, RelativeLayout relativeResources_dialogOffer) {
        formatTools = new FormatTools();
        this.offers = offers;
        this.viewSide = viewSide;
        this.context = context;
        this.relativeResources_dialogOffer = relativeResources_dialogOffer;
    }

    @NonNull
    @Override
    public DataHandler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_items_offer_global, parent, false);
        return new DataHandler(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull DataHandler holder, int position) {
        if (viewSide.equals("user")) {
            holder.viewStatus_offer.setVisibility(View.VISIBLE);
            holder.txtItem_offer.setText(offers.get(position).getItem().getTitle());
            holder.txtPhone_offer.setText(offers.get(position).getItem().getPhone());
            holder.txtStatus_offer.setVisibility(View.VISIBLE);
            holder.txtStatus_offer.setText(offers.get(position).getStatus());
            if (offers.get(position).getStatus().equals("Confirmada")) {
                holder.txtStatus_offer.setTextColor(Color.parseColor("#43A047"));
                holder.viewStatus_offer.setBackground(context.getDrawable(R.drawable.bg_lbl_situation));
            } else {
                holder.txtStatus_offer.setTextColor(Color.parseColor("#E53935"));
                holder.viewStatus_offer.setBackground(context.getDrawable(R.drawable.bg_view_background));

            }
            holder.lblStatus_offer.setVisibility(View.VISIBLE);
            if (offers.get(position).getItem().getImages().get(0).getUrlImage() != null)
                Glide.with(context).load(offers.get(position).getItem().getImages().get(0).getUrlImage())
                        .transition(DrawableTransitionOptions.withCrossFade()).into(holder.imgSource_offer);
        }
        if (viewSide.equals("owner")) {
            holder.txtItem_offer.setText(offers.get(position).getUser().getFirstName() + offers.get(position).getUser().getLastName());
            holder.txtPhone_offer.setText(offers.get(position).getUser().getPhone());
            holder.btnRemove_offer.setText("Recusar");
            if (offers.get(position).getUser().getUrlPhoto() != null)
                Glide.with(context).load(offers.get(position).getUser().getUrlPhoto())
                        .transition(DrawableTransitionOptions.withCrossFade()).into(holder.imgSource_offer);
        }
        holder.txtPriceOffer_layout.setText(formatTools.decimalFormat(offers.get(position).getPrice()));
        holder.txtPriceItem_layout.setText(formatTools.decimalFormat(offers.get(position).getItem().getPrice()));
        holder.txtDate_offer.setText(formatTools.dateFormatnoHour(offers.get(position).getDateOffer()));

    }

    @Override
    public int getItemCount() {
        if (offers.size() == 0)
            relativeResources_dialogOffer.setVisibility(View.VISIBLE);
        else
            relativeResources_dialogOffer.setVisibility(View.GONE);
        return offers.size();
    }

    public class DataHandler extends RecyclerView.ViewHolder {

        ImageView imgSource_offer;
        TextView txtPriceOffer_layout;
        TextView txtPriceItem_layout;
        Button btnCall_offer;
        Button btnRemove_offer;
        TextView txtItem_offer;
        TextView txtDate_offer;
        TextView txtPhone_offer;
        TextView lblStatus_offer;
        TextView txtStatus_offer;
        View viewStatus_offer;

        public DataHandler(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            imgSource_offer = itemView.findViewById(R.id.imgSource_offer);
            txtPriceOffer_layout = itemView.findViewById(R.id.txtPriceOffer_layout);
            txtPriceItem_layout = itemView.findViewById(R.id.txtPriceItem_layout);
            txtDate_offer = itemView.findViewById(R.id.txtDate_offer);
            btnCall_offer = itemView.findViewById(R.id.btnCall_offer);
            btnRemove_offer = itemView.findViewById(R.id.btnRemove_offer);
            txtItem_offer = itemView.findViewById(R.id.txtItem_offer);
            lblStatus_offer = itemView.findViewById(R.id.lblStatus_offer);
            txtStatus_offer = itemView.findViewById(R.id.txtStatus_offer);
            txtPhone_offer = itemView.findViewById(R.id.txtPhone_offer);
            viewStatus_offer = itemView.findViewById(R.id.viewStatus_offer);
            btnCall_offer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
            btnRemove_offer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemDeleteClick(position);
                        }
                    }
                }
            });

        }

    }

}
