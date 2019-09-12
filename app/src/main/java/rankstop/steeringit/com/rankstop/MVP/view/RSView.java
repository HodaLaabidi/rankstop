package com.steeringit.rankstop.MVP.view;

import com.steeringit.rankstop.data.model.network.GeoPluginResponse;
import com.steeringit.rankstop.data.model.network.RSDeviceIP;

public interface RSView {

    interface LoginView {

        void loginSuccess(Object o);

        void loginError();

        void onFollowSuccess(String target, Object data);

        void onFollowFailure(String target);

        void showProgressBar(String target);

        void hideProgressBar(String target);

        void onSuccess(String target);

        void onError(String target);

        void onOffLine();
    }

    interface RegisterView {

        void registerSuccess(Object o);

        void registerError();

        void onFollowSuccess(String target, Object data);

        void onFollowFailure(String target);

        void showProgressBar(String target);

        void hideProgressBar(String target);

        void onAddressFetched(GeoPluginResponse response);

        void onAddressFailed();

        void onPublicIPFetched(RSDeviceIP response);

        void onPublicIPFailed();

        void onOffLine();
    }

    interface SignupView {

        void findEmailValidations();

        void findEmailSuccess(boolean isEmailExist, Object data);

        void socialLoginSuccess(Object data);

        void socialLoginError(String message);

        void onFollowSuccess(String target, Object data);

        void onFollowFailure(String target);

        void showProgressBar(String target);

        void hideProgressBar(String target);

        void showMessage(String message);

        void onAddressFetched(GeoPluginResponse response);

        void onAddressFailed();

        void onPublicIPFetched(RSDeviceIP response);

        void onPublicIPFailed();

        void onOffLine();
    }

    interface StandardView {

        void onSuccess(String target, Object data);

        void onFailure(String target);

        void onError(String target);

        void showProgressBar(String target);

        void hideProgressBar(String target);

        void showMessage(String target, String message);

        void onOffLine();
    }


    interface StandardView2 {

        void onSuccessRefreshItem(String target,String itemId,String message, Object data);

    }

    interface SearchByBarcodeView {

        void onSuccess(String target, Object data);

        void onFailure(String target, String barcode);

        void onError(String target);

        void showProgressBar(String target);

        void hideProgressBar(String target);

        void showMessage(String target, String message);

        void onOffLine();
    }

    interface EditLangView {

        void onSuccess(String lang, Object data);

        void onFailure();

        void onError();

        void showProgressBar();

        void hideProgressBar();

        void onOffLine();
    }

    interface ListNotifView {

        void onSuccess(String target, Object data, String itemId);

        void onFailure(String target);

        void onError(String target);

        void showProgressBar(String target);

        void hideProgressBar(String target);

        void onOffLine();
    }

    interface UpdateItemView {

        void onSuccess(String target, Object data);

        void onExistItem(String message ,Object data);

        void onFailure(String target);

        void onError(String target);

        void showProgressBar();

        void hideProgressBar();

        void onOffLine();
    }

    interface UpdateProfileView {

        void onSuccess(String target, Object data);

        void onFailure(String target);

        void onError(String target);

        void onOldPwdIncorrect(String message);

        void showProgressBar(String target);

        void hideProgressBar(String target);

        void showMessage(String target, String message);

        void onOffLine();
    }

    interface AbuseView {

        void onReportClicked();

        void onSuccess(String target, Object data);

        void onFailure(String target);

        void showProgressBar(String target);

        void hideProgressBar(String target);

        void showMessage(String target, String message);

        void onOffLine();
    }

    interface SearchView {

        void onSuccess(String target, Object data);

        void onFailure(String target);

        void onError(String target);

        void showProgressBar(String target);

        void hideProgressBar(String target);

        void onOffLine();
    }
}
