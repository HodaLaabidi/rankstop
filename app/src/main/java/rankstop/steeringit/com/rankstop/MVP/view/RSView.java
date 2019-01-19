package rankstop.steeringit.com.rankstop.MVP.view;

import rankstop.steeringit.com.rankstop.data.model.network.GeoPluginResponse;
import rankstop.steeringit.com.rankstop.data.model.network.RSDeviceIP;

public interface RSView {

    interface LoginView {

        void loginValidations();

        void loginSuccess(Object o);

        void loginError();

        void onFollowSuccess(String target, Object data);

        void onFollowFailure(String target);

        void showProgressBar();

        void hideProgressBar();
    }

    interface RegisterView {

        void registerValidations();

        void registerSuccess(Object o);

        void registerError();

        void onFollowSuccess(String target, Object data);

        void onFollowFailure(String target);

        void showProgressBar();

        void hideProgressBar();

        void onAddressFetched(GeoPluginResponse response);

        void onAddressFailed();

        void onPublicIPFetched(RSDeviceIP response);

        void onPublicIPFailed();
    }

    interface SignupView {

        void findEmailValidations();

        void findEmailSuccess(boolean isEmailExist, Object data);

        void findEmailError();

        void socialLoginSuccess(Object data);

        void socialLoginError(String message);

        void onFollowSuccess(String target, Object data);

        void onFollowFailure(String target);

        void showProgressBar();

        void hideProgressBar();

        void showMessage(String message);

        void onAddressFetched(GeoPluginResponse response);

        void onAddressFailed();

        void onPublicIPFetched(RSDeviceIP response);

        void onPublicIPFailed();
    }

    interface StandardView {

        void onSuccess(String target, Object data);

        void onFailure(String target);

        void onError(String target);

        void showProgressBar(String target);

        void hideProgressBar(String target);

        void showMessage(String target, String message);
    }

    interface UpdateItemView {

        void onSuccess(String target, Object data);

        void onFailure(String target);

        void onError(String target);

        void showProgressBar();

        void hideProgressBar();
    }

    interface AbuseView {

        void onReportClicked();

        void onDialogCanceled();

        void onSuccess(String target, Object data);

        void onFailure(String target);

        void showProgressBar(String target);

        void hideProgressBar(String target);

        void showMessage(String target, String message);
    }

    interface SearchView {

        void onSuccess(String target, Object data);

        void onFailure(String target);

        void onError(String target);

        void showProgressBar(String target);

        void hideProgressBar(String target);
    }
}
