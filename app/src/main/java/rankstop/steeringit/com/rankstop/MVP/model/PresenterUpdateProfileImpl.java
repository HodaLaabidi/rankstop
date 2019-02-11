package rankstop.steeringit.com.rankstop.MVP.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.data.model.db.RSRequestEditProfile;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponse;
import rankstop.steeringit.com.rankstop.data.webservices.WebService;
import rankstop.steeringit.com.rankstop.utils.FileUtils;
import rankstop.steeringit.com.rankstop.utils.Helpers;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenterUpdateProfileImpl implements RSPresenter.UpdateProfilePresenter {

    private RSView.UpdateProfileView standardView;
    private Call<RSResponse> callEditProfile, callloadCountries;
    private Context context;

    public PresenterUpdateProfileImpl(RSView.UpdateProfileView standardView, Context context) {
        this.standardView = standardView;
        this.context = context;
    }

    @Override
    public void editPassword() {
        if (standardView != null) {
        }
    }

    @Override
    public void editProfile(RSRequestEditProfile user) {
        if (RSNetwork.isConnected()) {
            if (standardView != null) {
                Log.i("TAG_IMG_URI", "" + user.getFile());
                MultipartBody.Part part = null;
                if (user.getFile() != null) {
                    part = prepareFilePart("file", user.getFile());
                }
                callEditProfile = WebService.getInstance().getApi().updateUser(
                        part,
                        user,
                        Helpers.createPartFormString(user.getUserId())
                );
                callEditProfile.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body().getStatus() == 1) {
                            standardView.onSuccess(RSConstants.UPDATE_PROFILE, response.body().getData());
                            standardView.showMessage(RSConstants.UPDATE_PROFILE, response.body().getMessage());
                        } else if (response.body().getStatus() == 0) {
                            standardView.onFailure(RSConstants.UPDATE_PROFILE);
                            standardView.showMessage(RSConstants.UPDATE_PROFILE, response.body().getMessage());
                        } else if (response.body().getStatus() == 2) {
                            standardView.onOldPwdIncorrect(response.body().getMessage());
                        }
                        //standardView.hideProgressBar(RSConstants.ADD_ITEM);
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!call.isCanceled()) {
                            standardView.showMessage(RSConstants.UPDATE_PROFILE, "erreur");
                        }
                    }
                });
            }
        }else {
            standardView.onOffLine();
        }
    }

    @Override
    public void loadCountriesList() {
        if (RSNetwork.isConnected()) {
            if (standardView != null) {
                callloadCountries = WebService.getInstance().getApi().loadCountries();
                standardView.showProgressBar(RSConstants.COUNTRIES_LIST);
                callloadCountries.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body().getStatus() == 1) {
                            standardView.onSuccess(RSConstants.COUNTRIES_LIST, response.body().getData());
                        } else if (response.body().getStatus() == 0) {
                            standardView.onError(RSConstants.COUNTRIES_LIST);
                        }
                        standardView.hideProgressBar(RSConstants.COUNTRIES_LIST);
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!callloadCountries.isCanceled()) {
                            standardView.hideProgressBar(RSConstants.COUNTRIES_LIST);
                            standardView.onFailure(RSConstants.COUNTRIES_LIST);
                        }
                    }
                });
            }
        }else {
            standardView.onOffLine();
        }
    }

    @Override
    public void onDestroy() {
        if (callEditProfile != null)
            if (callEditProfile.isExecuted())
                callEditProfile.cancel();
        if (callloadCountries != null)
            if (callloadCountries.isExecuted())
                callloadCountries.cancel();
        standardView = null;
    }

    public MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File file = FileUtils.getFile(context, fileUri);
        RequestBody requestFile = RequestBody.create(MediaType.parse(context.getContentResolver().getType(fileUri)), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }
}
