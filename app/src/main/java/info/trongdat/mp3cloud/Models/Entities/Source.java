package info.trongdat.mp3cloud.Models.Entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Alone on 12/1/2016.
 */

public class Source {

//    @SerializedName("128")
    private String s128;

    @SerializedName("320")
    private String s320;

//    @SerializedName("lossless")
    @SerializedName("128")
    private String lossless;

    public String getS128() {
        return s128;
    }

    public Source setS128(String s128) {
        this.s128 = s128;
        return this;
    }

    public String getS320() {
        return s320;
    }

    public Source setS320(String s320) {
        this.s320 = s320;
        return this;
    }

    public String getLossless() {
        return lossless;
    }

    public Source setLossless(String lossless) {
        this.lossless = lossless;
        return this;
    }
}
