package sample.build

import org.w3c.dom.Document
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileNotFoundException
import java.util.ArrayList
import javax.naming.NoPermissionException
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.TransformerFactory


class XmlManager(private var pathToXml: String) {

    private val documentBuilder: DocumentBuilder
    private val xPath: XPath
    private var doc: Document


    init {
        checkFile()
        val factory = DocumentBuilderFactory.newInstance()
        factory.isNamespaceAware = true
        documentBuilder = factory.newDocumentBuilder()

        val xPathFactory = XPathFactory.newInstance()
        xPath = xPathFactory.newXPath()

        doc = documentBuilder.parse(pathToXml)
    }

    fun setXml(pathToXml: String) {
        checkFile(pathToXml)
        this.pathToXml = pathToXml
        doc = documentBuilder.parse(pathToXml)
    }



    fun getListValue(): ArrayList<Pair<String, String>>{

        val list = ArrayList<Pair<String, String>>()

        val xPathExpression = xPath.compile("/resources/*")
        val nodeList = xPathExpression.evaluate(doc, XPathConstants.NODESET) as NodeList

        for (i in 0 until nodeList.length) {

            val item = nodeList.item(i)
            val key = item.attributes.getNamedItem("name").nodeValue
            val value = item.textContent

            list.add(Pair(key, value))
        }

        return list
    }

    fun getMapListValue(): HashMap<String, String>{

        val list = HashMap<String, String>()

        val xPathExpression = xPath.compile("/resources/*")
        val nodeList = xPathExpression.evaluate(doc, XPathConstants.NODESET) as NodeList

        for (i in 0 until nodeList.length) {

            val item = nodeList.item(i)
            val key = item.attributes.getNamedItem("name").nodeValue
            val value = item.textContent

            list[key] = value
        }

        return list
    }

    fun setValue(name: String, newValue: String) {

        val xPathExpression = xPath.compile("/resources/*[@name='$name']/text()")
        val nodeList = xPathExpression.evaluate(doc, XPathConstants.NODESET) as NodeList

        for (i in 0 until nodeList.length) {
            nodeList.item(i).nodeValue = newValue
        }
    }

    fun saveXml() {
        val xFormer = TransformerFactory.newInstance().newTransformer()
        xFormer.transform(DOMSource(doc), StreamResult(pathToXml))
    }


    private fun checkFile() {
        val file = File(pathToXml)
        if (!file.exists()) throw FileNotFoundException()
        if (!file.canRead()) throw NoPermissionException()
        if (file.name.endsWith(".xml")) throw FileNotFoundException("Xml not found. File: ${file.name}")
    }

    private fun checkFile(pathToXml: String) {
        val file = File(pathToXml)
        if (!file.exists()) throw FileNotFoundException()
        if (!file.canRead()) throw NoPermissionException()
        if (file.name.endsWith(".xml")) throw FileNotFoundException("Xml not found")
    }
}