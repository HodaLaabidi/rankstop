package rankstop.steeringit.com.rankstop.ui.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.zhouyou.view.seekbar.SignSeekBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.ButterKnife;
import rankstop.steeringit.com.rankstop.customviews.RSTVBold;
import rankstop.steeringit.com.rankstop.customviews.RSTVRegular;
import rankstop.steeringit.com.rankstop.data.model.db.CriteriaEval;
import rankstop.steeringit.com.rankstop.ui.callbacks.CriteriaEvalListener;
import rankstop.steeringit.com.rankstop.R;

public class EvalCriteriasAdapter extends RecyclerView.Adapter<EvalCriteriasAdapter.ViewHolder> {

    private CriteriaEvalListener noteListener;
    private List<CriteriaEval> listCriteria = new ArrayList<>();

    public EvalCriteriasAdapter(List<CriteriaEval> listCriteria, CriteriaEvalListener noteListener) {
        this.listCriteria = listCriteria;
        this.noteListener = noteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_criteria_eval, parent, false), noteListener);
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
        private RSTVBold criteriaNameTV;
        private RadioGroup importanceToggle;
        private int lastCheckedId = R.id.importance_normal;

        @BindColor(R.color.colorGreenPie)
        int greenPieColor;
        @BindColor(R.color.colorOrangePie)
        int orangePieColor;
        @BindColor(R.color.colorRedPie)
        int redPieColor;
        @BindColor(R.color.colorPrimary)
        int primaryColor;
        @BindColor(R.color.colorAccent)
        int accentColor;
        @BindColor(R.color.colorGray)
        int grayColor;
        @BindString(R.string.not_set)
        String notSet;

        @BindDimen(R.dimen.thumb_text_size)
        int thumbTextSize;

        private ViewHolder(@NonNull View itemView, CriteriaEvalListener noteListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.noteListener = noteListener;

            signSeekBar = itemView.findViewById(R.id.seek_bar);
            criteriaNameTV = itemView.findViewById(R.id.tv_criteria_name);
            importanceToggle = itemView.findViewById(R.id.importance_toggle);


            importanceToggle.setOnCheckedChangeListener((group, checkedId) -> {
                ((RadioButton) itemView.findViewById(lastCheckedId)).setTextColor(accentColor);
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
            });

            signSeekBar.setValueFormatListener(progress -> {
                if (progress == -1)
                    return notSet;
                else
                    return "" + (int) progress + "/5";
            });
            signSeekBar.setOnProgressChangedListener(new SignSeekBar.OnProgressChangedListener() {
                @Override
                public void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {
                    if (progress >= 0 && progress < 2) {
                        if (!isRed) {
                            isRed = true;
                            isOrange = false;
                            isGreen = false;
                            isGray = false;
                            signSeekBar.getConfigBuilder()
                                    .signColor(redPieColor)
                                    .secondTrackColor(redPieColor)
                                    .thumbColor(redPieColor)
                                    .build();
                        }
                    } else if (progress >= 2 && progress < 4) {
                        if (!isOrange) {
                            isOrange = true;
                            isRed = false;
                            isGreen = false;
                            isGray = false;
                            signSeekBar.getConfigBuilder()
                                    .signColor(orangePieColor)
                                    .secondTrackColor(orangePieColor)
                                    .thumbColor(orangePieColor)
                                    .build();
                        }
                    } else if (progress >= 4) {
                        if (!isGreen) {
                            isGreen = true;
                            isOrange = false;
                            isRed = false;
                            isGray = false;
                            signSeekBar.getConfigBuilder()
                                    .signColor(greenPieColor)
                                    .secondTrackColor(greenPieColor)
                                    .thumbColor(greenPieColor)
                                    .build();
                        }
                    } else if (progress < 0) {
                        if (!isGray) {
                            isGray = true;
                            isOrange = false;
                            isRed = false;
                            isGreen = false;
                            signSeekBar.getConfigBuilder()
                                    .signColor(primaryColor)
                                    .secondTrackColor(primaryColor)
                                    .thumbColor(primaryColor)
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

        private void setData(CriteriaEval criteriaEval) {
            signSeekBar.getConfigBuilder()
                    .min(-1)
                    .max(5)
                    .progress(criteriaEval.getNote())
                    .sectionCount(6)
                    .thumbColor(primaryColor)
                    .sectionTextColor(grayColor)
                    .sectionTextSize(thumbTextSize)
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
