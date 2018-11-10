package sample.build

import com.android.ide.common.xml.AndroidManifestParser
import com.android.io.IAbstractFile
import java.io.FileInputStream
import java.io.InputStream

class ManifestManager {

    val manifestParser = AndroidManifestParser()

    init {

        val filePath = "C:\\Users\\nika-\\Desktop\\test\\manifestTest\\AndroidManifest.xml"
        val ins = FileInputStream(filePath)
        val d = AndroidManifestParser.parse(ins)


        d.activities.forEach {
            println(it.name)
        }
        println()
        d.usesLibraries.forEach {
            println(it.name)
        }
        println()
        println(d.`package`)
    }
}