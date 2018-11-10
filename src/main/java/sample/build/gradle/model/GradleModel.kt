package sample.build.gradle.model

import java.io.Serializable

data class GradleModel(
        val ext: HashMap<String, ExtModel>,
        val version: HashMap<VersionModel.Version, VersionModel>
) : Serializable {


    fun copy(): GradleModel {

        val newExt = HashMap<String, ExtModel>()
        ext.forEach {
            newExt[it.key] = ExtModel(it.value.line, it.value.value)
        }

        val newVersion = HashMap<VersionModel.Version, VersionModel>()
        version.forEach {
            newVersion[it.key] = VersionModel(it.value.line, it.value.value)
        }

        return GradleModel(newExt, newVersion)
    }

}