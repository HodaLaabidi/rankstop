package rankstop.steeringit.com.rankstop.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.db.Abuse;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;

public class AbusesListAdapter extends RecyclerView.Adapter<AbusesListAdapter.ViewHolder> {

    private List<Abuse> abusesList = new ArrayList<>();
    private RecyclerViewClickListener listener;
    private Context context;

    public AbusesListAdapter(List<Abuse> abusesList, RecyclerViewClickListener listener, Context context) {
        this.abusesList = abusesList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_abuse, parent, false),listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        //return abusesList == null ? 0 : abusesList.size();
        return 3;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RecyclerViewClickListener mListener;

        public ViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            this.mListener = listener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }
}
