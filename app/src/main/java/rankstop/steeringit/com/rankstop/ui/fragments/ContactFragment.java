package rankstop.steeringit.com.rankstop.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.BindInt;
import butterknife.BindString;
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
import rankstop.steeringit.com.rankstop.ui.dialogFragment.RSLoader;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;

public class ContactFragment extends Fragment implements RSView.StandardView {

    private View rootView;
    private Unbinder unbinder;
    private User user;
    private String fullname, email, subject, message;
    private RSLoader rsLoader;

    private RSPresenter.ContactPresenter presenter;

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

    @BindString(R.string.message_received)
    String messageReceived;
    @BindString(R.string.field_required)
    String requiredField;
    @BindString(R.string.text_contact)
    String contactTitle;
    @BindString(R.string.email_format_incorrect)
    String emailFormatIncorrect;
    @BindString(R.string.loading_msg)
    String loadingMsg;
    @BindString(R.string.off_line)
    String offlineMsg;

    @BindInt(R.integer.max_length_500)
    int maxLength500;
    @BindInt(R.integer.max_length_50)
    int maxLength50;

    private TextWatcher
            emailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().trim().length() > 0) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    inputLayoutEmail.setError(emailFormatIncorrect);
                } else {
                    inputLayoutEmail.setErrorEnabled(false);
                }
            } else {
                inputLayoutEmail.setError(requiredField);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    },
            messageTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().trim().length() > 0) {
                        inputLayoutMessage.setErrorEnabled(false);
                    } else {
                        inputLayoutMessage.setError(requiredField);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            },
            fullNameTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().trim().length() > 0) {
                        inputLayoutFullName.setErrorEnabled(false);
                    } else {
                        inputLayoutFullName.setError(requiredField);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            },
            subjectTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().trim().length() > 0) {
                        inputLayoutSubject.setErrorEnabled(false);
                    } else {
                        inputLayoutSubject.setError(requiredField);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };

    @OnClick(R.id.btn_send_request)
    void sendRequest() {
        fullname = inputFullName.getText().toString().trim();
        email = inputEmail.getText().toString().trim();
        subject = inputSubject.getText().toString().trim();
        message = inputMessage.getText().toString().trim();
        if (validForm()) {
            if (RSNetwork.isConnected()) {
                RSContact rsContact = new RSContact();
                rsContact.setName(fullname);
                rsContact.setEmail(email);
                rsContact.setSubject(subject);
                rsContact.setMessage(message);
                presenter.contact(rsContact);
            } else {
                onOffLine();
            }
        }
    }

    private boolean validForm() {
        int x = 0;
        inputLayoutFullName.setErrorEnabled(false);
        inputLayoutEmail.setErrorEnabled(false);
        inputLayoutSubject.setErrorEnabled(false);
        inputLayoutMessage.setErrorEnabled(false);

        if (TextUtils.isEmpty(fullname)) {
            inputLayoutFullName.setError(requiredField);
            x++;
        }
        if (TextUtils.isEmpty(email)) {
            inputLayoutEmail.setError(requiredField);
            x++;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputLayoutEmail.setError(emailFormatIncorrect);
            x++;
        }
        if (TextUtils.isEmpty(subject)) {
            inputLayoutSubject.setError(requiredField);
            x++;
        } else if (subject.length() > maxLength50) {
            x++;
        }
        if (TextUtils.isEmpty(message)) {
            inputLayoutMessage.setError(requiredField);
            x++;
        } else if (message.length() > maxLength500) {
            x++;
        }
        return x == 0;
    }

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
        rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bindViews();

        toolbar.setTitle(contactTitle);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (RSSession.isLoggedIn()) {
            user = RSSession.getCurrentUser();
            if (user.getFirstName() != null)
                inputFullName.setText(user.getFirstName());
            if (user.getLastName() != null)
                inputFullName.setText(inputFullName.getText().toString() + " " + user.getLastName());
            inputEmail.setText(user.getEmail());
        }

        presenter = new PresenterContact(ContactFragment.this);
    }

    private void bindViews() {
        setFragmentActionListener((ContainerActivity) getActivity());
        addTextWatcher();
        createLoader();
    }

    private void addTextWatcher() {
        inputFullName.addTextChangedListener(fullNameTextWatcher);
        inputEmail.addTextChangedListener(emailTextWatcher);
        inputSubject.addTextChangedListener(subjectTextWatcher);
        inputMessage.addTextChangedListener(messageTextWatcher);
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
                fragmentActionListener.startFragment(SettingsFragment.getInstance(), RSConstants.FRAGMENT_SETTINGS);
                break;
            case R.id.history:
                fragmentActionListener.startFragment(HistoryFragment.getInstance(""), RSConstants.FRAGMENT_HISTORY);
                break;
            case R.id.contact:
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
        inputMessage.removeTextChangedListener(messageTextWatcher);
        inputEmail.removeTextChangedListener(emailTextWatcher);
        inputSubject.removeTextChangedListener(subjectTextWatcher);
        inputFullName.removeTextChangedListener(fullNameTextWatcher);
        rootView = null;
        fragmentActionListener = null;
        if (unbinder != null)
            unbinder.unbind();
        if (presenter != null)
            presenter.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onSuccess(String target, Object data) {
        switch (target) {
            case RSConstants.RS_CONTACT:
                Toast.makeText(getContext(), messageReceived, Toast.LENGTH_LONG).show();
                fragmentActionListener.pop();
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
        rsLoader.show(getFragmentManager(), RSLoader.TAG);
    }

    @Override
    public void hideProgressBar(String target) {
        rsLoader.dismiss();
    }

    @Override
    public void showMessage(String target, String message) {

    }

    @Override
    public void onOffLine() {
        Toast.makeText(getContext(), offlineMsg, Toast.LENGTH_LONG).show();
    }
}
