package rankstop.steeringit.com.rankstop.ui.adapter;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.ButterKnife;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.RankStop;
import rankstop.steeringit.com.rankstop.customviews.RSTVMedium;
import rankstop.steeringit.com.rankstop.data.model.db.Comment;
import rankstop.steeringit.com.rankstop.ui.callbacks.ReviewCardListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSDateParser;

public class ItemCommentsAdapter extends RecyclerView.Adapter<ItemCommentsAdapter.ViewHolder> {

    private ReviewCardListener listener;
    private List<Comment> comments;
    private String target;

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;

    public ItemCommentsAdapter(ReviewCardListener listener, String target) {
        this.listener = listener;
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
        return viewHolder;
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

    public void refreshData(List<Comment> comments) {
        this.comments.clear();
        this.comments.addAll(comments);
        notifyDataSetChanged();
    }

    public void removeComment(Comment comment) {
        this.comments.remove(comment);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ReviewCardListener mListener;
        private RSTVMedium commentTV, usernameTV, dateTV;
        private RelativeLayout layout;
        private LinearLayout commentContainer;
        private CardView cardView;
        private ImageButton removeCommentBTN;
        private SimpleDraweeView avatar;
        @BindString(R.string.date_time_format)
        String dateTimeFormat;

        public ViewHolder(@NonNull View itemView, ReviewCardListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mListener = listener;

            layout = itemView.findViewById(R.id.layout);
            usernameTV = itemView.findViewById(R.id.tv_username);
            dateTV = itemView.findViewById(R.id.tv_date);
            commentTV = itemView.findViewById(R.id.tv_comment);
            commentContainer = itemView.findViewById(R.id.comment_container);
            cardView = itemView.findViewById(R.id.card_view);
            removeCommentBTN = itemView.findViewById(R.id.btn_remove_comment);
            avatar = itemView.findViewById(R.id.avatar);

            if (target.equals(RSConstants.MINE)) {
                try {
                    RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams((int)RankStop.getInstance().getResources().getDimension(R.dimen.width_comment_card), (int)RankStop.getInstance().getResources().getDimension(R.dimen.width_comment_card));
                    cardView.setLayoutParams(layoutParams);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    commentContainer.setLayoutParams(params);
                    removeCommentBTN.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                }
            }

            itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    try {
                        if (target.equals(RSConstants.OTHER)) {
                            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, commentContainer.getWidth());
                            commentContainer.setLayoutParams(layoutParams);
                        }
                        itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } catch (Exception e) {
                    }
                }
            });

            try {
                removeCommentBTN.setOnClickListener(this);
            } catch (Exception e) {
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_remove_comment:
                    mListener.onRemoveClicked(getAdapterPosition());
                    break;
                default:
                    mListener.onClick(v, getAdapterPosition(), comments);
            }
        }

        public void setData(Comment comment) {
            try {
                layout.setBackgroundColor(RankStop.getInstance().getResources().getColor(comment.getColor()));
                commentTV.setText(comment.getText().trim());
                usernameTV.setText(comment.getUserId().getNameToUse().getValue());
                dateTV.setText(RSDateParser.convertToDateTimeFormat(comment.getDate(), dateTimeFormat));
                String pictureProfile = comment.getUserId().getPictureProfile();
                if (pictureProfile != null) {

                    if (pictureProfile != "") {
                        avatar.setImageURI(Uri.parse(pictureProfile));
                    } else {
                        ImageRequest request =
                                ImageRequestBuilder.newBuilderWithResourceId(R.drawable.ava_256)
                                        .build();
                        DraweeController controller = Fresco.newDraweeControllerBuilder()
                                .setImageRequest(request)
                                .setOldController(avatar.getController())
                                .build();
                        avatar.setController(controller);
                    }

                } else {
                    ImageRequest request =
                            ImageRequestBuilder.newBuilderWithResourceId(R.drawable.ava_256)
                                    .build();
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .setOldController(avatar.getController())
                            .build();
                    avatar.setController(controller);
                }

            } catch (Exception e) {
            }
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

        comments.remove(position);
        notifyItemRemoved(position);
    }

    public Comment getItem(int position) {
        return comments.get(position);
    }
}
