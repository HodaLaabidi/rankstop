package rankstop.steeringit.com.rankstop.ui.callbacks;

import android.view.View;

public interface ItemPieListener {
    void onFollowChanged(boolean isFollow, int position);
    void onClick(View view, int position);
}
