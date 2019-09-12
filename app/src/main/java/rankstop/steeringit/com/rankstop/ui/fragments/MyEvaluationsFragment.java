package com.steeringit.rankstop.ui.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import com.google.android.material.button.MaterialButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.steeringit.rankstop.MVP.model.PresenterItemImpl;
import com.steeringit.rankstop.MVP.presenter.RSPresenter;
import com.steeringit.rankstop.MVP.view.RSView;
import com.steeringit.rankstop.R;
import com.steeringit.rankstop.RankStop;
import com.steeringit.rankstop.customviews.RSCustomToast;
import com.steeringit.rankstop.customviews.RSTVMedium;
import com.steeringit.rankstop.data.model.db.Item;
import com.steeringit.rankstop.data.model.db.User;
import com.steeringit.rankstop.data.model.network.RSFollow;
import com.steeringit.rankstop.data.model.network.RSNavigationData;
import com.steeringit.rankstop.data.model.network.RSRequestListItem;
import com.steeringit.rankstop.data.model.network.RSResponseListingItem;
import com.steeringit.rankstop.session.RSSession;
import com.steeringit.rankstop.ui.activities.ContainerActivity;
import com.steeringit.rankstop.ui.adapter.MyEvalsAdapter;
import com.steeringit.rankstop.ui.callbacks.FragmentActionListener;
import com.steeringit.rankstop.ui.callbacks.ItemPieListener;
import com.steeringit.rankstop.ui.dialogFragment.ContactDialog;
import com.steeringit.rankstop.utils.EndlessScrollListener;
import com.steeringit.rankstop.utils.RSConstants;
import com.steeringit.rankstop.utils.RSNetwork;
import com.steeringit.rankstop.utils.VerticalSpace;

public class MyEvaluationsFragment extends Fragment implements RSView.StandardView , RSView.StandardView2 {

    private View rootView;

