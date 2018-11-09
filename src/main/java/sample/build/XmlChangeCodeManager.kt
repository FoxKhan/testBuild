package sample.build

import org.w3c.dom.Node
import org.w3c.dom.NodeList
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class XmlChangeCodeManager {

    private val documentBuilder: DocumentBuilder
    private val xPath: XPath


    init {
        val factory = DocumentBuilderFactory.newInstance()
        factory.isNamespaceAware = true
        documentBuilder = factory.newDocumentBuilder()

        val xPathFactory = XPathFactory.newInstance()
        xPath = xPathFactory.newXPath()
    }

    fun noneme(pathToXml: String) {

        val doc = documentBuilder.parse(pathToXml)
        val xPathExpression = xPath.compile("//color")
        val nodeList = xPathExpression.evaluate(doc, XPathConstants.NODESET) as NodeList

        for (i in 0 until nodeList.length) {
            System.out.println(nodeList.item(i).nodeValue)
        }

    }
}