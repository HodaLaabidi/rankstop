package rankstop.steeringit.com.rankstop.ui.fragments;

import android.content.Intent;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.Serializable;
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
import rankstop.steeringit.com.rankstop.customviews.RSRBMedium;
import rankstop.steeringit.com.rankstop.customviews.RSTVRegular;
import rankstop.steeringit.com.rankstop.customviews.RSTVSemiBold;
import rankstop.steeringit.com.rankstop.data.model.db.Category;
import rankstop.steeringit.com.rankstop.data.model.db.Item;
import rankstop.steeringit.com.rankstop.data.model.db.Picture;
import rankstop.steeringit.com.rankstop.data.model.network.RSAddReview;
import rankstop.steeringit.com.rankstop.data.model.network.RSNavigationData;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestItemData;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponseItemData;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.activities.DiaporamaActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.ItemPixAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.DialogConfirmationListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.ReviewCardListener;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.AlertConfirmationDialog;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.AskToLoginDialog;
import rankstop.steeringit.com.rankstop.utils.EndlessScrollListener;
import rankstop.steeringit.com.rankstop.utils.HorizontalSpace;
import rankstop.steeringit.com.rankstop.utils.LinearScrollListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;
import rankstop.steeringit.com.rankstop.utils.VerticalSpace;

public class ItemPicsFragment extends Fragment implements RSView.StandardView, DialogConfirmationListener {

    private View rootView;
    private Unbinder unbinder;

    @BindView(R.id.recycler_view_pics)
    RecyclerView pixRV;
    @BindView(R.id.recycler_view_my_pix)
    RecyclerView myPixRV;
    @BindView(R.id.title_other_pix)
    RSTVSemiBold titleOtherPix;
    @BindView(R.id.main_progress)
    ProgressBar progressBar;
    @BindView(R.id.mp_progress)
    ProgressBar mpProgressBar;
    @BindView(R.id.rl_my_pix)
    RelativeLayout myPixLayout;
    @BindView(R.id.tv_no_pix)
    RSTVRegular noPixTV;
    @BindView(R.id.tv_no_other_pix)
    RSTVRegular noOtherPixTV;
    @BindView(R.id.filter_toggle)
    RadioGroup filterToggle;

    @BindView(R.id.btn_add_pix2)
    RSBTNMedium addPixBTN;

    @BindString(R.string.off_line)
    String offlineMsg;
    @BindString(R.string.no_pix_by_filter)
    String noPixByFilter;

