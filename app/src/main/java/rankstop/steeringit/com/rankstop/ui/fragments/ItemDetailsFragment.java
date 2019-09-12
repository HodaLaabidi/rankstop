package com.steeringit.rankstop.ui.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
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
import com.steeringit.rankstop.customviews.RSBTNMedium;
import com.steeringit.rankstop.customviews.RSCustomToast;
import com.steeringit.rankstop.customviews.RSTVBold;
import com.steeringit.rankstop.customviews.RSTVMedium;
import com.steeringit.rankstop.data.model.db.Category;
import com.steeringit.rankstop.data.model.db.Gallery;
import com.steeringit.rankstop.data.model.db.Item;
import com.steeringit.rankstop.data.model.db.ItemDetails;
import com.steeringit.rankstop.data.model.db.User;
import com.steeringit.rankstop.data.model.network.RSAddReview;
import com.steeringit.rankstop.data.model.network.RSFollow;
import com.steeringit.rankstop.data.model.network.RSNavigationData;
import com.steeringit.rankstop.session.RSSession;
import com.steeringit.rankstop.ui.activities.ContainerActivity;
import com.steeringit.rankstop.ui.activities.ItemGalleryActivity;
import com.steeringit.rankstop.ui.adapter.GalleryAdapter;
import com.steeringit.rankstop.ui.adapter.ViewPagerAdapter;
import com.steeringit.rankstop.ui.callbacks.FragmentActionListener;
import com.steeringit.rankstop.ui.callbacks.RecyclerViewClickListener;
import com.steeringit.rankstop.ui.dialogFragment.AskToLoginDialog;
import com.steeringit.rankstop.ui.dialogFragment.ContactDialog;
import com.steeringit.rankstop.ui.dialogFragment.ItemInfoDialog;
import com.steeringit.rankstop.ui.dialogFragment.ReportAbuseDialog;
import com.steeringit.rankstop.ui.dialogFragment.RequestOwnerShipDialog;
import com.steeringit.rankstop.utils.HorizontalSpace;
import com.steeringit.rankstop.utils.RSConstants;
import com.steeringit.rankstop.utils.RSNetwork;

public class ItemDetailsFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener, RSView.StandardView ,  RSView.StandardView2{

    private View rootView;
    @BindView(R.id.pie_chart)
    PieChart pieChart;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;

    @BindView(R.id.tv_item_description)
    RSTVMedium itemDescriptionTV;

    @BindView(R.id.tv_item_name)
    RSTVBold itemNameTV;

    @BindView(R.id.tv_item_category)
    RSTVBold itemCategoryTV;

    @BindView(R.id.tv_ownership)
    RSTVBold ownerShipTV;

    @BindView(R.id.recycler_view_gallery)
    RecyclerView recyclerViewGallery;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @BindView(R.id.btn_add_review)
    RSBTNMedium addReviewBtn;

    @BindView(R.id.btn_add_pix)
    RSBTNMedium addItemPixBtn;

    @BindView(R.id.btn_report_abuse)
    RSBTNMedium reportAbuseBTN;

    @BindView(R.id.header_content)
    RelativeLayout headerContent;

    @BindString(R.string.ownership_open)
    String ownershipOpen;

    @BindString(R.string.ownership_official)
    String ownershipOfficial;

    @BindString(R.string.ownership_pending)
    String ownershipPending;

    @BindString(R.string.score_of_5)
    String scoreOf5;

    @BindString(R.string.alert_login_to_follow)
    String alertLoginToFollowMsg;
    @BindString(R.string.alert_login_to_add_review)
    String alertLoginToAddReviewMsg;
    @BindString(R.string.alert_login_to_report_abuse)
    String alertLoginToReportAbuseMsg;
    @BindString(R.string.alert_login_to_send_req_ownership)
    String alertLoginToSendReqOwnershipMsg;
    @BindString(R.string.already_followed)
    String alreadyFollowedMsg;
    @BindString(R.string.update_item)
    String updateItemPicsMsg;
    @BindString(R.string.evals_title)
    String evalsTitleMsg;
    @BindString(R.string.comments_title)
    String commentsTitleMsg;
    @BindString(R.string.pics_title)
    String picsTitleMsg;
    @BindString(R.string.follow)
    String followMsg;
    @BindString(R.string.already_signaled_msg)
    String alreadySignaledMsg;

