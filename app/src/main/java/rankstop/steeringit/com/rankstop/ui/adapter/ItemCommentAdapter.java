package rankstop.steeringit.com.rankstop.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
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
import rankstop.steeringit.com.rankstop.data.model.Comment;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;

public class ItemCommentAdapter extends RecyclerView.Adapter<ItemCommentAdapter.ViewHolder> {

    private List<Comment> comments = new ArrayList<>();
    private RecyclerViewClickListener listener;
    private Context context;

    public ItemCommentAdapter(List<Comment> comments, RecyclerViewClickListener listener, Context context) {
        this.comments = comments;
        this.listener = listener;
        this.context = context;
    }

    public void refreshData(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_item_comments, viewGroup, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.setData(comments.get(i));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RecyclerViewClickListener mListener;
        private TextView noteColorView, commentTV;
        private LinearLayout commentContainer;

        public ViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);

            mListener = listener;

            noteColorView = itemView.findViewById(R.id.view_note);
            commentTV = itemView.findViewById(R.id.tv_comment);
            commentContainer = itemView.findViewById(R.id.comment_container);

            itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, commentContainer.getWidth());
                    commentContainer.setLayoutParams(layoutParams);
                }
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }

        public void setData(Comment comment) {
            noteColorView.setBackgroundColor(context.getResources().getColor(comment.getColor()));
            commentTV.setText(comment.getText());
        }
    }
}
