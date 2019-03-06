package rankstop.steeringit.com.rankstop.ui.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
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
import rankstop.steeringit.com.rankstop.MVP.model.PresenterItemImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.RankStop;
import rankstop.steeringit.com.rankstop.customviews.RSBTNMedium;
import rankstop.steeringit.com.rankstop.customviews.RSTVBold;
import rankstop.steeringit.com.rankstop.customviews.RSTVMedium;
import rankstop.steeringit.com.rankstop.data.model.db.Category;
import rankstop.steeringit.com.rankstop.data.model.db.Gallery;
import rankstop.steeringit.com.rankstop.data.model.db.Item;
import rankstop.steeringit.com.rankstop.data.model.db.ItemDetails;
import rankstop.steeringit.com.rankstop.data.model.db.User;
import rankstop.steeringit.com.rankstop.data.model.network.RSAddReview;
import rankstop.steeringit.com.rankstop.data.model.network.RSFollow;
import rankstop.steeringit.com.rankstop.data.model.network.RSNavigationData;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.activities.ItemGalleryActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.GalleryAdapter;
import rankstop.steeringit.com.rankstop.ui.adapter.ViewPagerAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.AskToLoginDialog;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.ContactDialog;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.ItemInfoDialog;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.ReportAbuseDialog;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.RequestOwnerShipDialog;
import rankstop.steeringit.com.rankstop.utils.HorizontalSpace;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;

public class ItemDetailsFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener, RSView.StandardView {

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
    @BindString(R.string.update_item_pics)
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
        if (RSNetwork.isConnected()) {
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
        showItemInfo(item.getItemDetails());
    }

    @OnClick(R.id.btn_report_abuse)
    void reportAbuse() {
        if (RSNetwork.isConnected()) {
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
        if (RSNetwork.isConnected()) {
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
            if (RSNetwork.isConnected()) {
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

        itemPresenter = new PresenterItemImpl(ItemDetailsFragment.this);
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

        itemPresenter.loadItem(itemId, userId, RankStop.getDeviceLanguage());
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

        // values of the pie
        ArrayList<PieEntry> pieEntry = new ArrayList<>();
        if (item.getGood() == 0 && item.getNeutral() == 0 && item.getBad() == 0) {
            isPieEmpty = true;
            pieEntry.add(new PieEntry(1, ""));
        } else {
            if (item.getGood() > 0)
                pieEntry.add(new PieEntry(item.getGood(), ""));
            if (item.getNeutral() > 0)
                pieEntry.add(new PieEntry(item.getNeutral(), ""));
            if (item.getBad() > 0)
                pieEntry.add(new PieEntry(item.getBad(), ""));
        }


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
        if (isPieEmpty) {
            dataSet.setColors(new int[]{R.color.colorLightGray}, getContext());
        } else {
            dataSet.setColors(new int[]{R.color.colorGreenPie, R.color.colorOrangePie, R.color.colorRedPie}, getContext());
        }
        // initialize PieData
        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE);
        // disable/ enable values on the pieChart
        dataSet.setDrawValues(!isPieEmpty);
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
        initToolbarStyle();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
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
                itemPresenter.followItem(rsFollow);
            else
                itemPresenter.unfollowItem(rsFollow);
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

    private void showItemInfo(ItemDetails itemDetails) {
        ItemInfoDialog dialog = ItemInfoDialog.newInstance(itemDetails);
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "");
    }

    private void openAbusesDialog(RSNavigationData rsNavigationData) {
        ReportAbuseDialog dialog = ReportAbuseDialog.newInstance(rsNavigationData);
        dialog.show(getFragmentManager(), "");
    }

    private void tintMenuIcon(MenuItem item, @ColorRes int color) {
        Drawable wrapDrawable = DrawableCompat.wrap(item.getIcon());
        if (wrapDrawable != null) {
            DrawableCompat.setTint(wrapDrawable, getResources().getColor(color));
            item.setIcon(wrapDrawable);
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
            itemPresenter.onDestroyItem();
        for (int i = 0; i < listMenuItem.size(); i++) {
            tintMenuIcon(listMenuItem.get(i), android.R.color.white);

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
                if (((User) item.getItemDetails().getCreator()).get_id().equals(currentUser.get_id())) {
                    addItemPixBtn.setVisibility(View.VISIBLE);
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
        Toast.makeText(getContext(), offlineMsg, Toast.LENGTH_LONG).show();
    }

    private void openOwnershipDialog(Bundle bundle) {
        RequestOwnerShipDialog dialog = new RequestOwnerShipDialog();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.setArguments(bundle);
        dialog.show(ft, RequestOwnerShipDialog.TAG);
    }
}
