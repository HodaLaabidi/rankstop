
package rankstop.steeringit.com.rankstop.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.callbacks.ItemPieListener;
import rankstop.steeringit.com.rankstop.data.model.Item;
import rankstop.steeringit.com.rankstop.R;


public class PieAdapter extends RecyclerView.Adapter<PieAdapter.ViewHolder> {

    private List<Item> items = new ArrayList<>();
    private ItemPieListener pieListener;
    private Context context;

    public PieAdapter(List<Item> items, ItemPieListener pieListener, Context context) {
        this.items = items;
        this.pieListener = pieListener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_pie_chart, parent, false), pieListener); // TODO
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payload) {
        if (!payload.isEmpty()){
            holder.changeIcon(items.get(position));
        }else {
            super.onBindViewHolder(holder, position, payload);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(items.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // TODO - Your view members
        public Item item;
        private ItemPieListener pieListener;
        private PieChart pieChart;
        private TextView itemName, countReviewsTV;
        private CheckBox likeIcon;

        public ViewHolder(View itemView, ItemPieListener pieListener) {
            super(itemView);
            this.pieListener = pieListener;

            pieChart = itemView.findViewById(R.id.pie_chart);
            itemName = itemView.findViewById(R.id.item_name);
            countReviewsTV = itemView.findViewById(R.id.tv_count_reviews);
            likeIcon = itemView.findViewById(R.id.icon_like);
            itemView.setOnClickListener(this);
            // TODO instantiate/assign view members
        }

        public void setData(Item item) {
            this.item = item;
            // TODO set data to view
            itemName.setText(item.getItemDetails().getTitle());
            countReviewsTV.setText(String.valueOf(item.getNumberEval()) + " reviews");
            likeIcon.setChecked(item.isFollow());

            likeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pieListener.onFollowChanged(getAdapterPosition());
                }
            });
            // add listener to like icon
            likeIcon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!RSSession.isLoggedIn(context)) {
                        likeIcon.setChecked(!isChecked);
                    }
                }
            });
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
            pieChart.setCenterTextColor(context.getResources().getColor(R.color.colorPrimary));

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

        @Override
        public void onClick(View v) {
            pieListener.onClick(v, getAdapterPosition());
        }

        public void changeIcon(Item item) {
            this.item = item;
            likeIcon.setChecked(item.isFollow());
        }
    }


}
                                