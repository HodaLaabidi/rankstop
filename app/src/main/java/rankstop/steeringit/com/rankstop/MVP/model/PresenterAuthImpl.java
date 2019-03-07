package rankstop.steeringit.com.rankstop.MVP.model;

import android.text.TextUtils;

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
import rankstop.steeringit.com.rankstop.utils.RSNetwork;
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

    private Call<RSResponse> callFindEmail, callLogin, callRegister, callFollowItem, callSocialLogin;
    private Call<GeoPluginResponse> callAddress;
    private Call<RSDeviceIP> callDeviceIP;

    @Override
    public void performFindEmail(String email) {
        if (RSNetwork.isConnected()) {
            if (signupView != null) {
                if (isValidEmail(email)) {
                    signupView.showProgressBar(RSConstants.LOGIN);
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
                            signupView.hideProgressBar(RSConstants.LOGIN);
                        }

                        @Override
                        public void onFailure(Call<RSResponse> call, Throwable t) {
                            if (!call.isCanceled())
                                signupView.hideProgressBar(RSConstants.LOGIN);
                        }
                    });
                } else {
                    signupView.findEmailValidations();
                }
            }
        }else {
            signupView.onOffLine();
        }
    }

    @Override
    public void performSocialLogin(RSRequestSocialLogin user) {
        if (RSNetwork.isConnected()) {
            if (signupView != null) {
                callSocialLogin = WebService.getInstance().getApi().socialLogin(user);
                callSocialLogin.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body().getStatus() == 1) {
                            signupView.socialLoginSuccess(response.body().getData());
                        } else if (response.body().getStatus() == 0) {
                            signupView.hideProgressBar(RSConstants.SOCIAL_LOGIN);
                            signupView.socialLoginError(response.body().getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!call.isCanceled())
                            signupView.hideProgressBar(RSConstants.SOCIAL_LOGIN);
                    }
                });
            }
        }else {
            signupView.onOffLine();
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
        if (RSNetwork.isConnected()) {
            if (loginView != null) {
                loginView.showProgressBar(RSConstants.LOGIN);
                callLogin = WebService.getInstance().getApi().loginUser(user);
                callLogin.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body().getStatus() == 0) {
                            loginView.loginError();
                            loginView.hideProgressBar(RSConstants.LOGIN);
                        } else if (response.body().getStatus() == 1) {
                            loginView.loginSuccess(response.body().getData());
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!call.isCanceled())
                            loginView.hideProgressBar(RSConstants.LOGIN);
                    }
                });
            }
        }else {
            loginView.onOffLine();
        }
    }

    @Override
    public void followItem(RSFollow rsFollow, String target) {
        if (RSNetwork.isConnected()) {
            callFollowItem = WebService.getInstance().getApi().followItem(rsFollow);
            callFollowItem.enqueue(new Callback<RSResponse>() {
                @Override
                public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                    if (response.body().getStatus() == 1) {
                        if (target.equals(RSConstants.LOGIN)) {
                            loginView.onFollowSuccess(RSConstants.FOLLOW_ITEM, "1");
                            loginView.hideProgressBar(RSConstants.FOLLOW_ITEM);
                        } else if (target.equals(RSConstants.REGISTER)) {
                            registerView.onFollowSuccess(RSConstants.FOLLOW_ITEM, "1");
                            registerView.hideProgressBar(RSConstants.FOLLOW_ITEM);
                        } else if (target.equals(RSConstants.SOCIAL_LOGIN)) {
                            signupView.onFollowSuccess(RSConstants.FOLLOW_ITEM, "1");
                            signupView.hideProgressBar(RSConstants.FOLLOW_ITEM);
                        }
                    } else if (response.body().getStatus() == 0) {
                        if (target.equals(RSConstants.LOGIN)) {
                            loginView.onFollowSuccess(RSConstants.FOLLOW_ITEM, "0");
                            loginView.hideProgressBar(RSConstants.FOLLOW_ITEM);
                        } else if (target.equals(RSConstants.REGISTER)) {
                            registerView.onFollowSuccess(RSConstants.FOLLOW_ITEM, "0");
                            registerView.hideProgressBar(RSConstants.FOLLOW_ITEM);
                        } else if (target.equals(RSConstants.SOCIAL_LOGIN)) {
                            signupView.onFollowSuccess(RSConstants.FOLLOW_ITEM, "0");
                            signupView.hideProgressBar(RSConstants.FOLLOW_ITEM);
                        }
                    }

                }

                @Override
                public void onFailure(Call<RSResponse> call, Throwable t) {
                    if (!callFollowItem.isCanceled()) {
                        if (target.equals(RSConstants.LOGIN)) {
                            loginView.onFollowFailure(RSConstants.FOLLOW_ITEM);
                            loginView.hideProgressBar(RSConstants.FOLLOW_ITEM);
                        } else if (target.equals(RSConstants.REGISTER)) {
                            registerView.onFollowFailure(RSConstants.FOLLOW_ITEM);
                            registerView.hideProgressBar(RSConstants.FOLLOW_ITEM);
                        } else if (target.equals(RSConstants.SOCIAL_LOGIN)) {
                            signupView.onFollowFailure(RSConstants.FOLLOW_ITEM);
                            signupView.hideProgressBar(RSConstants.FOLLOW_ITEM);
                        }
                    }
                }
            });
        }else {
            if (target.equals(RSConstants.LOGIN)) {
                loginView.onOffLine();
            } else if (target.equals(RSConstants.REGISTER)) {
                registerView.onOffLine();
            } else if (target.equals(RSConstants.SOCIAL_LOGIN)) {
                signupView.onOffLine();
            }
        }
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
        if (RSNetwork.isConnected()) {
            if (registerView != null) {
                callRegister = WebService.getInstance().getApi().registerUser(user);
                callRegister.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body().getStatus() == 0) {
                            registerView.registerError();
                            registerView.hideProgressBar(RSConstants.REGISTER);
                        } else if (response.body().getStatus() == 1) {
                            registerView.registerSuccess(response.body().getData());
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!call.isCanceled())
                            registerView.hideProgressBar(RSConstants.REGISTER);
                    }
                });
            }
        }else {
            registerView.onOffLine();
        }
    }

    @Override
    public void getAddress(String ip, String target) {
        if (RSNetwork.isConnected()) {
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
                    } else if (target.equals(RSConstants.SOCIAL_LOGIN)) {
                        if (response.body() != null) {
                            signupView.onAddressFetched(response.body());
                        } else {
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
        }else {
            if (target.equals(RSConstants.REGISTER)) {
                registerView.onOffLine();
            } else if (target.equals(RSConstants.SOCIAL_LOGIN)) {
                signupView.onOffLine();
            }
        }
    }

    @Override
    public void getPublicIP(String format, String target) {

        if (RSNetwork.isConnected()) {
            if (target.equals(RSConstants.REGISTER)) {
                registerView.showProgressBar(RSConstants.PUBLIC_IP);
            } else if (target.equals(RSConstants.SOCIAL_LOGIN)) {
                signupView.showProgressBar(RSConstants.PUBLIC_IP);
            }

            callDeviceIP = WebService.getInstance(Urls.IP_FINDER).getApi().getPublicIP(format);
            callDeviceIP.enqueue(new Callback<RSDeviceIP>() {
                @Override
                public void onResponse(Call<RSDeviceIP> call, Response<RSDeviceIP> response) {

                    if (target.equals(RSConstants.REGISTER)) {
                        if (response.body() != null) {
                            registerView.onPublicIPFetched(response.body());
                        } else {
                            registerView.onPublicIPFailed();
                        }
                    } else if (target.equals(RSConstants.SOCIAL_LOGIN)) {
                        if (response.body() != null) {
                            signupView.onPublicIPFetched(response.body());
                        } else {
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
        }else {
            if (target.equals(RSConstants.REGISTER)) {
                registerView.onOffLine();
            } else if (target.equals(RSConstants.SOCIAL_LOGIN)) {
                signupView.onOffLine();
            }
        }
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
