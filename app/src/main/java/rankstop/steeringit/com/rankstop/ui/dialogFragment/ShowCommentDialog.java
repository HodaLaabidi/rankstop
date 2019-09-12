package com.steeringit.rankstop.ui.dialogFragment;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.steeringit.rankstop.R;
import com.steeringit.rankstop.customviews.RSBTNMedium;
import com.steeringit.rankstop.customviews.RSTVMedium;
import com.steeringit.rankstop.data.model.db.Comment;
import com.steeringit.rankstop.ui.callbacks.DialogConfirmationListener;
import com.steeringit.rankstop.utils.RSConstants;
import com.steeringit.rankstop.utils.RSDateParser;

public class ShowCommentDialog extends DialogFragment implements DialogConfirmationListener {

    public static String TAG = "SHOW_COMMENT_DIALOG";
    private DialogConfirmationListener callback;
    private View rootView;
    private Unbinder unbinder;
    private Comment comment;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_remove_comment)
    RSBTNMedium removeCommentBTN;
    @BindView(R.id.tv_comment)
    RSTVMedium commentTV;
    @BindString(R.string.message_delete_comment)
    String messageDeleteComment;
    @BindString(R.string.date_time_format)
    String dateTimeFormat;
    @OnClick(R.id.btn_remove_comment)
    void openDialogConfirmation() {
        Bundle bundle = new Bundle();
        bundle.putString(RSConstants.MESSAGE, messageDeleteComment);
        bundle.putString(RSConstants._ID, comment.get_id());
        AlertConfirmationDialog dialog = new AlertConfirmationDialog();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.setCancelable(false);
        dialog.setArguments(bundle);
        dialog.setTargetFragment(this, 0);
        dialog.show(ft, AlertConfirmationDialog.TAG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.dialog_show_comment, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(view1 -> dismiss());

        try {
            callback = (DialogConfirmationListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");
        }

        Bundle b = getArguments();
        comment = (Comment) b.getSerializable(RSConstants.COMMENT);

        if (b.getString(RSConstants.USER_ID) != null) {
            if (comment.getUserId() != null){
                if (b.getString(RSConstants.USER_ID).equals(comment.getUserId().get_id()))
                    removeCommentBTN.setVisibility(View.VISIBLE);
            }

        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (comment.getUserId() != null){
            toolbar.setTitle(comment.getUserId().getNameToUse().getValue());
        }

        toolbar.setSubtitle(RSDateParser.convertToDateTimeFormat(comment.getDate(), dateTimeFormat));
        commentTV.setText(comment.getText());
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onDestroyView() {

        if (unbinder != null)
            unbinder.unbind();
        rootView = null;
        super.onDestroyView();
    }

    @Override
    public void onCancelClicked() {

    }

    @Override
    public void onConfirmClicked(String id) {
        callback.onConfirmClicked(id);
        dismiss();
    }
}
