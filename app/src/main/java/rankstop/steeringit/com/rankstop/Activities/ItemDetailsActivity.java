package rankstop.steeringit.com.rankstop.Activities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
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

import rankstop.steeringit.com.rankstop.Adapter.GalleryAdapter;
import rankstop.steeringit.com.rankstop.Adapter.ViewPagerAdapter;
import rankstop.steeringit.com.rankstop.Fragments.ItemCommentsFragment;
import rankstop.steeringit.com.rankstop.Fragments.ItemEvalsFragment;
import rankstop.steeringit.com.rankstop.Fragments.ItemPicsFragment;
import rankstop.steeringit.com.rankstop.Interface.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.Model.Picture;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.Utils.HorizontalSpace;

public class ItemDetailsActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener{

    private PieChart pieChart;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private TextView itemDescriptionTV, itemNameTV;
    private RecyclerView recyclerViewGallery;

    private PorterDuffColorFilter lightColorFilter, darkColorFilter;

    private List<MenuItem> listMenuItem;

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private boolean isTransparentBg= true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_item_details);

        toolbar = findViewById(R.id.toolbar);
        pieChart = findViewById(R.id.pie_chart);

        itemNameTV = findViewById(R.id.tv_item_name);
        itemDescriptionTV = findViewById(R.id.tv_item_description);

        recyclerViewGallery = findViewById(R.id.recycler_view_gallery);

        appBarLayout = findViewById(R.id.app_bar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("");

        appBarLayout.addOnOffsetChangedListener(this);

        ViewPager mViewPager = findViewById(R.id.viewpager);
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFragment(ItemEvalsFragment.newInstance(), getResources().getString(R.string.evals_title));
        mViewPagerAdapter.addFragment(ItemCommentsFragment.newInstance(), getResources().getString(R.string.comments_title));
        mViewPagerAdapter.addFragment(ItemPicsFragment.newInstance(), getResources().getString(R.string.pics_title));
        mViewPager.setAdapter(mViewPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        getItemData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_details_menu, menu);

        initToolbarStyle();
        return true;
    }

    private void initToolbarStyle() {
        lightColorFilter = new PorterDuffColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        darkColorFilter = new PorterDuffColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        listMenuItem = new ArrayList<>();
        for (int i = 0; i< toolbar.getMenu().size(); i++){
            MenuItem menuItem = toolbar.getMenu().getItem(i);
            listMenuItem.add(menuItem);
            tintMenuIcon(menuItem, R.color.colorPrimary);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_favorite:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_shopping:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            case R.id.action_share:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void getItemData() {

        itemNameTV.setText("Baguette et baguette ariana");
        itemDescriptionTV.setText("But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system");
        initPieChart();
        List<Picture> listGalleryPics= new ArrayList<>();
        listGalleryPics.add(new Picture("","","","","","https://scontent.ftun1-1.fna.fbcdn.net/v/t1.0-9/15940845_1300951476615258_4049671823041162693_n.jpg?_nc_cat=0&oh=95ce10869da2541a750ba3c6a7023b41&oe=5C164B7E"));
        listGalleryPics.add(new Picture("","","","","","https://scontent.fnbe1-1.fna.fbcdn.net/v/t1.0-9/32186668_103965590479958_7688992294594150400_n.jpg?_nc_cat=105&oh=839705afb5e544316c6e377fe2f8e20a&oe=5C16F2F8"));
        listGalleryPics.add(new Picture("","","","","","https://scontent.fnbe1-1.fna.fbcdn.net/v/t1.0-9/13606533_1160694717286450_2226770478468605775_n.jpg?_nc_cat=101&oh=ac41c9c9232bb6ee65ed3bcf6683a3c7&oe=5C54E504"));
        listGalleryPics.add(new Picture("","","","","","https://scontent.fnbe1-1.fna.fbcdn.net/v/t1.0-9/43300281_2392097027473991_2378636348329295872_n.jpg?_nc_cat=109&oh=5e66ae825824d196f94ff90913bce570&oe=5C547218"));
        listGalleryPics.add(new Picture("","","","","","https://scontent.ftun1-1.fna.fbcdn.net/v/t1.0-9/15940845_1300951476615258_4049671823041162693_n.jpg?_nc_cat=0&oh=95ce10869da2541a750ba3c6a7023b41&oe=5C164B7E"));
        listGalleryPics.add(new Picture("","","","","","https://scontent.ftun1-1.fna.fbcdn.net/v/t1.0-9/15940845_1300951476615258_4049671823041162693_n.jpg?_nc_cat=0&oh=95ce10869da2541a750ba3c6a7023b41&oe=5C164B7E"));
        listGalleryPics.add(new Picture("","","","","","https://scontent.ftun1-1.fna.fbcdn.net/v/t1.0-9/15940845_1300951476615258_4049671823041162693_n.jpg?_nc_cat=0&oh=95ce10869da2541a750ba3c6a7023b41&oe=5C164B7E"));

        if (listGalleryPics.size() > 0) {
            initGallery(listGalleryPics);
        }
    }

    private void initGallery(List<Picture> listGalleryPics) {
        recyclerViewGallery.setVisibility(View.VISIBLE);
        RecyclerViewClickListener listener = (view, position) -> {
            Toast.makeText(this, "Position " + position, Toast.LENGTH_SHORT).show();
        };
        recyclerViewGallery.setLayoutManager(new LinearLayoutManager(recyclerViewGallery.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewGallery.setAdapter(new GalleryAdapter(listGalleryPics, listener, this));
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
        dataSet.setColors(new int[]{R.color.colorGreenPie, R.color.colorOrangePie, R.color.colorRedPie}, this);
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
                Toast.makeText(ItemDetailsActivity.this, "" + e.getY(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(i) / (float) maxScroll;

        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (isTransparentBg) {
                toolbar.setTitle("Baguette et baguette Ariana");
                isTransparentBg = false;
                for (int i = 0; i< listMenuItem.size(); i++){
                    tintMenuIcon(listMenuItem.get(i), android.R.color.white);
                }
                toolbar.getNavigationIcon().setColorFilter(lightColorFilter);
                toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
            }
        } else {
            if (!isTransparentBg) {
                toolbar.setTitle("");
                isTransparentBg = true;
                for (int i = 0; i< listMenuItem.size(); i++){
                    tintMenuIcon(listMenuItem.get(i), R.color.colorPrimary);
                }
                toolbar.getNavigationIcon().setColorFilter(darkColorFilter);
            }
        }
    }

    private void tintMenuIcon(MenuItem item, @ColorRes int color) {
        Drawable wrapDrawable = DrawableCompat.wrap(item.getIcon());
        DrawableCompat.setTint(wrapDrawable, getResources().getColor(color));
        item.setIcon(wrapDrawable);
    }
}
