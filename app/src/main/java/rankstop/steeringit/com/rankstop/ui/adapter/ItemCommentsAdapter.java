package rankstop.steeringit.com.rankstop.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.db.Comment;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;

public class ItemCommentsAdapter extends RecyclerView.Adapter<ItemCommentsAdapter.ViewHolder> {

    private RecyclerViewClickListener listener;
    private List<Comment> comments;
    private Context context;
    private String target;

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;

    public ItemCommentsAdapter(RecyclerViewClickListener listener, Context context, String target) {
        this.listener = listener;
        this.context = context;
        this.comments = new ArrayList<>();
        this.target = target;
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
                viewHolder = new ViewHolder(v2, listener);
                break;
        }
        return  viewHolder;
    }

    @NonNull
    private ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.layout_item_comments, parent, false);
        viewHolder = new ViewHolder(v1, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                viewHolder.setData(comments.get(position));
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return comments == null ? 0 : comments.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == comments.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void refreshData(List<Comment> pictures) {
        this.comments = pictures;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RecyclerViewClickListener mListener;
        private TextView noteColorView, commentTV;
        private LinearLayout commentContainer;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;

            noteColorView = itemView.findViewById(R.id.view_note);
            commentTV = itemView.findViewById(R.id.tv_comment);
            commentContainer = itemView.findViewById(R.id.comment_container);
            cardView = itemView.findViewById(R.id.card_view);

            if (target.equals("mine")){
                try{
                    RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(200, 200);
                    cardView.setLayoutParams(layoutParams);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    commentContainer.setLayoutParams(params);
                }catch(Exception e){}
            }

            itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    try {
                        if (target.equals("other")){
                            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, commentContainer.getWidth());
                            commentContainer.setLayoutParams(layoutParams);
                        }
                    }catch(Exception e){}
                }
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }

        public void setData(Comment comment) {
            try{
                noteColorView.setBackgroundColor(context.getResources().getColor(comment.getColor()));
                commentTV.setText(comment.getText());
            }catch(Exception e){}
        }
    }

    /*
    Helpers
    _________________________________________________________________________________________________
    */
    public void addAll(List<Comment> comments) {
        for (Comment comment : comments) {
            add(comment);
        }
    }

    public void add(Comment r) {
        comments.add(r);
        notifyItemInserted(comments.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Comment());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = comments.size() - 1;
        Comment comment = getItem(position);

        //if (comment != null) {
            comments.remove(position);
            notifyItemRemoved(position);
        //}
    }

    public Comment getItem(int position) {
        return comments.get(position);
    }
}
