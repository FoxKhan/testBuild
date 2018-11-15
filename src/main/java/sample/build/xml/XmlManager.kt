package sample.build.xml


class XmlManager(pathToXml: String) : XmlBase(pathToXml){

    fun getMapListValue(): HashMap<String, String>{

        val list = HashMap<String, String>()

        val nodeList =getNodeListByExpression("/resources/*")

        for (i in 0 until nodeList.length) {

            val item = nodeList.item(i)
            val key = item.attributes.getNamedItem("name").nodeValue
            val value = item.textContent

            list[key] = value
        }

        return list
    }

    fun setValue(name: String, newValue: String) {

        val nodeList = getNodeListByExpression("/resources/*[@name='$name']/text()")

        for (i in 0 until nodeList.length) {
            nodeList.item(i).nodeValue = newValue
        }
    }
}