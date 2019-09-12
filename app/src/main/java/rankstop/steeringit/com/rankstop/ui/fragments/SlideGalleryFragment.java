package com.steeringit.rankstop.ui.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.steeringit.rankstop.R;
import com.steeringit.rankstop.customviews.RSTVMedium;
import com.steeringit.rankstop.data.model.db.Gallery;
import com.steeringit.rankstop.utils.RSConstants;
import com.steeringit.rankstop.utils.RSDateParser;

public class SlideGalleryFragment extends Fragment {

    private Unbinder unbinder;

    @BindView(R.id.image_view)
    ImageView imageView;

    @BindView(R.id.tv_date_post)
    RSTVMedium postDateTV;

    @BindString(R.string.posted_by)
    String postedBy;

    @BindString(R.string.date_time_format)
    String dateTimeFormat;

    Gallery picture;

    public static SlideGalleryFragment newInstance(Gallery picture) {

        Bundle args = new Bundle();
        args.putSerializable(RSConstants.PICTURE, picture);
        SlideGalleryFragment fragment = new SlideGalleryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_slide_photo, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        picture = (Gallery) getArguments().getSerializable(RSConstants.PICTURE);

        try {
            postDateTV.setText(RSDateParser.convertToDateTimeFormat(picture.getDate(), dateTimeFormat));
            if (picture != null){
                if (picture.getUrlPicture() != null){
                    if (picture.getUrlPicture() != ""){
                        Picasso.get()
                                .load(picture.getUrlPicture())
                                .placeholder(R.drawable.no_image_available)
                                .error(R.drawable.no_image_available)
                                .into(imageView);
                    }
                }
            }

        } catch (Exception e) {
        }


    }

    @Override
    public void onDestroyView() {

        if (unbinder != null)
            unbinder.unbind();

        super.onDestroyView();
    }

}
