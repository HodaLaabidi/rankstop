package rankstop.steeringit.com.rankstop.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.db.Evaluation;
import rankstop.steeringit.com.rankstop.data.model.db.Item;
import rankstop.steeringit.com.rankstop.ui.callbacks.ItemPieListener;
import rankstop.steeringit.com.rankstop.utils.DisabledRecyclerView;
import rankstop.steeringit.com.rankstop.utils.VerticalSpace;

public class MyEvalsAdapter extends RecyclerView.Adapter<MyEvalsAdapter.ViewHolder> {

    private List<Item> items = new ArrayList<>();
    private ItemPieListener pieListener;
    private Context context;

    public MyEvalsAdapter(List<Item> items, ItemPieListener pieListener, Context context) {
        this.items = items;
        this.pieListener = pieListener;
        this.context = context;
    }

    @NonNull
    @Override
    public MyEvalsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyEvalsAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_my_evals, viewGroup, false), pieListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public Item item;
        private ItemPieListener pieListener;
        private PieChart pieChart;
        private TextView itemName, countReviewsTV, noteEvalTV, dateEvalTV, countFollowersTV;
        private CheckBox likeIcon;
        private DisabledRecyclerView recyclerViewCriteriaEvaluated;

        public ViewHolder(@NonNull View itemView, ItemPieListener pieListener) {
            super(itemView);
            this.pieListener = pieListener;

            pieChart = itemView.findViewById(R.id.pie_chart);
            itemName = itemView.findViewById(R.id.item_name);
            countReviewsTV = itemView.findViewById(R.id.tv_count_reviews);
            countFollowersTV = itemView.findViewById(R.id.tv_count_followers);
            likeIcon = itemView.findViewById(R.id.icon_like);
            noteEvalTV = itemView.findViewById(R.id.tv_note_eval);
            dateEvalTV = itemView.findViewById(R.id.tv_date_eval);
            recyclerViewCriteriaEvaluated = itemView.findViewById(R.id.recycler_view_criteria_evaluated);
            recyclerViewCriteriaEvaluated.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                    // true: consume touch event
                    // false: dispatch touch event
                    return false;
                }
            });
            recyclerViewCriteriaEvaluated.setLayoutFrozen(true);
            recyclerViewCriteriaEvaluated.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            itemView.setOnClickListener(this);
        }

        public void setData(Item item) {
            this.item = item;
            // TODO set data to view
            itemName.setText(item.getItemDetails().getTitle());
            countReviewsTV.setText(String.valueOf(item.getNumberEval()) + " reviews");
            countFollowersTV.setText(String.valueOf(item.getNumberFollows()) + " followers");
            likeIcon.setChecked(item.isFollow());


            noteEvalTV.setText(String.valueOf(item.getMyEval().getNoteEval()));
            dateEvalTV.setText(item.getMyEval().getDate());
            // add listener to like icon
            likeIcon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    pieListener.onFollowChanged(isChecked, getAdapterPosition());
                }
            });
            initCriteriasList(item.getMyEval());
            initPieChart(item);
        }

        private void initCriteriasList(Evaluation myEval) {

            //myEval.getEvalCriterias()
            recyclerViewCriteriaEvaluated.setLayoutManager(new GridLayoutManager(recyclerViewCriteriaEvaluated.getContext(), 1));
            recyclerViewCriteriaEvaluated.setAdapter(new MyEvalCriteriaAdapter(myEval.getEvalCriterias(), context));
            recyclerViewCriteriaEvaluated.addItemDecoration(new VerticalSpace(context.getResources().getInteger(R.integer.m_card_view), 1));
            recyclerViewCriteriaEvaluated.setNestedScrollingEnabled(false);

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
        }

        @Override
        public void onClick(View v) {
            pieListener.onClick(v, getAdapterPosition());
        }
    }
}
