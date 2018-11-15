package sample.build.xml

data class XmlVectorParams(
        val width: String,
        val height: String,
        val tint: String?,
        val fillColor: String
) {

    companion object {

        const val WIDTH = "android:width"
        const val HEIGHT = "android:height"
        const val TINT = "android:tint"
        const val FILL_COLOR = "android:fillColor"
    }
}