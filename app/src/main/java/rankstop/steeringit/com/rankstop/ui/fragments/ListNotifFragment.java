package com.steeringit.rankstop.ui.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.steeringit.rankstop.MVP.model.PresenterNotifImpl;
import com.steeringit.rankstop.MVP.presenter.RSPresenter;
import com.steeringit.rankstop.MVP.view.RSView;
import com.steeringit.rankstop.R;
import com.steeringit.rankstop.RankStop;
import com.steeringit.rankstop.customviews.RSBTNBold;
import com.steeringit.rankstop.customviews.RSCustomToast;
import com.steeringit.rankstop.customviews.RSTVRegular;
import com.steeringit.rankstop.data.model.db.RSNotif;
import com.steeringit.rankstop.data.model.network.RSNavigationData;
import com.steeringit.rankstop.data.model.network.RSRequestListItem;
import com.steeringit.rankstop.data.model.network.RSResponseNotif;
import com.steeringit.rankstop.session.RSSession;
import com.steeringit.rankstop.ui.activities.ContainerActivity;
import com.steeringit.rankstop.ui.adapter.NotifAdapter;
import com.steeringit.rankstop.ui.callbacks.FragmentActionListener;
import com.steeringit.rankstop.ui.callbacks.RecyclerViewClickListener;
import com.steeringit.rankstop.ui.dialogFragment.RSLoader;
import com.steeringit.rankstop.utils.EndlessScrollListener;
import com.steeringit.rankstop.utils.RSConstants;
import com.steeringit.rankstop.utils.RSNetwork;
import com.steeringit.rankstop.utils.VerticalSpace;

public class ListNotifFragment extends Fragment implements RSView.ListNotifView {

