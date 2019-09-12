package com.steeringit.rankstop.ui.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.steeringit.rankstop.R;
import com.steeringit.rankstop.customviews.RSTVRegular;
import com.steeringit.rankstop.data.model.db.ItemDetails;
import com.steeringit.rankstop.ui.callbacks.RecyclerViewClickListener;

public class ItemsFetchedAdapter extends RecyclerView.Adapter<ItemsFetchedAdapter.ViewHolder> {

    private RecyclerViewClickListener listener;
    private List<ItemDetails> itemsList;

    public ItemsFetchedAdapter(RecyclerViewClickListener listener) {
        this.listener = listener;
        this.itemsList = new ArrayList<>();
    }

    public void clear(){
        this.itemsList.clear();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_data_fetched, viewGroup, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.setData(itemsList.get(i));
    }

    @Override
    public int getItemCount() {
        return itemsList == null ? 0 : itemsList.size();
    }

    public void refreshData(List<ItemDetails> itemsList) {
        this.itemsList = itemsList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RecyclerViewClickListener listener;

        @BindView(R.id.tv_title)
        RSTVRegular titleTV;

        public ViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        public void setData(ItemDetails item) {
            titleTV.setText(item.getTitle());
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }
}
