package rankstop.steeringit.com.rankstop.ui.dialogFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.text.DecimalFormat;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterUserImpl;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.customviews.RSCustomToast;
import rankstop.steeringit.com.rankstop.customviews.RSTVBold;
import rankstop.steeringit.com.rankstop.data.model.db.ItemDetails;
import rankstop.steeringit.com.rankstop.data.model.db.User;
import rankstop.steeringit.com.rankstop.data.model.db.UserInfo;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponseItemData;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.WorkaroundMapFragment;

public class UserInfoDialog extends DialogFragment implements RSView.StandardView {

    private Unbinder unbinder;
    private String userId ;
    private View rootView;
    private FragmentActionListener fragmentActionListener;
    AlertDialog alertDialog;
    PresenterUserImpl userInfoPresenter ;

    @BindString(R.string.off_line)
    String offlineMsg;

    @BindView(R.id.avatar)
    SimpleDraweeView avatar ;

    @BindView(R.id.tv_user_name)
    RSTVBold userName ;

    private static UserInfoDialog instance;

    public static UserInfoDialog newInstance(String userId) {
        if (instance == null) {
            instance = new UserInfoDialog();
        }
        Bundle args = new Bundle();
        args.putSerializable(RSConstants.RS_USER_Details, userId);
        instance.setArguments(args);
        return instance;
    }

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (rootView == null)
            rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_user_info, null, false);
        unbinder = ButterKnife.bind(this, rootView);
        setFragmentActionListener((ContainerActivity) getActivity());

        if (alertDialog == null)
            alertDialog = new AlertDialog.Builder(getContext()).setView(rootView).setCancelable(false).create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnShowListener(dialog -> onDialogShow(alertDialog));
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    alertDialog.cancel();
                }
                return false;
            }
        });
        return alertDialog;
    }

    private void onDialogShow(AlertDialog dialog) {
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog_ask_login));

        userId =  (String ) getArguments().getSerializable(RSConstants.RS_USER_Details);
        userInfoPresenter = new PresenterUserImpl(this);
        //Add item
        if (userId != null) {
           userInfoPresenter.loadUserInfo(userId , getContext());
        }



    }


    @Override
    public void onDestroyView() {
        userId = null;
        rootView = null;
        instance = null;
        unbinder.unbind();
        fragmentActionListener = null;
        super.onDestroyView();
    }
    @Override
    public void onSuccess(String target, Object data) {
        switch (target){
            case RSConstants.USER_INFO:
                UserInfo userInfo =  new Gson().fromJson(new Gson().toJson(data), UserInfo.class);
                User user = userInfo.getUser() ;
                if ( user.getFirstName() != null && user.getFirstName() != ""){
                    userName.setText(user.getFirstName() + " "+ user.getLastName());
                } else if (user.getUsername() != "" && user.getUsername() != null){
                    userName.setText(user.getUsername());
                }


        }
    }

    @Override
    public void onFailure(String target) {

    }

    @Override
    public void onError(String target) {

    }

    @Override
    public void showProgressBar(String target) {

    }

    @Override
    public void hideProgressBar(String target) {

    }

    @Override
    public void showMessage(String target, String message) {

    }

    @Override
    public void onOffLine() {
        new RSCustomToast(getActivity(), getResources().getString(R.string.error), offlineMsg, R.drawable.ic_error, RSCustomToast.ERROR).show();
    }
}
