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
import rankstop.steeringit.com.rankstop.data.model.Picture;
import rankstop.steeringit.com.rankstop.data.model.custom.RSRequestItemData;
import rankstop.steeringit.com.rankstop.data.model.custom.RSResponseItemData;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.ItemPixAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.utils.EndlessScrollListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.VerticalSpace;

public class ItemPicsFragment extends Fragment implements RSView.StandardView {

    private RecyclerView recyclerViewPics;
    private View rootView;
    private List<Picture> pictures;
    private RadioGroup filterToggle;
    private int lastCheckedId = R.id.all_comment;
    private ItemPixAdapter itemPicsAdapter;
    private String itemId, userId;
    private RSPresenter.ItemPresenter itemPresenter;
    private RecyclerViewClickListener listener;

    private ProgressBar progressBar;
    private int currentPage = 1;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private int PAGES_COUNT;

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
        itemId = getArguments().getString(RSConstants.ITEM_ID);
        if (RSSession.isLoggedIn(getContext())) {
            userId = RSSession.getCurrentUser(getContext()).get_id();
        }
        loadItemPix(currentPage);
        setFragmentActionListener((ContainerActivity) getActivity());
        listener = (view, position) -> {
        };
        initPixList();
        setFilterListener();
    }

    private void initPixList() {
        GridLayoutManager layoutManager = new GridLayoutManager(recyclerViewPics.getContext(), getResources().getInteger(R.integer.count_item_per_row));
        itemPicsAdapter = new ItemPixAdapter(listener, getContext());
        recyclerViewPics.setLayoutManager(layoutManager);
        recyclerViewPics.setAdapter(itemPicsAdapter);
        recyclerViewPics.addItemDecoration(new VerticalSpace(getResources().getInteger(R.integer.m_card_view), getResources().getInteger(R.integer.count_item_per_row)));
        recyclerViewPics.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadItemPix(currentPage);
                    }
                }, 1000);
                Toast.makeText(getContext(), "load more items at current page = " + currentPage, Toast.LENGTH_LONG).show();
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

    private void bindViews() {
        recyclerViewPics = rootView.findViewById(R.id.recycler_view_pics);
        filterToggle = rootView.findViewById(R.id.filter_toggle);
        progressBar = rootView.findViewById(R.id.main_progress);
        itemPresenter = new PresenterItemImpl(ItemPicsFragment.this);
    }

    private void loadItemPix(int pageNumber) {
        RSRequestItemData rsRequestItemData = new RSRequestItemData(itemId, userId, RSConstants.MAX_FIELD_TO_LOAD, pageNumber);
        itemPresenter.loadItemPix(rsRequestItemData);
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

    public static ItemPicsFragment getInstance(String itemId) {
        Bundle args = new Bundle();
        args.putString(RSConstants.ITEM_ID, itemId);
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
            if (currentPage <= PAGES_COUNT) {
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
                        break;
                    case R.id.good_comment:
                        filterToggle.setBackgroundResource(R.drawable.rs_filter_view_green);
                        itemPicsAdapter.refreshData(getFilterOutput(pictures, RSConstants.PIE_GREEN));
                        break;
                    case R.id.neutral_comment:
                        filterToggle.setBackgroundResource(R.drawable.rs_filter_view_orange);
                        itemPicsAdapter.refreshData(getFilterOutput(pictures, RSConstants.PIE_ORANGE));
                        break;
                    case R.id.bad_comment:
                        filterToggle.setBackgroundResource(R.drawable.rs_filter_view_red);
                        itemPicsAdapter.refreshData(getFilterOutput(pictures, RSConstants.PIE_RED));
                        break;
                }
            }
        });
    }
}
