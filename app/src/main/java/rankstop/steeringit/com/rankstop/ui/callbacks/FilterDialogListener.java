package com.steeringit.rankstop.ui.callbacks;

import android.content.Context;

import com.steeringit.rankstop.data.model.network.RSRequestFilter;

public interface FilterDialogListener {
    void onfilterClicked(RSRequestFilter data, Context context);
}
