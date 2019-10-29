package rankstop.steeringit.com.rankstop.ui.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.ButterKnife;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.customviews.RSTVMedium;
import rankstop.steeringit.com.rankstop.customviews.RSTVRegular;
import rankstop.steeringit.com.rankstop.data.model.db.RSNotif;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.utils.RSDateParser;

public class NotifAdapter extends RecyclerView.Adapter<NotifAdapter.ViewHolder> {
    private List<RSNotif> notifications;
    private RecyclerViewClickListener itemListener;

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;

    public NotifAdapter(RecyclerViewClickListener itemListener) {
        this.itemListener = itemListener;
        notifications = new ArrayList<>();
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
        View v1 = inflater.inflate(R.layout.layout_notifs_item, parent, false);
        viewHolder = new ViewHolder(v1, itemListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                viewHolder.setData(notifications.get(position));
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return notifications == null ? 0 : notifications.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == notifications.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void refreshData(List<RSNotif> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RecyclerViewClickListener listener;
        public RSNotif notif;
        private RSTVRegular messageTV;
        private RSTVMedium dateTV;

        @BindString(R.string.date_time_format)
        String dateTimeFormat;

        public ViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.listener = listener;
            messageTV = itemView.findViewById(R.id.tv_message);
            dateTV = itemView.findViewById(R.id.tv_date);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }

        public void setData(RSNotif notif) {
            this.notif = notif;

            String message = "";
            message = notif.getText();
            if (notif.getItem() != null){
                message += " " + notif.getItem().getTitle();
                Spannable wordtoSpan = new SpannableString(message);
                wordtoSpan.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        listener.onClick(itemView, getAdapterPosition());
                    }
                }, notif.getText().length()+1, message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                messageTV.setText(wordtoSpan);
                messageTV.setMovementMethod(LinkMovementMethod.getInstance());
            }else {
                messageTV.setText(message);
            }

            dateTV.setText(RSDateParser.convertToDateTimeFormat(notif.getDate(), dateTimeFormat));
        }
    }

    //------------------------------Helpers
    public void addAll(List<RSNotif> notifications) {
        for (RSNotif notif : notifications) {
            add(notif);
        }
    }

    public void clear() {
        notifications.clear();
        notifyDataSetChanged();
    }

    public void add(RSNotif notif) {
        notifications.add(notif);
        notifyItemInserted(notifications.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new RSNotif());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = notifications.size() - 1;
        RSNotif notif = getItem(position);
        if (notif != null) {
            notifications.remove(position);
            notifyItemRemoved(position);
        }
    }

    public RSNotif getItem(int position) {
        return notifications.get(position);
    }
}
