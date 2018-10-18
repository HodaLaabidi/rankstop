package rankstop.steeringit.com.rankstop.Model;

import android.text.TextUtils;

import rankstop.steeringit.com.rankstop.Presenter.LoginPresenter;
import rankstop.steeringit.com.rankstop.View.LoginView;

public class PresenterImpl implements LoginPresenter {

    private LoginView loginView;

    public PresenterImpl(LoginView loginView) {
        this.loginView = loginView;
    }

    @Override
    public void performLogin(String password) {
        if(isValidPassword(password)){
            loginView.showProgressBar();

            if (password.equals("samire")){
                loginView.loginSuccess();
            }else {
                loginView.loginError();
            }

            loginView.hideProgressBar();
        }else {
            loginView.LoginValidations();
        }
    }

    private boolean isValidPassword(String value) {
        if (TextUtils.isEmpty(value) || value.length() < 6) {
            return false;
        }
        return true;
    }
}
