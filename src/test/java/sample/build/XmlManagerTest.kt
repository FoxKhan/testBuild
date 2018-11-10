package sample.build

import org.junit.Test

class XmlManagerTest {

    private val pathToXml = "C:\\Users\\nika-\\Desktop\\test\\colorsOut.xml"
    private val xml = XmlManager(pathToXml)

    @Test
    fun changeAndSaveXml() {

        val listResources = xml.getMapListValue()

        listResources.forEach {
            xml.setValue(it.key, "ALEX_FIRE")
        }
        xml.setValue("colorPrimary", "#000000000")
        xml.saveXml()
    }

    @Test
    fun getListValue() {

        val list = xml.getListValue()

        list.forEach {
            println(it)
        }
    }

    @Test
    fun setXml(){
        xml.setXml("C:\\Users\\nika-\\Desktop\\test\\strings-copy.xml")
    }

    @Test
    fun getMapListValue() {

        val list = xml.getMapListValue()
        list.forEach {
            println(it)
        }
    }
}