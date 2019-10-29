package rankstop.steeringit.com.rankstop.ui.callbacks;

public interface CriteriaEvalListener {
    void onNoteChanged(int note, int position);
    void onImportanceChanged(int importance, int position);
}
