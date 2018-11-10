package sample.build.gradle

import sample.build.gradle.model.ExtModel
import sample.build.gradle.model.GradleModel
import sample.build.gradle.model.VersionModel
import sample.build.gradle.model.VersionModel.Version.VERSION_CODE
import sample.build.gradle.model.VersionModel.Version.VERSION_NAME
import sample.common.open
import java.io.File


class GradleManager(private val gradleFile: String) {

    val gradleProp: GradleModel
    private val input: List<String> = gradleFile.open(GRADLE_EXTENSION)

    init {

        gradleProp = getProp()
    }

    fun save(newProp: GradleModel, newPath: String) {

        val fileName = "$newPath\\${gradleFile.split('\\').last()}"

        val output = input.toMutableList()

        newProp.ext.forEach {
            output[it.value.line] = "    ${it.key} = \"${it.value.value}\""
        }
        newProp.version.forEach {
            output[it.value.line] = "        ${it.key.get()} ${it.value.value}"
        }

        File(fileName).printWriter().use { out ->
            output.forEach { out.println(it) }
        }
    }

    private fun getProp(): GradleModel {

        val extMap = HashMap<String, ExtModel>()
        val versionMap = HashMap<VersionModel.Version, VersionModel>()

        input.forEachIndexed { lineNumber, s ->
            when {
                s.contains(VERSION_CODE.get()) -> {
                    val split = s.trim().split(' ')
                    versionMap[VERSION_CODE] = VersionModel(lineNumber, split[1])
                }
                s.contains(VERSION_NAME.get()) -> {
                    val split = s.trim().split(' ')
                    versionMap[VERSION_NAME] = VersionModel(lineNumber, split[1])
                }
                else -> extParse(s, lineNumber, input, extMap)
            }
        }
        return GradleModel(extMap, versionMap)
    }

    private fun extParse(s: String, i: Int, input: List<String>, extMap: HashMap<String, ExtModel>) {
        if (s.contains("ext {")) {
            var currentIndex = i + 1
            while (true) {
                if (input[currentIndex].contains("}")) break

                val currentLine = input[currentIndex]
                if (!currentLine.isEmpty()) {
                    val split = currentLine.split("=")
                    val extName = split[0].trim()
                    val extValue = split[1].trim().trim('\"')

                    extMap[extName] = ExtModel(currentIndex, extValue)
                }
                currentIndex++
            }
        }
    }

    companion object {
        private const val GRADLE_EXTENSION = "gradle"
    }
}