    @OnClick({R.id.btn_add_pix2, R.id.btn_add_pix})
    void addComment() {
        if (RSNetwork.isConnected(getContext())) {
            if (RSSession.isLoggedIn()) {
                RSAddReview rsAddReview = new RSAddReview();
                rsAddReview.setItemId(currentItem.getItemDetails().get_id());
                rsAddReview.setCategoryId(currentCategory.get_id());
                fragmentActionListener.startFragment(AddReviewFragment.getInstance(rsAddReview, currentItem.getLastEvalUser(), "", RSConstants.ACTION_PIX), RSConstants.FRAGMENT_ADD_REVIEW);
            } else {
                RSNavigationData rsNavigationData = new RSNavigationData(RSConstants.FRAGMENT_ADD_REVIEW, RSConstants.ACTION_ADD_REVIEW, alertLoginToAddPixMsg, itemId, "", currentCategory.get_id(), RSConstants.ACTION_PIX);
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

    @BindInt(R.integer.m_card_view)
    int marginCardView;
    @BindInt(R.integer.count_item_per_row)
    int countItemPerRow;
    @BindColor(R.color.colorAccent)
    int colorAccent;

    @BindString(R.string.message_delete_picture)
    String deletePicMsg;
    @BindString(R.string.alert_login_to_add_pix)
    String alertLoginToAddPixMsg;
    @BindString(R.string.no_pix_part1)
    String noPixPart1;
    @BindString(R.string.no_pix_part2)
    String noPixPart2;
    @BindString(R.string.picture_deleted_successfully)
    String pictureDeletedSuccessfully;


    private ReviewCardListener listener, myListener;
    private ItemPixAdapter itemPicsAdapter, myItemPixAdapter;
    private List<Picture> pictures, myPictures;
    private Category currentCategory;

    private String itemId, userId = "";
    private Item currentItem;
    private RSPresenter.ItemPresenter itemPresenter;
    private int lastCheckedId = R.id.all_comment;
    private RSRequestItemData rsRequestItemData;

    private int currentPage = 1;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private int PAGES_COUNT;

    // *mp: my pictures
    private int mpCurrentPage = 1;
    private boolean mpIsLastPage = false;
    private boolean mpIsLoading = false;
    private int MP_PAGES_COUNT = 1;

    public ItemPicsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_item_pics, container, false);
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
        if (pictures == null)
            pictures = new ArrayList<>();
        if (myPictures == null)
            myPictures = new ArrayList<>();
        currentItem = (Item) getArguments().getSerializable(RSConstants.ITEM);
        currentCategory = new Gson().fromJson(new Gson().toJson(currentItem.getItemDetails().getCategory()), Category.class);
        itemId = currentItem.getItemDetails().get_id();
        if (RSSession.isLoggedIn()) {
            myPixLayout.setVisibility(View.VISIBLE);
            userId = RSSession.getCurrentUser().get_id();
            myPixLayout.setVisibility(View.VISIBLE);
            titleOtherPix.setVisibility(View.VISIBLE);
        } else {
            addPixBTN.setVisibility(View.VISIBLE);
        }
        rsRequestItemData = new RSRequestItemData(itemId, userId, RSConstants.MAX_FIELD_TO_LOAD, 1);
        setFragmentActionListener((ContainerActivity) getActivity());
        listener = new ReviewCardListener() {
            @Override
            public void onRemoveClicked(int position) {

            }

            @Override
            public void onClick(View view, int position) {
                startActivity(
                        new Intent(getContext(), DiaporamaActivity.class)
                                .putExtra(RSConstants.PICTURES, pictures.size())
                                .putExtra(RSConstants.FILTERED_PICTURES, (Serializable) itemPicsAdapter.getAll())
                                .putExtra(RSConstants.POSITION, position)
                                .putExtra(RSConstants.COUNT_PAGES, PAGES_COUNT)
                                .putExtra(RSConstants.FILTER, lastCheckedId)
                                .putExtra(RSConstants.FROM, RSConstants.ALL_PIX)
                                .putExtra(RSConstants.RS_REQUEST_ITEM_DATA, rsRequestItemData));
            }
        };
        myListener = new ReviewCardListener() {
            @Override
            public void onRemoveClicked(int position) {
                openDialogConfirmation(myPictures.get(position));
            }

            @Override
            public void onClick(View view, int position) {
                startActivity(
                        new Intent(getContext(), DiaporamaActivity.class)
                                .putExtra(RSConstants.PICTURES, myPictures.size())
                                .putExtra(RSConstants.FILTERED_PICTURES, (Serializable) myItemPixAdapter.getAll())
                                .putExtra(RSConstants.POSITION, position)
                                .putExtra(RSConstants.COUNT_PAGES, MP_PAGES_COUNT)
                                .putExtra(RSConstants.FILTER, lastCheckedId)
                                .putExtra(RSConstants.FROM, RSConstants.MY_PIX)
                                .putExtra(RSConstants.RS_REQUEST_ITEM_DATA, rsRequestItemData));
            }
        };
        if (RSNetwork.isConnected(getContext())) {
            progressBar.setVisibility(View.VISIBLE);
            mpProgressBar.setVisibility(View.VISIBLE);
            loadItemPix(currentPage);
            loadMyItemPix(mpCurrentPage);
        } else {
            onOffLine();
        }
        initPixList();
        initMyPixList();
        setFilterListener();
    }

    private void openDialogConfirmation(Picture picture) {
        Bundle bundle = new Bundle();
        bundle.putString(RSConstants.MESSAGE, deletePicMsg);
        bundle.putString(RSConstants._ID, picture.get_id());
        AlertConfirmationDialog dialog = new AlertConfirmationDialog();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.setCancelable(false);
        dialog.setArguments(bundle);
        dialog.setTargetFragment(this, 0);
        dialog.show(ft, AlertConfirmationDialog.TAG);
    }

    private void initPixList() {
        GridLayoutManager layoutManager = new GridLayoutManager(pixRV.getContext(), countItemPerRow);
        itemPicsAdapter = new ItemPixAdapter(listener, RSConstants.OTHER);
        pixRV.setLayoutManager(layoutManager);
        pixRV.setAdapter(itemPicsAdapter);
        pixRV.addItemDecoration(new VerticalSpace(marginCardView, countItemPerRow));
        pixRV.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                loadItemPix(currentPage);
                //Toast.makeText(getContext(), "load more items at current page = " + currentPage, Toast.LENGTH_LONG).show();
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

    private void initMyPixList() {
        myItemPixAdapter = new ItemPixAdapter(myListener, RSConstants.MINE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(myPixRV.getContext(), LinearLayoutManager.HORIZONTAL, false);
        myPixRV.setLayoutManager(layoutManager);
        myPixRV.addItemDecoration(new HorizontalSpace(marginCardView));
        myPixRV.setAdapter(myItemPixAdapter);
        myPixRV.addOnScrollListener(new LinearScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                mpIsLoading = true;
                mpCurrentPage += 1;

                loadMyItemPix(mpCurrentPage);
            }

            @Override
            public int getTotalPageCount() {
                return MP_PAGES_COUNT;
            }

            @Override
            public boolean isLastPage() {
                return mpIsLastPage;
            }

            @Override
            public boolean isLoading() {
                return mpIsLoading;
            }
        });
    }

    private void bindViews() {
        itemPresenter = new PresenterItemImpl(ItemPicsFragment.this);
    }

    private void loadItemPix(int pageNumber) {
        rsRequestItemData.setPage(pageNumber);
        itemPresenter.loadItemPix(rsRequestItemData, getContext());
    }

    private void loadMyItemPix(int pageNumber) {
        rsRequestItemData.setPage(pageNumber);
        /*Toast.makeText(getContext(), "userId = " + rsRequestItemData.getUserId()
                + "\nitemId = " + rsRequestItemData.getItemId()
                + "\npage = " + rsRequestItemData.getPage()
                + "\nperPage = " + rsRequestItemData.getPerPage(), Toast.LENGTH_LONG).show();*/
        itemPresenter.loadItemPixByUser(rsRequestItemData, getContext());
    }

    private List<Picture> getFilterOutput(List<Picture> pictures, int filter, String target) {
        List<Picture> result = new ArrayList<>();
        for (Picture picture : pictures) {
            if (filter == picture.getColor()) {
                result.add(picture);
            }
        }
        if (result.size() == 0) {
            if (target.equals(RSConstants.MINE)) {
                noPixTV.setText(noPixByFilter);
                noPixTV.setVisibility(View.VISIBLE);
            } else if (target.equals(RSConstants.OTHER)) {
                noOtherPixTV.setText(noPixByFilter);
                noOtherPixTV.setVisibility(View.VISIBLE);
            }
        } else {
            if (target.equals(RSConstants.MINE)) {
                noPixTV.setVisibility(View.GONE);
            } else if (target.equals(RSConstants.OTHER)) {
                noOtherPixTV.setVisibility(View.GONE);
            }
        }
        return result;
    }

    private FragmentActionListener fragmentActionListener;

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    private static ItemPicsFragment instance;

    public static ItemPicsFragment getInstance(Item item) {
        Bundle args = new Bundle();
        args.putSerializable(RSConstants.ITEM, item);
        if (instance == null) {
            instance = new ItemPicsFragment();
        }
        instance.setArguments(args);
        return instance;
    }


    @Override
    public void onDestroyView() {
        instance = null;
        rootView = null;
        fragmentActionListener = null;
        if (pictures != null)
            pictures.clear();
        pictures = null;
        if (myPictures != null)
            myPictures.clear();
        myPictures = null;
        if (unbinder != null)
            unbinder.unbind();
        /*if (itemPresenter != null)
            itemPresenter.onDestroyItem();*/
        super.onDestroyView();
    }

    @Override
    public void onSuccess(String target, Object data) {
        switch (target) {
            case RSConstants.ITEM_PIX:
                RSResponseItemData rsResponseItemData = new Gson().fromJson(new Gson().toJson(data), RSResponseItemData.class);
                try {
                    if (rsResponseItemData.getPictures().size() == 0) {
                        progressBar.setVisibility(View.GONE);
                        pixRV.setVisibility(View.GONE);
                        noOtherPixTV.setVisibility(View.VISIBLE);
                    } else {
                        managePicsList(rsResponseItemData);
                    }
                } catch (Exception e) {

                }
                break;
            case RSConstants.ITEM_PIX_BY_USER:
                RSResponseItemData response = new Gson().fromJson(new Gson().toJson(data), RSResponseItemData.class);
                try {
                    if (response.getPictures().size() == 0) {
                        setTextNoPix();
                        noPixTV.setVisibility(View.VISIBLE);
                        myPixRV.setVisibility(View.GONE);
                        mpProgressBar.setVisibility(View.GONE);
                    } else {
                        manageMyPixList(response);
                    }
                } catch (Exception e) {
                }
                break;
            case RSConstants.DELETE_PICTURE:
                removePicture(data);
                break;
        }
    }

    private void setTextNoPix() {
        Spannable wordtoSpan = new SpannableString(noPixPart1 + " " + currentItem.getItemDetails().getTitle() + " " + noPixPart2);
        wordtoSpan.setSpan(new ForegroundColorSpan(colorAccent), noPixPart1.length() + 1, noPixPart1.length() + 1 + currentItem.getItemDetails().getTitle().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        noPixTV.setText(wordtoSpan);
    }

    private void removePicture(Object data) {
        Picture picture = findCommentIndex(data);
        if (picture != null) {
            myPictures.remove(picture);
            myItemPixAdapter.removePicture(picture);
            Toast.makeText(getContext(), pictureDeletedSuccessfully, Toast.LENGTH_SHORT).show();
            if (myPictures.size() == 0) {
                setTextNoPix();
                noPixTV.setVisibility(View.VISIBLE);
                myPixRV.setVisibility(View.GONE);
                mpProgressBar.setVisibility(View.GONE);
            }
        }
    }

    private Picture findCommentIndex(Object data) {
        for (int i = 0; i < myPictures.size(); i++) {
            if (myPictures.get(i).get_id().equals(data.toString()))
                return myPictures.get(i);
        }
        return null;
    }

    private void managePicsList(RSResponseItemData rsResponseItemData) {
        pictures.addAll(rsResponseItemData.getPictures());
        if (rsResponseItemData.getCurrent() == 1) {
            progressBar.setVisibility(View.GONE);
            switch (lastCheckedId) {
                case R.id.all_comment:
                    itemPicsAdapter.addAll(rsResponseItemData.getPictures());
                    break;
                case R.id.good_comment:
                    itemPicsAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_GREEN, RSConstants.OTHER));
                    break;
                case R.id.neutral_comment:
                    itemPicsAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_ORANGE, RSConstants.OTHER));
                    break;
                case R.id.bad_comment:
                    itemPicsAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_RED, RSConstants.OTHER));
                    break;
            }
            PAGES_COUNT = rsResponseItemData.getPages();

            Log.i("TAG_PIX", "current page from pix == " + currentPage);
            Log.i("TAG_PIX", "page count from pix == " + PAGES_COUNT);
            if (currentPage < PAGES_COUNT) {
                itemPicsAdapter.addLoadingFooter();
            } else {
                isLastPage = true;
            }
        } else if (rsResponseItemData.getCurrent() > 1) {
            itemPicsAdapter.removeLoadingFooter();
            isLoading = false;
            switch (lastCheckedId) {
                case R.id.all_comment:
                    itemPicsAdapter.addAll(rsResponseItemData.getPictures());
                    break;
                case R.id.good_comment:
                    itemPicsAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_GREEN, RSConstants.OTHER));
                    break;
                case R.id.neutral_comment:
                    itemPicsAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_ORANGE, RSConstants.OTHER));
                    break;
                case R.id.bad_comment:
                    itemPicsAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_RED, RSConstants.OTHER));
                    break;
            }
            if (currentPage != PAGES_COUNT) {
                itemPicsAdapter.addLoadingFooter();
            } else {
                isLastPage = true;
            }
        }
    }

    private void manageMyPixList(RSResponseItemData rsResponseItemData) {
        myPictures.addAll(rsResponseItemData.getPictures());

        if (rsResponseItemData.getCurrent() == 1) {
            mpProgressBar.setVisibility(View.GONE);
            switch (lastCheckedId) {
                case R.id.all_comment:
                    myItemPixAdapter.addAll(rsResponseItemData.getPictures());
                    break;
                case R.id.good_comment:
                    myItemPixAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_GREEN, RSConstants.MINE));
                    break;
                case R.id.neutral_comment:
                    myItemPixAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_ORANGE, RSConstants.MINE));
                    break;
                case R.id.bad_comment:
                    myItemPixAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_RED, RSConstants.MINE));
                    break;
            }
            MP_PAGES_COUNT = rsResponseItemData.getPages();

            if (mpCurrentPage < MP_PAGES_COUNT) {
                myItemPixAdapter.addLoadingFooter();
            } else {
                mpIsLastPage = true;
            }
        } else if (rsResponseItemData.getCurrent() > 1) {
            myItemPixAdapter.removeLoadingFooter();
            mpIsLoading = false;
            switch (lastCheckedId) {
                case R.id.all_comment:
                    myItemPixAdapter.addAll(rsResponseItemData.getPictures());
                    break;
                case R.id.good_comment:
                    myItemPixAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_GREEN, RSConstants.MINE));
                    break;
                case R.id.neutral_comment:
                    myItemPixAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_ORANGE, RSConstants.MINE));
                    break;
                case R.id.bad_comment:
                    myItemPixAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_RED, RSConstants.MINE));
                    break;
            }
            if (mpCurrentPage != MP_PAGES_COUNT) myItemPixAdapter.addLoadingFooter();
            else mpIsLastPage = true;
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

    }

    @Override
    public void onOffLine() {
        Toast.makeText(getContext(), offlineMsg, Toast.LENGTH_LONG).show();
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
                    if (pictures.size() > 0) {
                        noOtherPixTV.setVisibility(View.GONE);
                        itemPicsAdapter.refreshData(pictures);
                    }
                    if (myPictures.size() > 0) {
                        noPixTV.setVisibility(View.GONE);
                        myItemPixAdapter.refreshData(myPictures);
                    }
                    break;
                case R.id.good_comment:
                    filterToggle.setBackgroundResource(R.drawable.rs_filter_view_green);
                    if (pictures.size() > 0)
                        itemPicsAdapter.refreshData(getFilterOutput(pictures, RSConstants.PIE_GREEN, RSConstants.OTHER));
                    if (myPictures.size() > 0)
                        myItemPixAdapter.refreshData(getFilterOutput(myPictures, RSConstants.PIE_GREEN, RSConstants.MINE));
                    break;
                case R.id.neutral_comment:
                    filterToggle.setBackgroundResource(R.drawable.rs_filter_view_orange);
                    if (pictures.size() > 0)
                        itemPicsAdapter.refreshData(getFilterOutput(pictures, RSConstants.PIE_ORANGE, RSConstants.OTHER));
                    if (myPictures.size() > 0)
                        myItemPixAdapter.refreshData(getFilterOutput(myPictures, RSConstants.PIE_ORANGE, RSConstants.MINE));
                    break;
                case R.id.bad_comment:
                    filterToggle.setBackgroundResource(R.drawable.rs_filter_view_red);
                    if (pictures.size() > 0)
                        itemPicsAdapter.refreshData(getFilterOutput(pictures, RSConstants.PIE_RED, RSConstants.OTHER));
                    if (myPictures.size() > 0)
                        myItemPixAdapter.refreshData(getFilterOutput(myPictures, RSConstants.PIE_RED, RSConstants.MINE));
                    break;
            }
        });
    }

    @Override
    public void onCancelClicked() {

    }

    @Override
    public void onConfirmClicked(String targetId) {
        itemPresenter.deletePicture(targetId, itemId, getContext());
    }
}
