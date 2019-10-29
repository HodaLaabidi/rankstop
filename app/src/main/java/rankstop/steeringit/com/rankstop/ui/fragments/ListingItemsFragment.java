package rankstop.steeringit.com.rankstop.ui.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Toast;

import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterItemImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.RankStop;
import rankstop.steeringit.com.rankstop.customviews.RSCustomToast;
import rankstop.steeringit.com.rankstop.data.model.db.Item;
import rankstop.steeringit.com.rankstop.data.model.db.User;
import rankstop.steeringit.com.rankstop.data.model.network.RSFollow;
import rankstop.steeringit.com.rankstop.data.model.network.RSNavigationData;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestListItem;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponseListingItem;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.ItemsAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.ItemPieListener;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.AskToLoginDialog;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.ContactDialog;
import rankstop.steeringit.com.rankstop.utils.EndlessScrollListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;
import rankstop.steeringit.com.rankstop.utils.VerticalSpace;

public class ListingItemsFragment extends Fragment implements RSView.StandardView ,RSView.StandardView2 {

    private View rootView;
    private Unbinder unbinder;

    private String from;
    private RSPresenter.ItemPresenter itemPresenter;
    private RSRequestListItem rsRequestListItem = new RSRequestListItem();
    private User currentUser;
    private boolean isLoggedIn;
    private WeakReference<ListingItemsFragment> fragmentContext;
    private List<Item> itemsList = new ArrayList<>();
    private String itemIdToFollow;
    private ItemsAdapter itemsAdapter;
    private EndlessScrollListener scrollListener;
    RSNavigationData rsNavigationData = new RSNavigationData();

    // panigation variables
    private int currentPage = 1;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private int PAGES_COUNT = 1;

    // bind layout
    @BindView(R.id.rv_item_list)
    RecyclerView itemsListRV;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindString(R.string.alert_login_to_follow)
    String alertLoginToFollowMsg;
    @BindString(R.string.off_line)
    String offlineMsg;