    private String itemIdToFollow ;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recycler_view_my_evals)
    RecyclerView recyclerViewMyEvals;

    @BindView(R.id.no_item)
    LinearLayout layoutNoItem;

    @BindView(R.id.tv_login_or_search)
    RSTVMedium nodataTV;

    @BindView(R.id.btn_login_or_search)
    MaterialButton noDataBtn;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindString(R.string.off_line)
    String offlineMsg;

    @BindView(R.id.shimmer_recycler_view)
    ShimmerRecyclerView shimmerRecyclerView ;

    @OnClick(R.id.btn_login_or_search)
    void fun() {
        if (!isLoggedIn) {
            RSNavigationData rsNavigationData = new RSNavigationData(RSConstants.FRAGMENT_MY_EVALS, RSConstants.ACTION_CONNECT, "", "", "", "", "");
            navigateToSignUp(rsNavigationData);
        } else {
            navigateToSearch();
        }
    }

    // panigation variables
    private int currentPage = 1;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private int PAGES_COUNT = 1;

    private MyEvalsAdapter myEvalsAdapter;

    private boolean isLoggedIn;

    private Unbinder unbinder;

    private RSPresenter.ItemPresenter itemPresenter;
    private RSRequestListItem rsRequestListItem = new RSRequestListItem();
    private List<Item> listMyEvals = new ArrayList<>();
    private User user;
    private WeakReference<MyEvaluationsFragment> fragmentContext;
    RSNavigationData rsNavigationData = new RSNavigationData();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentContext = new WeakReference<>(this);
        rootView = inflater.inflate(R.layout.fragment_my_evaluations, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bindViews();

        isLoggedIn = RSSession.isLoggedIn();
        if (isLoggedIn) {
            itemPresenter = new PresenterItemImpl(MyEvaluationsFragment.this , MyEvaluationsFragment.this);
            initMyEvals();
            user = RSSession.getCurrentUser();
            rsRequestListItem.setPage(currentPage);
            rsRequestListItem.setPerPage(RSConstants.MAX_FIELD_TO_LOAD);
            rsRequestListItem.setUserId(user.get_id());
            rsRequestListItem.setLang(RankStop.getDeviceLanguage());
            loadMyEvals(rsRequestListItem);
        } else {
            noDataBtn.setText(getResources().getString(R.string.login_btn));
            nodataTV.setText(getResources().getString(R.string.login_show_eval));
            layoutNoItem.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            shimmerRecyclerView.setVisibility(View.GONE);
        }
    }

    private void loadMyEvals(RSRequestListItem rsRequestListItem) {
        itemPresenter.loadMyEvals(rsRequestListItem, getContext());
    }

    private void initMyEvals() {
        ItemPieListener listener = new ItemPieListener() {
            @Override
            public void onFollowChanged(int position) {
                manageFollow(listMyEvals.get(position).getItemDetails().get_id(), !listMyEvals.get(position).isFollow());
            }

            @Override
            public void onClick(View view, int position) {
                if (RSNetwork.isConnected(getContext()))
                    fragmentActionListener.startFragment(ItemDetailsFragment.getInstance(listMyEvals.get(position).getItemDetails().get_id()), RSConstants.FRAGMENT_ITEM_DETAILS);
                else
                    onOffLine();
            }
        };
        GridLayoutManager layoutManager = new GridLayoutManager(recyclerViewMyEvals.getContext(), 1);
        recyclerViewMyEvals.setLayoutManager(layoutManager);

        myEvalsAdapter = new MyEvalsAdapter(listener, getContext());
        recyclerViewMyEvals.setLayoutManager(layoutManager);
        recyclerViewMyEvals.setAdapter(myEvalsAdapter);
        recyclerViewMyEvals.addItemDecoration(new VerticalSpace(getResources().getInteger(R.integer.m_card_view), 1));
        recyclerViewMyEvals.setNestedScrollingEnabled(false);
        recyclerViewMyEvals.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                rsRequestListItem.setPage(currentPage);
                // mocking network delay for API call
                loadMyEvals(rsRequestListItem);
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

    private void manageFollow(String itemId, boolean isFollow) {
        itemIdToFollow = itemId;
        RSFollow rsFollow = new RSFollow(user.get_id(), itemId);
        if (isFollow)
            itemPresenter.followItem(rsFollow, getContext());
        else
            itemPresenter.unfollowItem(rsFollow, getContext());

    }

    private void bindViews() {
        toolbar.setTitle(getResources().getString(R.string.title_my_evals));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setFragmentActionListener((ContainerActivity) getActivity());
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.rs_menu, menu);

            MenuItem item = menu.findItem(R.id.logout);
            item.setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.setting:
                fragmentActionListener.startFragment(SettingsFragment.getInstance(), RSConstants.FRAGMENT_SETTINGS);
                break;
            case R.id.logout:
                RSSession.cancelSession();
                ((ContainerActivity) getActivity()).manageSession(false, new RSNavigationData(RSConstants.FRAGMENT_SIGN_UP, ""));
                break;
            case R.id.history:
                fragmentActionListener.startFragment(HistoryFragment.getInstance(), RSConstants.FRAGMENT_HISTORY);
                break;
            case R.id.contact:
                openContactDialog();
                break;
            case R.id.notifications:
                fragmentActionListener.startFragment(ListNotifFragment.getInstance(), RSConstants.FRAGMENT_NOTIF);
                break;
            case R.id.profil:
                if(RSSession.isLoggedIn())
                    fragmentActionListener.startFragment(ProfileFragment.getInstance(), RSConstants.FRAGMENT_PROFILE);
                else
                    fragmentActionListener.startFragment(SignupFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_SIGN_UP);
        }

        return super.onOptionsItemSelected(item);
    }

    private FragmentActionListener fragmentActionListener;

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    private void openContactDialog() {
        ContactDialog dialog = new ContactDialog();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.show(ft, ContactDialog.TAG);
    }

    private static MyEvaluationsFragment instance;

    public static MyEvaluationsFragment getInstance() {
        if (instance == null) {
            instance = new MyEvaluationsFragment();
        }
        return instance;
    }

    @Override
    public void onDestroyView() {

        currentPage = 1;
        isLastPage = false;
        isLoading = false;
        PAGES_COUNT = 1;


        instance = null;
        if (unbinder != null)
            unbinder.unbind();
        rootView = null;
        if (itemPresenter != null)
            itemPresenter.onDestroyItem(getContext());
        fragmentActionListener = null;
        super.onDestroyView();
    }

    @Override
    public void onSuccess(String target, Object data) {
        switch (target) {
            case RSConstants.MY_EVALS:
                try {
                    RSResponseListingItem listingItemResponse = new Gson().fromJson(new Gson().toJson(data), RSResponseListingItem.class);

                    listMyEvals.addAll(listingItemResponse.getItems());
                    if (listingItemResponse.getCurrent() == 1) {
                        myEvalsAdapter.clear();
                        PAGES_COUNT = listingItemResponse.getPages();
                        if (listingItemResponse.getItems().size() == 0) {
                            noDataBtn.setText(getResources().getString(R.string.search_items));
                            nodataTV.setText(getResources().getString(R.string.no_item_evaluated));
                            layoutNoItem.setVisibility(View.VISIBLE);
                        } else {
                            recyclerViewMyEvals.setVisibility(View.VISIBLE);
                        }
                    } else if (listingItemResponse.getCurrent() > 1) {
                        myEvalsAdapter.removeLoadingFooter();
                        isLoading = false;
                    }
                    myEvalsAdapter.addAll(listingItemResponse.getItems());
                    if (currentPage < PAGES_COUNT) {
                        myEvalsAdapter.addLoadingFooter();
                        isLastPage = false;
                    } else {
                        isLastPage = true;
                    }
                } catch (Exception e) {
                    //Toast.makeText(getContext(), "exception", Toast.LENGTH_SHORT).show();
                    myEvalsAdapter.removeLoadingFooter();
                }
                break;
            case RSConstants.FOLLOW_ITEM:
                if (data.equals("1")) {
                    Toast.makeText(getContext(), getResources().getString(R.string.follow), Toast.LENGTH_SHORT).show();
                    changeIconFollow(itemIdToFollow, true);
                } else if (data.equals("0")) {
                    Toast.makeText(getContext(), getResources().getString(R.string.already_followed), Toast.LENGTH_SHORT).show();
                }
                break;
            case RSConstants.UNFOLLOW_ITEM:
                Toast.makeText(getContext(), getResources().getString(R.string.unfollow), Toast.LENGTH_SHORT).show();
                changeIconFollow(itemIdToFollow, false);
                break;
        }
    }
    private int findItemIndex(List<Item> itemList, String itemId) {
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getItemDetails().get_id().equals(itemId))
                return i;
        }
        return -1;
    }

    private void changeIconFollow(String itemId, boolean follow) {
        if (itemId != null){

            int indexIntoList = findItemIndex(listMyEvals, itemId);
            if (indexIntoList != -1) {
                listMyEvals.get(indexIntoList).setFollow(follow);
                //itemPresenter.refreshItems(this.getContext(),user.get_id(), itemId, "icon", RankStop.getDeviceLanguage());

            }
        }

    }

    @Override
    public void onFailure(String target) {
        switch (target) {
            case RSConstants.MY_EVALS:
                //Toast.makeText(getContext(), "failure", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onError(String target) {
        switch (target) {
            case RSConstants.MY_EVALS:
                //Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void showProgressBar(String target) {
        // shimmer recycler view must be shown only for first page otherwise progressbar will be shown
        if (currentPage == 1){
            shimmerRecyclerView.setVisibility(View.VISIBLE);
            shimmerRecyclerView.showShimmerAdapter();

        } else {
            progressBar.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void hideProgressBar(String target) {

        if (currentPage == 1){
            shimmerRecyclerView.hideShimmerAdapter();

        } else {
            progressBar.setVisibility(View.GONE);
        }

    }

    @Override
    public void showMessage(String target, String message) {

    }

    @Override
    public void onOffLine() {
       // Toast.makeText(getContext(), offlineMsg, Toast.LENGTH_LONG).show();
        new RSCustomToast(getActivity(), getResources().getString(R.string.error), offlineMsg, R.drawable.ic_error, RSCustomToast.ERROR).show();

    }

    private void navigateToSignUp(RSNavigationData rsNavigationData) {
        fragmentActionListener.startFragment(SignupFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_SIGN_UP);
    }

    private void navigateToSearch() {
        fragmentActionListener.navigateTo(R.id.navigation_search, RSConstants.FRAGMENT_SEARCH);
    }

    @Override
    public void onSuccessRefreshItem(String target, String itemId, String message, Object data) {
        Item item = null;
        if (!(data instanceof String)) {
            item = new Gson().fromJson(new Gson().toJson(data), Item.class);

            for (int i = 0; i < listMyEvals.size(); i++) {
                if (listMyEvals.get(i).getItemDetails().get_id().equalsIgnoreCase(itemId)) {

                    myEvalsAdapter.refreshOneItem(i, item, message);
                    break;


                }

            }

        }

    }
}
