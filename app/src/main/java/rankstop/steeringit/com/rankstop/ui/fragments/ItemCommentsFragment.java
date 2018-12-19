package rankstop.steeringit.com.rankstop.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import rankstop.steeringit.com.rankstop.MVP.model.PresenterItemImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.Comment;
import rankstop.steeringit.com.rankstop.data.model.Item;
import rankstop.steeringit.com.rankstop.data.model.custom.RSAddReview;
import rankstop.steeringit.com.rankstop.data.model.custom.RSRequestItemData;
import rankstop.steeringit.com.rankstop.data.model.custom.RSResponseItemData;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.ItemCommentsAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.utils.EndlessScrollListener;
import rankstop.steeringit.com.rankstop.utils.HorizontalSpace;
import rankstop.steeringit.com.rankstop.utils.LinearScrollListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.VerticalSpace;

public class ItemCommentsFragment extends Fragment implements RSView.StandardView {

    private View rootView;
    private RecyclerView commentsRV, myCommentsRV;
    private TextView titleOtherCom;
    private ItemCommentsAdapter itemCommentsAdapter, myItemCommentsAdapter;
    private List<Comment> comments, myComments;
    private MaterialButton addCommentBTN;
    private ProgressBar progressBar, mcProgressBar;
    private RelativeLayout myCommentsLayout;
    private LinearLayout addCommentLayout;
    private RecyclerViewClickListener listener, myListener;


    private RadioGroup filterToggle;
    private int lastCheckedId = R.id.all_comment;
    private String itemId, userId;
    private Item currentItem;
    private RSPresenter.ItemPresenter itemPresenter;
    private RSRequestItemData rsRequestItemData;
    // for pagination
    private int currentPage = 1;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private int PAGES_COUNT = 1;

    // *mc: my comments
    private int mcCurrentPage = 1;
    private boolean mcIsLastPage = false;
    private boolean mcIsLoading = false;
    private int MC_PAGES_COUNT = 1;

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
        if (myComments == null)
            myComments = new ArrayList<>();
        currentItem = (Item) getArguments().getSerializable(RSConstants.ITEM);
        itemId = currentItem.getItemDetails().get_id();
        if (RSSession.isLoggedIn(getContext())) {
            myCommentsLayout.setVisibility(View.VISIBLE);
            userId = RSSession.getCurrentUser(getContext()).get_id();
            myCommentsLayout.setVisibility(View.VISIBLE);
            titleOtherCom.setVisibility(View.VISIBLE);
        }
        rsRequestItemData = new RSRequestItemData(itemId, userId, RSConstants.MAX_FIELD_TO_LOAD, 1);
        loadItemComments(currentPage);
        loadMyItemComments(mcCurrentPage);
        setFragmentActionListener((ContainerActivity) getActivity());
        listener = (view, position) -> {
        };
        myListener = (view, position) -> {
        };
        initCommentsList();
        initMyCommentsList();
        setFilterListener();

        addCommentBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RSAddReview rsAddReview = new RSAddReview();
                rsAddReview.setItemId(currentItem.getItemDetails().get_id());
                rsAddReview.setCategoryId(currentItem.getItemDetails().getCategory().get_id());
                fragmentActionListener.startFragment(AddReviewFragment.getInstance(rsAddReview, currentItem.getLastEvalUser(), ""), RSConstants.FRAGMENT_ADD_REVIEW);
            }
        });
    }

    private void initCommentsList() {
        itemCommentsAdapter = new ItemCommentsAdapter(listener, getContext(), RSConstants.OTHER);
        GridLayoutManager layoutManager = new GridLayoutManager(commentsRV.getContext(), getResources().getInteger(R.integer.count_item_per_row));
        commentsRV.setLayoutManager(layoutManager);
        commentsRV.addItemDecoration(new VerticalSpace(getResources().getInteger(R.integer.m_card_view), getResources().getInteger(R.integer.count_item_per_row)));
        commentsRV.setAdapter(itemCommentsAdapter);
        commentsRV.addOnScrollListener(new EndlessScrollListener(layoutManager) {
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

    private void initMyCommentsList() {
        myItemCommentsAdapter = new ItemCommentsAdapter(myListener, getContext(), RSConstants.MINE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(myCommentsRV.getContext(), LinearLayoutManager.HORIZONTAL, false);
        myCommentsRV.setLayoutManager(layoutManager);
        myCommentsRV.addItemDecoration(new HorizontalSpace(getResources().getInteger(R.integer.m_card_view)));
        myCommentsRV.setAdapter(myItemCommentsAdapter);
        myCommentsRV.addOnScrollListener(new LinearScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                mcIsLoading = true;
                mcCurrentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadMyItemComments(mcCurrentPage);
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return MC_PAGES_COUNT;
            }

            @Override
            public boolean isLastPage() {
                return mcIsLastPage;
            }

            @Override
            public boolean isLoading() {
                return mcIsLoading;
            }
        });
    }

    private void loadItemComments(int pageNumber) {
        rsRequestItemData.setPage(pageNumber);
        itemPresenter.loadItemComments(rsRequestItemData);
    }

    private void loadMyItemComments(int pageNumber) {
        rsRequestItemData.setPage(pageNumber);
        /*Toast.makeText(getContext(), "userId = " + rsRequestItemData.getUserId()
                + "\nitemId = " + rsRequestItemData.getItemId()
                + "\npage = " + rsRequestItemData.getPage()
                + "\nperPage = " + rsRequestItemData.getPerPage(), Toast.LENGTH_LONG).show();*/
        itemPresenter.loadItemCommentsByUser(rsRequestItemData);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void bindViews() {
        filterToggle = rootView.findViewById(R.id.filter_toggle);
        commentsRV = rootView.findViewById(R.id.recycler_view_comments);
        myCommentsRV = rootView.findViewById(R.id.recycler_view_my_comments);
        progressBar = rootView.findViewById(R.id.main_progress);
        mcProgressBar = rootView.findViewById(R.id.mc_progress);
        myCommentsLayout = rootView.findViewById(R.id.rl_my_comments);
        addCommentLayout = rootView.findViewById(R.id.ll_add_comments);
        titleOtherCom = rootView.findViewById(R.id.title_other_com);
        addCommentBTN = rootView.findViewById(R.id.btn_add_comment);
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
                        myItemCommentsAdapter.refreshData(myComments);
                        break;
                    case R.id.good_comment:
                        filterToggle.setBackgroundResource(R.drawable.rs_filter_view_green);
                        itemCommentsAdapter.refreshData(getFilterOutput(comments, RSConstants.PIE_GREEN));
                        myItemCommentsAdapter.refreshData(getFilterOutput(myComments, RSConstants.PIE_GREEN));
                        break;
                    case R.id.neutral_comment:
                        filterToggle.setBackgroundResource(R.drawable.rs_filter_view_orange);
                        itemCommentsAdapter.refreshData(getFilterOutput(comments, RSConstants.PIE_ORANGE));
                        myItemCommentsAdapter.refreshData(getFilterOutput(myComments, RSConstants.PIE_ORANGE));
                        break;
                    case R.id.bad_comment:
                        filterToggle.setBackgroundResource(R.drawable.rs_filter_view_red);
                        itemCommentsAdapter.refreshData(getFilterOutput(comments, RSConstants.PIE_RED));
                        myItemCommentsAdapter.refreshData(getFilterOutput(myComments, RSConstants.PIE_RED));
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

    public static ItemCommentsFragment getInstance(Item item) {
        Bundle args = new Bundle();
        args.putSerializable(RSConstants.ITEM, item);
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
        myComments.clear();
        myComments = null;
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
                break;
            case RSConstants.ITEM_COMMENTS_BY_USER:
                RSResponseItemData response = new Gson().fromJson(new Gson().toJson(data), RSResponseItemData.class);
                try {
                    if (response.getComments().size() == 0){
                        addCommentLayout.setVisibility(View.VISIBLE);
                        myCommentsRV.setVisibility(View.GONE);
                        mcProgressBar.setVisibility(View.GONE);
                    }else {
                        manageMyCommentsList(response);
                    }
                } catch (Exception e) {
                }
                break;
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

    private void manageMyCommentsList(RSResponseItemData rsResponseItemData) {
        myComments.addAll(rsResponseItemData.getComments());

        if (rsResponseItemData.getCurrent() == 1) {
            mcProgressBar.setVisibility(View.GONE);
            switch (lastCheckedId) {
                case R.id.all_comment:
                    myItemCommentsAdapter.addAll(rsResponseItemData.getComments());
                    break;
                case R.id.good_comment:
                    myItemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_GREEN));
                    break;
                case R.id.neutral_comment:
                    myItemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_ORANGE));
                    break;
                case R.id.bad_comment:
                    myItemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_RED));
                    break;
            }
            MC_PAGES_COUNT = rsResponseItemData.getPages();

            if (mcCurrentPage < MC_PAGES_COUNT) {
                myItemCommentsAdapter.addLoadingFooter();
            } else {
                mcIsLastPage = true;
            }
        } else if (rsResponseItemData.getCurrent() > 1) {
            myItemCommentsAdapter.removeLoadingFooter();
            mcIsLoading = false;
            switch (lastCheckedId) {
                case R.id.all_comment:
                    myItemCommentsAdapter.addAll(rsResponseItemData.getComments());
                    break;
                case R.id.good_comment:
                    myItemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_GREEN));
                    break;
                case R.id.neutral_comment:
                    myItemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_ORANGE));
                    break;
                case R.id.bad_comment:
                    myItemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_RED));
                    break;
            }
            if (mcCurrentPage != MC_PAGES_COUNT) myItemCommentsAdapter.addLoadingFooter();
            else mcIsLastPage = true;
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
