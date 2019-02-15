package rankstop.steeringit.com.rankstop.ui.fragments;

import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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

import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterItemImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.RankStop;
import rankstop.steeringit.com.rankstop.customviews.RSTVMedium;
import rankstop.steeringit.com.rankstop.data.model.db.Item;
import rankstop.steeringit.com.rankstop.data.model.db.User;
import rankstop.steeringit.com.rankstop.data.model.network.RSFollow;
import rankstop.steeringit.com.rankstop.data.model.network.RSNavigationData;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestListItem;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponseListingItem;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.MyEvalsAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.ItemPieListener;
import rankstop.steeringit.com.rankstop.utils.EndlessScrollListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;
import rankstop.steeringit.com.rankstop.utils.VerticalSpace;

public class MyEvaluationsFragment extends Fragment implements RSView.StandardView {

    private View rootView;

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

    @OnClick(R.id.btn_login_or_search)
    void fun() {
        if (!isLoggedIn) {
            RSNavigationData rsNavigationData = new RSNavigationData(RSConstants.FRAGMENT_MY_EVALS, RSConstants.ACTION_CONNECT, "", "", "", "", "");
            navigateToSignUp(rsNavigationData);
        } else {
            navigateToHome();
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentContext = new WeakReference<MyEvaluationsFragment>(this);
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
            itemPresenter = new PresenterItemImpl(MyEvaluationsFragment.this);
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
        }
    }

    private void loadMyEvals(RSRequestListItem rsRequestListItem) {
        itemPresenter.loadMyEvals(rsRequestListItem);
    }

    private void initMyEvals() {
        ItemPieListener listener = new ItemPieListener() {
            @Override
            public void onFollowChanged(boolean isFollow, int position) {
                //manageFollow(listMyEvals.get(position).getItemDetails().get_id(), isFollow);
            }

            @Override
            public void onFollowChanged(int position) {
                manageFollow(listMyEvals.get(position).getItemDetails().get_id(), !listMyEvals.get(position).isFollow());
            }

            @Override
            public void onClick(View view, int position) {
                if (RSNetwork.isConnected())
                    fragmentActionListener.startFragment(ItemDetailsFragment.getInstance(listMyEvals.get(position).getItemDetails().get_id()), RSConstants.FRAGMENT_ITEM_DETAILS);
                else
                    onOffLine();
            }
        };
        GridLayoutManager layoutManager = new GridLayoutManager(recyclerViewMyEvals.getContext(), 1);
        recyclerViewMyEvals.setLayoutManager(layoutManager);

        myEvalsAdapter = new MyEvalsAdapter(listener);
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
        RSFollow rsFollow = new RSFollow(user.get_id(), itemId);
        if (isFollow)
            itemPresenter.followItem(rsFollow);
        else
            itemPresenter.unfollowItem(rsFollow);

    }

    private void bindViews() {
        toolbar.setTitle(getResources().getString(R.string.title_my_evals));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setFragmentActionListener((ContainerActivity) getActivity());
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.rs_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.setting:
                fragmentActionListener.startFragment(SettingsFragment.getInstance(), RSConstants.FRAGMENT_SETTINGS);
                break;
            case R.id.history:
                fragmentActionListener.startFragment(HistoryFragment.getInstance(""), RSConstants.FRAGMENT_HISTORY);
                break;
            case R.id.contact:
                fragmentActionListener.startFragment(ContactFragment.getInstance(), RSConstants.FRAGMENT_CONTACT);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private FragmentActionListener fragmentActionListener;

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
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
            itemPresenter.onDestroyItem();
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
                        Log.i("TAG_MY_EVALS", "PAGES_COUNT = " + PAGES_COUNT + ", currentPage = " + currentPage + ", userId = " + user.get_id());
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
                    //changeIconFollow(itemIdToFollow, true);
                } else if (data.equals("0")) {
                    Toast.makeText(getContext(), getResources().getString(R.string.already_followed), Toast.LENGTH_SHORT).show();
                }
                break;
            case RSConstants.UNFOLLOW_ITEM:
                Toast.makeText(getContext(), getResources().getString(R.string.unfollow), Toast.LENGTH_SHORT).show();
                //changeIconFollow(itemIdToFollow, false);
                break;
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
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar(String target) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String target, String message) {

    }

    @Override
    public void onOffLine() {
        Toast.makeText(getContext(), offlineMsg, Toast.LENGTH_LONG).show();
    }

    private void navigateToSignUp(RSNavigationData rsNavigationData) {
        fragmentActionListener.startFragment(SignupFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_SIGN_UP);
    }

    private void navigateToHome() {
        fragmentActionListener.navigateTo(R.id.navigation_search, RSConstants.FRAGMENT_SEARCH);
    }
}