    @BindColor(R.color.colorPrimary)
    int primaryColor;
    @BindColor(R.color.colorWhite)
    int whiteColor;

    @OnClick(R.id.btn_add_review)
    void addReview() {
        if (RSNetwork.isConnected(getContext())) {
            if (isLoggedIn) {
                RSAddReview rsAddReview = new RSAddReview();
                rsAddReview.setItemId(item.getItemDetails().get_id());
                rsAddReview.setCategoryId(currentCategory.get_id());
                fragmentActionListener.startFragment(AddReviewFragment.getInstance(rsAddReview, item.getLastEvalUser(), "", RSConstants.ACTION_EVAL), RSConstants.FRAGMENT_ADD_REVIEW);
            } else {
                RSNavigationData rsNavigationData = new RSNavigationData(RSConstants.FRAGMENT_ADD_REVIEW, RSConstants.ACTION_ADD_REVIEW, alertLoginToAddReviewMsg, itemId, "", currentCategory.get_id(), RSConstants.ACTION_EVAL);
                askToLoginDialog(rsNavigationData);
            }
        } else {
            onOffLine();
        }
    }

    @OnClick(R.id.tv_about_item)
    void aboutItem() {
        showItemInfo(item);
    }

    @OnClick(R.id.btn_report_abuse)
    void reportAbuse() {
        if (RSNetwork.isConnected(getContext())) {
            if (isLoggedIn) {
                RSNavigationData rsNavigationData = new RSNavigationData(RSConstants.FRAGMENT_ITEM_DETAILS, RSConstants.ACTION_REPORT_ABUSE, "", itemId, "", "", "");
                openAbusesDialog(rsNavigationData);
            } else {
                RSNavigationData rsNavigationData = new RSNavigationData(RSConstants.FRAGMENT_ITEM_DETAILS, RSConstants.ACTION_REPORT_ABUSE, alertLoginToReportAbuseMsg, itemId, "", currentCategory.get_id(), "");
                askToLoginDialog(rsNavigationData);
            }
        } else {
            onOffLine();
        }
    }

    @OnClick(R.id.btn_add_pix)
    void addPix() {
        fragmentActionListener.startFragment(UpdateItemFragment.getInstance(item.getItemDetails()), RSConstants.FRAGMENT_UPDATE_ITEM);
    }

    @OnClick(R.id.tv_item_category)
    void goToSearch() {
        if (RSNetwork.isConnected(getContext())) {
            fragmentActionListener.startFragment(SearchFragment.getInstance(currentCategory), RSConstants.FRAGMENT_SEARCH);
        } else {
            onOffLine();
        }
    }

    @OnClick(R.id.tv_ownership)
    void askForOwnerShip() {
        if (isLoggedIn) {
            Bundle bundle = new Bundle();
            bundle.putString(RSConstants.ITEM_ID, itemId);
            bundle.putString(RSConstants.ITEM_NAME, item.getItemDetails().getTitle());
            openOwnershipDialog(bundle);
        } else {
            if (RSNetwork.isConnected(getContext())) {
                RSNavigationData rsNavigationData = new RSNavigationData(RSConstants.FRAGMENT_ITEM_DETAILS, RSConstants.ACTION_SEND_REQ_OWNERSHIP, alertLoginToSendReqOwnershipMsg, itemId, "", "", "");
                askToLoginDialog(rsNavigationData);
            } else {
                onOffLine();
            }
        }
    }

    @BindString(R.string.my_eval_value)
    String myEvalValue;
    @BindString(R.string.off_line)
    String offlineMsg;

    private Unbinder unbinder;

    private MenuItem menuItem;

    private PorterDuffColorFilter lightColorFilter, darkColorFilter;

    private List<MenuItem> listMenuItem;
    private RSNavigationData rsNavigationData;

    private boolean isTransparentBg = true;

    private boolean isFavorite = false;
    private Item item;
    private String itemId;
    private int currentColor;
    private boolean isPieEmpty = false;

    private boolean isLoggedIn = RSSession.isLoggedIn();
    private User currentUser;
    private Category currentCategory;

    private RSPresenter.ItemPresenter itemPresenter;

    private WeakReference<ItemDetailsFragment> fragmentContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentContext = new WeakReference<>(this);
        rootView = inflater.inflate(R.layout.fragment_item_details, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        itemPresenter = new PresenterItemImpl(ItemDetailsFragment.this , ItemDetailsFragment.this);
        bindViews();
        if (isLoggedIn)
            currentUser = RSSession.getCurrentUser();
        loadItemData();

    }

