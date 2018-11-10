package sample.build.common.svg2vectorandroid

import org.junit.Test
import sample.model.FileController
import java.io.File

class SvgFilesProcessorTest{


    @Test
    fun svg2xml(){

        val folderPath = "C:\\Users\\nika-\\Desktop\\test\\svgTest"
        val svgFilesProcessor = SvgFilesProcessor(folderPath, "$folderPath/xml")
        svgFilesProcessor.process()

//        FileController.delete(File("$folderPath/xml"))
    }
}