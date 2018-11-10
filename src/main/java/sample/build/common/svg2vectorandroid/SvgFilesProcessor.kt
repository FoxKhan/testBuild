package sample.build.common.svg2vectorandroid

import com.android.ide.common.vectordrawable.Svg2Vector
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.*
import java.nio.file.FileVisitResult.CONTINUE
import java.nio.file.FileVisitResult.SKIP_SUBTREE
import java.nio.file.attribute.BasicFileAttributes
import java.util.*

class SvgFilesProcessor(
        sourceSvgDirectory: String,
        destinationVectorDirectory: String = "$sourceSvgDirectory/ProcessedSVG"
) {

    private val sourceSvgPath: Path = Paths.get(sourceSvgDirectory)
    private val destinationVectorPath: Path = Paths.get(destinationVectorDirectory)
    private val maxValue = Int.MAX_VALUE
    private val extension: String = "xml"

    fun process() {
        try {
            val options = EnumSet.of(FileVisitOption.FOLLOW_LINKS)

            if (Files.isDirectory(sourceSvgPath)) {
                Files.walkFileTree(sourceSvgPath, options, maxValue, Visitor())
            } else {
                println("source not a directory")
            }

        } catch (e: IOException) {
            println("IOException " + e.message)
        }
    }

    @Throws(IOException::class)
    private fun convertToVector(source: Path, target: Path) {

        if (source.fileName.toString().endsWith(".svg")) {
            val targetFile = getFileWithXMlExtension(target, extension)
            val fos = FileOutputStream(targetFile)
            Svg2Vector.parseSvgToXml(source.toFile(), fos)
        } else {
            println("Skipping file as its not svg " + source.fileName.toString())
        }
    }

    private fun getFileWithXMlExtension(target: Path, extension: String): File {
        val svgFilePath = target.toFile().absolutePath
        val svgBaseFile = StringBuilder()
        val index = svgFilePath.lastIndexOf(".")
        if (index != -1) {
            val subStr = svgFilePath.substring(0, index)
            svgBaseFile.append(subStr)
        }
        svgBaseFile.append(".$extension")
        return File(svgBaseFile.toString())
    }

    private inner class Visitor : FileVisitor<Path> {

        override fun preVisitDirectory(dir: Path,
                                       attrs: BasicFileAttributes): FileVisitResult {

            if (dir == destinationVectorPath) {
                return SKIP_SUBTREE
            }

            val newDirectory = destinationVectorPath.resolve(sourceSvgPath.relativize(dir))
            try {
                Files.createDirectory(newDirectory)
            } catch (ex: FileAlreadyExistsException) {
                println("FileAlreadyExistsException " + ex.toString())
            } catch (x: IOException) {
                return SKIP_SUBTREE
            }

            return CONTINUE
        }

        @Throws(IOException::class)
        override fun visitFile(file: Path,
                               attrs: BasicFileAttributes): FileVisitResult {
            val newDirectory = destinationVectorPath.resolve(sourceSvgPath.relativize(file))
            convertToVector(file, newDirectory)
            return CONTINUE
        }

        @Throws(IOException::class)
        override fun postVisitDirectory(dir: Path?,
                                        exc: IOException?): FileVisitResult {
            return CONTINUE
        }

        @Throws(IOException::class)
        override fun visitFileFailed(file: Path?,
                                     exc: IOException?): FileVisitResult {
            return CONTINUE
        }
    }
}
