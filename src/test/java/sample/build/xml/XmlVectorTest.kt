package sample.build.xml

import org.junit.Test

class XmlVectorTest {

    private val svgVector = "C:\\Users\\PC\\Desktop\\testFolder\\copyParamTest\\login.svg"
    private val newPath = "C:\\Users\\PC\\Desktop\\testFolder\\copyParamTest\\ic_new_boy_new.xml"
    private val patternPath = "C:\\Users\\PC\\Desktop\\testFolder\\copyParamTest\\ic_Pattern.xml"

    @Test
    fun setXmlParamsLike() {

        val xmlVector = XmlVector.svgToXml(svgVector, newPath)
        xmlVector.setXmlParamsLike(patternPath)
        xmlVector.saveXml(newPath)
    }
}