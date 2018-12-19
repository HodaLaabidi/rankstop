package rankstop.steeringit.com.rankstop.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.ItemDetails;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;

public class ItemsFetchedAdapter extends RecyclerView.Adapter<ItemsFetchedAdapter.ViewHolder> {

    private Context context;
    private RecyclerViewClickListener listener;
    private List<ItemDetails> itemsList;

    public ItemsFetchedAdapter(Context context, RecyclerViewClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.itemsList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_data_fetched, viewGroup, false), listener);
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
        TextView titleTV;

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
