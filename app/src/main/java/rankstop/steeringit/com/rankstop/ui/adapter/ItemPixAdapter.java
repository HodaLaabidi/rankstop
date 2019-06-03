package rankstop.steeringit.com.rankstop.ui.adapter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.ButterKnife;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.RankStop;
import rankstop.steeringit.com.rankstop.customviews.RSTVMedium;
import rankstop.steeringit.com.rankstop.data.model.db.Comment;
import rankstop.steeringit.com.rankstop.data.model.db.Picture;
import rankstop.steeringit.com.rankstop.ui.callbacks.ReviewCardForItemPicsListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.ReviewCardListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSDateParser;

public class ItemPixAdapter extends RecyclerView.Adapter<ItemPixAdapter.ViewHolder> {

    private ReviewCardForItemPicsListener listener;
    private List<Picture> pictures;
    private String target;

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;

    public ItemPixAdapter(ReviewCardForItemPicsListener listener, String target) {
        this.listener = listener;
        this.pictures = new ArrayList<>();
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
        View v1 = inflater.inflate(R.layout.layout_item_pix, parent, false);
        viewHolder = new ViewHolder(v1, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                viewHolder.setData(pictures.get(position));
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return pictures == null ? 0 : pictures.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == pictures.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void refreshData(List<Picture> pictures) {
        this.pictures = pictures;
        notifyDataSetChanged();
    }

    public void removePicture(Picture picture) {
        this.pictures.remove(picture);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ReviewCardForItemPicsListener mListener;
        private RSTVMedium usernameTV, dateTV;
        private RelativeLayout layout;
        private ImageButton removePicBTN;
        private SimpleDraweeView avatar;
        private SimpleDraweeView imageView;
        private LinearLayout pixContainer;
        private CardView cardView;
        @BindString(R.string.date_time_format)
        String dateTimeFormat;

        public ViewHolder(@NonNull View itemView, ReviewCardForItemPicsListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mListener = listener;

            imageView = itemView.findViewById(R.id.pic_review);
            pixContainer = itemView.findViewById(R.id.pix_container);
            cardView = itemView.findViewById(R.id.card_view);
            layout = itemView.findViewById(R.id.layout);
            usernameTV = itemView.findViewById(R.id.tv_username);
            dateTV = itemView.findViewById(R.id.tv_date);
            removePicBTN = itemView.findViewById(R.id.btn_remove_pic);
            avatar = itemView.findViewById(R.id.avatar);

            if (target.equals(RSConstants.MINE)) {
                try {
                    RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams((int)RankStop.getInstance().getResources().getDimension(R.dimen.width_comment_card), (int)RankStop.getInstance().getResources().getDimension(R.dimen.width_comment_card));
                    cardView.setLayoutParams(layoutParams);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    pixContainer.setLayoutParams(params);
                    removePicBTN.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                }
            }

            itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    try {
                        if (target.equals(RSConstants.OTHER)) {
                            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixContainer.getWidth());
                            pixContainer.setLayoutParams(layoutParams);
                        }
                        itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } catch (Exception e) {
                    }
                }
            });

            try {
                removePicBTN.setOnClickListener(this);
            }catch(Exception e){}
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_remove_pic:
                    listener.onRemoveClicked(getAdapterPosition());
                    break;
                default:
                    listener.onClick(v, getAdapterPosition());
            }
        }

        public void setData(Picture picture) {
            layout.setBackgroundColor(RankStop.getInstance().getResources().getColor(picture.getColor()));
            usernameTV.setText(picture.getUser().getNameToUse().getValue());
            try {
                dateTV.setText(RSDateParser.convertToDateTimeFormat(picture.getDate(), dateTimeFormat));
            }catch (Exception e){}
            avatar.setImageURI(Uri.parse(picture.getUser().getPictureProfile()));
            try {
                imageView.setImageURI(Uri.parse(picture.getPictureEval()));
            } catch (Exception e) {
            }
        }
    }

    /*
    Helpers
    _________________________________________________________________________________________________
    */
    public void addAll(List<Picture> pictures) {
        for (Picture result : pictures) {
            add(result);
        }
    }

    public void add(Picture r) {
        pictures.add(r);
        notifyItemInserted(pictures.size() - 1);
    }

    public List<Picture> getAll() {
        return pictures;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Picture());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = pictures.size() - 1;

        pictures.remove(position);
        notifyItemRemoved(position);
    }

    public Picture getItem(int position) {
        return pictures.get(position);
    }
}
