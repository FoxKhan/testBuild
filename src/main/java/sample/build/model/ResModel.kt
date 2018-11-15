package sample.build.model

data class ResModel(
        val name: String,
        val type: String,
        val path: String,
        var res: FXmlRes?
)