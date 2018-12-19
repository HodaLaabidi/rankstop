package rankstop.steeringit.com.rankstop.ui.fragments;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
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
import android.widget.TextView;
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

import rankstop.steeringit.com.rankstop.MVP.model.PresenterItemImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.CriteriaNote;
import rankstop.steeringit.com.rankstop.data.model.Gallery;
import rankstop.steeringit.com.rankstop.data.model.Item;
import rankstop.steeringit.com.rankstop.data.model.User;
import rankstop.steeringit.com.rankstop.data.model.custom.RSAddReview;
import rankstop.steeringit.com.rankstop.data.model.custom.RSFollow;
import rankstop.steeringit.com.rankstop.data.model.custom.RSNavigationData;
import rankstop.steeringit.com.rankstop.data.model.custom.RSResponseListingItem;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.GalleryAdapter;
import rankstop.steeringit.com.rankstop.ui.adapter.ViewPagerAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.data.model.Picture;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.AskToLoginDialog;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.ReportAbuseDialog;
import rankstop.steeringit.com.rankstop.utils.HorizontalSpace;
import rankstop.steeringit.com.rankstop.utils.RSConstants;

public class ItemDetailsFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener, RSView.StandardView {

    private PieChart pieChart;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private TextView itemDescriptionTV, itemNameTV, itemCategoryTV;
    private View rootView;
    private RecyclerView recyclerViewGallery;
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private MaterialButton addReviewBtn, addItemPixBtn, reportAbuseBTN;

    private MenuItem menuItem;

    private PorterDuffColorFilter lightColorFilter, darkColorFilter;

    private List<MenuItem> listMenuItem;
    private RSNavigationData rsNavigationData;

    private boolean isTransparentBg = true;

    private boolean isFavorite = false;
    private User user;
    private Item item;
    private String itemId;
    private int currentColor;

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