    private void bindViews() {
        itemCategoryTV.setPaintFlags(itemCategoryTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        setFragmentActionListener((ContainerActivity) getActivity());
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("");
        appBarLayout.addOnOffsetChangedListener(this);
    }

    private void loadItemData() {
        String userId = "";
        if (isLoggedIn) {
            userId = currentUser.get_id();
        }
        itemId = getArguments().getString(RSConstants._ID);
        if (itemId == null) {
            rsNavigationData = (RSNavigationData) getArguments().getSerializable(RSConstants.NAVIGATION_DATA);
            itemId = rsNavigationData.getItemId();
        }

        itemPresenter.loadItem(itemId, userId, RankStop.getDeviceLanguage(), getContext());
    }

    private void initGallery(List<Gallery> listGalleryPics) {
        recyclerViewGallery.setVisibility(View.VISIBLE);
        RecyclerViewClickListener listener = (view, position) -> {
            //Toast.makeText(getContext(), "Position " + position, Toast.LENGTH_SHORT).show();
            startActivity(
                    new Intent(getContext(), ItemGalleryActivity.class)
                            .putExtra(RSConstants.PICTURES, (Serializable) listGalleryPics)
                            .putExtra(RSConstants.POSITION, position));
        };
        recyclerViewGallery.setLayoutManager(new LinearLayoutManager(recyclerViewGallery.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewGallery.setAdapter(new GalleryAdapter(listGalleryPics, listener));
        recyclerViewGallery.addItemDecoration(new HorizontalSpace(10));
    }

    private void initPieChart(Item item) {
        int[] tabColor;
        // values of the pie
        ArrayList<PieEntry> pieEntry = new ArrayList<>();
        if (item.getGood() == 0 && item.getNeutral() == 0 && item.getBad() == 0) {
            isPieEmpty = true;
            pieEntry.add(new PieEntry(1, ""));
            tabColor = new int[]{R.color.colorLightGray};
        } else {
            List<Integer> intList = new ArrayList<Integer>();
            if (item.getGood() > 0) {
                pieEntry.add(new PieEntry(item.getGood(), ""));
                intList.add(R.color.colorGreenPie);
            }
            if (item.getNeutral() > 0) {
                pieEntry.add(new PieEntry(item.getNeutral(), ""));
                intList.add(R.color.colorOrangePie);
            }
            if (item.getBad() > 0) {
                pieEntry.add(new PieEntry(item.getBad(), ""));
                intList.add(R.color.colorRedPie);
            }
            tabColor = new int[intList.size()];
            for (int i =0; i < intList.size(); i++) {
                tabColor[i] = intList.get(i);
            }
        }

        //Log.i("goode",item.getGood()+"");
        item.getItemDetails().setGood(item.getGood());
        item.getItemDetails().setBad(item.getBad());
        item.getItemDetails().setNeutral(item.getNeutral());

        pieChart.setUsePercentValues(true);
        pieChart.setCenterTextSize(14f);
        SpannableString spannablecontent = new SpannableString(item.getScoreItem() + scoreOf5);
        spannablecontent.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, item.getScoreItem().length(), 0);
        spannablecontent.setSpan(new RelativeSizeSpan(2f), 0, item.getScoreItem().length(), 0);
        pieChart.setCenterText(spannablecontent);
        pieChart.setCenterTextColor(primaryColor);

        // disable description of the pie
        pieChart.getDescription().setEnabled(false);
        // margin of the pie
        // pieChart.setExtraOffsets(5, 10, 5, 5);
        // disable/ enable rotation of the pie
        pieChart.setRotationEnabled(false);
        // define speed of rotation
        //pieChart.setDragDecelerationFrictionCoef(0.95f);
        // define the hole radius of the pie
        pieChart.setHoleRadius(65f);
        // disable/ enable the hole of the pie
        pieChart.setDrawHoleEnabled(true);
        // set hole color of the pie
        pieChart.setHoleColor(Color.TRANSPARENT);
        //pieChart.setTransparentCircleRadius(60f);
        // animate pie
        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);
        // disable/ enable legend on the pieChart
        pieChart.getLegend().setEnabled(false);
        // initialize PieDataSet
        PieDataSet dataSet = new PieDataSet(pieEntry, "Item");
        // distance between pie slices
        dataSet.setSliceSpace(3f);
        // scale when select a pie slice
        dataSet.setSelectionShift(5f);
        // colors of the pie slices
        dataSet.setColors(tabColor, getContext());
        // initialize PieData
        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE);
        // disable/ enable values on the pieChart
        dataSet.setDrawValues(!isPieEmpty);
        data.setDrawValues(false);
        // affect data to pieChart
        pieChart.setData(data);
        // add listener of value selection
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //Toast.makeText(getContext(), "" + e.getY(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.item_details_menu, menu);
        menuItem = menu.findItem(R.id.action_favorite);

        MenuItem item = menu.findItem(R.id.logout);
        item.setVisible(false);
        initToolbarStyle();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;

            case R.id.logout:
                RSSession.cancelSession();
                ((ContainerActivity) getActivity()).manageSession(false, new RSNavigationData(RSConstants.FRAGMENT_SIGN_UP, ""));
                break;
            case R.id.setting:
                fragmentActionListener.startFragment(SettingsFragment.getInstance(), RSConstants.FRAGMENT_SETTINGS);
                break;
            case R.id.history:
                fragmentActionListener.startFragment(HistoryFragment.getInstance(), RSConstants.FRAGMENT_HISTORY);
                break;
            case R.id.contact:
                openContactDialog();
                break;
            case R.id.action_favorite:
                manageFollow(itemId, !isFavorite);
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

    private void openContactDialog() {
        ContactDialog dialog = new ContactDialog();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.show(ft, ContactDialog.TAG);
    }

    private void initToolbarStyle() {
        lightColorFilter = new PorterDuffColorFilter(whiteColor, PorterDuff.Mode.SRC_ATOP);
        darkColorFilter = new PorterDuffColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);

        listMenuItem = new ArrayList<>();
        MenuItem menuItem;
        for (int i = 0; i < toolbar.getMenu().size(); i++) {
            menuItem = toolbar.getMenu().getItem(i);
            listMenuItem.add(menuItem);
            tintMenuIcon(menuItem, R.color.colorPrimary);
        }
    }

