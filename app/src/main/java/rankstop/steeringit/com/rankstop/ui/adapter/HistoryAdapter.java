package rankstop.steeringit.com.rankstop.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.ButterKnife;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.customviews.RSTVBold;
import rankstop.steeringit.com.rankstop.customviews.RSTVRegular;
import rankstop.steeringit.com.rankstop.data.model.db.History;
import rankstop.steeringit.com.rankstop.ui.callbacks.ItemHistoryListener;
import rankstop.steeringit.com.rankstop.utils.RSDateParser;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<History> histories;
    private ItemHistoryListener itemListener;

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;

    public HistoryAdapter(ItemHistoryListener itemListener) {
        this.itemListener = itemListener;
        histories = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new ViewHolder(v2, itemListener);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.layout_history_item, parent, false);
        viewHolder = new ViewHolder(v1, itemListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                viewHolder.setData(histories.get(position));
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return histories == null ? 0 : histories.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == histories.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void refreshData(List<History> histories) {
        this.histories = histories;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemHistoryListener listener;
        public History history;
        private RSTVRegular subjectTV, messageTV, timeTV;
        private RSTVBold dateTV;

        @BindString(R.string.date_format)
        String dateFormat;

        public ViewHolder(@NonNull View itemView, ItemHistoryListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.listener = listener;
            messageTV = itemView.findViewById(R.id.tv_message);
            subjectTV = itemView.findViewById(R.id.tv_subject);
            dateTV = itemView.findViewById(R.id.tv_date);
            timeTV = itemView.findViewById(R.id.tv_time);
            //itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }

        public void setData(History history) {
            this.history = history;

            String message = "";
            message = history.getMessage();
            if (history.getItem() != null){
                message += " " + history.getItem().getTitle();
                Spannable wordtoSpan = new SpannableString(message);
                //wordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), history.getMessage().length()+1, message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                wordtoSpan.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        listener.onClick(itemView, getAdapterPosition());
                    }
                }, history.getMessage().length()+1, message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                messageTV.setText(wordtoSpan);
                messageTV.setMovementMethod(LinkMovementMethod.getInstance());
            }else {
                messageTV.setText(message);
            }

            dateTV.setText(RSDateParser.convertToDateFormat(history.getDate(), dateFormat));
            timeTV.setText(history.getTime());
            subjectTV.setText(history.getSubject());
        }
    }

    //------------------------------Helpers
    public void addAll(List<History> histories) {
        for (History history : histories) {
            add(history);
        }
    }

    public void clear() {
        histories.clear();
        notifyDataSetChanged();
    }

    public void add(History history) {
        histories.add(history);
        notifyItemInserted(histories.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new History());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = histories.size() - 1;
        History history = getItem(position);
        if (history != null) {
            histories.remove(position);
            notifyItemRemoved(position);
        }
    }

    public History getItem(int position) {
        return histories.get(position);
    }
}
