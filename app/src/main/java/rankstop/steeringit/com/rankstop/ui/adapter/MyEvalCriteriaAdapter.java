package rankstop.steeringit.com.rankstop.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.db.Criteria;
import rankstop.steeringit.com.rankstop.data.model.db.CriteriaEval;

public class MyEvalCriteriaAdapter extends RecyclerView.Adapter<MyEvalCriteriaAdapter.ViewHolder> {

    private List<CriteriaEval> criteriasEvaluated = new ArrayList<>();
    private Context context;

    public MyEvalCriteriaAdapter(List<CriteriaEval> criteriasEvaluated, Context context) {
        this.criteriasEvaluated = criteriasEvaluated;
        this.context = context;
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

        private TextView criteriaNameTV, criteriaNoteTV;
        private ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            criteriaNameTV = itemView.findViewById(R.id.tv_criteria_name);
            criteriaNoteTV = itemView.findViewById(R.id.tv_criteria_note);
            progressBar = itemView.findViewById(R.id.progress_bar);

        }

        public void setData(CriteriaEval criteriaEval) {
            criteriaNameTV.setText(((Criteria) criteriaEval.getCriteria()).getName());
            if (criteriaEval.getNote() == -1){
                criteriaNoteTV.setText("Not set");progressBar.setProgress(0);
            }else {
                criteriaNoteTV.setText(String.valueOf(criteriaEval.getNote())+"/5");
                progressBar.setProgress(criteriaEval.getNote()*2+1);
            }

            if (criteriaEval.getNote() <0){
                progressBar.getProgressDrawable().setColorFilter(context.getResources().getColor(R.color.colorGray), android.graphics.PorterDuff.Mode.SRC_IN);
            }else if (criteriaEval.getNote() <= 1) {
                progressBar.getProgressDrawable().setColorFilter(context.getResources().getColor(R.color.colorRedPie), android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (criteriaEval.getNote() <= 3) {
                progressBar.getProgressDrawable().setColorFilter(context.getResources().getColor(R.color.colorOrangePie), android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (criteriaEval.getNote() <= 5) {
                progressBar.getProgressDrawable().setColorFilter(context.getResources().getColor(R.color.colorGreenPie), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        }
    }
}
