package rankstop.steeringit.com.rankstop.ui.dialogFragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
import rankstop.steeringit.com.rankstop.customviews.RSCustomToast;
import rankstop.steeringit.com.rankstop.customviews.RSETMedium;
import rankstop.steeringit.com.rankstop.data.model.db.RSContact;
import rankstop.steeringit.com.rankstop.data.model.db.User;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;

public class ContactDialog extends DialogFragment implements RSView.StandardView {


    public static String TAG = "CONTACT_DIALOG";

    private View rootView;
    private Unbinder unbinder;
    private String fullname, email, subject, message;
    private RSLoader rsLoader;

    private RSPresenter.ContactPresenter presenter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.input_layout_full_name)
    TextInputLayout inputLayoutFullName;
    @BindView(R.id.input_full_name)
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
            if (RSNetwork.isConnected(getContext())) {
                RSContact rsContact = new RSContact();
                rsContact.setName(fullname);
                rsContact.setEmail(email);
                rsContact.setSubject(subject);
                rsContact.setMessage(message);
                presenter.contact(rsContact, getContext());
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.dialog_contact, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(view1 -> dismiss());
        toolbar.setTitle(contactTitle);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bindViews();

        if (RSSession.isLoggedIn()) {
            User user = RSSession.getCurrentUser();
            if (user.getFirstName() != null)
                inputFullName.setText(user.getFirstName());
            if (user.getLastName() != null)
                inputFullName.setText(inputFullName.getText().toString() + " " + user.getLastName());
            inputEmail.setText(user.getEmail());
        }

        presenter = new PresenterContact(ContactDialog.this);

    }

    private void bindViews() {
        addTextWatcher();
        createLoader();
    }

    private void addTextWatcher() {
        inputFullName.addTextChangedListener(fullNameTextWatcher);
        inputEmail.addTextChangedListener(emailTextWatcher);
        inputSubject.addTextChangedListener(subjectTextWatcher);
        inputMessage.addTextChangedListener(messageTextWatcher);
    }

    @Override
    public void onDestroyView() {
        inputMessage.removeTextChangedListener(messageTextWatcher);
        inputEmail.removeTextChangedListener(emailTextWatcher);
        inputSubject.removeTextChangedListener(subjectTextWatcher);
        inputFullName.removeTextChangedListener(fullNameTextWatcher);
        rootView = null;
        if (unbinder != null)
            unbinder.unbind();
        if (presenter != null)
            presenter.onDestroy(getContext());
        super.onDestroyView();
    }


    @Override
    public void onSuccess(String target, Object data) {
        switch (target) {
            case RSConstants.RS_CONTACT:
                Toast.makeText(getContext(), messageReceived, Toast.LENGTH_LONG).show();
                dismiss();
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
        //Toast.makeText(getContext(), offlineMsg, Toast.LENGTH_LONG).show();
        new RSCustomToast(getActivity(), getResources().getString(R.string.error), offlineMsg, R.drawable.ic_error, RSCustomToast.ERROR).show();

    }
}
