package com.steeringit.rankstop.ui.adapter;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import com.steeringit.rankstop.R;
import com.steeringit.rankstop.ui.callbacks.RecyclerViewClickListener;

public class ReviewPixAdapter extends RecyclerView.Adapter<ReviewPixAdapter.ViewHolder> {
    private List<Uri> pixList = new ArrayList<>();
    private RecyclerViewClickListener listener;

    public ReviewPixAdapter(List<Uri> pixList, RecyclerViewClickListener listener) {
        this.pixList = pixList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_pix_review, viewGroup, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.setData(pixList.get(position));
    }

    @Override
    public int getItemCount() {
        if (pixList != null)
        return pixList.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public Uri uri;
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

        public void setData(Uri uri) {
            this.uri = uri;
            imageView.setImageURI(uri);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }
}
