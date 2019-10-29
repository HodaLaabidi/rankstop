package rankstop.steeringit.com.rankstop.ui.callbacks;

import android.content.Context;

import rankstop.steeringit.com.rankstop.data.model.network.RSRequestFilter;

public interface FilterDialogListener {
    void onfilterClicked(RSRequestFilter data, Context context);
}