    private void manageBtnLike(boolean isFavorite, int color) {
        if (isFavorite) {
            menuItem.setIcon(R.drawable.ic_favorite_24dp);
            tintMenuIcon(menuItem, color);
        } else {
            menuItem.setIcon(R.drawable.ic_favorite_border);
            tintMenuIcon(menuItem, color);
        }
        this.isFavorite = isFavorite;
    }

    private void manageFollow(String itemId, boolean isFollow) {
        if (isTransparentBg)
            currentColor = R.color.colorPrimary;
        else
            currentColor = android.R.color.white;

        if (isLoggedIn) {
            RSFollow rsFollow = new RSFollow(currentUser.get_id(), itemId);
            if (isFollow)
                itemPresenter.followItem(rsFollow, getContext());
            else
                itemPresenter.unfollowItem(rsFollow, getContext());
            isFavorite = isFollow;
        } else {
            RSNavigationData rsNavigationData = new RSNavigationData(RSConstants.FRAGMENT_ADD_REVIEW, RSConstants.ACTION_FOLLOW, alertLoginToFollowMsg, itemId, "", "", "");
            askToLoginDialog(rsNavigationData);
        }
    }

    private void askToLoginDialog(RSNavigationData rsNavigationData) {
        AskToLoginDialog dialog = AskToLoginDialog.newInstance(rsNavigationData);
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "");
    }

    private void showItemInfo(Item item) {
        ItemInfoDialog dialog = ItemInfoDialog.newInstance(item);
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "");
    }

    private void openAbusesDialog(RSNavigationData rsNavigationData) {
        ReportAbuseDialog dialog = ReportAbuseDialog.newInstance(rsNavigationData);
        dialog.show(getFragmentManager(), "");
    }

    private void tintMenuIcon(MenuItem item, @ColorRes int color) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Drawable wrapDrawable = DrawableCompat.wrap(item.getIcon());
            int couleur = ContextCompat.getColor(getContext(), color);
            if (wrapDrawable != null) {
                DrawableCompat.setTint(wrapDrawable, couleur);
                item.setIcon(wrapDrawable);
            }
        }else {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            Drawable wrapDrawable = DrawableCompat.wrap(item.getIcon());
            int couleur = getResources().getColor(color);
            /*if (wrapDrawable != null) {
                //DrawableCompat.setTint(wrapDrawable, couleur);
                wrapDrawable.setColorFilter(couleur, PorterDuff.Mode.DARKEN);
                item.setIcon(wrapDrawable);
            }*/
            if (item.getIcon() != null){
                DrawableCompat.setTint(item.getIcon(), couleur);
            }
        }
    }

    int maxScroll;
    float percentage;

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        maxScroll = appBarLayout.getTotalScrollRange();
        percentage = (float) Math.abs(i) / (float) maxScroll;
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (isTransparentBg) {
                toolbar.setTitle(item.getItemDetails().getTitle());
                isTransparentBg = false;
                for (int i = 0; i < listMenuItem.size(); i++) {
                    tintMenuIcon(listMenuItem.get(i), android.R.color.white);
                }
                toolbar.getNavigationIcon().setColorFilter(lightColorFilter);
                toolbar.getOverflowIcon().setColorFilter(lightColorFilter);
                toolbar.setTitleTextColor(whiteColor);
            }
        } else {
            if (!isTransparentBg) {
                toolbar.setTitle("");
                isTransparentBg = true;
                for (int i = 0; i < listMenuItem.size(); i++) {
                    tintMenuIcon(listMenuItem.get(i), R.color.colorPrimary);
                }
                toolbar.getNavigationIcon().setColorFilter(darkColorFilter);
                toolbar.getOverflowIcon().setColorFilter(darkColorFilter);
            }
        }
    }

    @Override
    public void onDestroyView() {
        instance = null;
        rootView = null;
        lightColorFilter = null;
        darkColorFilter = null;
        if (pieChart != null)
            pieChart.clear();
        fragmentActionListener = null;
        if (unbinder != null)
            unbinder.unbind();
        if (itemPresenter != null)
            itemPresenter.onDestroyItem(getContext());
        if (listMenuItem != null) {
            for (int i = 0; i < listMenuItem.size(); i++) {
                tintMenuIcon(listMenuItem.get(i), android.R.color.white);

            }
        }
        super.onDestroyView();
    }

    private FragmentActionListener fragmentActionListener;

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    private static ItemDetailsFragment instance;

    public static ItemDetailsFragment getInstance(String id) {
        Bundle args = new Bundle();
        args.putString(RSConstants._ID, id);
        if (instance == null) {
            instance = new ItemDetailsFragment();
        }
        instance.setArguments(args);
        return instance;
    }

    public static ItemDetailsFragment getInstance(RSNavigationData rsNavigationData) {
        Bundle args = new Bundle();
        args.putSerializable(RSConstants.NAVIGATION_DATA, rsNavigationData);
        if (instance == null) {
            instance = new ItemDetailsFragment();
        }
        instance.setArguments(args);
        return instance;
    }

    private void bindData(Item item) {

        // manage btn report abuse
        if (!item.getReportAbuse()) {
            reportAbuseBTN.setVisibility(View.VISIBLE);
        }
        // manage color and state of btn like
        manageBtnLike(item.isFollow(), R.color.colorPrimary);
        try {
            if (rsNavigationData != null) {
                if (rsNavigationData.getAction().equals(RSConstants.ACTION_FOLLOW)) {
                    if (item.isFollow()) {
                        Toast.makeText(getContext(), alreadyFollowedMsg, Toast.LENGTH_SHORT).show();
                    } else {
                        //manageFollow(itemId, true);
                    }
                } else if (rsNavigationData.getAction().equals(RSConstants.ACTION_REPORT_ABUSE)) {
                    if (!item.getReportAbuse()) {
                        RSNavigationData rsNavigationData = new RSNavigationData(RSConstants.FRAGMENT_ITEM_DETAILS, RSConstants.ACTION_REPORT_ABUSE, "", itemId, "", "", "");
                        openAbusesDialog(rsNavigationData);
                    } else {
                        Toast.makeText(getContext(), alreadySignaledMsg, Toast.LENGTH_SHORT).show();
                    }
                } else if (rsNavigationData.getAction().equals(RSConstants.ACTION_SEND_REQ_OWNERSHIP)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(RSConstants.ITEM_ID, itemId);
                    bundle.putString(RSConstants.ITEM_NAME, item.getItemDetails().getTitle());
                    openOwnershipDialog(bundle);

                    /*if (!item.isUserSendReqOwnership()) {
                        Bundle bundle = new Bundle();
                        bundle.putString(RSConstants.ITEM_ID, itemId);
                        bundle.putString(RSConstants.ITEM_NAME, item.getItemDetails().getTitle());
                        openOwnershipDialog(bundle);
                    } else {
                        Toast.makeText(getContext(), "Vous avez déjà envoyé une requête", Toast.LENGTH_SHORT).show();
                    }*/
                }
            }
        } catch (Exception e) {
        }
        itemNameTV.setText(item.getItemDetails().getTitle());
        itemCategoryTV.setText(currentCategory.getName());
        itemDescriptionTV.setText(item.getItemDetails().getDescription());
        initPieChart(item);
        List<Gallery> listGalleryPics = new ArrayList<>();
        listGalleryPics = item.getItemDetails().getGallery();
        String ownershipStatus = ownershipOpen;
        if (item.getItemDetails().getOwner() != null) {
            ownershipStatus = ownershipOfficial;
        }

        if (isLoggedIn) {
            if (listGalleryPics.size() > 0) {
                initGallery(listGalleryPics);
                addItemPixBtn.setText(updateItemPicsMsg);
            }
            if (item.getItemDetails().getOwner() == null) {
                if (item.getItemDetails() != null){
                   if (item.getItemDetails().getCreator() != null){
                       if (((User) item.getItemDetails().getCreator()).get_id() != null){
                           if (((User) item.getItemDetails().getCreator()).get_id().equals(currentUser.get_id())) {
                               addItemPixBtn.setVisibility(View.VISIBLE);
                           }
                       }
                   }
                }


                // isUserSendRequestOwnership
                /*if (item.isUserSendReqOwnership)
                    ownershipStatus= ownershipPending;*/
            } else {
                if (((User) item.getItemDetails().getOwner()).get_id().equals(currentUser.get_id())) {
                    addItemPixBtn.setVisibility(View.VISIBLE);
                }
            }
            if (item.getLastEvalUser() != null)
                if (item.getLastEvalUser().get_id() != null)
                    addReviewBtn.setText(myEvalValue);
            addReviewBtn.setVisibility(View.VISIBLE);
        } else {
            if (listGalleryPics.size() > 0) {
                initGallery(listGalleryPics);
            }
        }
        ownerShipTV.setText(ownershipStatus);
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        mViewPagerAdapter.addFragment(ItemEvalsFragment.getInstance(item.getTabCritereDetails()), evalsTitleMsg);
        mViewPagerAdapter.addFragment(ItemCommentsFragment.getInstance(item), commentsTitleMsg);
        mViewPagerAdapter.addFragment(ItemPicsFragment.getInstance(item), picsTitleMsg);
        mViewPager.setAdapter(mViewPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onSuccess(String target, Object data) {
        switch (target) {
            case RSConstants.ONE_ITEM:
                item = new Gson().fromJson(new Gson().toJson(data), Item.class);
                currentCategory = new Gson().fromJson(new Gson().toJson(item.getItemDetails().getCategory()), Category.class);
                bindData(item);
                break;
            case RSConstants.FOLLOW_ITEM:
                if (data.equals("1")) {
                    Toast.makeText(getContext(), followMsg, Toast.LENGTH_SHORT).show();
                } else if (data.equals("0")) {
                    Toast.makeText(getContext(), alreadyFollowedMsg, Toast.LENGTH_SHORT).show();
                }
                manageBtnLike(isFavorite, currentColor);
                break;
            case RSConstants.UNFOLLOW_ITEM:
                manageBtnLike(isFavorite, currentColor);
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
        switch (target) {
            case RSConstants.ONE_ITEM:
                progressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void hideProgressBar(String target) {
        switch (target) {
            case RSConstants.ONE_ITEM:
                progressBar.setVisibility(View.GONE);
                headerContent.setVisibility(View.VISIBLE);
                break;
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

    private void openOwnershipDialog(Bundle bundle) {
        RequestOwnerShipDialog dialog = new RequestOwnerShipDialog();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.setArguments(bundle);
        dialog.show(ft, RequestOwnerShipDialog.TAG);
    }

    @Override
    public void onSuccessRefreshItem(String target, String itemId, String message, Object data) {

    }
}
