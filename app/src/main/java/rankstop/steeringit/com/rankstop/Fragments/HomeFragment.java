package rankstop.steeringit.com.rankstop.Fragments;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import rankstop.steeringit.com.rankstop.R;

public class HomeFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

    final String TAG = "HOME FRAGMENT";

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final int COLOR_ANIMATIONS_DURATION              = 200;

    private AppBarLayout appbar;
    private ImageView coverImage;
    private Toolbar toolbar;

    private boolean isTransparentBg= true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("LIFE_CYCLE",""+TAG+" onCreateView");
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("LIFE_CYCLE",""+TAG+" onActivityCreated");

        appbar = getActivity().findViewById( R.id.appbar );
        coverImage = getActivity().findViewById( R.id.imageview_placeholder );
        toolbar = getActivity().findViewById( R.id.toolbar );

        toolbar.setTitle("");
        appbar.addOnOffsetChangedListener(this);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        coverImage.setImageResource(R.drawable.cover);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(i) / (float) maxScroll;

        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (isTransparentBg) {
                startColorAnimation(toolbar, COLOR_ANIMATIONS_DURATION, Color.TRANSPARENT,getResources().getColor(R.color.colorPrimary));
                isTransparentBg = false;
            }
        } else {
            if (!isTransparentBg) {
                startColorAnimation(toolbar, COLOR_ANIMATIONS_DURATION, getResources().getColor(R.color.colorPrimary),Color.TRANSPARENT);
                isTransparentBg = true;
            }
        }
    }

    public static void startColorAnimation (final View view, long duration, int colorFrom, int colorTo) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(duration); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }

























    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("LIFE_CYCLE",""+TAG+" onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("LIFE_CYCLE",""+TAG+" onCreate");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("LIFE_CYCLE",""+TAG+" onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("LIFE_CYCLE",""+TAG+" onResume");
    }

    @Override
    public void onPause() {
        Log.i("LIFE_CYCLE",""+TAG+" onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("LIFE_CYCLE",""+TAG+" onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i("LIFE_CYCLE",""+TAG+" onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i("LIFE_CYCLE",""+TAG+" onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.i("LIFE_CYCLE",""+TAG+" onDetach");
        super.onDetach();
    }
}