    @BindInt(R.integer.m_card_view)
    int marginCardView;
    @BindInt(R.integer.count_item_per_row)
    int countItemPerRow;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_listing_items, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        fragmentContext = new WeakReference<>(this);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RSNavigationData navigationData = (RSNavigationData) getArguments().getSerializable(RSConstants.NAVIGATION_DATA);
        from = navigationData.getSection();
        bindViews();
        itemPresenter = new PresenterItemImpl(ListingItemsFragment.this, ListingItemsFragment.this);
        isLoggedIn = RSSession.isLoggedIn();
        if (isLoggedIn) {
            currentUser = RSSession.getCurrentUser();
            rsRequestListItem.setUserId(currentUser.get_id());
        }
        rsRequestListItem.setPerPage(RSConstants.MAX_FIELD_TO_LOAD);
        laodData(from, currentPage);
        initItemsList();
    }

    private void initItemsList() {
        ItemPieListener itemsListener = new ItemPieListener() {
            @Override
            public void onFollowChanged(int position) {
                manageFollow(itemsList.get(position).getItemDetails().get_id(), !itemsList.get(position).isFollow());
            }

            @Override
            public void onClick(View view, int position) {
                if (RSNetwork.isConnected(getContext())) {
                    fragmentActionListener.startFragment(ItemDetailsFragment.getInstance(itemsList.get(position).getItemDetails().get_id()), RSConstants.FRAGMENT_ITEM_DETAILS);
                } else {
                    onOffLine();
                }
            }
        };
        GridLayoutManager layoutManager = new GridLayoutManager(itemsListRV.getContext(), countItemPerRow);
        itemsAdapter = new ItemsAdapter(itemsListener, true);
        itemsListRV.setLayoutManager(layoutManager);
        itemsListRV.setAdapter(itemsAdapter);
        itemsListRV.addItemDecoration(new VerticalSpace(marginCardView, countItemPerRow));
        scrollListener = new EndlessScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                rsRequestListItem.setPage(currentPage);
                laodData(from, currentPage);
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
        };
        itemsListRV.addOnScrollListener(scrollListener);
    }

    private void manageFollow(String itemId, boolean isFollow) {
        itemIdToFollow = itemId;
        if (isLoggedIn) {
            RSFollow rsFollow = new RSFollow(currentUser.get_id(), itemId);
            if (isFollow)
                itemPresenter.followItem(rsFollow, getContext());
            else
                itemPresenter.unfollowItem(rsFollow, getContext());
        } else {
            openAlertDialog(alertLoginToFollowMsg, itemId);
        }
    }

    private void openAlertDialog(String message, String itemId) {
        RSNavigationData data = new RSNavigationData();
        data.setItemId(itemId);
        data.setAction(RSConstants.ACTION_FOLLOW);
        data.setMessage(message);
        data.setFrom(RSConstants.FRAGMENT_LISTING_ITEMS);
        data.setSection(from);
        AskToLoginDialog dialog = AskToLoginDialog.newInstance(data);
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "");
    }

    private void laodData(String target, int pageNumber) {
        rsRequestListItem.setPage(pageNumber);
        switch (target) {
            case RSConstants.ITEM_CREATED:
                itemPresenter.loadItemCreated(rsRequestListItem, getContext());
                break;
            case RSConstants.ITEM_OWNED:
                itemPresenter.loadItemOwned(rsRequestListItem, getContext());
                break;
            case RSConstants.ITEM_FOLLOWED:
                itemPresenter.loadItemFollowed(rsRequestListItem , getContext());
                break;
            case RSConstants.TOP_RANKED_ITEMS:
                itemPresenter.loadTopRankedItems(rsRequestListItem, getContext());
                break;
            case RSConstants.TOP_COMMENTED_ITEMS:
                itemPresenter.loadTopCommentedItems(rsRequestListItem, getContext());
                break;
            case RSConstants.TOP_VIEWED_ITEMS:
                itemPresenter.loadTopViewedItems(rsRequestListItem, getContext());
                break;
            case RSConstants.TOP_FOLLOWED_ITEMS:
                itemPresenter.loadTopFollowedItems(rsRequestListItem, getContext());
                break;
        }
    }

    private void changeIconFollow(String itemId, boolean follow) {
        int index = findItemIndex(itemsList, itemId);
        if (index != -1) {
            itemsList.get(index).setFollow(follow);
            //itemsAdapter.notifyItemChanged(index, "icon");
            itemPresenter.refreshItems(this.getContext(),currentUser.get_id(), itemId, "icon", RankStop.getDeviceLanguage());
        }
    }

    private int findItemIndex(List<Item> itemList, String itemId) {
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getItemDetails().get_id().equals(itemId))
                return i;
        }
        return -1;
    }

    private void bindViews() {
        setFragmentActionListener((ContainerActivity) getActivity());
        switch (from) {
            case RSConstants.ITEM_CREATED:
                toolbar.setTitle(getResources().getString(R.string.text_page_created));
                break;
            case RSConstants.ITEM_OWNED:
                toolbar.setTitle(getResources().getString(R.string.text_page_owned));
                break;
            case RSConstants.ITEM_FOLLOWED:
                toolbar.setTitle(getResources().getString(R.string.text_page_followed));
                break;
            case RSConstants.TOP_RANKED_ITEMS:
                toolbar.setTitle(getResources().getString(R.string.text_page_top_ranked));
                break;
            case RSConstants.TOP_COMMENTED_ITEMS:
                toolbar.setTitle(getResources().getString(R.string.text_page_top_commented));
                break;
            case RSConstants.TOP_VIEWED_ITEMS:
                toolbar.setTitle(getResources().getString(R.string.text_page_top_viewed));
                break;
            case RSConstants.TOP_FOLLOWED_ITEMS:
                toolbar.setTitle(getResources().getString(R.string.text_page_top_followed));
                break;
        }
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
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
                break;

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

    private static ListingItemsFragment instance;

    public static ListingItemsFragment getInstance(RSNavigationData rsNavigationData) {
        Bundle args = new Bundle();
        args.putSerializable(RSConstants.NAVIGATION_DATA, rsNavigationData);
        if (instance == null) {
            instance = new ListingItemsFragment();
        }
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onDestroyView() {


        currentPage = 1;
        isLastPage = false;
        isLoading = false;
        PAGES_COUNT = 1;

        Log.i("TAG_LISTING", "on destroy view");
        instance = null;
        rootView = null;
        scrollListener = null;
        fragmentActionListener = null;
        if (fragmentContext != null)
            fragmentContext.clear();
        if (itemPresenter != null)
            itemPresenter.onDestroyItem(getContext());
        if (itemsAdapter != null)
            itemsAdapter.clear();
        itemsAdapter = null;
        if (unbinder != null)
            unbinder.unbind();
        super.onDestroyView();
    }


    @Override
    public void onSuccess(String target, Object data) {
        RSResponseListingItem listingItemResponse = null;
        if (!(data instanceof String)) {
            listingItemResponse = new Gson().fromJson(new Gson().toJson(data), RSResponseListingItem.class);
        }


        switch (target) {
            case RSConstants.TOP_RANKED_ITEMS:
            case RSConstants.TOP_COMMENTED_ITEMS:
            case RSConstants.TOP_VIEWED_ITEMS:
            case RSConstants.TOP_FOLLOWED_ITEMS:
            case RSConstants.ITEM_CREATED:
            case RSConstants.ITEM_FOLLOWED:
            case RSConstants.ITEM_OWNED:
                Log.i("TAG_CURRENT_PAGE", "" + listingItemResponse.getCurrent());
                itemsList.addAll(listingItemResponse.getItems());
                if (listingItemResponse.getCurrent() == 1) {
                    itemsAdapter.clear();
                    PAGES_COUNT = listingItemResponse.getPages();
                } else if (listingItemResponse.getCurrent() > 1) {
                    itemsAdapter.removeLoadingFooter();
                    isLoading = false;
                }
                itemsAdapter.addAll(listingItemResponse.getItems());
                if (currentPage < PAGES_COUNT) {
                    itemsAdapter.addLoadingFooter();
                    isLastPage = false;
                } else {
                    isLastPage = true;
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
        //Toast.makeText(getContext(), offlineMsg, Toast.LENGTH_LONG).show();
        new RSCustomToast(getActivity(), getResources().getString(R.string.error), offlineMsg, R.drawable.ic_error, RSCustomToast.ERROR).show();

    }

    @Override
    public void onSuccessRefreshItem(String target, String itemId, String message, Object data) {

        Item item = null;
        if (!(data instanceof String)) {
            item = new Gson().fromJson(new Gson().toJson(data), Item.class);

            for (int i = 0; i < itemsList.size(); i++) {
                if (itemsList.get(i).getItemDetails().get_id().equalsIgnoreCase(itemId)) {

                    itemsAdapter.refreshOneItem(i, item, message);
                    break;


                }

            }

        }

    }
}
