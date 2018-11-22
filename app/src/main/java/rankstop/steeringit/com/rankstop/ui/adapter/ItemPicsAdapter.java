package rankstop.steeringit.com.rankstop.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.Picture;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;

public class ItemPicsAdapter extends RecyclerView.Adapter<ItemPicsAdapter.ViewHolder> {

    private RecyclerViewClickListener listener;
    private List<Picture> pictures;
    private Context context;

    public ItemPicsAdapter(List<Picture> pictures, RecyclerViewClickListener listener, Context context) {
        this.listener = listener;
        this.pictures = pictures;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_pix, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.setData(pictures.get(i));
    }

    @Override
    public int getItemCount() {
        return pictures.size();
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

        public ViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;

            noteColorView = itemView.findViewById(R.id.view_note);
            imageView = itemView.findViewById(R.id.pic_review);
            pixContainer = itemView.findViewById(R.id.pix_container);

            itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixContainer.getWidth());
                    pixContainer.setLayoutParams(layoutParams);
                }
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }

        public void setData(Picture picture) {
            noteColorView.setBackgroundColor(context.getResources().getColor(picture.getColor()));
            imageView.setImageURI(Uri.parse(picture.getPictureEval()));
        }
    }
}
