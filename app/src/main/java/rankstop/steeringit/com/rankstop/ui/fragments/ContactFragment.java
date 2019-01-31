package rankstop.steeringit.com.rankstop.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterContact;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.customviews.RSETMedium;
import rankstop.steeringit.com.rankstop.data.model.db.RSContact;
import rankstop.steeringit.com.rankstop.data.model.db.User;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;

public class ContactFragment extends Fragment implements RSView.StandardView {

    private View rootView;
    private Unbinder unbinder;
    private User user;
    private String fullname, email, subject, message;

    private RSPresenter.ContactPresenter presenter;

    private ProgressDialog progressDialog;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.input_layout_fullname)
    TextInputLayout inputLayoutFullName;
    @BindView(R.id.input_fullname)
    RSETMedium inputFullName;

    @BindView(R.id.input_layout_email)
    TextInputLayout inputLayoutEmail;
    @BindView(R.id.input_email)
    RSETMedium inputEmail;

    @BindView(R.id.input_layout_subject)
    TextInputLayout inputLayoutSubject;
    @BindView(R.id.input_subject)
    RSETMedium inputSubject;

    @BindView(R.id.input_layout_message)
    TextInputLayout inputLayoutMessage;
    @BindView(R.id.input_message)
    RSETMedium inputMessage;

    @OnClick(R.id.btn_send_request)
    void sendRequest() {

        fullname = inputFullName.getText().toString();
        email = inputEmail.getText().toString();
        subject = inputSubject.getText().toString();
        message = inputMessage.getText().toString();

        if (validForm()) {
            RSContact rsContact = new RSContact();
            rsContact.setName(fullname);
            rsContact.setEmail(email);
            rsContact.setSubject(subject);
            rsContact.setMessage(message);

            presenter.contact(rsContact);
        }
    }

    private boolean validForm() {
        int x = 0;

        if (TextUtils.isEmpty(fullname)) {
            inputLayoutFullName.setError("this field is required");
            x++;
        }
        if (TextUtils.isEmpty(email)) {
            inputLayoutEmail.setError("this field is required");
            x++;
        }
        if (TextUtils.isEmpty(subject)) {
            inputLayoutSubject.setError("this field is required");
            x++;
        }
        if (TextUtils.isEmpty(message)) {
            inputLayoutMessage.setError("this field is required");
            x++;
        }
        return x == 0;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bindViews();

        toolbar.setTitle(getResources().getString(R.string.text_contact));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (RSSession.isLoggedIn()){
            user = RSSession.getCurrentUser();
            inputFullName.setText(user.getFirstName() + " " + user.getLastName());
            inputEmail.setText(user.getEmail());
        }

        presenter = new PresenterContact(ContactFragment.this);
    }

    private void bindViews() {

        setFragmentActionListener((ContainerActivity)getActivity());

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
            case R.id.setting:
                fragmentActionListener.startFragment(SettingsFragment.getInstance(), RSConstants.FRAGMENT_SETTINGS);
                break;
            case R.id.logout:
                /*RSSession.removeToken(getContext());
                ((ContainerActivity)getActivity()).manageSession(false);*/
                break;
            case R.id.history:
                fragmentActionListener.startFragment(HistoryFragment.getInstance(""), RSConstants.FRAGMENT_HISTORY);
                break;
            case R.id.contact:
                fragmentActionListener.startFragment(ContactFragment.getInstance(), RSConstants.FRAGMENT_CONTACT);
                break;
            case R.id.notifications:
                fragmentActionListener.startFragment(ListNotifFragment.getInstance(), RSConstants.FRAGMENT_NOTIF);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private FragmentActionListener fragmentActionListener;
    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }
    private static ContactFragment instance;

    public static ContactFragment getInstance() {
        if (instance == null) {
            instance = new ContactFragment();
        }
        return instance;
    }

    @Override
    public void onDestroyView() {
        instance = null;
        rootView=null;
        fragmentActionListener = null;
        unbinder.unbind();
        presenter.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onSuccess(String target, Object data) {
        switch (target) {
            case RSConstants.RS_CONTACT:
                Toast.makeText(getContext(), "Votre message envoyé avec succés", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onFailure(String target) {

    }

    @Override
    public void onError(String target) {

    }

    @Override
    public void showProgressBar(String target) {
        progressDialog.show();
    }

    @Override
    public void hideProgressBar(String target) {
        progressDialog.dismiss();
    }

    @Override
    public void showMessage(String target, String message) {

    }
}
