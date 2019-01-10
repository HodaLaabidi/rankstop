package rankstop.steeringit.com.rankstop.ui.adapter;

import android.content.Context;
import android.net.Uri;
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

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.db.Picture;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;

public class ItemPixAdapter extends RecyclerView.Adapter<ItemPixAdapter.ViewHolder> {

    private RecyclerViewClickListener listener;
    private List<Picture> pictures;
    private Context context;
    private String target;

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;

    public ItemPixAdapter(RecyclerViewClickListener listener, Context context, String target) {
        this.listener = listener;
        this.context = context;
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RecyclerViewClickListener mListener;
        private TextView noteColorView;
        private SimpleDraweeView imageView;
        private LinearLayout pixContainer;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;

            noteColorView = itemView.findViewById(R.id.view_note);
            imageView = itemView.findViewById(R.id.pic_review);
            pixContainer = itemView.findViewById(R.id.pix_container);
            cardView = itemView.findViewById(R.id.card_view);

            if (target.equals(RSConstants.MINE)) {
                try {
                    RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(200, 200);
                    cardView.setLayoutParams(layoutParams);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    pixContainer.setLayoutParams(params);
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
                    } catch (Exception e) {
                    }
                }
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }

        public void setData(Picture picture) {
            noteColorView.setBackgroundColor(context.getResources().getColor(picture.getColor()));
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
