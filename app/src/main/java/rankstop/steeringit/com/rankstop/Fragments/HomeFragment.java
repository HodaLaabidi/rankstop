package rankstop.steeringit.com.rankstop.Fragments;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import rankstop.steeringit.com.rankstop.Activities.AddItemActivity;
import rankstop.steeringit.com.rankstop.Activities.ItemCreatedActivity;
import rankstop.steeringit.com.rankstop.Activities.ItemDetailsActivity;
import rankstop.steeringit.com.rankstop.Activities.ItemFollowedActivity;
import rankstop.steeringit.com.rankstop.Activities.ItemOwnedActivity;
import rankstop.steeringit.com.rankstop.Adapter.PieAdapter;
import rankstop.steeringit.com.rankstop.Interface.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.Model.Item;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.Utils.HorizontalSpace;

public class HomeFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    final String TAG = "HOME FRAGMENT";

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final int COLOR_ANIMATIONS_DURATION = 200;

    private AppBarLayout appbar;
    private ImageView coverImage;
    private Toolbar toolbar;
    private ProgressBar progressBarTopRanked, progressBarTopViewed, progressBarTopFollowed, progressBarTopCommented;

    private List<Item> listTopRankedItem, listTopViewedItem, listTopFollowedItem, listTopCommentedItem;

    private RecyclerView recyclerViewTopRanked, recyclerViewTopViewed, recyclerViewTopFollowed, recyclerViewTopCommented;

    private boolean isTransparentBg = true;
    private MaterialButton moreTopRankedBtn, moreTopViewedBtn, moreTopCommentedBtn, moreTopFollowedBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("LIFE_CYCLE", "" + TAG + " onCreateView");
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("LIFE_CYCLE", "" + TAG + " onActivityCreated");

        bindViews();
    }

    private void bindViews() {
        appbar = getActivity().findViewById(R.id.appbar);
        coverImage = getActivity().findViewById(R.id.imageview_placeholder);
        toolbar = getActivity().findViewById(R.id.toolbar);

        recyclerViewTopRanked = getActivity().findViewById(R.id.recycler_view_top_ranked);
        recyclerViewTopViewed = getActivity().findViewById(R.id.recycler_view_top_viewed);
        recyclerViewTopFollowed = getActivity().findViewById(R.id.recycler_view_top_followed);
        recyclerViewTopCommented = getActivity().findViewById(R.id.recycler_view_top_commented);

        progressBarTopRanked = getActivity().findViewById(R.id.progress_bar_top_ranked);
        progressBarTopViewed = getActivity().findViewById(R.id.progress_bar_top_viewed);
        progressBarTopCommented = getActivity().findViewById(R.id.progress_bar_top_commented);
        progressBarTopFollowed = getActivity().findViewById(R.id.progress_bar_top_followed);

        moreTopRankedBtn = getActivity().findViewById(R.id.more_page_top_ranked);
        moreTopViewedBtn = getActivity().findViewById(R.id.more_page_top_viewed);
        moreTopCommentedBtn = getActivity().findViewById(R.id.more_page_top_commented);
        moreTopFollowedBtn = getActivity().findViewById(R.id.more_page_top_followed);

        moreTopRankedBtn.setOnClickListener(this);
        moreTopViewedBtn.setOnClickListener(this);
        moreTopCommentedBtn.setOnClickListener(this);
        moreTopFollowedBtn.setOnClickListener(this);

        toolbar.setTitle("");
        appbar.addOnOffsetChangedListener(this);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        coverImage.setImageResource(R.drawable.cover);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddItemActivity.class));
            }
        });

        loadHomeData();
    }

    private void loadHomeData() {
        loadTopRankedItem();
        loadTopViewedItem();
        loadTopFollowedItem();
        loadTopCommentedItem();
    }

    private void loadTopRankedItem() {
        progressBarTopRanked.setVisibility(View.GONE);
        listTopRankedItem = new ArrayList<>();
        initTopRanked(listTopRankedItem);
    }

    private void loadTopViewedItem() {
        progressBarTopViewed.setVisibility(View.GONE);
        listTopViewedItem = new ArrayList<>();
        initTopViewed(listTopViewedItem);
    }

    private void loadTopFollowedItem() {
        progressBarTopFollowed.setVisibility(View.GONE);
        listTopFollowedItem = new ArrayList<>();
        initTopFollowed(listTopFollowedItem);
    }

    private void loadTopCommentedItem() {
        progressBarTopCommented.setVisibility(View.GONE);
        listTopCommentedItem = new ArrayList<>();
        initTopCommented(listTopCommentedItem);
    }

    private void initTopRanked(List<Item> listTopRankedItem) {
        recyclerViewTopRanked.setVisibility(View.VISIBLE);
        RecyclerViewClickListener listener = (view, position) -> {
            Intent intent = new Intent(getContext(), ItemDetailsActivity.class);
            intent.putExtra("item", new Item());

            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    new Pair<View, String>(view.findViewById(R.id.item_name), "transition_title"),
                    new Pair<View, String>(view.findViewById(R.id.pie_chart), "transition_pie"));
            startActivity(intent, optionsCompat.toBundle());

        };
        recyclerViewTopRanked.setLayoutManager(new LinearLayoutManager(recyclerViewTopRanked.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTopRanked.setAdapter(new PieAdapter(listTopRankedItem, listener, getContext()));
        recyclerViewTopRanked.addItemDecoration(new HorizontalSpace(getResources().getInteger(R.integer.m_card_view)));
        recyclerViewTopRanked.setNestedScrollingEnabled(false);
    }

    private void initTopViewed(List<Item> listTopViewedItem) {
        recyclerViewTopViewed.setVisibility(View.VISIBLE);
        RecyclerViewClickListener listener = (view, position) -> {
            Intent intent = new Intent(getContext(), ItemDetailsActivity.class);
            intent.putExtra("item", new Item());

            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    new Pair<View, String>(view.findViewById(R.id.item_name), "transition_title"),
                    new Pair<View, String>(view.findViewById(R.id.pie_chart), "transition_pie"));
            startActivity(intent, optionsCompat.toBundle());
        };
        recyclerViewTopViewed.setLayoutManager(new LinearLayoutManager(recyclerViewTopViewed.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTopViewed.setAdapter(new PieAdapter(listTopViewedItem, listener, getContext()));
        recyclerViewTopViewed.addItemDecoration(new HorizontalSpace(getResources().getInteger(R.integer.m_card_view)));
        recyclerViewTopViewed.setNestedScrollingEnabled(false);
    }

    private void initTopFollowed(List<Item> listTopFollowedItem) {
        recyclerViewTopFollowed.setVisibility(View.VISIBLE);
        RecyclerViewClickListener listener = (view, position) -> {
            Intent intent = new Intent(getContext(), ItemDetailsActivity.class);
            intent.putExtra("item", new Item());

            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    new Pair<View, String>(view.findViewById(R.id.item_name), "transition_title"),
                    new Pair<View, String>(view.findViewById(R.id.pie_chart), "transition_pie"));
            startActivity(intent, optionsCompat.toBundle());
        };
        recyclerViewTopFollowed.setLayoutManager(new LinearLayoutManager(recyclerViewTopFollowed.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTopFollowed.setAdapter(new PieAdapter(listTopFollowedItem, listener, getContext()));
        recyclerViewTopFollowed.addItemDecoration(new HorizontalSpace(getResources().getInteger(R.integer.m_card_view)));
        recyclerViewTopFollowed.setNestedScrollingEnabled(false);
    }

    private void initTopCommented(List<Item> listTopCommentedItem) {
        recyclerViewTopCommented.setVisibility(View.VISIBLE);
        RecyclerViewClickListener listener = (view, position) -> {
            Intent intent = new Intent(getContext(), ItemDetailsActivity.class);
            intent.putExtra("item", new Item());

            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    new Pair<View, String>(view.findViewById(R.id.item_name), "transition_title"),
                    new Pair<View, String>(view.findViewById(R.id.pie_chart), "transition_pie"));
            startActivity(intent, optionsCompat.toBundle());
        };
        recyclerViewTopCommented.setLayoutManager(new LinearLayoutManager(recyclerViewTopCommented.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTopCommented.setAdapter(new PieAdapter(listTopCommentedItem, listener, getContext()));
        recyclerViewTopCommented.addItemDecoration(new HorizontalSpace(getResources().getInteger(R.integer.m_card_view)));
        recyclerViewTopCommented.setNestedScrollingEnabled(false);
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
                startColorAnimation(toolbar, COLOR_ANIMATIONS_DURATION, Color.TRANSPARENT, getResources().getColor(R.color.colorPrimary));
                isTransparentBg = false;
            }
        } else {
            if (!isTransparentBg) {
                startColorAnimation(toolbar, COLOR_ANIMATIONS_DURATION, getResources().getColor(R.color.colorPrimary), Color.TRANSPARENT);
                isTransparentBg = true;
            }
        }
    }

    public static void startColorAnimation(final View view, long duration, int colorFrom, int colorTo) {
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more_page_top_ranked:
                startActivity(new Intent(getContext(), ItemCreatedActivity.class));
                break;
            case R.id.more_page_top_viewed:
                startActivity(new Intent(getContext(), ItemOwnedActivity.class));
                break;
            case R.id.more_page_top_commented:
                startActivity(new Intent(getContext(), ItemFollowedActivity.class));
                break;
            case R.id.more_page_top_followed:
                startActivity(new Intent(getContext(), ItemFollowedActivity.class));
                break;
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("LIFE_CYCLE", "" + TAG + " onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("LIFE_CYCLE", "" + TAG + " onCreate");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("LIFE_CYCLE", "" + TAG + " onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("LIFE_CYCLE", "" + TAG + " onResume");
    }

    @Override
    public void onPause() {
        Log.i("LIFE_CYCLE", "" + TAG + " onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("LIFE_CYCLE", "" + TAG + " onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i("LIFE_CYCLE", "" + TAG + " onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i("LIFE_CYCLE", "" + TAG + " onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.i("LIFE_CYCLE", "" + TAG + " onDetach");
        super.onDetach();
    }
}
