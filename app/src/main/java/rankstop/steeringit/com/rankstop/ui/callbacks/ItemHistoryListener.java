package rankstop.steeringit.com.rankstop.ui.callbacks;

import android.view.View;

public interface ItemHistoryListener {

    void onHideClicked(boolean hide, int position);
    void onClick(View view, int position);
}
