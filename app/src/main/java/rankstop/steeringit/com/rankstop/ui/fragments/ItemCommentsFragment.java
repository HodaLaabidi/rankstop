package rankstop.steeringit.com.rankstop.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterItemImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.customviews.RSBTNMedium;
import rankstop.steeringit.com.rankstop.customviews.RSCustomToast;
import rankstop.steeringit.com.rankstop.customviews.RSRBMedium;
import rankstop.steeringit.com.rankstop.customviews.RSTVRegular;
import rankstop.steeringit.com.rankstop.customviews.RSTVSemiBold;
import rankstop.steeringit.com.rankstop.data.model.db.Category;
import rankstop.steeringit.com.rankstop.data.model.db.Comment;
import rankstop.steeringit.com.rankstop.data.model.db.Item;
import rankstop.steeringit.com.rankstop.data.model.network.RSAddReview;
import rankstop.steeringit.com.rankstop.data.model.network.RSNavigationData;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestItemData;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponseItemData;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.ItemCommentsAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.ReviewCardListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.DialogConfirmationListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.AlertConfirmationDialog;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.AskToLoginDialog;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.ShowCommentDialog;
import rankstop.steeringit.com.rankstop.utils.EndlessScrollListener;
import rankstop.steeringit.com.rankstop.utils.HorizontalSpace;
import rankstop.steeringit.com.rankstop.utils.LinearScrollListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;
import rankstop.steeringit.com.rankstop.utils.VerticalSpace;

public class ItemCommentsFragment extends Fragment implements RSView.StandardView, DialogConfirmationListener {

    private View rootView;
    private Unbinder unbinder;

    @BindView(R.id.recycler_view_comments)
    RecyclerView commentsRV;
    @BindView(R.id.recycler_view_my_comments)
    RecyclerView myCommentsRV;
    @BindView(R.id.title_other_com)
    RSTVSemiBold titleOtherCom;
    @BindView(R.id.main_progress)
    ProgressBar progressBar;
    @BindView(R.id.mc_progress)
    ProgressBar mcProgressBar;
    @BindView(R.id.rl_my_comments)
    RelativeLayout myCommentsLayout;
    @BindView(R.id.tv_no_comment)
    RSTVRegular noCommentTV;
    @BindView(R.id.tv_no_other_comment)
    RSTVRegular noOtherCommentTV;
    @BindView(R.id.filter_toggle)
    RadioGroup filterToggle;

    @BindView(R.id.btn_add_comment2)
    RSBTNMedium addCommentBTN;

    @OnClick({R.id.btn_add_comment2, R.id.btn_add_comment})
    void addComment() {
        if (RSNetwork.isConnected(getContext())) {
            if (RSSession.isLoggedIn()) {
                RSAddReview rsAddReview = new RSAddReview();
                rsAddReview.setItemId(currentItem.getItemDetails().get_id());
                rsAddReview.setCategoryId(currentCategory.get_id());
                fragmentActionListener.startFragment(AddReviewFragment.getInstance(rsAddReview, currentItem.getLastEvalUser(), "", RSConstants.ACTION_COMMENT), RSConstants.FRAGMENT_ADD_REVIEW);
            } else {
                RSNavigationData rsNavigationData = new RSNavigationData(RSConstants.FRAGMENT_ADD_REVIEW, RSConstants.ACTION_ADD_REVIEW, alertLoginToAddCommentsMsg, itemId, "", currentCategory.get_id(), RSConstants.ACTION_COMMENT);
                askToLoginDialog(rsNavigationData);
            }
        } else {
            onOffLine();
        }
    }

