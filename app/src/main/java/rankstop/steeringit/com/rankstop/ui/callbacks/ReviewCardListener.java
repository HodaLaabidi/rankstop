package rankstop.steeringit.com.rankstop.ui.callbacks;

import android.view.View;

import java.util.List;

import rankstop.steeringit.com.rankstop.data.model.db.Comment;

public interface ReviewCardListener {
    void onRemoveClicked(int position);
    void onClick(View view, int position, List<Comment> comments);
}
