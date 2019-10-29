package rankstop.steeringit.com.rankstop.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import butterknife.BindInt;
import butterknife.BindString;
import butterknife.ButterKnife;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.RankStop;
import rankstop.steeringit.com.rankstop.customviews.RSTVBold;
import rankstop.steeringit.com.rankstop.customviews.RSTVMedium;
import rankstop.steeringit.com.rankstop.data.model.db.Evaluation;
import rankstop.steeringit.com.rankstop.data.model.db.Item;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.callbacks.ItemPieListener;
import rankstop.steeringit.com.rankstop.utils.DisabledRecyclerView;
import rankstop.steeringit.com.rankstop.utils.RSDateParser;
import rankstop.steeringit.com.rankstop.utils.VerticalSpace;

public class MyEvalsAdapter extends RecyclerView.Adapter<MyEvalsAdapter.ViewHolder> {

    private List<Item> items = new ArrayList<>();
    private ItemPieListener pieListener;
    private Context context ;

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;

    public MyEvalsAdapter(ItemPieListener pieListener, Context context) {
        this.pieListener = pieListener;
        this.context = context ;
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
        View v1 = inflater.inflate(R.layout.layout_my_evals, parent, false);
        viewHolder = new ViewHolder(v1, pieListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position, List<Object> payload) {
        switch (getItemViewType(position)) {
            case ITEM:
                if (!payload.isEmpty()) {
                    //viewHolder.changeIcon(items.get(position));
                } else {
                    super.onBindViewHolder(viewHolder, position, payload);
                }
                break;
            case LOADING:
                break;
        }
    }


    @UiThread
    public void refreshOneItem(int i , Item item , String message ){
        this.items.set(i, item);
        this.notifyItemChanged(i);



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

        public Item item;
        private ItemPieListener pieListener;
        private PieChart pieChart;
        private RSTVBold itemName, noteEvalTV;
        private RSTVMedium dateEvalTV, countReviewsTV, countFollowersTV;
        private CheckBox likeIcon;
        private DisabledRecyclerView recyclerViewCriteriaEvaluated;

        private MyEvalCriteriaAdapter myEvalCriteriaAdapter = new MyEvalCriteriaAdapter();

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
        @BindInt(R.integer.m_card_view)
        int marginCardView;
        @BindString(R.string.date_time_format)
        String dateTimeFormat;

        public ViewHolder(@NonNull View itemView, ItemPieListener pieListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.pieListener = pieListener;

            pieChart = itemView.findViewById(R.id.pie_chart);
            itemName = itemView.findViewById(R.id.item_name);
            countReviewsTV = itemView.findViewById(R.id.tv_count_reviews);
            countFollowersTV = itemView.findViewById(R.id.tv_count_followers);
            likeIcon = itemView.findViewById(R.id.icon_like);
            noteEvalTV = itemView.findViewById(R.id.tv_note_eval);
            dateEvalTV = itemView.findViewById(R.id.tv_date_eval);
            recyclerViewCriteriaEvaluated = itemView.findViewById(R.id.recycler_view_criteria_evaluated);
            if (recyclerViewCriteriaEvaluated != null) {
                recyclerViewCriteriaEvaluated.setLayoutManager(new GridLayoutManager(recyclerViewCriteriaEvaluated.getContext(), 1));
                recyclerViewCriteriaEvaluated.addItemDecoration(new VerticalSpace(marginCardView, 1));
                recyclerViewCriteriaEvaluated.setNestedScrollingEnabled(false);
                recyclerViewCriteriaEvaluated.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
                    @Override
                    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                        // true: consume touch event
                        // false: dispatch touch event
                        return false;
                    }
                });
                recyclerViewCriteriaEvaluated.setLayoutFrozen(true);
                recyclerViewCriteriaEvaluated.setOnTouchListener((v, event) -> false);
                myEvalCriteriaAdapter = new MyEvalCriteriaAdapter();
                recyclerViewCriteriaEvaluated.setAdapter(myEvalCriteriaAdapter);
            }
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
            likeIcon.setChecked(item.isFollow());

            if ( item.getMyEval() != null){
                noteEvalTV.setText(String.valueOf(item.getMyEval().getNoteEval()));
                dateEvalTV.setText(RSDateParser.convertToDateTimeFormat(item.getMyEval().getDate(), dateTimeFormat));
            }


            // add listener to like icon
            likeIcon.setOnClickListener(v -> {
                if(likeIcon.isChecked()){

                    item.setNumberFollows(item.getNumberFollows() + 1);
                } else {
                    item.setNumberFollows(item.getNumberFollows() - 1);

                }
                if (item.getNumberFollows() > 1) {

                    countFollowersTV.setText(String.valueOf(item.getNumberFollows()) + " " + multipleFollower);
                } else {
                    countFollowersTV.setText(String.valueOf(item.getNumberFollows()) + " " + singleFollower);

                }
                initCriteriasList(item.getMyEval());
                initPieChart(item);
                pieListener.onFollowChanged(getAdapterPosition());


            });
            // add listener to like icon
            likeIcon.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!RSSession.isLoggedIn()) {
                    likeIcon.setChecked(!isChecked);

                }
            });
            initCriteriasList(item.getMyEval());
            initPieChart(item);
        }

        private void initCriteriasList(Evaluation myEval) {
            //myEval.getEvalCriterias()
            myEvalCriteriaAdapter.clear();
            myEvalCriteriaAdapter.addAll(myEval.getEvalCriterias());


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
    }

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
