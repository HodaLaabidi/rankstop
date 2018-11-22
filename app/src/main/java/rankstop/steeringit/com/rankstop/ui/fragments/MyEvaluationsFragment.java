package rankstop.steeringit.com.rankstop.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.List;

import rankstop.steeringit.com.rankstop.MVP.model.PresenterItemImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.Item;
import rankstop.steeringit.com.rankstop.data.model.User;
import rankstop.steeringit.com.rankstop.data.model.custom.RSFollow;
import rankstop.steeringit.com.rankstop.data.model.custom.RSRequestListItem;
import rankstop.steeringit.com.rankstop.data.model.custom.RSResponseListingItem;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.MyEvalsAdapter;
import rankstop.steeringit.com.rankstop.ui.adapter.PieAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.ItemPieListener;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.AskToLoginDialog;
import rankstop.steeringit.com.rankstop.utils.HorizontalSpace;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.VerticalSpace;

public class MyEvaluationsFragment extends Fragment implements RSView.StandardView {

    private Toolbar toolbar;
    private View rootView;
    private RecyclerView recyclerViewMyEvals;
    private LinearLayout layoutNoItem;
    private TextView nodataTV;
    private MaterialButton noDataBtn;
    private ProgressBar progressBar;

    private RSPresenter.ItemPresenter itemPresenter;
    private RSRequestListItem rsRequestListItem = new RSRequestListItem();
    private User user;
    private List<Item> listMyEvals;
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
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindViews();

        if (RSSession.isLoggedIn(getContext())) {
            user = RSSession.getCurrentUser(getContext());
            rsRequestListItem.setPage(1);
            rsRequestListItem.setPerPage(RSConstants.MAX_ITEM_TO_LOAD);
            rsRequestListItem.setUserId(user.get_id());
            //rsRequestListItem.setUserId("5be9a0d5a1a78a31781fffb3");
            loadMyEvals();
        } else {
            noDataBtn.setText(getResources().getString(R.string.login_btn));
            nodataTV.setText(getResources().getString(R.string.login_show_eval));
            layoutNoItem.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void loadMyEvals() {
        itemPresenter.loadMyEvals(rsRequestListItem);
    }

    private void initMyEvals(List<Item> listMyEvals) {
        recyclerViewMyEvals.setVisibility(View.VISIBLE);
        ItemPieListener listener = new ItemPieListener() {
            @Override
            public void onFollowChanged(boolean isFollow, int position) {
                manageFollow(listMyEvals.get(position).getItemDetails().get_id(), isFollow);
            }

            @Override
            public void onClick(View view, int position) {
                fragmentActionListener.startFragment(ItemDetailsFragment.getInstance(listMyEvals.get(position).getItemDetails().get_id()));
            }
        };
        recyclerViewMyEvals.setLayoutManager(new GridLayoutManager(recyclerViewMyEvals.getContext(), 1));
        recyclerViewMyEvals.setAdapter(new MyEvalsAdapter(listMyEvals, listener, getContext()));
        recyclerViewMyEvals.addItemDecoration(new VerticalSpace(getResources().getInteger(R.integer.m_card_view), 1));
        recyclerViewMyEvals.setNestedScrollingEnabled(false);
    }

    private void manageFollow(String itemId, boolean isFollow) {
        if (RSSession.isLoggedIn(getContext())) {
            RSFollow rsFollow = new RSFollow(user.get_id(), itemId);
            if (isFollow)
                itemPresenter.followItem(rsFollow);
            else
                itemPresenter.unfollowItem(rsFollow);
        } else {
            openAlertDialog(fragmentContext.get().getResources().getString(R.string.alert_login_to_follow));
        }
    }

    private void bindViews() {
        toolbar = rootView.findViewById(R.id.toolbar);

        recyclerViewMyEvals = rootView.findViewById(R.id.recycler_view_my_evals);
        layoutNoItem = rootView.findViewById(R.id.no_item);
        nodataTV = rootView.findViewById(R.id.tv_login_or_search);
        noDataBtn = rootView.findViewById(R.id.btn_login_or_search);
        progressBar = rootView.findViewById(R.id.progress_bar);

        toolbar.setTitle(getResources().getString(R.string.title_my_evals));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        itemPresenter = new PresenterItemImpl(MyEvaluationsFragment.this);
        setFragmentActionListener((ContainerActivity)getActivity());
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.setting:
                fragmentActionListener.startFragment(SettingsFragment.getInstance());
                break;
            case R.id.logout:
                /*RSSession.removeToken(getContext());
                ((ContainerActivity)getActivity()).manageSession(false);*/
                break;
            case R.id.history:
                fragmentActionListener.startFragment(HistoryFragment.getInstance());
                break;
            case R.id.contact:
                fragmentActionListener.startFragment(ContactFragment.getInstance());
                break;
            case R.id.notifications:
                fragmentActionListener.startFragment(ListNotifFragment.getInstance());
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
        instance = null;
        rootView = null;
        itemPresenter.onDestroyItem();
        fragmentActionListener = null;
        super.onDestroyView();
    }

    @Override
    public void onSuccess(String target, Object data) {
        switch (target) {
            case RSConstants.MY_EVALS:
                RSResponseListingItem listingItemResponse = new Gson().fromJson(new Gson().toJson(data), RSResponseListingItem.class);
                listMyEvals = listingItemResponse.getItems();
                if (listMyEvals.size() == 0) {
                    noDataBtn.setText(getResources().getString(R.string.search_items));
                    nodataTV.setText(getResources().getString(R.string.no_item_evaluated));
                    layoutNoItem.setVisibility(View.VISIBLE);
                } else {
                    initMyEvals(listMyEvals);
                }
                break;
        }
    }

    @Override
    public void onFailure(String target) {

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

    private void openAlertDialog(String message) {
        AskToLoginDialog dialog = AskToLoginDialog.newInstance(fragmentContext.get(), message);
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "");
    }
}
