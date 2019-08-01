package rankstop.steeringit.com.rankstop.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterDeviceLangImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.RankStop;
import rankstop.steeringit.com.rankstop.customviews.RSRBMedium;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.RSLoader;

public class SettingsFragment extends Fragment implements RSView.EditLangView {

    private View rootView;
    private Unbinder unbinder;

    private RSPresenter.EditDeviceLangPresenter presenter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rg_btn_language)
    RadioGroup listLangRG;
    @BindView(R.id.rb_en)
    RSRBMedium englishRB;
    @BindView(R.id.rb_fr)
    RSRBMedium frenshRB;
    @BindView(R.id.rb_de)
    RSRBMedium germanRB;

    @BindString(R.string.text_settings)
    String settingsTitle;

    @BindString(R.string.loading_msg)
    String loadingMsg;
    private RSLoader rsLoader;

    private void createLoader() {
        rsLoader = RSLoader.newInstance(loadingMsg);
        rsLoader.setCancelable(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bindViews();

        toolbar.setTitle(settingsTitle);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void bindViews() {
        createLoader();
        presenter = new PresenterDeviceLangImpl(SettingsFragment.this);

        switch (RankStop.getDeviceLanguage()) {
            case "fr":
                frenshRB.setChecked(true);
                break;
            case "en":
                englishRB.setChecked(true);
                break;
            case "de":
                germanRB.setChecked(true);
                break;

        }

        listLangRG.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_en)
                manageLanguage("en");
            else if (checkedId == R.id.rb_fr)
                manageLanguage("fr");
            else if (checkedId == R.id.rb_de)
                manageLanguage("de");
        });
    }

    private void manageLanguage(String lang) {
        if (RSSession.isLoggedIn()) {
            presenter.editLang(RSSession.getCurrentUser().get_id(), lang, getContext());
        } else {
            setNewLocale(lang, false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private static SettingsFragment instance;

    public static SettingsFragment getInstance() {
        if (instance == null) {
            instance = new SettingsFragment();
        }
        return instance;
    }

    @Override
    public void onDestroyView() {
        instance = null;
        rootView = null;
        if (unbinder != null)
            unbinder.unbind();
        if (presenter != null)
            presenter.onDestroy(getContext());
        super.onDestroyView();
    }

    private boolean setNewLocale(String language, boolean restartProcess) {
        RankStop.localeManager.setNewLocale(getActivity(), language);
        RankStop.currentLanguage = language;

        Intent i = new Intent(getContext(), ContainerActivity.class);
        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

        if (restartProcess) {
            System.exit(0);
        } else {
        }
        return true;
    }


    @Override
    public void onSuccess(String lang, Object data) {
        setNewLocale(lang, false);
    }

    @Override
    public void onFailure() {

    }

    @Override
    public void onError() {

    }

    @Override
    public void showProgressBar() {
        rsLoader.show(getFragmentManager(), RSLoader.TAG);
    }

    @Override
    public void hideProgressBar() {
        rsLoader.dismiss();
    }

    @Override
    public void onOffLine() {

    }
}
