package rankstop.steeringit.com.rankstop.data.model.db;

import java.io.Serializable;

public class FCMRegistrationToken implements Serializable {
    private String registrationToken;
    public FCMRegistrationToken(String fcmRegistrationToken){
        this.registrationToken = fcmRegistrationToken ;
    }


    public String getRegistrationToken() {
        return registrationToken;
    }

    public void setRegistrationToken(String fcmRegistrationToken) {
        this.registrationToken = fcmRegistrationToken;
    }
}
