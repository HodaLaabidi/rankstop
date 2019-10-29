package rankstop.steeringit.com.rankstop.ui.dialogFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.Calendar;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterUserImpl;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.customviews.RSCustomToast;
import rankstop.steeringit.com.rankstop.customviews.RSTVBold;
import rankstop.steeringit.com.rankstop.customviews.RSTVMedium;
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
    RSTVMedium userName ;

    @BindView(R.id.ll_user_age)
    LinearLayout llUserAge;

    @BindView(R.id.ll_user_city)
    LinearLayout llUserCity ;

    @BindView(R.id.ll_user_country)
    LinearLayout llUserCountry ;

    @BindView(R.id.ll_user_gender)
    LinearLayout llUserGender ;

    @BindView(R.id.ll_user_nbr_eval)
    LinearLayout llUserNbrEval ;

    @BindView(R.id.user_age)
    RSTVMedium userAge ;

    @BindView(R.id.user_city)
    RSTVMedium userCity ;

    @BindView(R.id.user_country)
    RSTVMedium userCountry ;

    @BindView(R.id.user_gender)
    RSTVMedium userGender ;

    @BindView(R.id.user_nbr_eval)
    RSTVMedium userNbrEval ;

    @OnClick(R.id.negative_btn)
    void closeDialog() {
        dismiss();
    }


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
                if (userInfo != null){
                    User user = userInfo.getUser() ;
                    if ( user.getFirstName() != null && !user.getFirstName().equalsIgnoreCase("")){
                        if (user.getLastName() != null && !user.getLastName().equalsIgnoreCase("") ){
                            userName.setText(user.getFirstName() + " "+ user.getLastName().trim());
                        } else {
                            if (!user.getUsername().equalsIgnoreCase("") && user.getUsername() != null){
                                userName.setText(user.getUsername());
                            } else {
                                userName.setText(user.getFirstName());
                            }

                        }

                    }
                      else {

                        if (!user.getUsername().equalsIgnoreCase("") && user.getUsername() != null){
                            userName.setText(user.getUsername());
                        } else {
                            userName.setText(R.string.not_set);
                        }
                     }

                   /* Glide
                            .with(this)
                            .load(user.getPictureProfile())
                            .centerCrop()
                            .placeholder(R.drawable.ava_256)
                            .error(R.drawable.ava_256)
                            .apply(RequestOptions.circleCropTransform())
                            .into(avatar);*/
                    if (user.getPictureProfile() != null) {

                    if (user.getPictureProfile() != "") {
                        avatar.setImageURI(Uri.parse(user.getPictureProfile()));
                        avatar.getHierarchy().setFailureImage(R.drawable.ava_256);
                        avatar.getHierarchy().setPlaceholderImage(R.drawable.ava_256 , ScalingUtils.ScaleType.CENTER_CROP);
                    } else {



                        ImageRequest request =
                                ImageRequestBuilder.newBuilderWithResourceId(R.drawable.ava_256)
                                        .build();
                        DraweeController controller = Fresco.newDraweeControllerBuilder()
                                .setImageRequest(request)
                                .setOldController(avatar.getController())
                                .build();
                        avatar.setController(controller);
                        avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }

                } else {




                    ImageRequest request =
                            ImageRequestBuilder.newBuilderWithResourceId(R.drawable.ava_256)
                                    .build();
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .setOldController(avatar.getController())
                            .build();
                    avatar.setController(controller);
                    avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);


                }
                    if (  user.getGender() != null){
                        if (!user.getGender().equalsIgnoreCase("")) {
                            llUserGender.setVisibility(View.VISIBLE);
                            userGender.setText(user.getGender());
                        }
                    }

                    if (user.getLocation() != null){
                        if (user.getLocation().getCity() != null ) {
                            if (!user.getLocation().getCity().equalsIgnoreCase("")) {
                                llUserCity.setVisibility(View.VISIBLE);
                                userCity.setText(user.getLocation().getCity());
                            }
                        }

                        if (user.getLocation().getCountry() != null){
                            if (user.getLocation().getCountry().getCountryName() != null ) {
                                if (!user.getLocation().getCountry().getCountryName().equalsIgnoreCase("")) {
                                    userCountry.setText(user.getLocation().getCountry().getCountryName());
                                    llUserCountry.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                    if (!((userInfo.getCountEval()+"").equalsIgnoreCase("")) ){
                        if(userInfo.getCountEval() >= 0){
                            llUserNbrEval.setVisibility(View.VISIBLE);
                            userNbrEval.setText(userInfo.getCountEval() + "");
                        }

                    }
                    if (user.getBirthDate() != null ){
                        if ( !user.getBirthDate().equalsIgnoreCase("")) {
                            String yearOfBirth = user.getBirthDate().trim().substring(0, 4);
                            if (yearOfBirth.matches(RSConstants.REGEX_YEAR)) {
                                String age = (Calendar.getInstance().get(Calendar.YEAR) - Integer.valueOf(yearOfBirth)) + "";
                                if (age != null ) {
                                    if (!age.equalsIgnoreCase("")) {
                                        llUserAge.setVisibility(View.VISIBLE);
                                        userAge.setText(age);
                                    }
                                }

                            }
                        }

                    }


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
