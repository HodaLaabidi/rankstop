package rankstop.steeringit.com.rankstop.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.Item;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private List<Item> items;
    private Context context;
    private RecyclerViewClickListener pieListener;

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;

    public ItemsAdapter(RecyclerViewClickListener pieListener, Context context) {
        this.pieListener = pieListener;
        this.context = context;
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
        return  viewHolder;
    }

    @NonNull
    private ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.item_fetched, parent, false);
        viewHolder = new ViewHolder(v1, pieListener);
        return viewHolder;
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

        private RecyclerViewClickListener listener;
        public Item item;
        /*@BindView(R.id.pie_chart)
        PieChart pieChart;
        @BindView(R.id.item_name)
        TextView itemName;
        @BindView(R.id.tv_count_reviews)
        TextView countReviewsTV;*/
        private PieChart pieChart;
        private TextView itemName, countReviewsTV;
        @BindColor(R.color.colorPrimary)
        int primaryColor;

        public ViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.listener = listener;
            pieChart = itemView.findViewById(R.id.pie_chart);
            itemName = itemView.findViewById(R.id.item_name);
            countReviewsTV = itemView.findViewById(R.id.tv_count_reviews);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }

        public void setData(Item item) {
            this.item = item;
            itemName.setText(item.getItemDetails().getTitle());
            countReviewsTV.setText(String.valueOf(item.getNumberEval()) + " reviews");

            initPieChart(item);
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
            dataSet.setColors(new int[]{R.color.colorGreenPie, R.color.colorOrangePie, R.color.colorRedPie}, context);
            // initialize PieData
            PieData data = new PieData(dataSet);
            data.setValueTextSize(10f);
            data.setValueTextColor(Color.WHITE);
            // disable/ enable values on the piechart
            dataSet.setDrawValues(true);
            // affect data to pieChart
            pieChart.setData(data);
            //pieChart.setHighlightPerTapEnabled(false);
            pieChart.setTouchEnabled(false);
            // add listener of value selection
            /*pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    Toast.makeText(context, "" + e.getY(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected() {

                }
            });*/
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
