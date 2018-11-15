package sample.build.xml

import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import sample.common.checkFile
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

open class XmlBase(private var pathToXml: String) {


    private val documentBuilder: DocumentBuilder
    private val xPath: XPath
    private var doc: Document

    init {
        pathToXml.checkFile(XML_EXTENSION)
        val factory = DocumentBuilderFactory.newInstance()
        factory.isNamespaceAware = true
        documentBuilder = factory.newDocumentBuilder()

        val xPathFactory = XPathFactory.newInstance()
        xPath = xPathFactory.newXPath()

        doc = documentBuilder.parse(pathToXml)
    }

    fun setXml(pathToXml: String) {
        pathToXml.checkFile(XML_EXTENSION)
        this.pathToXml = pathToXml
        doc = documentBuilder.parse(pathToXml)
    }

    fun saveXml() {
        val xFormer = TransformerFactory.newInstance().newTransformer()
        xFormer.transform(DOMSource(doc), StreamResult(pathToXml))
    }

    fun saveXml(newPath: String) {
        val xFormer = TransformerFactory.newInstance().newTransformer()
        xFormer.transform(DOMSource(doc), StreamResult(newPath))
    }

    protected fun getNodeListByExpression(expression: String): NodeList =
            xPath.evaluate(expression, doc, XPathConstants.NODESET) as NodeList

    protected fun getNodeByExpression(expression: String): Node =
            xPath.evaluate(expression, doc, XPathConstants.NODE) as Node


    companion object {
        protected const val XML_EXTENSION = "xml"
    }
}