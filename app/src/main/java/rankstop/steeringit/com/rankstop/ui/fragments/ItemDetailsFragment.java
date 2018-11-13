package rankstop.steeringit.com.rankstop.ui.fragments;

import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.List;

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.ui.adapter.GalleryAdapter;
import rankstop.steeringit.com.rankstop.ui.adapter.ViewPagerAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.data.model.Picture;
import rankstop.steeringit.com.rankstop.utils.HorizontalSpace;

public class ItemDetailsFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

    private PieChart pieChart;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private TextView itemDescriptionTV, itemNameTV;
    private View rootView;
    private RecyclerView recyclerViewGallery;
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private MaterialButton addReviewBtn;

    private MenuItem menuItem;

    private PorterDuffColorFilter lightColorFilter, darkColorFilter;

    private List<MenuItem> listMenuItem;

    private boolean isTransparentBg = true;

    private boolean isFavorite = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_item_details, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bindViews();

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("");

        appBarLayout.addOnOffsetChangedListener(this);

        addReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentActionListener.startFragment(AddReviewFragment.getInstance());
            }
        });

        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        mViewPagerAdapter.addFragment(ItemEvalsFragment.getInstance(), getResources().getString(R.string.evals_title));
        mViewPagerAdapter.addFragment(ItemCommentsFragment.getInstance(), getResources().getString(R.string.comments_title));
        mViewPagerAdapter.addFragment(ItemPicsFragment.getInstance(), getResources().getString(R.string.pics_title));
        mViewPager.setAdapter(mViewPagerAdapter);

        tabLayout.setupWithViewPager(mViewPager);

        getItemData();

    }

    private void bindViews() {
        toolbar = rootView.findViewById(R.id.toolbar);
        pieChart = rootView.findViewById(R.id.pie_chart);
        itemNameTV = rootView.findViewById(R.id.tv_item_name);
        itemDescriptionTV = rootView.findViewById(R.id.tv_item_description);
        recyclerViewGallery = rootView.findViewById(R.id.recycler_view_gallery);
        appBarLayout = rootView.findViewById(R.id.app_bar);
        tabLayout = rootView.findViewById(R.id.tabs);
        mViewPager = rootView.findViewById(R.id.viewpager);
        addReviewBtn = rootView.findViewById(R.id.btn_add_review);
    }

    private void getItemData() {

        itemNameTV.setText("Baguette et baguette ariana");
        itemDescriptionTV.setText("But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system");
        initPieChart();
        List<Picture> listGalleryPics = new ArrayList<>();
        listGalleryPics.add(new Picture("", "", "", "", "", "https://scontent.ftun1-1.fna.fbcdn.net/v/t1.0-9/15940845_1300951476615258_4049671823041162693_n.jpg?_nc_cat=0&oh=95ce10869da2541a750ba3c6a7023b41&oe=5C164B7E"));
        listGalleryPics.add(new Picture("", "", "", "", "", "https://scontent.fnbe1-1.fna.fbcdn.net/v/t1.0-9/32186668_103965590479958_7688992294594150400_n.jpg?_nc_cat=105&oh=839705afb5e544316c6e377fe2f8e20a&oe=5C16F2F8"));
        listGalleryPics.add(new Picture("", "", "", "", "", "https://scontent.fnbe1-1.fna.fbcdn.net/v/t1.0-9/13606533_1160694717286450_2226770478468605775_n.jpg?_nc_cat=101&oh=ac41c9c9232bb6ee65ed3bcf6683a3c7&oe=5C54E504"));
        listGalleryPics.add(new Picture("", "", "", "", "", "https://scontent.fnbe1-1.fna.fbcdn.net/v/t1.0-9/43300281_2392097027473991_2378636348329295872_n.jpg?_nc_cat=109&oh=5e66ae825824d196f94ff90913bce570&oe=5C547218"));
        listGalleryPics.add(new Picture("", "", "", "", "", "https://scontent.ftun1-1.fna.fbcdn.net/v/t1.0-9/15940845_1300951476615258_4049671823041162693_n.jpg?_nc_cat=0&oh=95ce10869da2541a750ba3c6a7023b41&oe=5C164B7E"));
        listGalleryPics.add(new Picture("", "", "", "", "", "https://scontent.ftun1-1.fna.fbcdn.net/v/t1.0-9/15940845_1300951476615258_4049671823041162693_n.jpg?_nc_cat=0&oh=95ce10869da2541a750ba3c6a7023b41&oe=5C164B7E"));
        listGalleryPics.add(new Picture("", "", "", "", "", "https://scontent.ftun1-1.fna.fbcdn.net/v/t1.0-9/15940845_1300951476615258_4049671823041162693_n.jpg?_nc_cat=0&oh=95ce10869da2541a750ba3c6a7023b41&oe=5C164B7E"));

        if (listGalleryPics.size() > 0) {
            initGallery(listGalleryPics);
        }
    }

    private void initGallery(List<Picture> listGalleryPics) {
        recyclerViewGallery.setVisibility(View.VISIBLE);
        RecyclerViewClickListener listener = (view, position) -> {
            Toast.makeText(getContext(), "Position " + position, Toast.LENGTH_SHORT).show();
        };
        recyclerViewGallery.setLayoutManager(new LinearLayoutManager(recyclerViewGallery.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewGallery.setAdapter(new GalleryAdapter(listGalleryPics, listener, getContext()));
        recyclerViewGallery.addItemDecoration(new HorizontalSpace(10));
    }

    private void initPieChart() {

        // values of the pie
        ArrayList<PieEntry> pieEntry = new ArrayList<>();
        pieEntry.add(new PieEntry(34f, ""));
        pieEntry.add(new PieEntry(14f, ""));
        pieEntry.add(new PieEntry(71f, ""));


        pieChart.setUsePercentValues(true);
        // define center text of the pie
        pieChart.setCenterText("1.2 \n out of 5");
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
        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                //getActivity().onBackPressed();
                break;
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
            case R.id.action_favorite:
                if (isTransparentBg)
                    manageBtnLike(isFavorite, R.color.colorPrimary);
                else
                    manageBtnLike(isFavorite, android.R.color.white);
                break;
            case R.id.action_share:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initToolbarStyle() {
        lightColorFilter = new PorterDuffColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        darkColorFilter = new PorterDuffColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        manageBtnLike(isFavorite, R.color.colorPrimary);

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
        this.isFavorite = !isFavorite;
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
                toolbar.setTitle("Baguette et baguette Ariana");
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

    public static ItemDetailsFragment getInstance() {
        if (instance == null) {
            instance = new ItemDetailsFragment();
        }
        return instance;
    }
}
