package rankstop.steeringit.com.rankstop.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import rankstop.steeringit.com.rankstop.Activities.EditProfileActivity;
import rankstop.steeringit.com.rankstop.Activities.ItemCreatedActivity;
import rankstop.steeringit.com.rankstop.Activities.ItemDetailsActivity;
import rankstop.steeringit.com.rankstop.Activities.ItemFollowedActivity;
import rankstop.steeringit.com.rankstop.Activities.ItemOwnedActivity;
import rankstop.steeringit.com.rankstop.Activities.NotificationsActivity;
import rankstop.steeringit.com.rankstop.Activities.SettingsActivity;
import rankstop.steeringit.com.rankstop.Adapter.PieAdapter;
import rankstop.steeringit.com.rankstop.Interface.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.Model.Item;
import rankstop.steeringit.com.rankstop.Model.ItemDetails;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.Utils.HorizontalSpace;

public class ProfileFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    final String TAG = "PROFILE FRAGMENT";

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;

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

    private List<Item> listOwnedItem, listFollowedItem, listCreatedItem;

    private RecyclerView recyclerViewOwnedItem, recyclerViewFollowedItem, recyclerViewCreatedItem;
    private ProgressBar progressBarOwnedItem, progressBarFollowedItem, progressBarCreatedItem;

    private MaterialButton moreOwnedBtn, moreCreatedBtn, moreFollowedBtn;

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

        bindViews();
    }

    private void bindViews() {
        appbar = getActivity().findViewById( R.id.appbar );
        collapsing = getActivity().findViewById( R.id.collapsing );
        framelayoutTitle = getActivity().findViewById( R.id.framelayout_title );
        linearlayoutTitle = getActivity().findViewById( R.id.linearlayout_title );
        toolbar = getActivity().findViewById( R.id.toolbar );
        textviewTitle = getActivity().findViewById( R.id.textview_title );

        avatar = getActivity().findViewById(R.id.avatar);
        coverImage = getActivity().findViewById( R.id.imageview_placeholder );

        progressBarOwnedItem = getActivity().findViewById(R.id.progress_bar_page_owned);
        progressBarCreatedItem = getActivity().findViewById(R.id.progress_bar_page_created);
        progressBarFollowedItem = getActivity().findViewById(R.id.progress_bar_page_followed);

        recyclerViewOwnedItem = getActivity().findViewById(R.id.recycler_view_page_owned);
        recyclerViewCreatedItem = getActivity().findViewById(R.id.recycler_view_page_created);
        recyclerViewFollowedItem = getActivity().findViewById(R.id.recycler_view_page_followed);

        moreOwnedBtn = getActivity().findViewById(R.id.more_page_owned);
        moreCreatedBtn = getActivity().findViewById(R.id.more_page_created);
        moreFollowedBtn = getActivity().findViewById(R.id.more_page_followed);

        moreOwnedBtn.setOnClickListener(this);
        moreCreatedBtn.setOnClickListener(this);
        moreFollowedBtn.setOnClickListener(this);

        toolbar.setTitle("");
        appbar.addOnOffsetChangedListener(this);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        startAlphaAnimation(textviewTitle, 0, View.INVISIBLE);

        loadData();
    }

    private void loadData() {
        //set avatar and cover
        loadProfileData();
        loadOwnedItem();
        loadCreatedItem();
        loadFollowedItem();
    }

    private void loadProfileData() {
        Uri imageUri = Uri.parse("https://scontent.ftun1-1.fna.fbcdn.net/v/t1.0-9/15940845_1300951476615258_4049671823041162693_n.jpg?_nc_cat=0&oh=95ce10869da2541a750ba3c6a7023b41&oe=5C164B7E");
        avatar.setImageURI(imageUri);
        coverImage.setImageResource(R.drawable.cover);
    }

    private void loadOwnedItem() {
        progressBarOwnedItem.setVisibility(View.GONE);
        listOwnedItem = new ArrayList<>();
        initOwnedItem(listOwnedItem);
    }

    private void loadCreatedItem() {
        progressBarCreatedItem.setVisibility(View.GONE);
        listCreatedItem = new ArrayList<>();
        initCreatedItem(listCreatedItem);
    }

    private void loadFollowedItem() {
        progressBarFollowedItem.setVisibility(View.GONE);
        listFollowedItem = new ArrayList<>();
        initFollowedItem(listFollowedItem);
    }

    private void initOwnedItem(List<Item> listOwnedItem) {
        recyclerViewOwnedItem.setVisibility(View.VISIBLE);
        RecyclerViewClickListener listener = (view, position) -> {
            Intent intent = new Intent(getContext(), ItemDetailsActivity.class);
            intent.putExtra("item", new Item());

            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    new Pair<View, String>(view.findViewById(R.id.item_name), "transition_title"),
                    new Pair<View, String>(view.findViewById(R.id.pie_chart), "transition_pie"));
            startActivity(intent, optionsCompat.toBundle());
        };
        recyclerViewOwnedItem.setLayoutManager(new LinearLayoutManager(recyclerViewOwnedItem.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewOwnedItem.setAdapter(new PieAdapter(listOwnedItem, listener, getContext()));
        recyclerViewOwnedItem.addItemDecoration(new HorizontalSpace(getResources().getInteger(R.integer.m_card_view)));
        recyclerViewOwnedItem.setNestedScrollingEnabled(false);
    }

    private void initCreatedItem(List<Item> listCreatedItem) {
        recyclerViewCreatedItem.setVisibility(View.VISIBLE);
        RecyclerViewClickListener listener = (view, position) -> {
            Intent intent = new Intent(getContext(), ItemDetailsActivity.class);
            intent.putExtra("item", new Item());

            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    new Pair<View, String>(view.findViewById(R.id.item_name), "transition_title"),
                    new Pair<View, String>(view.findViewById(R.id.pie_chart), "transition_pie"));
            startActivity(intent, optionsCompat.toBundle());
        };
        recyclerViewCreatedItem.setLayoutManager(new LinearLayoutManager(recyclerViewCreatedItem.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCreatedItem.setAdapter(new PieAdapter(listCreatedItem, listener, getContext()));
        recyclerViewCreatedItem.addItemDecoration(new HorizontalSpace(getResources().getInteger(R.integer.m_card_view)));
        recyclerViewCreatedItem.setNestedScrollingEnabled(false);
    }

    private void initFollowedItem(List<Item> listFollowedItem) {
        recyclerViewFollowedItem.setVisibility(View.VISIBLE);
        RecyclerViewClickListener listener = (view, position) -> {
            Intent intent = new Intent(getContext(), ItemDetailsActivity.class);
            intent.putExtra("item", new Item());

            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    new Pair<View, String>(view.findViewById(R.id.item_name), "transition_title"),
                    new Pair<View, String>(view.findViewById(R.id.pie_chart), "transition_pie"));
            startActivity(intent, optionsCompat.toBundle());
        };
        recyclerViewFollowedItem.setLayoutManager(new LinearLayoutManager(recyclerViewFollowedItem.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewFollowedItem.setAdapter(new PieAdapter(listFollowedItem, listener, getContext()));
        recyclerViewFollowedItem.addItemDecoration(new HorizontalSpace(getResources().getInteger(R.integer.m_card_view)));
        recyclerViewFollowedItem.setNestedScrollingEnabled(false);
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.more_page_created:
                startActivity(new Intent(getContext(), ItemCreatedActivity.class));
                break;
            case R.id.more_page_owned:
                startActivity(new Intent(getContext(), ItemOwnedActivity.class));
                break;
            case R.id.more_page_followed:
                startActivity(new Intent(getContext(), ItemFollowedActivity.class));
                break;
        }
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

        return super.onOptionsItemSelected(item);
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