        fragmentContext = new WeakReference<ItemDetailsFragment>(this);
        rootView = inflater.inflate(R.layout.fragment_item_details, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        itemPresenter = new PresenterItemImpl(ItemDetailsFragment.this);
        bindViews();

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("");

        appBarLayout.addOnOffsetChangedListener(this);

        addReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RSSession.isLoggedIn(getContext())) {
                    RSAddReview rsAddReview = new RSAddReview();
                    rsAddReview.setItemId(item.getItemDetails().get_id());
                    rsAddReview.setCategoryId(item.getItemDetails().getCategory().get_id());
                    fragmentActionListener.startFragment(AddReviewFragment.getInstance(rsAddReview, item.getLastEvalUser(), ""), RSConstants.FRAGMENT_ADD_REVIEW);
                } else {
                    RSNavigationData rsNavigationData = new RSNavigationData(RSConstants.FRAGMENT_ADD_REVIEW, RSConstants.ACTION_ADD_REVIEW, fragmentContext.get().getResources().getString(R.string.alert_login_to_add_review), itemId, "", item.getItemDetails().getCategory().get_id());
                    askToLoginDialog(rsNavigationData);
                }
            }
        });

        reportAbuseBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RSSession.isLoggedIn(getContext())) {
                    RSNavigationData rsNavigationData = new RSNavigationData(RSConstants.FRAGMENT_ITEM_DETAILS, RSConstants.ACTION_REPORT_ABUSE, "", itemId, "", "");
                    openAbusesDialog(rsNavigationData);
                } else {
                    RSNavigationData rsNavigationData = new RSNavigationData(RSConstants.FRAGMENT_ITEM_DETAILS, RSConstants.ACTION_REPORT_ABUSE, fragmentContext.get().getResources().getString(R.string.alert_login_to_report_abuse), itemId, "", item.getItemDetails().getCategory().get_id());
                    askToLoginDialog(rsNavigationData);
                }
            }
        });

        addItemPixBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "okay", Toast.LENGTH_SHORT).show();
                fragmentActionListener.startFragment(UpdateItemFragment.getInstance(item.getItemDetails()), RSConstants.FRAGMENT_UPDATE_ITEM);
            }
        });

        loadItemData();

    }

    private void bindViews() {
        toolbar = rootView.findViewById(R.id.toolbar);
        pieChart = rootView.findViewById(R.id.pie_chart);
        itemNameTV = rootView.findViewById(R.id.tv_item_name);
        itemCategoryTV = rootView.findViewById(R.id.tv_item_category);
        itemCategoryTV.setPaintFlags(itemCategoryTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        itemDescriptionTV = rootView.findViewById(R.id.tv_item_description);
        recyclerViewGallery = rootView.findViewById(R.id.recycler_view_gallery);
        appBarLayout = rootView.findViewById(R.id.app_bar);
        tabLayout = rootView.findViewById(R.id.tabs);
        mViewPager = rootView.findViewById(R.id.viewpager);
        addReviewBtn = rootView.findViewById(R.id.btn_add_review);
        addItemPixBtn = rootView.findViewById(R.id.btn_add_pix);
        reportAbuseBTN = rootView.findViewById(R.id.btn_report_abuse);

        setFragmentActionListener((ContainerActivity) getActivity());
    }

    private void loadItemData() {
        String userId = "";
        if (RSSession.isLoggedIn(getContext())) {
            user = RSSession.getCurrentUser(getContext());
            userId = user.get_id();
        }
        itemId = getArguments().getString(RSConstants._ID);
        if (itemId == null) {
            rsNavigationData = (RSNavigationData) getArguments().getSerializable(RSConstants.NAVIGATION_DATA);
            itemId = rsNavigationData.getItemId();
        }

        itemPresenter.loadItem(itemId, userId);
    }

    private void initGallery(List<Gallery> listGalleryPics) {
        recyclerViewGallery.setVisibility(View.VISIBLE);
        RecyclerViewClickListener listener = (view, position) -> {
            Toast.makeText(getContext(), "Position " + position, Toast.LENGTH_SHORT).show();
        };
        recyclerViewGallery.setLayoutManager(new LinearLayoutManager(recyclerViewGallery.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewGallery.setAdapter(new GalleryAdapter(listGalleryPics, listener, getContext()));
        recyclerViewGallery.addItemDecoration(new HorizontalSpace(10));
    }

    private void initPieChart(Item item) {

        // values of the pie
        ArrayList<PieEntry> pieEntry = new ArrayList<>();
        pieEntry.add(new PieEntry(item.getGood(), ""));
        pieEntry.add(new PieEntry(item.getNeutral(), ""));
        pieEntry.add(new PieEntry(item.getBad(), ""));


        pieChart.setUsePercentValues(true);
        // define center text of the pie
        pieChart.setCenterText(item.getScoreItem() + " \n out of 5");
        pieChart.setCenterTextSize(17f);
        pieChart.setCenterTextColor(getResources().getColor(R.color.colorPrimary));

        // disable description of the pie
        pieChart.getDescription().setEnabled(false);
        // margin of the pie
        // pieChart.setExtraOffsets(5, 10, 5, 5);
        // disable/ enable rotation of the pie
        pieChart.setRotationEnabled(false);
        // define speed of rotation
        //pieChart.setDragDecelerationFrictionCoef(0.95f);
        // define the hole raduis of the pie
        pieChart.setHoleRadius(65f);
        // disable/ enable the hole of the pie
        pieChart.setDrawHoleEnabled(true);
        // set hole color of the pie
        pieChart.setHoleColor(Color.TRANSPARENT);
        //pieChart.setTransparentCircleRadius(60f);
        // animate pie
        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);
        // disable/ enable legend on the piechart
        pieChart.getLegend().setEnabled(false);
        // initialize PieDataSet
        PieDataSet dataSet = new PieDataSet(pieEntry, "Item");
        // distance between pie slices
        dataSet.setSliceSpace(3f);
        // scale when select a pie slice
        dataSet.setSelectionShift(5f);
        // colors of the pie slices
        dataSet.setColors(new int[]{R.color.colorGreenPie, R.color.colorOrangePie, R.color.colorRedPie}, getContext());
        // initialize PieData
        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE);
        // disable/ enable values on the piechart
        dataSet.setDrawValues(true);
        // affect data to pieChart
        pieChart.setData(data);
        // add listener of value selection
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Toast.makeText(getContext(), "" + e.getY(), Toast.LENGTH_SHORT).show();
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
            case R.id.logout:
                /*RSSession.removeToken(getContext());
                ((ContainerActivity)getActivity()).manageSession(false);*/
                break;
            case R.id.history:
                fragmentActionListener.startFragment(HistoryFragment.getInstance(), RSConstants.FRAGMENT_HISTORY);
                break;
            case R.id.contact:
                fragmentActionListener.startFragment(ContactFragment.getInstance(), RSConstants.FRAGMENT_CONTACT);
                break;
            case R.id.notifications:
                fragmentActionListener.startFragment(ListNotifFragment.getInstance(), RSConstants.FRAGMENT_NOTIF);
                break;
            case R.id.action_favorite:
                manageFollow(itemId, !isFavorite);
                break;
            case R.id.action_share:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initToolbarStyle() {
        lightColorFilter = new PorterDuffColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        darkColorFilter = new PorterDuffColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);

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

        if (RSSession.isLoggedIn(getContext())) {
            RSFollow rsFollow = new RSFollow(user.get_id(), itemId);
            if (isFollow)
                itemPresenter.followItem(rsFollow);
            else
                itemPresenter.unfollowItem(rsFollow);
            isFavorite = isFollow;
        } else {
            RSNavigationData rsNavigationData = new RSNavigationData(RSConstants.FRAGMENT_ADD_REVIEW, RSConstants.ACTION_FOLLOW, fragmentContext.get().getResources().getString(R.string.alert_login_to_follow), itemId, "", "");
            askToLoginDialog(rsNavigationData);
        }
    }

    private void askToLoginDialog(RSNavigationData rsNavigationData) {
        AskToLoginDialog dialog = AskToLoginDialog.newInstance(rsNavigationData);
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "");
    }

    private void openAbusesDialog(RSNavigationData rsNavigationData) {
        ReportAbuseDialog dialog = ReportAbuseDialog.newInstance(rsNavigationData);
        dialog.setCancelable(false);
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
                toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
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
        pieChart.clear();
        fragmentActionListener = null;
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
        if (!item.isReportAbuse()) {
            reportAbuseBTN.setVisibility(View.VISIBLE);
        }

        // manage color and state of btn like
        manageBtnLike(item.isFollow(), R.color.colorPrimary);
        try {
            if (rsNavigationData != null) {
                if (rsNavigationData.getAction().equals(RSConstants.ACTION_FOLLOW)) {
                    if (item.isFollow()) {
                        Toast.makeText(getContext(), getResources().getString(R.string.already_followed), Toast.LENGTH_SHORT).show();
                    } else {
                        //manageFollow(itemId, true);
                    }
                } else if (rsNavigationData.getAction().equals(RSConstants.ACTION_REPORT_ABUSE)) {
                    if (!item.isReportAbuse()) {
                        RSNavigationData rsNavigationData = new RSNavigationData(RSConstants.FRAGMENT_ITEM_DETAILS, RSConstants.ACTION_REPORT_ABUSE, "", itemId, "", "");
                        openAbusesDialog(rsNavigationData);
                    } else {
                        Toast.makeText(getContext(), "Vous avez signaler cet item", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
        }
        itemNameTV.setText(item.getItemDetails().getTitle());
        itemCategoryTV.setText(item.getItemDetails().getCategory().getName());
        itemDescriptionTV.setText(item.getItemDetails().getDescription());
        initPieChart(item);
        List<Gallery> listGalleryPics = new ArrayList<>();
        listGalleryPics = item.getItemDetails().getGallery();
        if (RSSession.isLoggedIn(getContext())) {
            if (listGalleryPics.size() > 0) {
                initGallery(listGalleryPics);
                addItemPixBtn.setText(getResources().getString(R.string.update_item_pics));
            }
            if (item.getItemDetails().getOwner() == null) {
                if (((User) item.getItemDetails().getCreator()).get_id().equals(RSSession.getCurrentUser(getContext()).get_id())) {
                    addItemPixBtn.setVisibility(View.VISIBLE);
                }
            } else {
                if (((User) item.getItemDetails().getOwner()).get_id().equals(RSSession.getCurrentUser(getContext()).get_id())) {
                    addItemPixBtn.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (listGalleryPics.size() > 0) {
                initGallery(listGalleryPics);
            }
        }

        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        mViewPagerAdapter.addFragment(ItemEvalsFragment.getInstance(item.getTabCritereDetails()), getResources().getString(R.string.evals_title));
        mViewPagerAdapter.addFragment(ItemCommentsFragment.getInstance(item), getResources().getString(R.string.comments_title));
        mViewPagerAdapter.addFragment(ItemPicsFragment.getInstance(item), getResources().getString(R.string.pics_title));
        mViewPager.setAdapter(mViewPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onSuccess(String target, Object data) {
        switch (target) {
            case RSConstants.ONE_ITEM:
                item = new Gson().fromJson(new Gson().toJson(data), Item.class);
                bindData(item);
                break;
            case RSConstants.FOLLOW_ITEM:
                if (data.equals("1")) {
                    Toast.makeText(getContext(), getResources().getString(R.string.follow), Toast.LENGTH_SHORT).show();
                } else if (data.equals("0")) {
                    Toast.makeText(getContext(), getResources().getString(R.string.already_followed), Toast.LENGTH_SHORT).show();
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
    public void showProgressBar(String target) {

    }

    @Override
    public void hideProgressBar(String target) {

    }

    @Override
    public void showMessage(String target, String message) {

    }
}
