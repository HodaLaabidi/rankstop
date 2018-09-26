package rankstop.steeringit.com.rankstop.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import rankstop.steeringit.com.rankstop.Activities.EditProfileActivity;
import rankstop.steeringit.com.rankstop.Activities.NotificationsActivity;
import rankstop.steeringit.com.rankstop.Activities.SettingsActivity;
import rankstop.steeringit.com.rankstop.R;

public class ProfileFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

    final String TAG = "PROFILE FRAGMENT";

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;
    final Uri imageUri = Uri.parse("https://scontent.ftun1-1.fna.fbcdn.net/v/t1.0-9/15940845_1300951476615258_4049671823041162693_n.jpg?_nc_cat=0&oh=95ce10869da2541a750ba3c6a7023b41&oe=5C164B7E");

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

    private AppBarLayout appbar;
    private CollapsingToolbarLayout collapsing;
    private ImageView coverImage;
    private FrameLayout framelayoutTitle;
    private LinearLayout linearlayoutTitle;
    private Toolbar toolbar;
    private TextView textviewTitle;
    private SimpleDraweeView avatar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("LIFE_CYCLE",""+TAG+" onCreateView");
        return inflater.inflate(R.layout.fragment_profile, container, false);
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
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("LIFE_CYCLE",""+TAG+" onActivityCreated");

        appbar = (AppBarLayout)getActivity().findViewById( R.id.appbar );
        collapsing = (CollapsingToolbarLayout)getActivity().findViewById( R.id.collapsing );
        coverImage = (ImageView)getActivity().findViewById( R.id.imageview_placeholder );
        framelayoutTitle = (FrameLayout)getActivity().findViewById( R.id.framelayout_title );
        linearlayoutTitle = (LinearLayout)getActivity().findViewById( R.id.linearlayout_title );
        toolbar = (Toolbar)getActivity().findViewById( R.id.toolbar );
        textviewTitle = (TextView)getActivity().findViewById( R.id.textview_title );
        avatar = (SimpleDraweeView)getActivity().findViewById(R.id.avatar);

        toolbar.setTitle("");
        appbar.addOnOffsetChangedListener(this);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        startAlphaAnimation(textviewTitle, 0, View.INVISIBLE);

        //set avatar and cover
        avatar.setImageURI(imageUri);
        coverImage.setImageResource(R.drawable.cover);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.setting:
                startActivity(new Intent(getContext(), SettingsActivity.class));
                break;
            case R.id.edit_profile:
                startActivity(new Intent(getContext(), EditProfileActivity.class));
                break;
            case R.id.notifications:
                startActivity(new Intent(getContext(), NotificationsActivity.class));
                break;
        }

        /*AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setMessage(message);
        alertDialog.show();*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(i) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!mIsTheTitleVisible) {
                startAlphaAnimation(textviewTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(textviewTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(linearlayoutTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(linearlayoutTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
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
