package rankstop.steeringit.com.rankstop.MVP.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

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
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.session.RSSessionToken;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenterAuthImpl implements RSPresenter.LoginPresenter, RSPresenter.SignupPresenter, RSPresenter.RegisterPresenter , RSPresenter.fcmPresenter{

    private RSView.LoginView loginView;
    private RSView.SignupView signupView;
    private RSView.RegisterView registerView;
    private RSView.StandardService standardService ;
    private static final String TAG = "PresenterAuthImpl";

    public PresenterAuthImpl(RSView.LoginView loginView) {
        this.loginView = loginView;
    }


    public PresenterAuthImpl(RSView.StandardService standardService) { this.standardService = standardService ;}

    public PresenterAuthImpl(RSView.SignupView signupView) {
        this.signupView = signupView;
    }

    public PresenterAuthImpl(RSView.RegisterView registerView) {
        this.registerView = registerView;
    }

    private Call<RSResponse> callFindEmail, callLogin, callForgotPwd, callRegister, callFollowItem, callSocialLogin, CallFCM;
    private Call<GeoPluginResponse> callAddress;
    private Call<RSDeviceIP> callDeviceIP;

    @Override
    public void performFindEmail(String email, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (signupView != null) {
                if (isValidEmail(email)) {
                    signupView.showProgressBar(RSConstants.LOGIN);
                    User user = new User();
                    user.setEmail(email);
                    callFindEmail = WebService.getInstance().getApi().findEmail(user);
                    callFindEmail.enqueue(new Callback<RSResponse>() {
                        @Override
                        public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                            // Log.d("ttttt",response.body().getStatus()+"");
                            if (response.body() != null) {
                                if (response.body().getStatus() == 2) {
                                    signupView.findEmailSuccess(true, response.body().getData());
                                } else if (response.body().getStatus() == 1) {
                                    signupView.findEmailSuccess(false, null);
                                }
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
        } else {
            signupView.onOffLine();
        }
    }


    @Override
    public void findEmailByToken(String token, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (signupView != null) {
                    signupView.showProgressBar(RSConstants.LOGIN);
                    callFindEmail = WebService.getInstance().getApi().findEmailByToken(token);
                    callFindEmail.enqueue(new Callback<RSResponse>() {

                        @Override
                        public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                            if (response.code() == 403){
                                signupView.findTokenSuccess(false, null);
                            } else if (response.body() != null) {
                                if (response.body().getStatus() == 0) {
                                    signupView.findTokenSuccess(true, response.body().getData());
                                } else if (response.body().getStatus() == 1) {
                                    signupView.findTokenSuccess(false, null);
                                }
                            }
                            signupView.hideProgressBar(RSConstants.LOGIN);
                        }

                        @Override
                        public void onFailure(Call<RSResponse> call, Throwable t) {
                            if (!call.isCanceled())
                                signupView.hideProgressBar(RSConstants.LOGIN);
                        }
                    });

            }
        } else {
            signupView.onOffLine();
        }
    }

    @Override
    public void performSocialLogin(RSRequestSocialLogin user, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (signupView != null) {
                callSocialLogin = WebService.getInstance().getApi().socialLogin(user);
                callSocialLogin.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus() == 1) {
                                signupView.socialLoginSuccess(response.body().getData());
                            } else if (response.body().getStatus() == 0) {
                                signupView.hideProgressBar(RSConstants.SOCIAL_LOGIN);
                                signupView.socialLoginError(response.body().getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!call.isCanceled())
                            signupView.hideProgressBar(RSConstants.SOCIAL_LOGIN);
                    }
                });
            }
        } else {
            signupView.onOffLine();
        }
    }

    @Override
    public void onDestroyFindEmail(Context context) {
        signupView = null;
        if (callFindEmail != null)
            if (callFindEmail.isExecuted())
                callFindEmail.cancel();

        if (callSocialLogin != null)
            if (callSocialLogin.isExecuted())
                callSocialLogin.cancel();
    }

    @Override
    public void performLogin(User user, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (loginView != null) {
                loginView.showProgressBar(RSConstants.LOGIN);
                callLogin = WebService.getInstance().getApi().loginUser(user);
                callLogin.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus() == 0) {
                                loginView.loginError();
                                loginView.hideProgressBar(RSConstants.LOGIN);
                            } else if (response.body().getStatus() == 1) {
                                loginView.loginSuccess(response.body().getData());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!call.isCanceled())
                            loginView.hideProgressBar(RSConstants.LOGIN);
                    }
                });
            }
        } else {
            loginView.onOffLine();
        }
    }

    @Override
    public void followItem(RSFollow rsFollow, String target, Context context) {
        if (RSNetwork.isConnected(context)) {
            callFollowItem = WebService.getInstance().getApi().followItem(RSSessionToken.getUsergestToken(), rsFollow);
            callFollowItem.enqueue(new Callback<RSResponse>() {
                @Override
                public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                    if (response.code() == RSConstants.CODE_TOKEN_EXPIRED) {
                        RSSession.Reconnecter();
                        followItem(rsFollow, target, context);
                    } else {
                        if (response.body() != null) {
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
                        } else {
                            if (target.equals(RSConstants.LOGIN)) {
                                loginView.hideProgressBar(RSConstants.FOLLOW_ITEM);
                            } else if (target.equals(RSConstants.REGISTER)){
                                registerView.hideProgressBar(RSConstants.FOLLOW_ITEM);
                            } else if (target.equals(RSConstants.SOCIAL_LOGIN)){
                                signupView.hideProgressBar(RSConstants.FOLLOW_ITEM);
                            }

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
        } else {
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
    public void forgotPassword(String email, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (loginView != null) {
                loginView.showProgressBar(RSConstants.FORGOT_PWD);
                callLogin = WebService.getInstance().getApi().forgotPassword(email);
                callLogin.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus() == 0) {
                                loginView.onError(RSConstants.FORGOT_PWD);
                            } else if (response.body().getStatus() == 1) {
                                loginView.onSuccess(RSConstants.FORGOT_PWD);
                            }
                        }

                        loginView.hideProgressBar(RSConstants.FORGOT_PWD);
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!call.isCanceled())
                            loginView.hideProgressBar(RSConstants.FORGOT_PWD);
                    }
                });
            }
        } else {
            loginView.onOffLine();
        }
    }

    @Override
    public void onDestroyLogin(Context context) {
        loginView = null;
        if (callLogin != null)
            if (callLogin.isExecuted())
                callLogin.cancel();

        if (callFollowItem != null)
            if (callFollowItem.isExecuted())
                callFollowItem.cancel();

        if (callForgotPwd != null)
            if (callForgotPwd.isExecuted())
                callForgotPwd.cancel();
    }

    private boolean isValidEmail(String value) {
        if (TextUtils.isEmpty(value) || value.length() < 6) {
            return false;
        }
        return true;
    }

    @Override
    public void performRegister(User user, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (registerView != null) {
                callRegister = WebService.getInstance().getApi().registerUser(user);
                callRegister.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus() == 0) {
                                registerView.registerError();
                                registerView.hideProgressBar(RSConstants.REGISTER);
                            } else if (response.body().getStatus() == 1) {
                                registerView.registerSuccess(response.body().getData());
                            }
                        } else {
                            registerView.hideProgressBar(RSConstants.REGISTER);
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!call.isCanceled())
                            registerView.hideProgressBar(RSConstants.REGISTER);
                    }
                });
            }
        } else {
            registerView.onOffLine();
        }
    }

    @Override
    public void getAddress(String ip, String target, Context context) {
        if (RSNetwork.isConnected(context)) {
            callAddress = WebService.getInstance(Urls.GEO_PLUGIN_URL, null).getApi().getAddressFromIP(ip);
            callAddress.enqueue(new Callback<GeoPluginResponse>() {
                @Override
                public void onResponse(Call<GeoPluginResponse> call, Response<GeoPluginResponse> response) {
                    if (target.equals(RSConstants.REGISTER)) {
                        if (response.body() != null) {
                            registerView.onAddressFetched(response.body());
                            Log.e(TAG , "success REGISTER");
                        } else {
                            registerView.onAddressFailed();
                            Log.e(TAG , "failed REGISTER");
                        }
                    } else if (target.equals(RSConstants.SOCIAL_LOGIN)) {
                        if (response.body() != null) {
                            Log.e(TAG , "success SOCIAL_LOGIN");
                            signupView.onAddressFetched(response.body());
                        } else {
                            signupView.onAddressFailed();
                            Log.e(TAG , "failed SOCIAL_LOGIN");
                        }
                    }
                }

                @Override
                public void onFailure(Call<GeoPluginResponse> call, Throwable t) {
                    if (!call.isCanceled()) {
                        if (target.equals(RSConstants.REGISTER)) {
                            registerView.hideProgressBar(RSConstants.REGISTER);
                        } else if (target.equals(RSConstants.SOCIAL_LOGIN) ){
                            registerView.hideProgressBar("");
                        } else {
                            registerView.hideProgressBar("");
                        }
                    }
                }
            });
        } else {
            if (target.equals(RSConstants.REGISTER)) {
                registerView.onOffLine();
            } else if (target.equals(RSConstants.SOCIAL_LOGIN)) {
                signupView.onOffLine();
            }
        }
    }

    @Override
    public void getPublicIP(String format, String target, Context context) {

        if (RSNetwork.isConnected(context)) {
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
        } else {
            if (target.equals(RSConstants.REGISTER)) {
                registerView.onOffLine();
            } else if (target.equals(RSConstants.SOCIAL_LOGIN)) {
                signupView.onOffLine();
            }
        }
    }

    @Override
    public void onDestroyRegister(Context context) {
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

    @Override
    public void sendRegistrationTokenToServer(String token, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (standardService != null) {

                CallFCM = WebService.getInstance().getApi().updateRegistrationToken(RSSession.getCurrentUser().get_id() ,token);
                CallFCM.enqueue(new Callback<RSResponse>() {

                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                         if (response.body() != null) {
                            if (response.body().getStatus() == 0) {
                                standardService.onFailure();
                            } else if (response.body().getStatus() == 1) {
                                standardService.onSuccess( response.body().getData());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!call.isCanceled()){
                            standardService.onFailure();

                        }
                    }
                });

            }
        }

    }
}
