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
import rankstop.steeringit.com.rankstop.customviews.RSETMedium;
import rankstop.steeringit.com.rankstop.data.model.db.RequestOwnership;
import rankstop.steeringit.com.rankstop.data.model.db.User;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.utils.RSConstants;

public class RequestOwnerShipDialog extends DialogFragment implements RSView.StandardView {

    public static String TAG = "FullScreenDialog";

    private View rootView;
    private Unbinder unbinder;
    private User user;
    private String itemId, itemName, fullname, email, phoneNumber, companyName, message;
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

    @BindView(R.id.input_layout_phone)
    TextInputLayout inputLayoutPhone;
    @BindView(R.id.input_phone)
    RSETMedium inputPhone;

    @BindView(R.id.input_layout_company)
    TextInputLayout inputLayoutCompany;
    @BindView(R.id.input_company)
    RSETMedium inputCompany;

    @BindView(R.id.input_layout_message)
    TextInputLayout inputLayoutMessage;
    @BindView(R.id.input_message)
    RSETMedium inputMessage;

    @BindString(R.string.request_received)
    String requestReceived;
    @BindString(R.string.field_required)
    String requiredField;
    @BindString(R.string.text_request_ownership)
    String requestOwnershipTitle;
    @BindString(R.string.email_format_incorrect)
    String emailFormatIncorrect;
    @BindString(R.string.loading_msg)
    String loadingMsg;
    @BindString(R.string.off_line)
    String offLineMsg;

    @BindInt(R.integer.max_length_500)
    int maxLength500;
    @BindInt(R.integer.max_length_50)
    int maxLength50;

    private
    TextWatcher
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
            companyTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().trim().length() > 0) {
                        inputLayoutCompany.setErrorEnabled(false);
                    } else {
                        inputLayoutCompany.setError(requiredField);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            },
            phoneTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().trim().length() > 0) {
                        inputLayoutPhone.setErrorEnabled(false);
                    } else {
                        inputLayoutPhone.setError(requiredField);
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
        phoneNumber = inputPhone.getText().toString().trim();
        companyName = inputCompany.getText().toString().trim();
        message = inputMessage.getText().toString().trim();

        if (validForm()) {
            RequestOwnership requestOwnerShip = new RequestOwnership();
            requestOwnerShip.setFullName(fullname);
            requestOwnerShip.setEmail(email);
            requestOwnerShip.setPhone(phoneNumber);
            requestOwnerShip.setCompany(companyName);
            requestOwnerShip.setMessage(message);
            requestOwnerShip.setItemId(itemId);
            requestOwnerShip.setTitleItem(itemName);
            requestOwnerShip.setUserId(user.get_id());

            presenter.requestOwnership(requestOwnerShip);
        }
    }

    private boolean validForm() {
        int x = 0;

        inputLayoutFullName.setErrorEnabled(false);
        inputLayoutEmail.setErrorEnabled(false);
        inputLayoutPhone.setErrorEnabled(false);
        inputLayoutCompany.setErrorEnabled(false);

        if (TextUtils.isEmpty(fullname)) {
            inputLayoutFullName.setError(requiredField);
            x++;
        }
        if (TextUtils.isEmpty(email)) {
            inputLayoutEmail.setError(requiredField);
            x++;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            inputLayoutPhone.setError(requiredField);
            x++;
        }
        if (TextUtils.isEmpty(companyName)) {
            inputLayoutCompany.setError(requiredField);
            x++;
        }
        if (message.length() > maxLength500) {
            x++;
        }
        return x == 0;
    }

    private void addTextWatcher() {
        inputFullName.addTextChangedListener(fullNameTextWatcher);
        inputEmail.addTextChangedListener(emailTextWatcher);
        inputCompany.addTextChangedListener(companyTextWatcher);
        inputPhone.addTextChangedListener(phoneTextWatcher);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.dialog_request_ownership, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(view1 -> dismiss());
        toolbar.setTitle(requestOwnershipTitle);
        addTextWatcher();
        createLoader();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        user = RSSession.getCurrentUser();

        inputFullName.setText(user.getFirstName() + " " + user.getLastName());
        inputEmail.setText(user.getEmail());
        inputPhone.setText(user.getPhone());

        Bundle b = getArguments();

        itemId = b.getString(RSConstants.ITEM_ID);
        itemName = b.getString(RSConstants.ITEM_NAME);

        presenter = new PresenterContact(RequestOwnerShipDialog.this);

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
    public void onDestroyView() {

        inputFullName.removeTextChangedListener(fullNameTextWatcher);
        inputEmail.removeTextChangedListener(emailTextWatcher);
        inputCompany.removeTextChangedListener(companyTextWatcher);
        inputPhone.removeTextChangedListener(phoneTextWatcher);
        if (unbinder != null)
            unbinder.unbind();
        if (presenter != null)
            presenter.onDestroy();
        rootView = null;

        super.onDestroyView();
    }

    @Override
    public void onSuccess(String target, Object data) {
        switch (target) {
            case RSConstants.SEND_REQ_OWNER_SHIP:
                Toast.makeText(getContext(), requestReceived, Toast.LENGTH_LONG).show();
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
        Toast.makeText(getContext(), offLineMsg, Toast.LENGTH_LONG).show();
    }
}
