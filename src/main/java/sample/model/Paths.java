package sample.model;

public class Paths {

    private String keytoolPath;
    private String zipalign;
    private String gradlew;

    public String getKeytoolPath() {
        return keytoolPath;
    }

    public void setKeytoolPath(String keytoolPath) {
        this.keytoolPath = keytoolPath;
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

    private String apksigner;
}
