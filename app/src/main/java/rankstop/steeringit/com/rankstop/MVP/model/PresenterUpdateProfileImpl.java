package rankstop.steeringit.com.rankstop.MVP.model;

import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.data.model.custom.RSResponse;
import retrofit2.Call;

public class PresenterUpdateProfileImpl implements RSPresenter.UpdateProfilePresenter {

    private RSView.StandardView standardView;
    private Call<RSResponse> callEditPwd, callEditProfile;

    public PresenterUpdateProfileImpl(RSView.StandardView standardView) {
        this.standardView = standardView;
    }

    @Override
    public void editPassword() {
        if (standardView != null) {}
    }

    @Override
    public void editProfile() {
        if (standardView != null) {}
    }

    @Override
    public void onDestroy() {
        if (callEditPwd != null)
            if (callEditPwd.isExecuted())
                callEditPwd.cancel();
        if (callEditProfile != null)
            if (callEditProfile.isExecuted())
                callEditProfile.cancel();
        standardView = null;
    }
}
