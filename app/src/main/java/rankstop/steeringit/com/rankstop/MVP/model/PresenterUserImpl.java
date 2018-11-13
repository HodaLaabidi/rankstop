package rankstop.steeringit.com.rankstop.MVP.model;

import android.text.TextUtils;

import rankstop.steeringit.com.rankstop.data.model.User;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.data.model.custom.RSResponse;
import rankstop.steeringit.com.rankstop.data.webservices.WebService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenterUserImpl implements RSPresenter.LoginPresenter, RSPresenter.SignupPresenter, RSPresenter.RegisterPresenter {

    private RSView.LoginView loginView;
    private RSView.SignupView signupView;
    private RSView.RegisterView registerView;

    public PresenterUserImpl(RSView.LoginView loginView) {
        this.loginView = loginView;
    }

    public PresenterUserImpl(RSView.SignupView signupView) {
        this.signupView = signupView;
    }

    public PresenterUserImpl(RSView.RegisterView registerView) {
        this.registerView = registerView;
    }

    private boolean isValidPassword(String value) {
        if (TextUtils.isEmpty(value) || value.length() < 6) {
            return false;
        }
        return true;
    }

    @Override
    public void performFindEmail(String email) {
        if (isValidEmail(email)) {
            signupView.showProgressBar();

            User user = new User();
            user.setEmail(email);

            WebService.getInstance().getApi().findEmail(user).enqueue(new Callback<RSResponse>() {
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
                    signupView.hideProgressBar();
                }
            });
        } else {
            signupView.findEmailValidations();
        }
    }

    @Override
    public void onDestroyFindEmail() {
        signupView = null;
    }

    @Override
    public void performLogin(User user) {
        if (isValidPassword(user.getPassword())) {
            loginView.showProgressBar();

            WebService.getInstance().getApi().loginUser(user).enqueue(new Callback<RSResponse>() {
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
                    loginView.hideProgressBar();
                }
            });
        } else {
            loginView.loginValidations();
        }
    }

    @Override
    public void onDestroyLogin() {
        loginView = null;
    }

    private boolean isValidEmail(String value) {
        if (TextUtils.isEmpty(value) || value.length() < 6) {
            return false;
        }
        return true;
    }

    @Override
    public void performRegister(User user) {
        if (isValidPassword(user.getPassword())) {
            registerView.showProgressBar();

            WebService.getInstance().getApi().registerUser(user).enqueue(new Callback<RSResponse>() {
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
                    registerView.hideProgressBar();
                }
            });
        } else {
            registerView.registerValidations();
        }
    }

    @Override
    public void onDestroyRegister() {
        registerView = null;
    }
}
