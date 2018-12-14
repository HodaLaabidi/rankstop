package rankstop.steeringit.com.rankstop.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import rankstop.steeringit.com.rankstop.MVP.model.PresenterItemImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.Comment;
import rankstop.steeringit.com.rankstop.data.model.custom.RSRequestItemData;
import rankstop.steeringit.com.rankstop.data.model.custom.RSResponseItemData;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.ItemCommentAdapter;
import rankstop.steeringit.com.rankstop.ui.adapter.ItemCommentsAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.utils.EndlessScrollListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.VerticalSpace;

public class ItemCommentsFragment extends Fragment implements RSView.StandardView {

    private RecyclerView recyclerViewComments;
    private View rootView;
    private List<Comment> comments;
    private RadioGroup filterToggle;
    private int lastCheckedId = R.id.all_comment;
    private ItemCommentsAdapter itemCommentsAdapter;
    private String itemId, userId;
    private RSPresenter.ItemPresenter itemPresenter;
    private RecyclerViewClickListener listener;
    private ProgressBar progressBar;
    // for pagination
    private int currentPage = 1;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private int PAGES_COUNT = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_item_comments, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bindViews();
        currentPage = 1;
        isLastPage = false;
        isLoading = false;
        if (comments == null)
            comments = new ArrayList<>();
        itemId = getArguments().getString(RSConstants.ITEM_ID);
        if (RSSession.isLoggedIn(getContext())) {
            userId = RSSession.getCurrentUser(getContext()).get_id();
        }
        loadItemComments(currentPage);
        setFragmentActionListener((ContainerActivity) getActivity());
        listener = (view, position) -> {
        };
        initCommentsList();
        setFilterListener();
    }

    private void initCommentsList() {
        itemCommentsAdapter = new ItemCommentsAdapter(listener, getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(recyclerViewComments.getContext(), getResources().getInteger(R.integer.count_item_per_row));
        recyclerViewComments.setLayoutManager(layoutManager);
        recyclerViewComments.addItemDecoration(new VerticalSpace(getResources().getInteger(R.integer.m_card_view), getResources().getInteger(R.integer.count_item_per_row)));
        recyclerViewComments.setAdapter(itemCommentsAdapter);
        recyclerViewComments.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadItemComments(currentPage);
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return PAGES_COUNT;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    private void loadItemComments(int pageNumber) {
        RSRequestItemData rsRequestItemData = new RSRequestItemData(itemId, userId, RSConstants.MAX_FIELD_TO_LOAD, pageNumber);
        itemPresenter.loadItemComments(rsRequestItemData);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void bindViews() {
        filterToggle = rootView.findViewById(R.id.filter_toggle);
        recyclerViewComments = rootView.findViewById(R.id.recycler_view_comments);
        progressBar = rootView.findViewById(R.id.main_progress);
        itemPresenter = new PresenterItemImpl(ItemCommentsFragment.this);
    }

    private void setFilterListener() {
        filterToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (lastCheckedId) {
                    case R.id.all_comment:
                        ((RadioButton) rootView.findViewById(lastCheckedId)).setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));
                        break;
                    case R.id.good_comment:
                        ((RadioButton) rootView.findViewById(lastCheckedId)).setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreenPie));
                        break;
                    case R.id.neutral_comment:
                        ((RadioButton) rootView.findViewById(lastCheckedId)).setTextColor(ContextCompat.getColor(getContext(), R.color.colorOrangePie));
                        break;
                    case R.id.bad_comment:
                        ((RadioButton) rootView.findViewById(lastCheckedId)).setTextColor(ContextCompat.getColor(getContext(), R.color.colorRedPie));
                        break;
                }
                lastCheckedId = checkedId;
                ((RadioButton) rootView.findViewById(checkedId)).setTextColor(Color.WHITE);
                switch (checkedId) {
                    case R.id.all_comment:
                        filterToggle.setBackgroundResource(R.drawable.rs_filter_view_gray);
                        itemCommentsAdapter.refreshData(comments);
                        break;
                    case R.id.good_comment:
                        filterToggle.setBackgroundResource(R.drawable.rs_filter_view_green);
                        itemCommentsAdapter.refreshData(getFilterOutput(comments, RSConstants.PIE_GREEN));
                        break;
                    case R.id.neutral_comment:
                        filterToggle.setBackgroundResource(R.drawable.rs_filter_view_orange);
                        itemCommentsAdapter.refreshData(getFilterOutput(comments, RSConstants.PIE_ORANGE));
                        break;
                    case R.id.bad_comment:
                        filterToggle.setBackgroundResource(R.drawable.rs_filter_view_red);
                        itemCommentsAdapter.refreshData(getFilterOutput(comments, RSConstants.PIE_RED));
                        break;
                }
            }
        });
    }

    private List<Comment> getFilterOutput(List<Comment> comments, int filter) {
        List<Comment> result = new ArrayList<>();
        for (Comment comment : comments) {
            if (filter == comment.getColor()) {
                result.add(comment);
            }
        }
        return result;
    }

    private FragmentActionListener fragmentActionListener;

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    private static ItemCommentsFragment instance;

    public static ItemCommentsFragment getInstance(String itemId) {
        Bundle args = new Bundle();
        args.putString(RSConstants.ITEM_ID, itemId);
        if (instance == null) {
            instance = new ItemCommentsFragment();
        }
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onDestroyView() {
        instance = null;
        rootView = null;
        fragmentActionListener = null;
        comments.clear();
        comments = null;
        //itemPresenter.onDestroyItem();
        super.onDestroyView();
    }

    @Override
    public void onSuccess(String target, Object data) {
        switch (target) {
            case RSConstants.ITEM_COMMENTS:
                RSResponseItemData rsResponseItemData = new Gson().fromJson(new Gson().toJson(data), RSResponseItemData.class);
                try {
                    manageCommentsList(rsResponseItemData);
                } catch (Exception e) {
                }
        }
    }

    private void manageCommentsList(RSResponseItemData rsResponseItemData) {
        comments.addAll(rsResponseItemData.getComments());
        if (rsResponseItemData.getCurrent() == 1) {
            progressBar.setVisibility(View.GONE);
            switch (lastCheckedId) {
                case R.id.all_comment:
                    itemCommentsAdapter.addAll(rsResponseItemData.getComments());
                    break;
                case R.id.good_comment:
                    itemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_GREEN));
                    break;
                case R.id.neutral_comment:
                    itemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_ORANGE));
                    break;
                case R.id.bad_comment:
                    itemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_RED));
                    break;
            }
            PAGES_COUNT = rsResponseItemData.getPages();

            Log.i("TAG_PIX", "current page from comments == " + currentPage);
            Log.i("TAG_PIX", "page count from comments == " + PAGES_COUNT);

            if (currentPage < PAGES_COUNT) {
                itemCommentsAdapter.addLoadingFooter();
            } else {
                isLastPage = true;
            }
        } else if (rsResponseItemData.getCurrent() > 1) {
            itemCommentsAdapter.removeLoadingFooter();
            isLoading = false;
            switch (lastCheckedId) {
                case R.id.all_comment:
                    itemCommentsAdapter.addAll(rsResponseItemData.getComments());
                    break;
                case R.id.good_comment:
                    itemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_GREEN));
                    break;
                case R.id.neutral_comment:
                    itemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_ORANGE));
                    break;
                case R.id.bad_comment:
                    itemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_RED));
                    break;
            }
            if (currentPage != PAGES_COUNT) itemCommentsAdapter.addLoadingFooter();
            else isLastPage = true;
        }
    }

    @Override
    public void onFailure(String target) {

    }

    @Override
    public void showProgressBar(String target) {

    }

    @Override
    public void hideProgressBar(String target) {

    }

    @Override
    public void showMessage(String target, String message) {
        switch (target) {
            case RSConstants.ITEM_COMMENTS:
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                break;
            case RSConstants.ITEM_PIX:
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
