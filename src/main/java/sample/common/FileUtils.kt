package sample.common

import java.io.*
import javax.naming.NoPermissionException

@Throws(IOException::class)


fun String.open(): List<String> {
    return openFile()
}

fun String.open(extension: String): List<String> {
    this.checkFile(extension)
    return openFile()
}

private fun String.openFile(): List<String> {
    val sb = StringBuilder()

    var fr: FileReader? = null
    var br: BufferedReader? = null

    try {
        val gragleFile = File(this)
        fr = FileReader(gragleFile)
        br = BufferedReader(fr)

        var line: String?

        while (true) {
            line = br.readLine()
            if (line == null) break
            sb.append(line).append("\n")
        }
    } catch (e: IOException) {
        throw e
    } finally {
        if (fr != null) {
            try {
                fr.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (br != null) {
            try {
                br.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    return sb.toString().split('\n')
}

fun String.checkFile(extension : String) {
    val file = File(this)
    if (!file.exists()) throw FileNotFoundException()
    if (!file.canRead()) throw NoPermissionException()
    if (!file.name.endsWith(".$extension")) throw FileNotFoundException("$extension not found")
}