    private View rootView;
    private Unbinder unbinder;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_no_notif)
    RSTVRegular noNotifTV;
    @BindView(R.id.tv_msg_connect)
    RSTVRegular msgConnectTV;
    @BindView(R.id.btn_connect)
    RSBTNBold connectBTN;
    @BindView(R.id.rv_list_notif)
    RecyclerView listNotifRV;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindInt(R.integer.m_card_view)
    int marginCardView;
    @BindInt(R.integer.count_item_per_row)
    int countItemPerRow;

    @BindString(R.string.notifications)
    String notifTitle;
    @BindString(R.string.off_line)
    String offlineMsg;

    @OnClick(R.id.btn_connect)
    void connect(){
        RSNavigationData rsNavigationData = new RSNavigationData(RSConstants.FRAGMENT_NOTIF, RSConstants.ACTION_CONNECT, "", "", "", "", "");
        navigateToSignUp(rsNavigationData);
    }

    private RSPresenter.ListNotifPresenter listNotifPresenter;

    // variables
    private RSRequestListItem rsRequestListItem = new RSRequestListItem();
    private NotifAdapter notifAdapter;
    private List<RSNotif> notifsList = new ArrayList<>();

    // panigation variables
    private int currentPage = 1;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private int PAGES_COUNT = 1;

    @BindString(R.string.loading_msg)
    String loadingMsg;
    private RSLoader rsLoader;

    private void createLoader() {
        rsLoader = RSLoader.newInstance(loadingMsg);
        rsLoader.setCancelable(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list_notif, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindViews();
        listNotifPresenter = new PresenterNotifImpl(ListNotifFragment.this);

        if (RSSession.isLoggedIn()) {
            rsRequestListItem.setUserId(RSSession.getCurrentUser().get_id());
            rsRequestListItem.setLang(RankStop.getDeviceLanguage());
            rsRequestListItem.setPerPage(RSConstants.MAX_FIELD_TO_LOAD);
            if (RSNetwork.isConnected(getContext())) {
                progressBar.setVisibility(View.VISIBLE);
                laodData(currentPage);
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
        RecyclerViewClickListener itemListener = (view, position) -> {
            if (RSNetwork.isConnected(getContext())) {
                if (notifsList.get(position).isVisibility()) {
                    listNotifPresenter.editNotifVisibility(notifsList.get(position).get_id(), notifsList.get(position).getItem().get_id(), getContext());
                } else {
                    fragmentActionListener.startFragment(ItemDetailsFragment.getInstance(notifsList.get(position).getItem().get_id()), RSConstants.FRAGMENT_ITEM_DETAILS);
                }
            } else {
                onOffLine();
            }
        };
        GridLayoutManager layoutManager = new GridLayoutManager(listNotifRV.getContext(), 1);
        notifAdapter = new NotifAdapter(itemListener);
        listNotifRV.setLayoutManager(layoutManager);
        listNotifRV.setAdapter(notifAdapter);
        listNotifRV.addItemDecoration(new VerticalSpace(marginCardView, countItemPerRow));
        // scroll listener
        EndlessScrollListener scrollListener = new EndlessScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                rsRequestListItem.setPage(currentPage);
                // mocking network delay for API call
                laodData(currentPage);
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
        listNotifRV.addOnScrollListener(scrollListener);
    }

    private void laodData(int pageNumber) {
        rsRequestListItem.setPage(pageNumber);
        listNotifPresenter.loadListNotif(rsRequestListItem, getContext());
    }

    private void bindViews() {
        toolbar.setTitle(notifTitle);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setFragmentActionListener((ContainerActivity) getActivity());
        createLoader();
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

    private static ListNotifFragment instance;

    public static ListNotifFragment getInstance() {
        if (instance == null) {
            instance = new ListNotifFragment();
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
        rootView = null;
        fragmentActionListener = null;
        if (unbinder != null)
            unbinder.unbind();
        if (listNotifPresenter != null)
            listNotifPresenter.onDestroy(getContext());
        super.onDestroyView();
    }

    @Override
    public void onSuccess(String target, Object data, String itemId) {
        RSResponseNotif notifResponse = null;
        if (!(data instanceof String)) {
            notifResponse = new Gson().fromJson(new Gson().toJson(data), RSResponseNotif.class);
        }
        switch (target) {
            case RSConstants.LIST_NOTIFS:
                notifsList.addAll(notifResponse.getNotification());
                if (notifResponse.getCurrent() == 1) {
                    notifAdapter.clear();
                    PAGES_COUNT = notifResponse.getPages();
                    progressBar.setVisibility(View.GONE);
                    if (notifResponse.getNotification().size() > 0)
                        listNotifRV.setVisibility(View.VISIBLE);
                    else
                        noNotifTV.setVisibility(View.VISIBLE);
                } else if (notifResponse.getCurrent() > 1) {
                    notifAdapter.removeLoadingFooter();
                    isLoading = false;
                }
                notifAdapter.addAll(notifResponse.getNotification());
                if (currentPage < PAGES_COUNT) {
                    notifAdapter.addLoadingFooter();
                    isLastPage = false;
                } else {
                    isLastPage = true;
                }
                break;
            case RSConstants.EDIT_NOTIF_VISIBILITY:
                fragmentActionListener.startFragment(ItemDetailsFragment.getInstance(itemId), RSConstants.FRAGMENT_ITEM_DETAILS);
                break;
        }
    }

    @Override
    public void onFailure(String target) {
    }

    @Override
    public void onError(String target) {

        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showProgressBar(String target) {
        switch (target) {
            case RSConstants.EDIT_NOTIF_VISIBILITY:
                rsLoader.show(getFragmentManager(), RSLoader.TAG);
                break;
        }
    }

    @Override
    public void hideProgressBar(String target) {
        switch (target) {
            case RSConstants.EDIT_NOTIF_VISIBILITY:
                rsLoader.dismiss();
                break;
        }
    }

    @Override
    public void onOffLine() {
        //Toast.makeText(getContext(), offlineMsg, Toast.LENGTH_LONG).show();
        new RSCustomToast(getActivity(), getResources().getString(R.string.error), offlineMsg, R.drawable.ic_error, RSCustomToast.ERROR).show();

    }

    private void navigateToSignUp(RSNavigationData rsNavigationData) {
        fragmentActionListener.startFragment(SignupFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_SIGN_UP);
    }
}
