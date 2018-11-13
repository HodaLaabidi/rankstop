package rankstop.steeringit.com.rankstop.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;

public class ReviewPixAdapter extends RecyclerView.Adapter<ReviewPixAdapter.ViewHolder> {
    private List<byte[]> pixList = new ArrayList<>();
    private RecyclerViewClickListener listener;
    private Context context;

    public ReviewPixAdapter(List<byte[]> pixList, RecyclerViewClickListener listener, Context context) {
        this.pixList = pixList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_pix_review, viewGroup, false), listener); // TODO
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.setData(pixList.get(position));
    }

    @Override
    public int getItemCount() {
        return pixList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public byte[] byteArray;
        private RecyclerViewClickListener mListener;

        private SimpleDraweeView imageView;
        private ImageButton removePicBtn;

        public ViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);

            this.mListener = listener;

            imageView = itemView.findViewById(R.id.image_view);
            removePicBtn = itemView.findViewById(R.id.btn_remove_pic);

            removePicBtn.setOnClickListener(this);

        }

        public void setData(byte[] byteArray) {
            this.byteArray = byteArray;
            // TODO set data to view

            Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bm, "Image Description", null);
            Uri imageUri = Uri.parse(path);
            imageView.setImageURI(imageUri);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }
}
