package sample.build.gradle.model

data class VersionModel (
        val line : Int,
        var value: String
){


    enum class Version(private val type : String){
        VERSION_CODE("versionCode"),
        VERSION_NAME("versionName");

        fun get() = type
    }
}