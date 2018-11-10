package sample.build

import org.junit.Test
import sample.build.gradle.GradleManager
import sample.build.gradle.model.VersionModel

class GradleManagerTest{

    private val newPath = "C:\\Users\\nika-\\Desktop\\test\\gradleTest\\newPath"

    private val pathToGradle = "C:\\Users\\nika-\\Desktop\\test\\gradleTest\\build.gradle"
    private val gm = GradleManager(pathToGradle)

    @Test
    fun readGradleFile(){

        val gradleModel = gm.gradleProp

        val newGradle = gradleModel.copy()
        newGradle.version[VersionModel.Version.VERSION_NAME]!!.value = "\"1.999\""

        val print = gradleModel.version[VersionModel.Version.VERSION_NAME]!!.value
        val printNew = newGradle.version[VersionModel.Version.VERSION_NAME]!!.value

        println("old value $print")
        println("new value $printNew")

    }

    @Test
    fun saveGradleFile(){

        val gradleModel = gm.gradleProp

        val newGradle = gradleModel.copy()
        newGradle.version[VersionModel.Version.VERSION_NAME]!!.value = "\"1.999\""

        gm.save(newGradle, newPath)
    }
}