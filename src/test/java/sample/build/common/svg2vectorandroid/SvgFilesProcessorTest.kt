package sample.build.common.svg2vectorandroid

import org.junit.Test
import sample.build.xml.SvgFilesProcessor

class SvgFilesProcessorTest{


    @Test
    fun svg2xml(){

        val folderPath = "C:\\Users\\PC\\Desktop\\testFolder"
        val svgFilesProcessor = SvgFilesProcessor(folderPath, "$folderPath/xml")
        svgFilesProcessor.process()

//        FileController.delete(File("$folderPath/xml"))
    }
}