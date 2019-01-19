package rankstop.steeringit.com.rankstop.ui.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rankstop.steeringit.com.rankstop.MVP.model.PresenterItemImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.db.Item;
import rankstop.steeringit.com.rankstop.data.model.db.Picture;
import rankstop.steeringit.com.rankstop.data.model.network.RSAddReview;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestItemData;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponseItemData;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.activities.DiaparomaActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.ItemPixAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.utils.EndlessScrollListener;
import rankstop.steeringit.com.rankstop.utils.HorizontalSpace;
import rankstop.steeringit.com.rankstop.utils.LinearScrollListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.VerticalSpace;

public class ItemPicsFragment extends Fragment implements RSView.StandardView {

    private View rootView;
    private RecyclerView pixRV, myPixRV;
    private List<Picture> pictures, myPictures;
    private RecyclerViewClickListener listener, myListener;
    private TextView titleOtherPix;
    private ItemPixAdapter itemPicsAdapter, myItemPixAdapter;
    private MaterialButton addPixBTN;
    private ProgressBar progressBar, mpProgressBar;
    private RelativeLayout myPixLayout;
    private LinearLayout addPixLayout;


    private String itemId, userId = "";
    private Item currentItem;
    private RSPresenter.ItemPresenter itemPresenter;
    private RadioGroup filterToggle;
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
        itemId = currentItem.getItemDetails().get_id();
        if (RSSession.isLoggedIn(getContext())) {
            myPixLayout.setVisibility(View.VISIBLE);
            userId = RSSession.getCurrentUser(getContext()).get_id();
            myPixLayout.setVisibility(View.VISIBLE);
            titleOtherPix.setVisibility(View.VISIBLE);
        }
        rsRequestItemData = new RSRequestItemData(itemId, userId, RSConstants.MAX_FIELD_TO_LOAD, 1);
        setFragmentActionListener((ContainerActivity) getActivity());
        listener = (view, position) -> {
            startActivity(
                    new Intent(getContext(), DiaparomaActivity.class)
                            .putExtra(RSConstants.PICTURES, pictures.size())
                            .putExtra(RSConstants.FILTERED_PICTURES, (Serializable) itemPicsAdapter.getAll())
                            .putExtra(RSConstants.POSITION, position)
                            .putExtra(RSConstants.COUNT_PAGES, PAGES_COUNT)
                            .putExtra(RSConstants.FILTER, lastCheckedId)
                            .putExtra(RSConstants.FROM, RSConstants.ALL_PIX)
                            .putExtra(RSConstants.RS_REQUEST_ITEM_DATA, rsRequestItemData));
        };
        myListener = (view, position) -> {
            startActivity(
                    new Intent(getContext(), DiaparomaActivity.class)
                            .putExtra(RSConstants.PICTURES, myPictures.size())
                            .putExtra(RSConstants.FILTERED_PICTURES, (Serializable) myItemPixAdapter.getAll())
                            .putExtra(RSConstants.POSITION, position)
                            .putExtra(RSConstants.COUNT_PAGES, MP_PAGES_COUNT)
                            .putExtra(RSConstants.FILTER, lastCheckedId)
                            .putExtra(RSConstants.FROM, RSConstants.MY_PIX)
                            .putExtra(RSConstants.RS_REQUEST_ITEM_DATA, rsRequestItemData));
        };
        loadItemPix(currentPage);
        loadMyItemPix(mpCurrentPage);
        initPixList();
        initMyPixList();
        setFilterListener();

        addPixBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RSAddReview rsAddReview = new RSAddReview();
                rsAddReview.setItemId(currentItem.getItemDetails().get_id());
                rsAddReview.setCategoryId(currentItem.getItemDetails().getCategory().get_id());
                fragmentActionListener.startFragment(AddReviewFragment.getInstance(rsAddReview, currentItem.getLastEvalUser(), ""), RSConstants.FRAGMENT_ADD_REVIEW);
            }
        });
    }

    private void initPixList() {
        GridLayoutManager layoutManager = new GridLayoutManager(pixRV.getContext(), getResources().getInteger(R.integer.count_item_per_row));
        itemPicsAdapter = new ItemPixAdapter(listener, getContext(), RSConstants.OTHER);
        pixRV.setLayoutManager(layoutManager);
        pixRV.setAdapter(itemPicsAdapter);
        pixRV.addItemDecoration(new VerticalSpace(getResources().getInteger(R.integer.m_card_view), getResources().getInteger(R.integer.count_item_per_row)));
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
        myItemPixAdapter = new ItemPixAdapter(myListener, getContext(), RSConstants.MINE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(myPixRV.getContext(), LinearLayoutManager.HORIZONTAL, false);
        myPixRV.setLayoutManager(layoutManager);
        myPixRV.addItemDecoration(new HorizontalSpace(getResources().getInteger(R.integer.m_card_view)));
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
        pixRV = rootView.findViewById(R.id.recycler_view_pics);
        filterToggle = rootView.findViewById(R.id.filter_toggle);
        progressBar = rootView.findViewById(R.id.main_progress);
        itemPresenter = new PresenterItemImpl(ItemPicsFragment.this);

        myPixRV = rootView.findViewById(R.id.recycler_view_my_pix);
        mpProgressBar = rootView.findViewById(R.id.mp_progress);
        myPixLayout = rootView.findViewById(R.id.rl_my_pix);
        addPixLayout = rootView.findViewById(R.id.ll_add_pix);
        titleOtherPix = rootView.findViewById(R.id.title_other_pix);
        addPixBTN = rootView.findViewById(R.id.btn_add_pix);
    }

    private void loadItemPix(int pageNumber) {
        rsRequestItemData.setPage(pageNumber);
        itemPresenter.loadItemPix(rsRequestItemData);
    }

    private void loadMyItemPix(int pageNumber) {
        rsRequestItemData.setPage(pageNumber);
        /*Toast.makeText(getContext(), "userId = " + rsRequestItemData.getUserId()
                + "\nitemId = " + rsRequestItemData.getItemId()
                + "\npage = " + rsRequestItemData.getPage()
                + "\nperPage = " + rsRequestItemData.getPerPage(), Toast.LENGTH_LONG).show();*/
        itemPresenter.loadItemPixByUser(rsRequestItemData);
    }

    private List<Picture> getFilterOutput(List<Picture> pictures, int filter) {
        List<Picture> result = new ArrayList<>();
        for (Picture picture : pictures) {
            if (filter == picture.getColor()) {
                result.add(picture);
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
        pictures.clear();
        pictures = null;
        myPictures.clear();
        myPictures = null;
        //itemPresenter.onDestroyItem();
        super.onDestroyView();
    }

    @Override
    public void onSuccess(String target, Object data) {
        switch (target) {
            case RSConstants.ITEM_PIX:
                RSResponseItemData rsResponseItemData = new Gson().fromJson(new Gson().toJson(data), RSResponseItemData.class);
                try {
                    managePicsList(rsResponseItemData);
                } catch (Exception e) {
                }
                break;
            case RSConstants.ITEM_PIX_BY_USER:
                RSResponseItemData response = new Gson().fromJson(new Gson().toJson(data), RSResponseItemData.class);
                try {
                    if (response.getPictures().size() == 0) {
                        addPixLayout.setVisibility(View.VISIBLE);
                        myPixRV.setVisibility(View.GONE);
                        mpProgressBar.setVisibility(View.GONE);
                    } else {
                        manageMyPixList(response);
                    }
                } catch (Exception e) {
                }
                break;
        }
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
                    itemPicsAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_GREEN));
                    break;
                case R.id.neutral_comment:
                    itemPicsAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_ORANGE));
                    break;
                case R.id.bad_comment:
                    itemPicsAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_RED));
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
                    itemPicsAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_GREEN));
                    break;
                case R.id.neutral_comment:
                    itemPicsAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_ORANGE));
                    break;
                case R.id.bad_comment:
                    itemPicsAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_RED));
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
                    myItemPixAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_GREEN));
                    break;
                case R.id.neutral_comment:
                    myItemPixAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_ORANGE));
                    break;
                case R.id.bad_comment:
                    myItemPixAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_RED));
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
                    myItemPixAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_GREEN));
                    break;
                case R.id.neutral_comment:
                    myItemPixAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_ORANGE));
                    break;
                case R.id.bad_comment:
                    myItemPixAdapter.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_RED));
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
                        itemPicsAdapter.refreshData(pictures);
                        myItemPixAdapter.refreshData(myPictures);
                        break;
                    case R.id.good_comment:
                        filterToggle.setBackgroundResource(R.drawable.rs_filter_view_green);
                        itemPicsAdapter.refreshData(getFilterOutput(pictures, RSConstants.PIE_GREEN));
                        myItemPixAdapter.refreshData(getFilterOutput(myPictures, RSConstants.PIE_GREEN));
                        break;
                    case R.id.neutral_comment:
                        filterToggle.setBackgroundResource(R.drawable.rs_filter_view_orange);
                        itemPicsAdapter.refreshData(getFilterOutput(pictures, RSConstants.PIE_ORANGE));
                        myItemPixAdapter.refreshData(getFilterOutput(myPictures, RSConstants.PIE_ORANGE));
                        break;
                    case R.id.bad_comment:
                        filterToggle.setBackgroundResource(R.drawable.rs_filter_view_red);
                        itemPicsAdapter.refreshData(getFilterOutput(pictures, RSConstants.PIE_RED));
                        myItemPixAdapter.refreshData(getFilterOutput(myPictures, RSConstants.PIE_RED));
                        break;
                }
            }
        });
    }
}
