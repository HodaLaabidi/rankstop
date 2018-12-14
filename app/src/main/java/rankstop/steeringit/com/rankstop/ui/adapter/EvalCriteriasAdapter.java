package rankstop.steeringit.com.rankstop.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zhouyou.view.seekbar.SignSeekBar;

import java.util.ArrayList;
import java.util.List;

import rankstop.steeringit.com.rankstop.data.model.Criteria;
import rankstop.steeringit.com.rankstop.data.model.CriteriaEval;
import rankstop.steeringit.com.rankstop.ui.callbacks.CriteriaEvalListener;
import rankstop.steeringit.com.rankstop.R;

public class EvalCriteriasAdapter extends RecyclerView.Adapter<EvalCriteriasAdapter.ViewHolder> {

    private CriteriaEvalListener noteListener;
    private Context context;
    private List<CriteriaEval> listCriteria = new ArrayList<>();

    public EvalCriteriasAdapter(List<CriteriaEval> listCriteria, CriteriaEvalListener noteListener, Context context) {
        this.listCriteria = listCriteria;
        this.noteListener = noteListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_criteria_eval, parent, false), noteListener); // TODO
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.setData(listCriteria.get(position));
    }

    @Override
    public int getItemCount() {
        return listCriteria.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CriteriaEvalListener noteListener;
        private CriteriaEval criteriaEval;
        private boolean isGray = true, isRed = false, isGreen = false, isOrange = false;

        private SignSeekBar signSeekBar;
        private TextView criteriaNameTV;
        private RadioGroup importanceToggle;
        private int lastCheckedId = R.id.importance_normal;

        public ViewHolder(@NonNull View itemView, CriteriaEvalListener noteListener) {
            super(itemView);
            this.noteListener = noteListener;

            signSeekBar = itemView.findViewById(R.id.seek_bar);
            criteriaNameTV = itemView.findViewById(R.id.tv_criteria_name);
            importanceToggle = itemView.findViewById(R.id.importance_toggle);


            importanceToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    ((RadioButton) itemView.findViewById(lastCheckedId)).setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                    lastCheckedId = checkedId;
                    ((RadioButton) itemView.findViewById(checkedId)).setTextColor(Color.WHITE);
                    switch (checkedId) {
                        case R.id.importance_normal:
                            noteListener.onImportanceChanged(1, getAdapterPosition());
                            break;
                        case R.id.importance_high:
                            noteListener.onImportanceChanged(2, getAdapterPosition());
                            break;
                    }
                }
            });

            signSeekBar.setValueFormatListener(new SignSeekBar.OnValueFormatListener() {
                @Override
                public String format(float progress) {
                    if (progress == -1)
                        return "Not set";
                    else
                        return "" + (int) progress + "/5";
                }
            });
            signSeekBar.setOnProgressChangedListener(new SignSeekBar.OnProgressChangedListener() {
                @Override
                public void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {
                    if (progress >= 0 && progress < 2) {
                        if (isRed == false) {
                            isRed = true;
                            isOrange = false;
                            isGreen = false;
                            isGray = false;
                            signSeekBar.getConfigBuilder()
                                    .signColor(context.getResources().getColor(R.color.colorRedPie))
                                    .secondTrackColor(context.getResources().getColor(R.color.colorRedPie))
                                    .thumbColor(context.getResources().getColor(R.color.colorRedPie))
                                    .build();
                        }
                    } else if (progress >= 2 && progress < 4) {
                        if (isOrange == false) {
                            isOrange = true;
                            isRed = false;
                            isGreen = false;
                            isGray = false;
                            signSeekBar.getConfigBuilder()
                                    .signColor(context.getResources().getColor(R.color.colorOrangePie))
                                    .secondTrackColor(context.getResources().getColor(R.color.colorOrangePie))
                                    .thumbColor(context.getResources().getColor(R.color.colorOrangePie))
                                    .build();
                        }
                    } else if (progress >= 4) {
                        if (isGreen == false) {
                            isGreen = true;
                            isOrange = false;
                            isRed = false;
                            isGray = false;
                            signSeekBar.getConfigBuilder()
                                    .signColor(context.getResources().getColor(R.color.colorGreenPie))
                                    .secondTrackColor(context.getResources().getColor(R.color.colorGreenPie))
                                    .thumbColor(context.getResources().getColor(R.color.colorGreenPie))
                                    .build();
                        }
                    } else if (progress < 0) {
                        if (isGray == false) {
                            isGray = true;
                            isOrange = false;
                            isRed = false;
                            isGreen = false;
                            signSeekBar.getConfigBuilder()
                                    .signColor(context.getResources().getColor(R.color.colorPrimary))
                                    .secondTrackColor(context.getResources().getColor(R.color.colorPrimary))
                                    .thumbColor(context.getResources().getColor(R.color.colorPrimary))
                                    .build();
                        }
                    }

                    noteListener.onNoteChanged(progress, getAdapterPosition());
                }

                @Override
                public void getProgressOnActionUp(SignSeekBar signSeekBar, int progress, float progressFloat) {

                }

                @Override
                public void getProgressOnFinally(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {
                }
            });
        }

        public void setData(CriteriaEval criteriaEval) {
            signSeekBar.getConfigBuilder()
                    .min(-1)
                    .max(5)
                    .progress(criteriaEval.getNote())
                    .sectionCount(6)
                    .thumbColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .sectionTextColor(ContextCompat.getColor(context, R.color.colorGray))
                    .sectionTextSize((int) context.getResources().getDimension(R.dimen.thumb_text_size))
                    .sectionTextPosition(SignSeekBar.TextPosition.BOTTOM_SIDES)
                    .build();
            criteriaNameTV.setText(criteriaEval.getCriteriaName());
            if (criteriaEval.getCoefficient() == 1) {
                ((RadioButton) importanceToggle.getChildAt(0)).setChecked(true);
            } else {
                ((RadioButton) importanceToggle.getChildAt(1)).setChecked(true);
            }
        }
    }
}
