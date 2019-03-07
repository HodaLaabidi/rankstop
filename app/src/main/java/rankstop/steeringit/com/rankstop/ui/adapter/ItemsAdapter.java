package rankstop.steeringit.com.rankstop.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.ButterKnife;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.RankStop;
import rankstop.steeringit.com.rankstop.customviews.RSTVRegular;
import rankstop.steeringit.com.rankstop.data.model.db.Item;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.callbacks.ItemPieListener;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private List<Item> items;
    private Context context = RankStop.getInstance();
    private ItemPieListener pieListener;
    private boolean showLikeBTN;

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;

    public ItemsAdapter(ItemPieListener pieListener, boolean showLikeBTN) {
        this.pieListener = pieListener;
        this.showLikeBTN = showLikeBTN;
        items = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new ViewHolder(v2, pieListener);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.layout_pie_chart_listing, parent, false);
        viewHolder = new ViewHolder(v1, pieListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position, List<Object> payload) {
        switch (getItemViewType(position)) {
            case ITEM:
                if (!payload.isEmpty()) {
                    viewHolder.changeIcon(items.get(position));
                } else {
                    super.onBindViewHolder(viewHolder, position, payload);
                }
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                viewHolder.setData(items.get(position));
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == items.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void refreshData(List<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemPieListener listener;
        public Item item;
        private PieChart pieChart;
        private RSTVRegular itemName, countReviewsTV, countFollowersTV;
        private CheckBox likeIcon;
        @BindColor(R.color.colorPrimary)
        int primaryColor;
        @BindString(R.string.single_review)
        String singleReview;
        @BindString(R.string.multiple_review)
        String multipleReview;
        @BindString(R.string.single_follower)
        String singleFollower;
        @BindString(R.string.multiple_follower)
        String multipleFollower;
        @BindString(R.string.score_of_5)
        String scoreOf5;

        public ViewHolder(@NonNull View itemView, ItemPieListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.listener = listener;
            pieChart = itemView.findViewById(R.id.pie_chart);
            itemName = itemView.findViewById(R.id.item_name);
            countReviewsTV = itemView.findViewById(R.id.tv_count_reviews);
            countFollowersTV = itemView.findViewById(R.id.tv_count_followers);
            likeIcon = itemView.findViewById(R.id.icon_like);
            if (showLikeBTN)
                try {
                    likeIcon.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }

        public void setData(Item item) {
            this.item = item;
            itemName.setText(item.getItemDetails().getTitle());
            if (item.getNumberEval() > 1) {
                countReviewsTV.setText(String.valueOf(item.getNumberEval()) + " " + multipleReview);
            } else {
                countReviewsTV.setText(String.valueOf(item.getNumberEval()) + " " + singleReview);
            }

            if (item.getNumberFollows() > 1) {
                countFollowersTV.setText(String.valueOf(item.getNumberFollows()) + " " + multipleFollower);
            } else {
                countFollowersTV.setText(String.valueOf(item.getNumberFollows()) + " " + singleFollower);
            }

            likeIcon.setChecked(item.isFollow());

            likeIcon.setOnClickListener(v -> pieListener.onFollowChanged(getAdapterPosition()));
            // add listener to like icon
            likeIcon.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!RSSession.isLoggedIn()) {
                    likeIcon.setChecked(!isChecked);
                }
            });

            initPieChart(item);
        }

        public void changeIcon(Item item) {
            this.item = item;
            likeIcon.setChecked(item.isFollow());
        }

        private void initPieChart(Item item) {
            int[] tabColor;
            // values of the pie
            boolean isPieEmpty = false;
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


            pieChart.setUsePercentValues(true);
            // define center text of the pie

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
            dataSet.setColors(tabColor, context);
            // initialize PieData
            PieData data = new PieData(dataSet);
            data.setValueTextSize(10f);
            data.setValueTextColor(Color.WHITE);
            // disable/ enable values on the piechart
            dataSet.setDrawValues(!isPieEmpty);
            // affect data to pieChart
            pieChart.setData(data);
            //pieChart.setHighlightPerTapEnabled(false);
            pieChart.setTouchEnabled(false);
        }
    }

    //------------------------------Helpers
    public void addAll(List<Item> items) {
        for (Item item : items) {
            add(item);
        }
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void add(Item item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Item());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = items.size() - 1;
        Item item = getItem(position);
        if (item != null) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Item getItem(int position) {
        return items.get(position);
    }

}
