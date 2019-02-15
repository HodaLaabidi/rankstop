package rankstop.steeringit.com.rankstop.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.customviews.RSTVMedium;
import rankstop.steeringit.com.rankstop.data.model.db.Picture;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSDateParser;

public class SlidePhotoFragment extends Fragment {

    private Unbinder unbinder;

    @BindView(R.id.image_view)
    ImageView imageView;

    @BindView(R.id.view_note)
    TextView noteColorView;

    @BindView(R.id.tv_user)
    RSTVMedium userTV;

    @BindView(R.id.tv_date_post)
    RSTVMedium postDateTV;

    @BindString(R.string.posted_by)
    String postedBy;

    @BindString(R.string.date_time_format)
    String dateTimeFormat;

    Picture picture;

    private static SlidePhotoFragment instance;

    public static SlidePhotoFragment newInstance(Picture picture) {

        Bundle args = new Bundle();
        args.putSerializable(RSConstants.PICTURE, picture);
        SlidePhotoFragment fragment = new SlidePhotoFragment();
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


        picture = (Picture) getArguments().getSerializable(RSConstants.PICTURE);
        noteColorView.setVisibility(View.VISIBLE);
        userTV.setVisibility(View.VISIBLE);

        try {
            userTV.setText(postedBy + " " + picture.getUser().getNameToUse().getValue());
            postDateTV.setText(RSDateParser.convertToDateTimeFormat(picture.getDate(), dateTimeFormat));
            noteColorView.setBackgroundColor(getResources().getColor(picture.getColor()));
            Picasso.get()
                    .load(picture.getPictureEval())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(imageView);
        } catch (Exception e) {
        }


    }

    @Override
    public void onDestroyView() {

        instance = null;
        if (unbinder != null)
            unbinder.unbind();

        super.onDestroyView();
    }
}
