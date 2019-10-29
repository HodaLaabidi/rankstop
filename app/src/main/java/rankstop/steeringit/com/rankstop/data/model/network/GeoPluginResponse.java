package rankstop.steeringit.com.rankstop.data.model.network;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GeoPluginResponse implements Serializable {

    @SerializedName("geoplugin_countryCode")
    private String geoplugin_countryCode;
    @SerializedName("geoplugin_countryName")
    private String geoplugin_countryName;

    public String getGeoplugin_countryCode() {
        return geoplugin_countryCode;
    }

    public void setGeoplugin_countryCode(String geoplugin_countryCode) {
        this.geoplugin_countryCode = geoplugin_countryCode;
    }

    public String getGeoplugin_countryName() {
        return geoplugin_countryName;
    }

    public void setGeoplugin_countryName(String geoplugin_countryName) {
        this.geoplugin_countryName = geoplugin_countryName;
    }

    @NonNull
    @Override
    public String toString() {
        return "geoplugin_countryCode = "+geoplugin_countryCode + ", geoplugin_countryName = "+geoplugin_countryName;
    }
}
