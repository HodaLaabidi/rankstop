
package rankstop.steeringit.com.rankstop.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.data.model.Picture;
import rankstop.steeringit.com.rankstop.R;


public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private List<Picture> picsGallery = new ArrayList<>();
    private RecyclerViewClickListener listener;
    private Context context;

    public GalleryAdapter(List<Picture> picsGallery, RecyclerViewClickListener listener, Context context) {
        this.picsGallery = picsGallery;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_gallery_pics, parent, false), listener); // TODO
    }

    @Override
    public int getItemCount() {
        return picsGallery.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(picsGallery.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // TODO - Your view members
        public Picture picsGallery;
        private RecyclerViewClickListener mListener;
        private SimpleDraweeView imageView;

        public ViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            this.mListener = listener;

            imageView = itemView.findViewById(R.id.image_view);
            itemView.setOnClickListener(this);
            // TODO instantiate/assign view members
        }

        public void setData(Picture picsGallery) {
            this.picsGallery = picsGallery;

            Uri imageUri = Uri.parse(picsGallery.getPictureEval());
            imageView.setImageURI(imageUri);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }


}
                                