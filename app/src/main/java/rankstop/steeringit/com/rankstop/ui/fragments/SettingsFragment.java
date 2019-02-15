package rankstop.steeringit.com.rankstop.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterContact;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterDeviceLangImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.RankStop;
import rankstop.steeringit.com.rankstop.customviews.RSRBMedium;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.RSLoader;
import rankstop.steeringit.com.rankstop.utils.RSConstants;

import static rankstop.steeringit.com.rankstop.utils.LocaleManager.LANGUAGE_ENGLISH;
import static rankstop.steeringit.com.rankstop.utils.LocaleManager.LANGUAGE_FRENSH;
import static rankstop.steeringit.com.rankstop.utils.LocaleManager.LANGUAGE_GERMAN;

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
        setFragmentActionListener((ContainerActivity) getActivity());
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

        listLangRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Toast.makeText(getContext(), "checked = ", Toast.LENGTH_SHORT).show();
                if (checkedId == R.id.rb_en)
                    manageLanguage("en");
                else if (checkedId == R.id.rb_fr)
                    manageLanguage("fr");
                else if (checkedId == R.id.rb_de)
                    manageLanguage("de");
            }
        });
    }

    private void manageLanguage(String lang) {
        if (RSSession.isLoggedIn()) {
            presenter.editLang(RSSession.getCurrentUser().get_id(), lang);
        } else {
            setNewLocale(lang, false);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.rs_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
            case R.id.setting:
                break;
            case R.id.history:
                fragmentActionListener.startFragment(HistoryFragment.getInstance(""), RSConstants.FRAGMENT_HISTORY);
                break;
            case R.id.contact:
                fragmentActionListener.startFragment(ContactFragment.getInstance(), RSConstants.FRAGMENT_CONTACT);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private FragmentActionListener fragmentActionListener;

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
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
        fragmentActionListener = null;
        if (unbinder != null)
            unbinder.unbind();
        if (presenter != null)
            presenter.onDestroy();
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
            Toast.makeText(getContext(), "Activity restarted", Toast.LENGTH_SHORT).show();
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
