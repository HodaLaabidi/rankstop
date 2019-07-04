
package rankstop.steeringit.com.rankstop.ui.adapter;

import android.graphics.Color;
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
import rankstop.steeringit.com.rankstop.RankStop;
import rankstop.steeringit.com.rankstop.customviews.RSTVMedium;
import rankstop.steeringit.com.rankstop.customviews.RSTVRegular;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.callbacks.ItemPieListener;
import rankstop.steeringit.com.rankstop.data.model.db.Item;
import rankstop.steeringit.com.rankstop.R;


public class PieAdapter extends RecyclerView.Adapter<PieAdapter.ViewHolder> {

    private List<Item> items = new ArrayList<>();
    private ItemPieListener pieListener;

    public PieAdapter(List<Item> items, ItemPieListener pieListener) {
        this.items = items;
        this.pieListener = pieListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_pie_chart, parent, false), pieListener);
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

        public Item item;
        private ItemPieListener pieListener;
        private PieChart pieChart;
        private RSTVRegular itemName;
        private RSTVMedium countReviewsTV, countFollowersTV;
        private CheckBox likeIcon;
        @BindString(R.string.score_of_5)
        String scoreOf5;
        @BindString(R.string.multiple_review)
        String multipleReview;
        @BindString(R.string.single_review)
        String singleReview;
        @BindString(R.string.multiple_follower)
        String multipleFollower;
        @BindString(R.string.single_follower)
        String singleFollower;
        @BindColor(R.color.colorPrimary)
        int primaryColor;

        public ViewHolder(View itemView, ItemPieListener pieListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.pieListener = pieListener;

            pieChart = itemView.findViewById(R.id.pie_chart);
            itemName = itemView.findViewById(R.id.item_name);
            countReviewsTV = itemView.findViewById(R.id.tv_count_reviews);
            countFollowersTV = itemView.findViewById(R.id.tv_count_followers);
            likeIcon = itemView.findViewById(R.id.icon_like);
            itemView.setOnClickListener(this);
        }

        public void setData(Item item) {
            this.item = item;
            itemName.setText(item.getItemDetails().getTitle());

            if (item.getNumberEval() > 1)
                countReviewsTV.setText(String.valueOf(item.getNumberEval()) + " "+multipleReview);
            else
                countReviewsTV.setText(String.valueOf(item.getNumberEval()) + " "+singleReview);

            if (item.getNumberFollows() > 1)
                countFollowersTV.setText(String.valueOf(item.getNumberFollows()) + " "+multipleFollower);
            else
                countFollowersTV.setText(String.valueOf(item.getNumberFollows()) + " "+singleFollower);

            likeIcon.setVisibility(View.VISIBLE);
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
            SpannableString spannablecontent=new SpannableString(item.getScoreItem() + scoreOf5);
            spannablecontent.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),0,item.getScoreItem().length(),0);
            spannablecontent.setSpan(new RelativeSizeSpan(2f), 0,item.getScoreItem().length(), 0);
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
            dataSet.setColors(tabColor, RankStop.getInstance());
            // initialize PieData
            PieData data = new PieData(dataSet);
            data.setValueTextSize(10f);
            data.setValueTextColor(Color.WHITE);
            // disable/ enable values on the piechart
            dataSet.setDrawValues(!isPieEmpty);
            data.setDrawValues(false);
            // affect data to pieChart
            pieChart.setData(data);
            //pieChart.setHighlightPerTapEnabled(false);
            pieChart.setTouchEnabled(false);
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
                                