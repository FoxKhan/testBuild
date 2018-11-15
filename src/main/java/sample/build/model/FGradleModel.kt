package sample.build.model

import sample.build.gradle.model.ExtModel
import sample.build.gradle.model.VersionModel

data class FGradleModel(
        val ext: HashMap<String, ExtModel>,
        val version: HashMap<VersionModel.Version, VersionModel>
): IFRes {

    fun copy(): FGradleModel {

        val newExt = HashMap<String, ExtModel>()
        ext.forEach {
            newExt[it.key] = ExtModel(it.value.line, it.value.value)
        }

        val newVersion = HashMap<VersionModel.Version, VersionModel>()
        version.forEach {
            newVersion[it.key] = VersionModel(it.value.line, it.value.value)
        }

        return FGradleModel(newExt, newVersion)
    }

}