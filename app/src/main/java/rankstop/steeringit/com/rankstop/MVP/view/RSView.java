package rankstop.steeringit.com.rankstop.MVP.view;

import rankstop.steeringit.com.rankstop.data.model.network.GeoPluginResponse;
import rankstop.steeringit.com.rankstop.data.model.network.RSDeviceIP;

public interface RSView {

    interface LoginView {

        void loginSuccess(Object o);

        void loginError();

        void onFollowSuccess(String target, Object data);

        void onFollowFailure(String target);

        void showProgressBar(String target);

        void hideProgressBar(String target);

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

        void findEmailError();

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

    interface UpdateItemView {

        void onSuccess(String target, Object data);

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

        void onPwdMismatch(String message);

        void showProgressBar(String target);

        void hideProgressBar(String target);

        void showMessage(String target, String message);

        void onOffLine();
    }

    interface AbuseView {

        void onReportClicked();

        void onDialogCanceled();

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
