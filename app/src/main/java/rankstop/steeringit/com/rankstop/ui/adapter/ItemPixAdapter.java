package com.steeringit.rankstop.ui.adapter;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.ButterKnife;
import com.steeringit.rankstop.R;
import com.steeringit.rankstop.RankStop;
import com.steeringit.rankstop.customviews.RSTVMedium;
import com.steeringit.rankstop.data.model.db.ItemDetails;
import com.steeringit.rankstop.data.model.db.Picture;
import com.steeringit.rankstop.ui.callbacks.ReviewCardForItemPicsListener;
import com.steeringit.rankstop.ui.dialogFragment.ItemInfoDialog;
import com.steeringit.rankstop.ui.dialogFragment.UserInfoDialog;
import com.steeringit.rankstop.utils.RSConstants;
import com.steeringit.rankstop.utils.RSDateParser;

public class ItemPixAdapter extends RecyclerView.Adapter<ItemPixAdapter.ViewHolder> {

    private ReviewCardForItemPicsListener listener;
    private List<Picture> pictures;
    private String target;
    private Context context ;
    private FragmentManager fm ;

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;
    private String itemPixResources ;

    public ItemPixAdapter(ReviewCardForItemPicsListener listener, String target, Context context, String itemPixResources , FragmentManager fm) {
        this.listener = listener;
        this.context = context ;
        this.pictures = new ArrayList<>();
        this.target = target;
        this.fm = fm ;
        this.itemPixResources = itemPixResources ;
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
        this.pictures.clear();
        this.pictures.addAll(pictures);
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
            if (picture != null) {
            layout.setBackgroundColor(RankStop.getInstance().getResources().getColor(picture.getColor()));
            if (picture.getUser() != null) {
                if (picture.getUser().getNameToUse() != null) {
                    usernameTV.setText(picture.getUser().getNameToUse().getValue());
                }

            }

            try {
                dateTV.setText(RSDateParser.convertToDateTimeFormat(picture.getDate(), dateTimeFormat));
            } catch (Exception e) {
            }
            if (picture != null) {
                if (picture.getUser() != null) {
                    if (picture.getUser().getPictureProfile() != null) {
                        avatar.setImageURI(Uri.parse(picture.getUser().getPictureProfile()));
                        avatar.getHierarchy().setFailureImage(R.drawable.ava_256);
                        avatar.getHierarchy().setPlaceholderImage(R.drawable.ava_256, ScalingUtils.ScaleType.CENTER_CROP);
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
                    avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
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
                avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            if (itemPixResources == RSConstants.ITEM_PIX) {
                avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (picture.getUser() != null) {
                            if (picture.getUser().get_id() != null && picture.getUser().get_id() != "")
                                showUserInfo(picture.getUser().get_id());
                        }
                    }
                });

                usernameTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (picture.getUser() != null) {
                            if (picture.getUser().get_id() != null && picture.getUser().get_id() != "")
                                showUserInfo(picture.getUser().get_id());
                        }
                    }
                });
            }


            try {
                if (picture != null) {
                    if (picture.getPictureEval() != null) {
                        if (picture.getPictureEval() != "") {
                            imageView.setImageURI(Uri.parse(picture.getPictureEval()));
                        }
                    } else {
                        ImageRequest request =
                                ImageRequestBuilder.newBuilderWithResourceId(R.drawable.no_image_available)
                                        .build();
                        DraweeController controller = Fresco.newDraweeControllerBuilder()
                                .setImageRequest(request)
                                .setOldController(imageView.getController())
                                .build();
                        imageView.setController(controller);
                    }
                } else {
                    ImageRequest request =
                            ImageRequestBuilder.newBuilderWithResourceId(R.drawable.no_image_available)
                                    .build();
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .setOldController(imageView.getController())
                            .build();
                    imageView.setController(controller);
                }

            } catch (Exception e) {
            }
        }
        }
    }

    private void showUserInfo(String userId) {
        UserInfoDialog dialog = UserInfoDialog.newInstance(userId);
        dialog.setCancelable(false);
        dialog.show(fm, "");
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
