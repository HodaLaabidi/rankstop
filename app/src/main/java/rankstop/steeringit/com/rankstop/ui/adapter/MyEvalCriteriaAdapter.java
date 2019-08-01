package rankstop.steeringit.com.rankstop.ui.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.ButterKnife;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.customviews.RSTVRegular;
import rankstop.steeringit.com.rankstop.data.model.db.Criteria;
import rankstop.steeringit.com.rankstop.data.model.db.CriteriaEval;

public class MyEvalCriteriaAdapter extends RecyclerView.Adapter<MyEvalCriteriaAdapter.ViewHolder> {

    private List<CriteriaEval> criteriasEvaluated;

    public MyEvalCriteriaAdapter() {
        this.criteriasEvaluated = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyEvalCriteriaAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_my_criterias_evals, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(criteriasEvaluated.get(position));
    }

    @Override
    public int getItemCount() {
        return criteriasEvaluated.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RSTVRegular criteriaNameTV, criteriaNoteTV;
        private ProgressBar progressBar;

        @BindColor(R.color.colorGray)
        int grayColor;
        @BindColor(R.color.colorRedPie)
        int redColor;
        @BindColor(R.color.colorOrangePie)
        int orangeColor;
        @BindColor(R.color.colorGreenPie)
        int greenColor;
        @BindString(R.string.not_set)
        String notSet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            criteriaNameTV = itemView.findViewById(R.id.tv_criteria_name);
            criteriaNoteTV = itemView.findViewById(R.id.tv_criteria_note);
            progressBar = itemView.findViewById(R.id.progress_bar);

        }

        public void setData(CriteriaEval criteriaEval) {
            criteriaNameTV.setText(((Criteria) criteriaEval.getCriteria()).getName().toString());
            if (criteriaEval.getNote() == -1){
                criteriaNoteTV.setText(notSet);progressBar.setProgress(0);
            }else {
                criteriaNoteTV.setText(String.valueOf(criteriaEval.getNote())+"/5");
                progressBar.setProgress(criteriaEval.getNote()*2+1);
            }

            if (criteriaEval.getNote() <0){
                progressBar.getProgressDrawable().setColorFilter(grayColor, android.graphics.PorterDuff.Mode.SRC_IN);
            }else if (criteriaEval.getNote() <= 1) {
                progressBar.getProgressDrawable().setColorFilter(redColor, android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (criteriaEval.getNote() <= 3) {
                progressBar.getProgressDrawable().setColorFilter(orangeColor, android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (criteriaEval.getNote() <= 5) {
                progressBar.getProgressDrawable().setColorFilter(greenColor, android.graphics.PorterDuff.Mode.SRC_IN);
            }
        }
    }

    public void addAll(List<CriteriaEval> items) {
        for (CriteriaEval item : items) {
            add(item);
        }
    }

    public void clear() {
        criteriasEvaluated.clear();
        notifyDataSetChanged();
    }

    public void add(CriteriaEval item) {
        criteriasEvaluated.add(item);
        notifyItemInserted(criteriasEvaluated.size() - 1);
    }
}
