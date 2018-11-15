package sample.build.xml

import com.android.ide.common.vectordrawable.Svg2Vector
import org.w3c.dom.Element
import sample.build.xml.XmlVectorParams.Companion.FILL_COLOR
import sample.build.xml.XmlVectorParams.Companion.HEIGHT
import sample.build.xml.XmlVectorParams.Companion.TINT
import sample.build.xml.XmlVectorParams.Companion.WIDTH
import sample.common.checkFile
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

class XmlVector(pathToXml: String) : XmlBase(pathToXml) {

    fun setXmlParamsLike(pathToPattern: String) {

        val xmlPattern = XmlVector(pathToPattern)

        val params = xmlPattern.getParams()

        val vectorNode = getNodeByExpression("/vector")
        val nodeListPaths = getNodeListByExpression("/vector/path")

        (vectorNode as Element).also { node ->
            node.setAttribute(WIDTH, params.width)
            node.setAttribute(HEIGHT, params.height)
            params.tint?.let { node.setAttribute(TINT, it) }
        }

        for (i in 0 until nodeListPaths.length) {
            (nodeListPaths.item(i) as Element).also { element ->
                element.setAttribute(FILL_COLOR, params.fillColor)
            }
        }
    }

    private fun getParams(): XmlVectorParams {

        val params: XmlVectorParams

        var width: String? = null
        var height: String? = null
        var tint: String? = null
        val fillColor: String

        val nodeListAttr = getNodeListByExpression("/vector/attribute::*")
        val nodeListPaths = getNodeListByExpression("/vector/path")

        for (i in 0 until nodeListAttr.length) {
            when (nodeListAttr.item(i).nodeName) {
                WIDTH -> width = nodeListAttr.item(i).nodeValue
                HEIGHT -> height = nodeListAttr.item(i).nodeValue
                TINT -> tint = nodeListAttr.item(i).nodeValue
            }
        }

        if (nodeListPaths.item(0) != null) {
            fillColor = nodeListPaths.item(0).attributes.getNamedItem(FILL_COLOR).nodeValue
        } else throw FileNotFoundException("xml is not a vector")
        if (width == null || height == null) throw  FileNotFoundException("xml is not a vector")

        params = XmlVectorParams(width, height, tint, fillColor)
        return params
    }

    companion object {

        fun svgToXml(source: String , targetPath: String) : XmlVector{

            val sourceFile = source.checkFile("svg")
            val fos = FileOutputStream(File(targetPath))
            Svg2Vector.parseSvgToXml(sourceFile, fos)

            return XmlVector(targetPath)
        }
    }
}