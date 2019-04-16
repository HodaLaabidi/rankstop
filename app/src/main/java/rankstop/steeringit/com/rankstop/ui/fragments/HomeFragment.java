package rankstop.steeringit.com.rankstop.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterItemImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.RankStop;
import rankstop.steeringit.com.rankstop.customviews.RSBTNBold;
import rankstop.steeringit.com.rankstop.data.model.db.FakeUser;
import rankstop.steeringit.com.rankstop.data.model.db.User;
import rankstop.steeringit.com.rankstop.data.model.network.RSFollow;
import rankstop.steeringit.com.rankstop.data.model.network.RSNavigationData;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestListItem;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponse;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponseListingItem;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponseLogin;
import rankstop.steeringit.com.rankstop.data.webservices.WebService;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.session.RSSessionToken;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.PieAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.ItemPieListener;
import rankstop.steeringit.com.rankstop.data.model.db.Item;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.AskToLoginDialog;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.ContactDialog;
import rankstop.steeringit.com.rankstop.utils.HorizontalSpace;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment implements RSView.StandardView {

    private static HomeFragment instance;

    @BindView(R.id.recycler_view_top_ranked)
    RecyclerView recyclerViewTopRanked;

    @BindView(R.id.recycler_view_top_viewed)
    RecyclerView recyclerViewTopViewed;

    @BindView(R.id.recycler_view_top_followed)
    RecyclerView recyclerViewTopFollowed;

    @BindView(R.id.recycler_view_top_commented)
    RecyclerView recyclerViewTopCommented;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progress_bar_top_ranked)
    ProgressBar progressBarTopRanked;

    @BindView(R.id.progress_bar_top_viewed)
    ProgressBar progressBarTopViewed;

    @BindView(R.id.progress_bar_top_followed)
    ProgressBar progressBarTopFollowed;

    @BindView(R.id.progress_bar_top_commented)
    ProgressBar progressBarTopCommented;

    @BindView(R.id.layout_view_top_ranked)
    LinearLayout layoutTopRanked;

    @BindView(R.id.layout_view_top_commented)
    LinearLayout layoutTopCommented;

    @BindView(R.id.layout_view_top_viewed)
    LinearLayout layoutTopViewed;

    @BindView(R.id.layout_view_top_followed)
    LinearLayout layoutTopFollowed;

    @BindView(R.id.more_page_top_ranked)
    RSBTNBold moreTopRankedBtn;

    @OnClick(R.id.more_page_top_ranked)
    void moreRanked() {
        if (RSNetwork.isConnected()) {
            rsNavigationData.setFrom(RSConstants.FRAGMENT_HOME);
            rsNavigationData.setSection(RSConstants.TOP_RANKED_ITEMS);
            fragmentActionListener.startFragment(ListingItemsFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_LISTING_ITEMS);
        } else {
            onOffLine();
        }
    }

    @BindView(R.id.more_page_top_viewed)
    RSBTNBold moreTopViewedBtn;

    @OnClick(R.id.more_page_top_viewed)
    void moreViewed() {
        if (RSNetwork.isConnected()) {
            rsNavigationData.setFrom(RSConstants.FRAGMENT_HOME);
            rsNavigationData.setSection(RSConstants.TOP_VIEWED_ITEMS);
            fragmentActionListener.startFragment(ListingItemsFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_LISTING_ITEMS);

        } else {
            onOffLine();
        }
    }

    @BindView(R.id.more_page_top_commented)
    RSBTNBold moreTopCommentedBtn;

    @OnClick(R.id.more_page_top_commented)
    void moreCommented() {
        if (RSNetwork.isConnected()) {
            rsNavigationData.setFrom(RSConstants.FRAGMENT_HOME);
            rsNavigationData.setSection(RSConstants.TOP_COMMENTED_ITEMS);
            fragmentActionListener.startFragment(ListingItemsFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_LISTING_ITEMS);
        } else {
            onOffLine();
        }
    }

    @BindView(R.id.more_page_top_followed)
    RSBTNBold moreTopFollowedBtn;

    @OnClick(R.id.more_page_top_followed)
    void moreFollowed() {
        if (RSNetwork.isConnected()) {
            rsNavigationData.setFrom(RSConstants.FRAGMENT_HOME);
            rsNavigationData.setSection(RSConstants.TOP_FOLLOWED_ITEMS);
            fragmentActionListener.startFragment(ListingItemsFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_LISTING_ITEMS);
        } else {
            onOffLine();
        }
    }


    @OnClick(R.id.btn_search)
    void search() {
        if (RSNetwork.isConnected())
            fragmentActionListener.navigateTo(R.id.navigation_search, RSConstants.FRAGMENT_SEARCH);
        else
            onOffLine();
    }

    @BindString(R.string.app_name)
    String homeTitle;
    @BindString(R.string.alert_login_to_follow)
    String alertLoginToFollowMsg;
    @BindString(R.string.unfollow)
    String unfollowMsg;
    @BindString(R.string.follow)
    String followMsg;
    @BindString(R.string.already_followed)
    String alreadyFollowedMsg;
    @BindString(R.string.off_line)
    String offlineMsg;

    @BindInt(R.integer.m_card_view)
    int marginCardView;

    private Unbinder unbinder;
    private View rootView;

    private List<Item> listTopRankedItem, listTopViewedItem, listTopFollowedItem, listTopCommentedItem;
    private FragmentActionListener fragmentActionListener;
    private PieAdapter topRankedAdapter, topViewedAdapter, topCommentedAdapter, topFollowedAdapter;
    private RSPresenter.ItemPresenter itemPresenter;
    private RSRequestListItem rsRequestListItem = new RSRequestListItem();
    private User user;
    private Call<RSResponse> callLogin;
    private String itemIdToFollow;
    private RSNavigationData rsNavigationData = new RSNavigationData();
    private WeakReference<HomeFragment> fragmentContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentContext = new WeakReference<>(this);
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindViews();

        itemPresenter = new PresenterItemImpl(HomeFragment.this);
        rsRequestListItem.setLang(RankStop.getDeviceLanguage());
        getCurrentUser();

        if (RSNetwork.isConnected()) {
            if (RSSessionToken.getLocalStorage() == null) {
                FakeUser user = new FakeUser();
                callLogin = WebService.getInstance().getApi().REloginUser(user);
                callLogin.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body().getStatus() == 1) {
                            RSResponseLogin loginResponse = new Gson().fromJson(new Gson().toJson(response.body().getData()), RSResponseLogin.class);
                            String token = loginResponse.getToken();
                            RSSessionToken.startSession(token, true);
//                            Toast.makeText(instance.getContext(), RSSessionToken.getUsergestToken() + "///"
//                                    + RSSessionToken.getStatutGestConnected(), Toast.LENGTH_SHORT).show();
                            loadHomeData();
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!call.isCanceled()) {
                            Log.i("err",t.getMessage());
                        }
                    }
                });
            } else {
                loadHomeData();
//                Toast.makeText(instance.getContext(), RSSessionToken.getUsergestToken() + "///"
//                        + RSSessionToken.getStatutGestConnected(), Toast.LENGTH_SHORT).show();
            }
        } else {
            onOffLine();
        }
    }

    private void getCurrentUser() {
        if (RSSession.isLoggedIn()) {
            user = RSSession.getCurrentUser();
            rsRequestListItem.setUserId(user.get_id());
        }
    }

    private void bindViews() {
        toolbar.setTitle(homeTitle);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setFragmentActionListener((ContainerActivity) getActivity());
    }

    private void loadHomeData() {
        try {
            Integer.parseInt("uuihuuh");
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        rsRequestListItem.setPage(1);
        rsRequestListItem.setPerPage(RSConstants.MAX_ITEM_TO_LOAD);
        loadTopRankedItem();
        loadTopViewedItem();
        loadTopFollowedItem();
        loadTopCommentedItem();
    }

    private void loadTopRankedItem() {
        itemPresenter.loadTopRankedItems(rsRequestListItem);
    }

    private void loadTopViewedItem() {
        itemPresenter.loadTopViewedItems(rsRequestListItem);
    }

    private void loadTopFollowedItem() {
        itemPresenter.loadTopFollowedItems(rsRequestListItem);
    }

    private void loadTopCommentedItem() {
        itemPresenter.loadTopCommentedItems(rsRequestListItem);
    }

    private void initTopRanked(List<Item> listTopRankedItem) {
        recyclerViewTopRanked.setVisibility(View.VISIBLE);
        ItemPieListener listener = new ItemPieListener() {
            @Override
            public void onFollowChanged(int position) {
                manageFollow(listTopRankedItem.get(position).getItemDetails().get_id(), !listTopRankedItem.get(position).isFollow());
            }

            @Override
            public void onClick(View view, int position) {
                if (RSNetwork.isConnected())
                    fragmentActionListener.startFragment(ItemDetailsFragment.getInstance(listTopRankedItem.get(position).getItemDetails().get_id()), RSConstants.FRAGMENT_ITEM_DETAILS);
                else
                    onOffLine();
            }
        };
        topRankedAdapter = new PieAdapter(listTopRankedItem, listener);
        recyclerViewTopRanked.setLayoutManager(new LinearLayoutManager(recyclerViewTopRanked.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTopRanked.setAdapter(topRankedAdapter);
        recyclerViewTopRanked.addItemDecoration(new HorizontalSpace(marginCardView));
        recyclerViewTopRanked.setNestedScrollingEnabled(false);
    }

    private void initTopViewed(List<Item> listTopViewedItem) {
        recyclerViewTopViewed.setVisibility(View.VISIBLE);
        ItemPieListener listener = new ItemPieListener() {
            @Override
            public void onFollowChanged(int position) {
                manageFollow(listTopViewedItem.get(position).getItemDetails().get_id(), !listTopViewedItem.get(position).isFollow());
            }

            @Override
            public void onClick(View view, int position) {
                if (RSNetwork.isConnected())
                    fragmentActionListener.startFragment(ItemDetailsFragment.getInstance(listTopViewedItem.get(position).getItemDetails().get_id()), RSConstants.FRAGMENT_ITEM_DETAILS);
                else
                    onOffLine();
            }
        };
        topViewedAdapter = new PieAdapter(listTopViewedItem, listener);
        recyclerViewTopViewed.setLayoutManager(new LinearLayoutManager(recyclerViewTopViewed.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTopViewed.setAdapter(topViewedAdapter);
        recyclerViewTopViewed.addItemDecoration(new HorizontalSpace(marginCardView));
        recyclerViewTopViewed.setNestedScrollingEnabled(false);
    }

    private void initTopFollowed(List<Item> listTopFollowedItem) {
        recyclerViewTopFollowed.setVisibility(View.VISIBLE);
        ItemPieListener listener = new ItemPieListener() {
            @Override
            public void onFollowChanged(int position) {
                manageFollow(listTopFollowedItem.get(position).getItemDetails().get_id(), !listTopFollowedItem.get(position).isFollow());
            }

            @Override
            public void onClick(View view, int position) {
                if (RSNetwork.isConnected())
                    fragmentActionListener.startFragment(ItemDetailsFragment.getInstance(listTopFollowedItem.get(position).getItemDetails().get_id()), RSConstants.FRAGMENT_ITEM_DETAILS);
                else
                    onOffLine();
            }
        };
        topFollowedAdapter = new PieAdapter(listTopFollowedItem, listener);
        recyclerViewTopFollowed.setLayoutManager(new LinearLayoutManager(recyclerViewTopFollowed.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTopFollowed.setAdapter(topFollowedAdapter);
        recyclerViewTopFollowed.addItemDecoration(new HorizontalSpace(marginCardView));
        recyclerViewTopFollowed.setNestedScrollingEnabled(false);
    }

    private void initTopCommented(List<Item> listTopCommentedItem) {
        recyclerViewTopCommented.setVisibility(View.VISIBLE);
        ItemPieListener listener = new ItemPieListener() {
            @Override
            public void onFollowChanged(int position) {
                manageFollow(listTopCommentedItem.get(position).getItemDetails().get_id(), !listTopCommentedItem.get(position).isFollow());
            }

            @Override
            public void onClick(View view, int position) {
                if (RSNetwork.isConnected())
                    fragmentActionListener.startFragment(ItemDetailsFragment.getInstance(listTopCommentedItem.get(position).getItemDetails().get_id()), RSConstants.FRAGMENT_ITEM_DETAILS);
                else
                    onOffLine();
            }
        };
        topCommentedAdapter = new PieAdapter(listTopCommentedItem, listener);
        recyclerViewTopCommented.setLayoutManager(new LinearLayoutManager(recyclerViewTopCommented.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTopCommented.setAdapter(topCommentedAdapter);
        recyclerViewTopCommented.addItemDecoration(new HorizontalSpace(marginCardView));
        recyclerViewTopCommented.setNestedScrollingEnabled(false);
    }

    private void manageFollow(String itemId, boolean isFollow) {
        itemIdToFollow = itemId;
        if (RSSession.isLoggedIn()) {
            RSFollow rsFollow = new RSFollow(user.get_id(), itemId);
            if (isFollow)
                itemPresenter.followItem(rsFollow);
            else
                itemPresenter.unfollowItem(rsFollow);
        } else {
            openAlertDialog(alertLoginToFollowMsg, itemId);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.rs_menu, menu);
        if (!RSSession.isLoggedIn()) {
            MenuItem item = menu.findItem(R.id.logout);
            item.setVisible(false);
        }
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
        }

        return super.onOptionsItemSelected(item);
    }

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    public static HomeFragment getInstance() {
        if (instance == null) {
            instance = new HomeFragment();
        }
        return instance;
    }

    public static HomeFragment getInstance(RSNavigationData rsNavigationData) {
        Bundle args = new Bundle();
        args.putSerializable(RSConstants.NAVIGATION_DATA, rsNavigationData);
        if (instance == null) {
            instance = new HomeFragment();
        }
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onDestroyView() {
        instance = null;
        fragmentActionListener = null;
        rootView = null;
        if (itemPresenter != null)
            itemPresenter.onDestroyItem();
        if (unbinder != null)
            unbinder.unbind();
        super.onDestroyView();
    }

    private void openContactDialog() {
        ContactDialog dialog = new ContactDialog();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.show(ft, ContactDialog.TAG);
    }


    @Override
    public void onSuccess(String target, Object data) {
        RSResponseListingItem listingItemResponse = null;
        if (!(data instanceof String)) {
            listingItemResponse = new Gson().fromJson(new Gson().toJson(data), RSResponseListingItem.class);
        }

        switch (target) {
            case RSConstants.TOP_RANKED_ITEMS:
                listTopRankedItem = listingItemResponse.getItems();
                if (listTopRankedItem.size() == 0) {
                    layoutTopRanked.setVisibility(View.VISIBLE);
                } else {
                    initTopRanked(listTopRankedItem);
                    if (listingItemResponse.getPages() > 1)
                        moreTopRankedBtn.setVisibility(View.VISIBLE);
                    else
                        moreTopRankedBtn.setVisibility(View.GONE);
                }
                break;
            case RSConstants.TOP_COMMENTED_ITEMS:
                listTopCommentedItem = listingItemResponse.getItems();
                if (listTopCommentedItem.size() == 0) {
                    layoutTopCommented.setVisibility(View.VISIBLE);
                } else {
                    initTopCommented(listTopCommentedItem);
                    if (listingItemResponse.getPages() > 1)
                        moreTopCommentedBtn.setVisibility(View.VISIBLE);
                    else
                        moreTopCommentedBtn.setVisibility(View.GONE);
                }
                break;
            case RSConstants.TOP_VIEWED_ITEMS:
                listTopViewedItem = listingItemResponse.getItems();
                if (listTopViewedItem.size() == 0) {
                    layoutTopViewed.setVisibility(View.VISIBLE);
                } else {
                    initTopViewed(listTopViewedItem);
                    if (listingItemResponse.getPages() > 1)
                        moreTopViewedBtn.setVisibility(View.VISIBLE);
                    else
                        moreTopViewedBtn.setVisibility(View.GONE);
                }
                break;
            case RSConstants.TOP_FOLLOWED_ITEMS:
                listTopFollowedItem = listingItemResponse.getItems();
                if (listTopFollowedItem.size() == 0) {
                    layoutTopFollowed.setVisibility(View.VISIBLE);
                } else {
                    initTopFollowed(listTopFollowedItem);
                    if (listingItemResponse.getPages() > 1)
                        moreTopFollowedBtn.setVisibility(View.VISIBLE);
                    else
                        moreTopFollowedBtn.setVisibility(View.GONE);
                }
                break;
            case RSConstants.FOLLOW_ITEM:
                if (data.equals("1")) {
                    Toast.makeText(getContext(), followMsg, Toast.LENGTH_SHORT).show();
                    changeIconFollow(itemIdToFollow, true);
                } else if (data.equals("0")) {
                    Toast.makeText(getContext(), alreadyFollowedMsg, Toast.LENGTH_SHORT).show();
                }
                break;
            case RSConstants.UNFOLLOW_ITEM:
                Toast.makeText(getContext(), unfollowMsg, Toast.LENGTH_SHORT).show();
                changeIconFollow(itemIdToFollow, false);
                break;
        }
    }

    private void changeIconFollow(String itemId, boolean follow) {
        int indexIntoRankedList = findItemIndex(listTopRankedItem, itemId);
        if (indexIntoRankedList != -1) {
            listTopRankedItem.get(indexIntoRankedList).setFollow(follow);
            topRankedAdapter.notifyItemChanged(indexIntoRankedList, "icon");
        }

        int indexIntoViewedList = findItemIndex(listTopViewedItem, itemId);
        if (indexIntoViewedList != -1) {
            listTopViewedItem.get(indexIntoViewedList).setFollow(follow);
            topViewedAdapter.notifyItemChanged(indexIntoViewedList, "icon");
        }

        int indexIntoCommentedList = findItemIndex(listTopCommentedItem, itemId);
        if (indexIntoCommentedList != -1) {
            listTopCommentedItem.get(indexIntoCommentedList).setFollow(follow);
            topCommentedAdapter.notifyItemChanged(indexIntoCommentedList, "icon");
        }

        int indexIntoFollowedList = findItemIndex(listTopFollowedItem, itemId);
        if (indexIntoFollowedList != -1) {
            listTopFollowedItem.get(indexIntoFollowedList).setFollow(follow);
            topFollowedAdapter.notifyItemChanged(indexIntoFollowedList, "icon");
        }
    }

    private int findItemIndex(List<Item> itemList, String itemId) {
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getItemDetails().get_id().equals(itemId))
                return i;
        }
        return -1;
    }

    @Override
    public void onFailure(String target) {
        switch (target) {
            case RSConstants.TOP_RANKED_ITEMS:
                recyclerViewTopRanked.setVisibility(View.GONE);
                layoutTopRanked.setVisibility(View.VISIBLE);
                moreTopRankedBtn.setVisibility(View.GONE);
                break;
            case RSConstants.TOP_COMMENTED_ITEMS:
                recyclerViewTopCommented.setVisibility(View.GONE);
                layoutTopCommented.setVisibility(View.VISIBLE);
                moreTopCommentedBtn.setVisibility(View.GONE);
                break;
            case RSConstants.TOP_VIEWED_ITEMS:
                recyclerViewTopViewed.setVisibility(View.GONE);
                layoutTopViewed.setVisibility(View.VISIBLE);
                moreTopViewedBtn.setVisibility(View.GONE);
                break;
            case RSConstants.TOP_FOLLOWED_ITEMS:
                recyclerViewTopFollowed.setVisibility(View.GONE);
                layoutTopFollowed.setVisibility(View.VISIBLE);
                moreTopFollowedBtn.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onError(String target) {

    }

    @Override
    public void showProgressBar(String target) {
        switch (target) {
            case RSConstants.TOP_RANKED_ITEMS:
                progressBarTopRanked.setVisibility(View.VISIBLE);
                break;
            case RSConstants.TOP_COMMENTED_ITEMS:
                progressBarTopCommented.setVisibility(View.VISIBLE);
                break;
            case RSConstants.TOP_VIEWED_ITEMS:
                progressBarTopViewed.setVisibility(View.VISIBLE);
                break;
            case RSConstants.TOP_FOLLOWED_ITEMS:
                progressBarTopFollowed.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void hideProgressBar(String target) {
        switch (target) {
            case RSConstants.TOP_RANKED_ITEMS:
                progressBarTopRanked.setVisibility(View.GONE);
                break;
            case RSConstants.TOP_COMMENTED_ITEMS:
                progressBarTopCommented.setVisibility(View.GONE);
                break;
            case RSConstants.TOP_VIEWED_ITEMS:
                progressBarTopViewed.setVisibility(View.GONE);
                break;
            case RSConstants.TOP_FOLLOWED_ITEMS:
                progressBarTopFollowed.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void showMessage(String target, String message) {

    }

    @Override
    public void onOffLine() {
        Toast.makeText(getContext(), offlineMsg, Toast.LENGTH_LONG).show();
    }

    private void openAlertDialog(String message, String itemId) {
        RSNavigationData data = new RSNavigationData();
        data.setMessage(message);
        data.setItemId(itemId);
        data.setAction(RSConstants.ACTION_FOLLOW);
        data.setFrom(RSConstants.FRAGMENT_HOME);
        AskToLoginDialog dialog = AskToLoginDialog.newInstance(data);
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "");
    }
}
