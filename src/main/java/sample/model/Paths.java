package sample.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Paths implements Serializable {

    private String keytool;
    private String zipalign;
    private String gradlew;
    private String apksigner;

    public String getKeytool() {
        return keytool;
    }

    public void setKeytool(String keytool) {
        this.keytool = keytool;
    }

    public String getZipalign() {
        return zipalign;
    }

    public void setZipalign(String zipalign) {
        this.zipalign = zipalign;
    }

    public String getGradlew() {
        return gradlew;
    }

    public void setGradlew(String gradlew) {
        this.gradlew = gradlew;
    }

    public String getApksigner() {
        return apksigner;
    }

    public void setApksigner(String apksigner) {
        this.apksigner = apksigner;
    }
}