    private void askToLoginDialog(RSNavigationData rsNavigationData) {
        AskToLoginDialog dialog = AskToLoginDialog.newInstance(rsNavigationData);
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "");
    }

    private ReviewCardListener listener, myListener;
    private ItemCommentsAdapter itemCommentsAdapter, myItemCommentsAdapter;
    private List<Comment> comments, myComments;
    private Category currentCategory;

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

    @BindInt(R.integer.m_card_view)
    int marginCardView;
    @BindInt(R.integer.count_item_per_row)
    int countItemPerRow;

    @BindColor(R.color.colorAccent)
    int colorAccent;

    @BindString(R.string.message_delete_comment)
    String deleteCommentMsg;
    @BindString(R.string.off_line)
    String offlineMsg;
    @BindString(R.string.alert_login_to_add_comment)
    String alertLoginToAddCommentsMsg;
    @BindString(R.string.no_comments)
    String noCommentsText;
    @BindString(R.string.no_comments_by_filter)
    String noCommentsByFilter;
    @BindString(R.string.comment_deleted_successfully)
    String commentDeletedSuccessfullyMsg;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_item_comments, container, false);
        unbinder = ButterKnife.bind(this, rootView);
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
        currentCategory = new Gson().fromJson(new Gson().toJson(currentItem.getItemDetails().getCategory()), Category.class);
        itemId = currentItem.getItemDetails().get_id();
        if (RSSession.isLoggedIn()) {
            myCommentsLayout.setVisibility(View.VISIBLE);
            userId = RSSession.getCurrentUser().get_id();
            myCommentsLayout.setVisibility(View.VISIBLE);
            titleOtherCom.setVisibility(View.VISIBLE);
        } else {
            addCommentBTN.setVisibility(View.VISIBLE);
        }
        rsRequestItemData = new RSRequestItemData(itemId, userId, RSConstants.MAX_FIELD_TO_LOAD, 1);
        if (RSNetwork.isConnected(getContext())) {
            progressBar.setVisibility(View.VISIBLE);
            mcProgressBar.setVisibility(View.VISIBLE);
            loadItemComments(currentPage);
            loadMyItemComments(mcCurrentPage);
        } else {
            onOffLine();
        }
        setFragmentActionListener((ContainerActivity) getActivity());
        listener = new ReviewCardListener() {
            @Override
            public void onRemoveClicked(int position) {

            }

            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(RSConstants.COMMENT, comments.get(position));
                bundle.putString(RSConstants.USER_ID, userId);
                showFullComment(bundle);
            }
        };
        myListener = new ReviewCardListener() {
            @Override
            public void onRemoveClicked(int position) {
                openDialogConfirmation(myComments.get(position));
            }

            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(RSConstants.COMMENT, myComments.get(position));
                bundle.putString(RSConstants.USER_ID, userId);
                showFullComment(bundle);
            }
        };
        initCommentsList();
        initMyCommentsList();
        setFilterListener();
    }

    private void openDialogConfirmation(Comment comment) {

        Bundle bundle = new Bundle();
        bundle.putString(RSConstants.MESSAGE, deleteCommentMsg);
        bundle.putString(RSConstants._ID, comment.get_id());
        AlertConfirmationDialog dialog = new AlertConfirmationDialog();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.setCancelable(false);
        dialog.setArguments(bundle);
        dialog.setTargetFragment(this, 0);
        dialog.show(ft, AlertConfirmationDialog.TAG);
    }

    private void showFullComment(Bundle bundle) {
        ShowCommentDialog dialog = new ShowCommentDialog();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.setArguments(bundle);
        dialog.setTargetFragment(this, 0);
        dialog.show(ft, ShowCommentDialog.TAG);
    }

    private void initCommentsList() {
        itemCommentsAdapter = new ItemCommentsAdapter(listener, RSConstants.OTHER);
        GridLayoutManager layoutManager = new GridLayoutManager(commentsRV.getContext(), countItemPerRow);
        commentsRV.setLayoutManager(layoutManager);
        commentsRV.addItemDecoration(new VerticalSpace(marginCardView, countItemPerRow));
        commentsRV.setAdapter(itemCommentsAdapter);
        commentsRV.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                loadItemComments(currentPage);
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
        myItemCommentsAdapter = new ItemCommentsAdapter(myListener, RSConstants.MINE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(myCommentsRV.getContext(), LinearLayoutManager.HORIZONTAL, false);
        myCommentsRV.setLayoutManager(layoutManager);
        myCommentsRV.addItemDecoration(new HorizontalSpace(marginCardView));
        myCommentsRV.setAdapter(myItemCommentsAdapter);
        myCommentsRV.addOnScrollListener(new LinearScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                mcIsLoading = true;
                mcCurrentPage += 1;

                loadMyItemComments(mcCurrentPage);
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
        itemPresenter.loadItemComments(rsRequestItemData, getContext());
    }

    private void loadMyItemComments(int pageNumber) {
        rsRequestItemData.setPage(pageNumber);
        itemPresenter.loadItemCommentsByUser(rsRequestItemData, getContext());
    }

    private void bindViews() {
        itemPresenter = new PresenterItemImpl(ItemCommentsFragment.this);
    }

    private void setFilterListener() {
        filterToggle.setOnCheckedChangeListener((group, checkedId) -> {
            switch (lastCheckedId) {
                case R.id.all_comment:
                    ((RSRBMedium) rootView.findViewById(lastCheckedId)).setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));
                    break;
                case R.id.good_comment:
                    ((RSRBMedium) rootView.findViewById(lastCheckedId)).setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreenPie));
                    break;
                case R.id.neutral_comment:
                    ((RSRBMedium) rootView.findViewById(lastCheckedId)).setTextColor(ContextCompat.getColor(getContext(), R.color.colorOrangePie));
                    break;
                case R.id.bad_comment:
                    ((RSRBMedium) rootView.findViewById(lastCheckedId)).setTextColor(ContextCompat.getColor(getContext(), R.color.colorRedPie));
                    break;
            }
            lastCheckedId = checkedId;
            ((RSRBMedium) rootView.findViewById(checkedId)).setTextColor(Color.WHITE);
            switch (checkedId) {
                case R.id.all_comment:
                    filterToggle.setBackgroundResource(R.drawable.rs_filter_view_gray);
                    if (comments.size() > 0) {
                        noOtherCommentTV.setVisibility(View.GONE);
                        itemCommentsAdapter.refreshData(comments);
                    }
                    if (myComments.size() > 0) {
                        noCommentTV.setVisibility(View.GONE);
                        myItemCommentsAdapter.refreshData(myComments);
                    }
                    break;
                case R.id.good_comment:
                    filterToggle.setBackgroundResource(R.drawable.rs_filter_view_green);
                    if (comments.size() > 0)
                        itemCommentsAdapter.refreshData(getFilterOutput(comments, RSConstants.PIE_GREEN, RSConstants.OTHER));
                    if (myComments.size() > 0)
                        myItemCommentsAdapter.refreshData(getFilterOutput(myComments, RSConstants.PIE_GREEN, RSConstants.MINE));
                    break;
                case R.id.neutral_comment:
                    filterToggle.setBackgroundResource(R.drawable.rs_filter_view_orange);
                    if (comments.size() > 0)
                        itemCommentsAdapter.refreshData(getFilterOutput(comments, RSConstants.PIE_ORANGE, RSConstants.OTHER));
                    if (myComments.size() > 0)
                        myItemCommentsAdapter.refreshData(getFilterOutput(myComments, RSConstants.PIE_ORANGE, RSConstants.MINE));
                    break;
                case R.id.bad_comment:
                    filterToggle.setBackgroundResource(R.drawable.rs_filter_view_red);
                    if (comments.size() > 0)
                        itemCommentsAdapter.refreshData(getFilterOutput(comments, RSConstants.PIE_RED, RSConstants.OTHER));
                    if (myComments.size() > 0)
                        myItemCommentsAdapter.refreshData(getFilterOutput(myComments, RSConstants.PIE_RED, RSConstants.MINE));
                    break;
            }
        });
    }

    private List<Comment> getFilterOutput(List<Comment> comments, int filter, String target) {
        List<Comment> result = new ArrayList<>();
        for (Comment comment : comments) {
            if (filter == comment.getColor()) {
                result.add(comment);
            }
        }
        if (result.size() == 0) {
            if (target.equals(RSConstants.MINE)) {
                noCommentTV.setText(noCommentsByFilter);
                noCommentTV.setVisibility(View.VISIBLE);
            } else if (target.equals(RSConstants.OTHER)) {
                noOtherCommentTV.setText(noCommentsByFilter);
                noOtherCommentTV.setVisibility(View.VISIBLE);
            }
        } else {
            if (target.equals(RSConstants.MINE)) {
                noCommentTV.setVisibility(View.GONE);
            } else if (target.equals(RSConstants.OTHER)) {
                noOtherCommentTV.setVisibility(View.GONE);
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
        if (comments != null)
            comments.clear();
        comments = null;
        if (myComments != null)
            myComments.clear();
        myComments = null;
        if (unbinder != null)
            unbinder.unbind();
        /*if (itemPresenter != null)
            itemPresenter.onDestroyItem();*/
        super.onDestroyView();
    }

    @Override
    public void onSuccess(String target, Object data) {
        switch (target) {
            case RSConstants.ITEM_COMMENTS:
                RSResponseItemData rsResponseItemData = new Gson().fromJson(new Gson().toJson(data), RSResponseItemData.class);
                try {
                    if (rsResponseItemData.getComments().size() == 0) {
                        progressBar.setVisibility(View.GONE);
                        commentsRV.setVisibility(View.GONE);
                        noOtherCommentTV.setVisibility(View.VISIBLE);
                    } else {
                        manageCommentsList(rsResponseItemData);
                    }
                } catch (Exception e) {
                }
                break;
            case RSConstants.ITEM_COMMENTS_BY_USER:
                RSResponseItemData response = new Gson().fromJson(new Gson().toJson(data), RSResponseItemData.class);
                try {
                    if (response.getComments().size() == 0) {
                        setTextNoPix();
                        noCommentTV.setVisibility(View.VISIBLE);
                        myCommentsRV.setVisibility(View.GONE);
                        mcProgressBar.setVisibility(View.GONE);
                    } else {
                        manageMyCommentsList(response);
                    }
                } catch (Exception e) {
                }
                break;
            case RSConstants.DELETE_COMMENT:
                removeComment(data);
                break;
        }
    }

    private void setTextNoPix() {
        Spannable wordtoSpan = new SpannableString(noCommentsText + " " + currentItem.getItemDetails().getTitle());
        wordtoSpan.setSpan(new ForegroundColorSpan(colorAccent), noCommentsText.length() + 1, noCommentsText.length() + 1 + currentItem.getItemDetails().getTitle().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        noCommentTV.setText(wordtoSpan);
    }

    private void removeComment(Object data) {
        Comment comment = findCommentIndex(data);
        if (comment != null) {
            myComments.remove(comment);
            myItemCommentsAdapter.removeComment(comment);
            Toast.makeText(getContext(), commentDeletedSuccessfullyMsg, Toast.LENGTH_SHORT).show();
            if (myComments.size() == 0) {
                setTextNoPix();
                noCommentTV.setVisibility(View.VISIBLE);
                myCommentsRV.setVisibility(View.GONE);
                mcProgressBar.setVisibility(View.GONE);
            }
        }
    }

    private Comment findCommentIndex(Object data) {
        for (int i = 0; i < myComments.size(); i++) {
            if (myComments.get(i).get_id().equals(data.toString()))
                return myComments.get(i);
        }
        return null;
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
                    itemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_GREEN, RSConstants.OTHER));
                    break;
                case R.id.neutral_comment:
                    itemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_ORANGE, RSConstants.OTHER));
                    break;
                case R.id.bad_comment:
                    itemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_RED, RSConstants.OTHER));
                    break;
            }
            PAGES_COUNT = rsResponseItemData.getPages();

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
                    itemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_GREEN, RSConstants.OTHER));
                    break;
                case R.id.neutral_comment:
                    itemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_ORANGE, RSConstants.OTHER));
                    break;
                case R.id.bad_comment:
                    itemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_RED, RSConstants.OTHER));
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
                    myItemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_GREEN, RSConstants.MINE));
                    break;
                case R.id.neutral_comment:
                    myItemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_ORANGE, RSConstants.MINE));
                    break;
                case R.id.bad_comment:
                    myItemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_RED, RSConstants.MINE));
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
                    myItemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_GREEN, RSConstants.MINE));
                    break;
                case R.id.neutral_comment:
                    myItemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_ORANGE, RSConstants.MINE));
                    break;
                case R.id.bad_comment:
                    myItemCommentsAdapter.addAll(getFilterOutput(rsResponseItemData.getComments(), RSConstants.PIE_RED, RSConstants.MINE));
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
    public void onError(String target) {

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

    @Override
    public void onOffLine() {
        //Toast.makeText(getContext(), offlineMsg, Toast.LENGTH_LONG).show();
        new RSCustomToast(getActivity(), getResources().getString(R.string.error), offlineMsg, R.drawable.ic_error, RSCustomToast.ERROR).show();

    }

    @Override
    public void onCancelClicked() {
    }

    @Override
    public void onConfirmClicked(String targetId) {
        itemPresenter.deleteComment(targetId, itemId, getContext());
    }
}
