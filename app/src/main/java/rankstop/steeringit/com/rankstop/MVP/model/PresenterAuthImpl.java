package rankstop.steeringit.com.rankstop.MVP.model;

import android.text.TextUtils;
import android.util.Log;

import rankstop.steeringit.com.rankstop.data.model.db.User;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.data.model.network.GeoPluginResponse;
import rankstop.steeringit.com.rankstop.data.model.network.RSDeviceIP;
import rankstop.steeringit.com.rankstop.data.model.network.RSFollow;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestSocialLogin;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponse;
import rankstop.steeringit.com.rankstop.data.webservices.Urls;
import rankstop.steeringit.com.rankstop.data.webservices.WebService;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenterAuthImpl implements RSPresenter.LoginPresenter, RSPresenter.SignupPresenter, RSPresenter.RegisterPresenter {

    private RSView.LoginView loginView;
    private RSView.SignupView signupView;
    private RSView.RegisterView registerView;

    public PresenterAuthImpl(RSView.LoginView loginView) {
        this.loginView = loginView;
    }

    public PresenterAuthImpl(RSView.SignupView signupView) {
        this.signupView = signupView;
    }

    public PresenterAuthImpl(RSView.RegisterView registerView) {
        this.registerView = registerView;
    }

    private boolean isValidPassword(String value) {
        if (TextUtils.isEmpty(value) || value.length() < 6) {
            return false;
        }
        return true;
    }

    private Call<RSResponse> callFindEmail, callLogin, callRegister, callFollowItem, callSocialLogin;
    private Call<GeoPluginResponse> callAddress;
    private Call<RSDeviceIP> callDeviceIP;

    @Override
    public void performFindEmail(String email) {
        if (signupView != null) {
            if (isValidEmail(email)) {
                signupView.showProgressBar();
                User user = new User();
                user.setEmail(email);
                callFindEmail = WebService.getInstance().getApi().findEmail(user);
                callFindEmail.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body().getStatus() == 2) {
                            signupView.findEmailSuccess(true, response.body().getData());
                        } else if (response.body().getStatus() == 1) {
                            signupView.findEmailSuccess(false, null);
                        }
                        signupView.hideProgressBar();
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!call.isCanceled())
                            signupView.hideProgressBar();
                    }
                });
            } else {
                signupView.findEmailValidations();
            }
        }
    }

    @Override
    public void performSocialLogin(RSRequestSocialLogin user) {
        if (signupView != null) {
            signupView.showProgressBar();
            callSocialLogin = WebService.getInstance().getApi().socialLogin(user);
            callSocialLogin.enqueue(new Callback<RSResponse>() {
                @Override
                public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                    if (response.body().getStatus() == 1) {
                        signupView.socialLoginSuccess(response.body().getData());
                    } else if (response.body().getStatus() == 0) {
                        signupView.socialLoginError(response.body().getMessage());
                    }
                    signupView.hideProgressBar();
                }

                @Override
                public void onFailure(Call<RSResponse> call, Throwable t) {

                    Log.i("TAG_REGISTER", "failure");
                    if (!call.isCanceled())
                        signupView.hideProgressBar();
                }
            });

        }
    }

    @Override
    public void onDestroyFindEmail() {
        signupView = null;
        if (callFindEmail != null)
            if (callFindEmail.isExecuted())
                callFindEmail.cancel();

        if (callSocialLogin != null)
            if (callSocialLogin.isExecuted())
                callSocialLogin.cancel();
    }

    @Override
    public void performLogin(User user) {
        if (loginView != null) {
            if (isValidPassword(user.getPassword())) {
                loginView.showProgressBar();

                callLogin = WebService.getInstance().getApi().loginUser(user);
                callLogin.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body().getStatus() == 0) {
                            loginView.loginError();
                        } else if (response.body().getStatus() == 1) {
                            loginView.loginSuccess(response.body().getData());
                        }
                        loginView.hideProgressBar();
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!call.isCanceled())
                            loginView.hideProgressBar();
                    }
                });
            } else {
                loginView.loginValidations();
            }
        }
    }

    @Override
    public void followItem(RSFollow rsFollow, String target) {
        callFollowItem = WebService.getInstance().getApi().followItem(rsFollow);
        callFollowItem.enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    if (target.equals(RSConstants.LOGIN))
                        loginView.onFollowSuccess(RSConstants.FOLLOW_ITEM, "1");
                    else if (target.equals(RSConstants.REGISTER))
                        registerView.onFollowSuccess(RSConstants.FOLLOW_ITEM, "1");
                    else if (target.equals(RSConstants.SOCIAL_LOGIN))
                        signupView.onFollowSuccess(RSConstants.FOLLOW_ITEM, "1");
                } else if (response.body().getStatus() == 0) {
                    if (target.equals(RSConstants.LOGIN))
                        loginView.onFollowSuccess(RSConstants.FOLLOW_ITEM, "0");
                    else if (target.equals(RSConstants.REGISTER))
                        registerView.onFollowSuccess(RSConstants.FOLLOW_ITEM, "0");
                    else if (target.equals(RSConstants.SOCIAL_LOGIN))
                        signupView.onFollowSuccess(RSConstants.FOLLOW_ITEM, "0");
                }
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {
                if (!callFollowItem.isCanceled()) {
                    if (target.equals(RSConstants.LOGIN))
                        loginView.onFollowFailure(RSConstants.FOLLOW_ITEM);
                    else if (target.equals(RSConstants.REGISTER))
                        registerView.onFollowFailure(RSConstants.FOLLOW_ITEM);
                    else if (target.equals(RSConstants.SOCIAL_LOGIN))
                        signupView.onFollowFailure(RSConstants.FOLLOW_ITEM);
                }
            }
        });
    }

    @Override
    public void onDestroyLogin() {
        loginView = null;
        if (callLogin != null)
            if (callLogin.isExecuted())
                callLogin.cancel();

        if (callFollowItem != null)
            if (callFollowItem.isExecuted())
                callFollowItem.cancel();
    }

    private boolean isValidEmail(String value) {
        if (TextUtils.isEmpty(value) || value.length() < 6) {
            return false;
        }
        return true;
    }

    @Override
    public void performRegister(User user) {
        if (registerView != null) {
            if (isValidPassword(user.getPassword())) {
                registerView.showProgressBar();

                callRegister = WebService.getInstance().getApi().registerUser(user);
                callRegister.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body().getStatus() == 0) {
                            registerView.registerError();
                        } else if (response.body().getStatus() == 1) {
                            registerView.registerSuccess(response.body().getData());
                        }
                        registerView.hideProgressBar();
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!call.isCanceled())
                            registerView.hideProgressBar();
                    }
                });
            } else {
                registerView.registerValidations();
            }
        }
    }

    @Override
    public void getAddress(String ip, String target) {
            callAddress = WebService.getInstance(Urls.GEO_PLUGIN_URL, null).getApi().getAddressFromIP(ip);
            callAddress.enqueue(new Callback<GeoPluginResponse>() {
                @Override
                public void onResponse(Call<GeoPluginResponse> call, Response<GeoPluginResponse> response) {

                    if (target.equals(RSConstants.REGISTER)) {
                        if (response.body() != null) {
                            registerView.onAddressFetched(response.body());
                        } else {
                            registerView.onAddressFailed();
                        }
                    }else if (target.equals(RSConstants.SOCIAL_LOGIN)) {
                        if (response.body() != null) {
                            signupView.onAddressFetched(response.body());
                        }else {
                            signupView.onAddressFailed();
                        }
                    }
                }

                @Override
                public void onFailure(Call<GeoPluginResponse> call, Throwable t) {
                    if (!call.isCanceled()) {
                        //registerView.hideProgressBar();
                    }
                }
            });
    }

    @Override
    public void getPublicIP(String format, String target) {
            callDeviceIP = WebService.getInstance(Urls.IP_FINDER).getApi().getPublicIP(format);
            callDeviceIP.enqueue(new Callback<RSDeviceIP>() {
                @Override
                public void onResponse(Call<RSDeviceIP> call, Response<RSDeviceIP> response) {

                    if (target.equals(RSConstants.REGISTER)) {
                        if (response.body() != null) {
                            registerView.onPublicIPFetched(response.body());
                        }else {
                            registerView.onPublicIPFailed();
                        }
                    }else if (target.equals(RSConstants.SOCIAL_LOGIN)) {
                        if (response.body() != null) {
                            signupView.onPublicIPFetched(response.body());
                        }else {
                            signupView.onPublicIPFailed();
                        }
                    }
                }

                @Override
                public void onFailure(Call<RSDeviceIP> call, Throwable t) {
                    if (!call.isCanceled()) {
                        //registerView.hideProgressBar();
                    }
                }
            });
    }

    @Override
    public void onDestroyRegister() {
        registerView = null;
        if (callRegister != null)
            if (callRegister.isExecuted())
                callRegister.cancel();

        if (callAddress != null)
            if (callAddress.isExecuted())
                callAddress.cancel();

        if (callDeviceIP != null)
            if (callDeviceIP.isExecuted())
                callDeviceIP.cancel();
    }
}
