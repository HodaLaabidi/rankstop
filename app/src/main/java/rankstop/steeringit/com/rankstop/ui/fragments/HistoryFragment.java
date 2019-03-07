package rankstop.steeringit.com.rankstop.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterUserHistoryImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.RankStop;
import rankstop.steeringit.com.rankstop.customviews.RSBTNBold;
import rankstop.steeringit.com.rankstop.customviews.RSTVMedium;
import rankstop.steeringit.com.rankstop.customviews.RSTVRegular;
import rankstop.steeringit.com.rankstop.data.model.db.History;
import rankstop.steeringit.com.rankstop.data.model.network.RSNavigationData;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestListItem;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponseHistory;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.HistoryAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.ItemHistoryListener;
import rankstop.steeringit.com.rankstop.utils.EndlessScrollListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;
import rankstop.steeringit.com.rankstop.utils.VerticalSpace;

public class HistoryFragment extends Fragment implements RSView.StandardView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_history)
    RecyclerView historyListRV;
    @BindView(R.id.tv_no_history)
    RSTVMedium noHistoryTV;
    @BindView(R.id.tv_msg_connect)
    RSTVRegular msgConnectTV;
    @BindView(R.id.btn_connect)
    RSBTNBold connectBTN;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindInt(R.integer.m_card_view)
    int marginCardView;
    @BindInt(R.integer.count_item_per_row)
    int countItemPerRow;

    @BindString(R.string.text_history)
    String historyTitle;
    @BindString(R.string.off_line)
    String offlineMsg;

    @OnClick(R.id.btn_connect)
    void connect() {
        RSNavigationData rsNavigationData = new RSNavigationData(RSConstants.FRAGMENT_HISTORY, RSConstants.ACTION_CONNECT, "", "", "", "", "");
        navigateToSignUp(rsNavigationData);
    }

    private View rootView;
    private Unbinder unbinder;

    // fragment context
    private WeakReference<HistoryFragment> fragmentContext;

    // presenter
    private RSPresenter.UserHistoryPresenter userHistoryPresenter;

    private RSRequestListItem rsRequestListItem = new RSRequestListItem();
    private HistoryAdapter historyAdapter;
    private List<History> historiesList = new ArrayList<>();

    // pagination variables
    private int currentPage = 1;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private int PAGES_COUNT = 1;
    // scroll listener
    private EndlessScrollListener scrollListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_history, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindViews();
        fragmentContext = new WeakReference<>(this);
        userHistoryPresenter = new PresenterUserHistoryImpl(HistoryFragment.this);

        if (RSSession.isLoggedIn()) {
            // variables
            rsRequestListItem.setUserId(RSSession.getCurrentUser().get_id());
            rsRequestListItem.setLang(RankStop.getDeviceLanguage());
            rsRequestListItem.setPerPage(RSConstants.MAX_FIELD_TO_LOAD);
            if (RSNetwork.isConnected()) {
                progressBar.setVisibility(View.VISIBLE);
                loadData(currentPage);
            } else {
                onOffLine();
            }
            initItemsList();
        } else {
            msgConnectTV.setVisibility(View.VISIBLE);
            connectBTN.setVisibility(View.VISIBLE);
        }
    }

    private void initItemsList() {
        ItemHistoryListener itemListener = new ItemHistoryListener() {
            @Override
            public void onClick(View view, int position) {
                fragmentActionListener.startFragment(ItemDetailsFragment.getInstance(historiesList.get(position).getItem().get_id()), RSConstants.FRAGMENT_ITEM_DETAILS);
            }
        };
        GridLayoutManager layoutManager = new GridLayoutManager(historyListRV.getContext(), 1);
        historyAdapter = new HistoryAdapter(itemListener);
        historyListRV.setLayoutManager(layoutManager);
        historyListRV.setAdapter(historyAdapter);
        historyListRV.addItemDecoration(new VerticalSpace(marginCardView, countItemPerRow));
        scrollListener = new EndlessScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                rsRequestListItem.setPage(currentPage);
                // mocking network delay for API call
                loadData(currentPage);
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
        historyListRV.addOnScrollListener(scrollListener);
    }

    private void loadData(int pageNumber) {
        rsRequestListItem.setPage(pageNumber);
        userHistoryPresenter.loadHistory(rsRequestListItem);
    }

    private void bindViews() {
        setFragmentActionListener((ContainerActivity) getActivity());
        toolbar.setTitle(historyTitle);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private FragmentActionListener fragmentActionListener;

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    private static HistoryFragment instance;

    public static HistoryFragment getInstance() {
        if (instance == null) {
            instance = new HistoryFragment();
        }
        return instance;
    }

    @Override
    public void onDestroyView() {

        currentPage = 1;
        isLastPage = false;
        isLoading = false;
        PAGES_COUNT = 1;

        scrollListener = null;
        if (historyAdapter != null)
            historyAdapter.clear();
        historyAdapter = null;
        instance = null;
        rootView = null;
        fragmentActionListener = null;
        if (unbinder != null)
            unbinder.unbind();
        if (userHistoryPresenter != null)
            userHistoryPresenter.onDestroy();
        if (fragmentContext != null)
            fragmentContext.clear();
        fragmentContext = null;
        super.onDestroyView();
    }


    @Override
    public void onSuccess(String target, Object data) {
        RSResponseHistory historyResponse = null;
        if (!(data instanceof String)) {
            historyResponse = new Gson().fromJson(new Gson().toJson(data), RSResponseHistory.class);
        }
        switch (target) {
            case RSConstants.USER_HISTORY:
                historiesList.addAll(historyResponse.getStories());
                if (historyResponse.getCurrent() == 1) {
                    historyAdapter.clear();
                    progressBar.setVisibility(View.GONE);
                    PAGES_COUNT = historyResponse.getPages();
                    if (historyResponse.getStories().size() > 0)
                        historyListRV.setVisibility(View.VISIBLE);
                    else
                        noHistoryTV.setVisibility(View.VISIBLE);
                } else if (historyResponse.getCurrent() > 1) {
                    historyAdapter.removeLoadingFooter();
                    isLoading = false;
                }
                historyAdapter.addAll(historyResponse.getStories());
                if (currentPage < PAGES_COUNT) {
                    historyAdapter.addLoadingFooter();
                    isLastPage = false;
                } else {
                    isLastPage = true;
                }
                break;
        }
    }

    @Override
    public void onFailure(String target) {
        switch (target) {
            case RSConstants.USER_HISTORY:
                break;
        }
    }

    @Override
    public void onError(String target) {
        switch (target) {
            case RSConstants.USER_HISTORY:
                break;
        }
    }

    @Override
    public void showProgressBar(String target) {
        switch (target) {
            case RSConstants.USER_HISTORY:
                break;
        }
    }

    @Override
    public void hideProgressBar(String target) {
        switch (target) {
            case RSConstants.USER_HISTORY:
                break;
        }
    }

    @Override
    public void showMessage(String target, String message) {
        switch (target) {
            case RSConstants.USER_HISTORY:
                break;
        }
    }

    @Override
    public void onOffLine() {
        Toast.makeText(getContext(), offlineMsg, Toast.LENGTH_LONG).show();
    }

    private void navigateToSignUp(RSNavigationData rsNavigationData) {
        fragmentActionListener.startFragment(SignupFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_SIGN_UP);
    }
}
