package rankstop.steeringit.com.rankstop.data.model.db;

import java.io.Serializable;

import rankstop.steeringit.com.rankstop.utils.RSConstants;

public class FakeUser implements Serializable {
    private String email;
    private String password;

    public FakeUser() {
        email = RSConstants.EMAIL_FAKE_USER;
        password = RSConstants.PASSWORD_FAKE_USER;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
