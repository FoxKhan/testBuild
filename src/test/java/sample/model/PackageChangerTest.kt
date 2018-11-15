package sample.model

import org.junit.Test
import sample.build.PackageChanger
import java.io.File
import java.io.IOException

class PackageChangerTest {


    private val projectPath = "d:\\1"

    @Test
    @Throws(IOException::class)
    fun rewriteFile() {
        val file = File(projectPath)
        val packageChanger = PackageChanger(file,
                "iceblood.computercomponents", "com.bla")
        packageChanger.rewriteFile(file)
    }

    @Test
    @Throws(IOException::class)
    fun searchFile() {
        val file = File(projectPath)
        val packageChanger = PackageChanger(file,
                "iceblood.computercomponents", "com.bla")
        packageChanger.searchFile(file)
    }
}