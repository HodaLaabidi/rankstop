package rankstop.steeringit.com.rankstop.data.model.network;

import java.io.Serializable;

public class RSResponseGetEmailByToken implements Serializable {

    String EmailVerif;
    public RSResponseGetEmailByToken(String emailVerif){
        this.EmailVerif = EmailVerif ;

    }

    public String getEmailVerif() {
        return EmailVerif;
    }

    public void setEmailVerif(String emailVerif) {
        EmailVerif = emailVerif;
    }
}
