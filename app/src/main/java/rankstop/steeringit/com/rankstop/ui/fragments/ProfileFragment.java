package rankstop.steeringit.com.rankstop.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterProfileImpl;
import rankstop.steeringit.com.rankstop.data.model.UserInfo;
import rankstop.steeringit.com.rankstop.data.model.custom.RSLocalStorage;
import rankstop.steeringit.com.rankstop.data.model.custom.RSRequestListItem;
import rankstop.steeringit.com.rankstop.data.model.custom.RSResponseListingItem;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.PieAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.data.model.Item;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.User;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.utils.HorizontalSpace;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;

public class ProfileFragment extends Fragment implements RSView.StandardView {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.avatar) SimpleDraweeView avatar;
    @BindView(R.id.tv_user_name) TextView userNameTV;

    @BindView(R.id.progress_bar_page_owned) ProgressBar progressBarOwnedItem;
    @BindView(R.id.progress_bar_page_created) ProgressBar progressBarCreatedItem;
    @BindView(R.id.progress_bar_page_followed) ProgressBar progressBarFollowedItem;

    @BindView(R.id.recycler_view_page_owned) RecyclerView recyclerViewOwnedItem;
    @BindView(R.id.recycler_view_page_created) RecyclerView recyclerViewCreatedItem;
    @BindView(R.id.recycler_view_page_followed) RecyclerView recyclerViewFollowedItem;

    @BindView(R.id.more_page_owned) MaterialButton moreOwnedBtn;
    @BindView(R.id.more_page_created) MaterialButton moreCreatedBtn;
    @BindView(R.id.more_page_followed) MaterialButton moreFollowedBtn;

    @BindView(R.id.layout_view_page_created) LinearLayout layoutItemCreated;
    @BindView(R.id.layout_view_page_owned) LinearLayout layoutItemOwned;
    @BindView(R.id.layout_view_page_followed) LinearLayout layoutItemFollowed;

    @BindView(R.id.tv_evals_number) TextView evalsNumberTV;
    @BindView(R.id.tv_comments_number) TextView commentsNumberTV;
    @BindView(R.id.tv_pix_number) TextView pixNumberTV;

    @OnClick({R.id.more_page_created, R.id.more_page_owned, R.id.more_page_followed})
    public void manageBtn(MaterialButton v) {
        switch (v.getId()){
            case R.id.more_page_created:
                fragmentActionListener.startFragment(ItemCreatedFragment.getInstance());
                break;
            case R.id.more_page_owned:
                fragmentActionListener.startFragment(ItemOwnedFragment.getInstance());
                break;
            case R.id.more_page_followed:
                fragmentActionListener.startFragment(ItemFollowedFragment.getInstance());
                break;
        }
    }

    private Unbinder unbinder;

    private View rootView;

    private UserInfo userInfo;
    private RSRequestListItem rsRequestListItem = new RSRequestListItem();
    private List<Item> listOwnedItem, listFollowedItem, listCreatedItem;

    private RSPresenter.ProfilePresenter profilePresenter;

    RSResponseListingItem listingItemResponse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bindViews();

    }

    private void bindViews() {

        toolbar.setTitle("Profile");

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        profilePresenter = new PresenterProfileImpl( ProfileFragment.this);

        // init data
        userInfo = RSSession.getCurrentUserInfo(getContext());
        rsRequestListItem.setPage(1);
        rsRequestListItem.setPerPage(RSConstants.MAX_ITEM_TO_LOAD);
        rsRequestListItem.setUserId(userInfo.getUser().get_id());
        bindLocalData();
        loadData();
    }

    private void bindLocalData() {
        setUserPic(userInfo.getUser().getPictureProfile());
        setUserName(userInfo.getUser().getFacebook().getName());
        setEvalsNumber(userInfo.getCountEval());
        setCommentsNumber(userInfo.getCountComments());
        setPixNumber(userInfo.getCountPictures());
    }

    private void setUserName(String username) {
        userNameTV.setText(username);
    }

    private void setUserPic(String picture) {
        Uri imageUri = Uri.parse(picture);
        avatar.setImageURI(imageUri);
    }

    private void loadData() {
        //set avatar and cover
        loadProfileData();
        loadOwnedItem();
        loadCreatedItem();
        loadFollowedItem();
    }

    private void loadProfileData() {
        profilePresenter.loadUserInfo(userInfo.getUser().get_id());
    }

    private void loadOwnedItem() {
        profilePresenter.loadItemOwned(rsRequestListItem);
    }

    private void loadCreatedItem() {
        profilePresenter.loadItemCreated(rsRequestListItem);
    }

    private void loadFollowedItem() {
        profilePresenter.loadItemFollowed(rsRequestListItem);
    }

    private void initOwnedItem(List<Item> listOwnedItem) {
        recyclerViewOwnedItem.setVisibility(View.VISIBLE);
        RecyclerViewClickListener listener = (view, position) -> {
            fragmentActionListener.startFragment(ItemDetailsFragment.getInstance());
        };
        recyclerViewOwnedItem.setLayoutManager(new LinearLayoutManager(recyclerViewOwnedItem.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewOwnedItem.setAdapter(new PieAdapter(listOwnedItem, listener, getContext()));
        recyclerViewOwnedItem.addItemDecoration(new HorizontalSpace(getResources().getInteger(R.integer.m_card_view)));
        recyclerViewOwnedItem.setNestedScrollingEnabled(false);
    }

    private void initCreatedItem(List<Item> listCreatedItem) {
        recyclerViewCreatedItem.setVisibility(View.VISIBLE);
        RecyclerViewClickListener listener = (view, position) -> {
            fragmentActionListener.startFragment(ItemDetailsFragment.getInstance());
        };
        recyclerViewCreatedItem.setLayoutManager(new LinearLayoutManager(recyclerViewCreatedItem.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCreatedItem.setAdapter(new PieAdapter(listCreatedItem, listener, getContext()));
        recyclerViewCreatedItem.addItemDecoration(new HorizontalSpace(getResources().getInteger(R.integer.m_card_view)));
        recyclerViewCreatedItem.setNestedScrollingEnabled(false);
    }

    private void initFollowedItem(List<Item> listFollowedItem) {
        recyclerViewFollowedItem.setVisibility(View.VISIBLE);
        RecyclerViewClickListener listener = (view, position) -> {
            fragmentActionListener.startFragment(ItemDetailsFragment.getInstance());
        };
        recyclerViewFollowedItem.setLayoutManager(new LinearLayoutManager(recyclerViewFollowedItem.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewFollowedItem.setAdapter(new PieAdapter(listFollowedItem, listener, getContext()));
        recyclerViewFollowedItem.addItemDecoration(new HorizontalSpace(getResources().getInteger(R.integer.m_card_view)));
        recyclerViewFollowedItem.setNestedScrollingEnabled(false);
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
                RSSession.cancelSession(getContext());
                ((ContainerActivity)getActivity()).manageSession(false);
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
    private static ProfileFragment instance;

    public static ProfileFragment getInstance() {
        if (instance == null) {
            instance = new ProfileFragment();
        }
        return instance;
    }

    @Override
    public void onDestroyView() {
        instance = null;
        rootView=null;
        fragmentActionListener = null;
        recyclerViewOwnedItem = null;
        recyclerViewCreatedItem = null;
        recyclerViewFollowedItem = null;
        if (listOwnedItem != null)
            listOwnedItem.clear();
        if (listFollowedItem != null)
            listFollowedItem.clear();
        if (listCreatedItem != null)
            listCreatedItem.clear();

        profilePresenter.onDestroy();
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onSuccess(String target, Object data) {

        switch (target){
            case RSConstants.ITEM_CREATED:
                listingItemResponse = new Gson().fromJson(new Gson().toJson(data), RSResponseListingItem.class);
                listCreatedItem = listingItemResponse.getItems();
                if (listCreatedItem.size() == 0) {
                    layoutItemCreated.setVisibility(View.VISIBLE);
                }else{
                    initCreatedItem(listCreatedItem);
                    if (listingItemResponse.getPages() > 1)
                        moreCreatedBtn.setVisibility(View.VISIBLE);
                    else
                        moreCreatedBtn.setVisibility(View.GONE);
                }
                break;
            case RSConstants.ITEM_OWNED:
                listingItemResponse = new Gson().fromJson(new Gson().toJson(data), RSResponseListingItem.class);
                listOwnedItem = listingItemResponse.getItems();
                if (listOwnedItem.size() == 0) {
                    layoutItemOwned.setVisibility(View.VISIBLE);
                }else {
                    initOwnedItem(listOwnedItem);
                    if (listingItemResponse.getPages() > 1)
                        moreOwnedBtn.setVisibility(View.VISIBLE);
                    else
                        moreOwnedBtn.setVisibility(View.GONE);
                }
                break;
            case RSConstants.ITEM_FOLLOWED:
                listingItemResponse = new Gson().fromJson(new Gson().toJson(data), RSResponseListingItem.class);
                listFollowedItem = listingItemResponse.getItems();
                if (listFollowedItem.size() == 0) {
                    layoutItemFollowed.setVisibility(View.VISIBLE);
                }else {
                    initFollowedItem(listFollowedItem);
                    if (listingItemResponse.getPages() > 1)
                        moreFollowedBtn.setVisibility(View.VISIBLE);
                    else
                        moreFollowedBtn.setVisibility(View.GONE);
                }
                break;
            case RSConstants.USER_INFO:
                UserInfo userInfo = new Gson().fromJson(new Gson().toJson(data), UserInfo.class);
                if (this.userInfo.getCountEval() != userInfo.getCountEval()) {
                    setEvalsNumber(userInfo.getCountEval());
                }
                if (this.userInfo.getCountComments() != userInfo.getCountComments()) {
                    setCommentsNumber(userInfo.getCountComments());
                }
                if (this.userInfo.getCountPictures() != userInfo.getCountPictures()) {
                    setPixNumber(userInfo.getCountPictures());
                }
                if (!this.userInfo.getUser().getPictureProfile().equals(userInfo.getUser().getPictureProfile())){
                    setUserPic(userInfo.getUser().getPictureProfile());
                }
                if (!this.userInfo.getUser().getUsername().equals(userInfo.getUser().getUsername())){
                    setUserName(userInfo.getUser().getUsername());
                }
                this.userInfo = userInfo;
                RSSession.refreshLocalStorage(userInfo, getContext());
                break;
        }
    }

    private void setEvalsNumber(int value) {
        evalsNumberTV.setText(String.valueOf(value));
    }

    private void setCommentsNumber(int value) {
        commentsNumberTV.setText(String.valueOf(value));
    }

    private void setPixNumber(int value) {
        pixNumberTV.setText(String.valueOf(value));
    }

    @Override
    public void onFailure(String target) {
        switch (target){
            case RSConstants.ITEM_CREATED:
                recyclerViewCreatedItem.setVisibility(View.GONE);
                layoutItemCreated.setVisibility(View.VISIBLE);
                moreCreatedBtn.setVisibility(View.GONE);
                break;
            case RSConstants.ITEM_OWNED:
                recyclerViewOwnedItem.setVisibility(View.GONE);
                layoutItemOwned.setVisibility(View.VISIBLE);
                moreOwnedBtn.setVisibility(View.GONE);
                break;
            case RSConstants.ITEM_FOLLOWED:
                recyclerViewFollowedItem.setVisibility(View.GONE);
                layoutItemFollowed.setVisibility(View.VISIBLE);
                moreFollowedBtn.setVisibility(View.GONE);
                break;
            case RSConstants.USER_INFO:
                //evalsNumberTV.setText("");
                //commentsNumberTV.setText("");
                //pixNumberTV.setText("");
                break;
        }
    }

    @Override
    public void showProgressBar(String target) {
        switch (target){
            case RSConstants.ITEM_CREATED:
                progressBarCreatedItem.setVisibility(View.VISIBLE);
                break;
            case RSConstants.ITEM_FOLLOWED:
                progressBarFollowedItem.setVisibility(View.VISIBLE);
                break;
            case RSConstants.ITEM_OWNED:
                progressBarOwnedItem.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void hideProgressBar(String target) {
        switch (target){
            case RSConstants.ITEM_CREATED:
                progressBarCreatedItem.setVisibility(View.GONE);
                break;
            case RSConstants.ITEM_FOLLOWED:
                progressBarFollowedItem.setVisibility(View.GONE);
                break;
            case RSConstants.ITEM_OWNED:
                progressBarOwnedItem.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void showMessage(String target, String message) {

    }














    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
