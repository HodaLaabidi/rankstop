package rankstop.steeringit.com.rankstop.ui.dialogFragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.ui.callbacks.BottomSheetDialogListener;

public class RSBottomSheetDialog extends BottomSheetDialogFragment {


    public static final String TAG = "BOTTOM_SHEET_DIALOG";
    private Unbinder unbinder;
    private View view;


    private BottomSheetDialogListener callback;

    @OnClick(R.id.layout_take_pic)
    void takePicture() {
        dismiss();
        callback.onTakePictureClicked();
    }

    @OnClick(R.id.layout_choose_pic)
    void openGallery() {
        dismiss();
        callback.onChoosePictureClicked();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            callback = (BottomSheetDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_dialog, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        view = null;
        if (unbinder != null)
            unbinder.unbind();
        super.onDestroyView();
    }
}
