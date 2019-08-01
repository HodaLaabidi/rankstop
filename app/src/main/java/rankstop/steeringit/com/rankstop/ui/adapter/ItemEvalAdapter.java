
package rankstop.steeringit.com.rankstop.ui.adapter;

import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import rankstop.steeringit.com.rankstop.RankStop;
import rankstop.steeringit.com.rankstop.customviews.RSTVBold;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.data.model.db.CriteriaNote;
import rankstop.steeringit.com.rankstop.R;


public class ItemEvalAdapter extends RecyclerView.Adapter<ItemEvalAdapter.ViewHolder> {

    private List<CriteriaNote> criterias = new ArrayList<>();
    private RecyclerViewClickListener listener;

    public ItemEvalAdapter(List<CriteriaNote> criterias, RecyclerViewClickListener listener) {
        this.criterias = criterias;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bar_chart, parent, false), listener);
    }

    @Override
    public int getItemCount() {
        return criterias.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(criterias.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CriteriaNote criteria;
        private RecyclerViewClickListener mListener;

        @BindView(R.id.bar_chart)
        BarChart barChart;
        @BindView(R.id.bar_chart_title_view)
        RSTVBold barChartTitleView;

        @BindString(R.string.text_good)
        String goodText;
        @BindString(R.string.text_neutral)
        String neutralText;
        @BindString(R.string.text_bad)
        String badText;

        public ViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.mListener = listener;
            itemView.setOnClickListener(this);
        }

        public void setData(CriteriaNote criteria) {
            this.criteria = criteria;

            barChartTitleView.setText(criteria.getNameCritere());
            //configure bar chart
            barChart.getDescription().setEnabled(false);
            // disable background of bar chart
            barChart.setDrawGridBackground(false);
            // disable zoom on bar chart
            barChart.setPinchZoom(false);
            // disable double tap to zoom of bar chart
            barChart.setDoubleTapToZoomEnabled(false);
            // disable touch on bar chart
            barChart.setTouchEnabled(false);

            XAxis xAxis = barChart.getXAxis();
            // set position of axe x  to bottom
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            // disable drawing grid vertical lines on bar chart
            xAxis.setDrawGridLines(false);
            // disable drawing values on x axe
           // xAxis.setDrawLabels(false);
            ArrayList xVals = new ArrayList();
            xVals.add("");
            xVals.add(goodText);
            xVals.add(neutralText);
            xVals.add(badText);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));

            YAxis leftAxis = barChart.getAxisLeft();
            leftAxis.setLabelCount(5, false);
            leftAxis.setSpaceTop(10f);
            // disable drawing grid horizontal lines on bar chart
            leftAxis.setDrawGridLines(false);

            // disable right axe of bar chart
            barChart.getAxisRight().setEnabled(false);

            barChart.setData(generateBarData(criteria));
            // disable/ enable legend on the piechart
            barChart.getLegend().setEnabled(false);
            barChart.animateY(1000);
        }

        private BarData generateBarData(CriteriaNote criteria) {

            ArrayList<BarEntry> barEntry = new ArrayList<>();

            barEntry.add(new BarEntry(1, Float.parseFloat(criteria.getCrit_good())));
            barEntry.add(new BarEntry(2, Float.parseFloat(criteria.getCrit_neutral())));
            barEntry.add(new BarEntry(3, Float.parseFloat(criteria.getCrit_bad())));

            BarDataSet barDataSet = new BarDataSet(barEntry, criteria.getNameCritere());
            barDataSet.setColors(new int[]{R.color.colorGreenPie, R.color.colorOrangePie, R.color.colorRedPie}, RankStop.getInstance());
            barDataSet.setBarShadowColor(Color.rgb(203,203,203));

            BarData barData = new BarData(barDataSet);
            barData.setBarWidth(0.6f);

            return barData;
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }


}
                                