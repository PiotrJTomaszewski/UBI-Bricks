package pl.pjt.ubi_bricks

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import pl.pjt.ubi_bricks.database.InventoryPart
import pl.pjt.ubi_bricks.database.Part
import java.io.File
import java.io.FileOutputStream
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

class BrickSet (
    private val setId: String,
    private val context: Context
) {
    class DownloadedPart (
        val itemType: String,
        val itemId: String,
        val quantityInSet: Int,
        val colorCode: Int
//        val extra: String
    ) {
    }

    private val url = "http://fcds.cs.put.poznan.pl/MyWeb/BL/$setId.xml"

    private var xmlDocument: Document? = null

    fun downloadInventory(): Boolean {
        var result = false
//        withContext(Dispatchers.IO) {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            if (connection.responseCode == 200) {
                val tmpFile = createTempFile()
                val netInputStream = connection.inputStream
                val outputStream = FileOutputStream(tmpFile)
                outputStream.use {
                        output -> netInputStream.copyTo(output)
                }
                val dbFactory = DocumentBuilderFactory.newInstance()
                val dBuilder = dbFactory.newDocumentBuilder()
                val xmlInput = InputSource(StringReader(tmpFile.readText()))
                xmlDocument = dBuilder.parse(xmlInput)
                result = true
            }
//        }
        return result
    }

    suspend fun checkSetExists(): Boolean {
        var result = false
        withContext(Dispatchers.IO) {
            val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
            connection.connect()
            result = (connection.responseCode == 200)
        }
        return result
    }

    fun createPartsList(): ArrayList<DownloadedPart> {
        xmlDocument!!.documentElement.normalize()
        val itemList: NodeList = xmlDocument!!.getElementsByTagName("ITEM")
        val parts = ArrayList<DownloadedPart>()
        for (i in 0 until itemList.length) {
            val itemNode: Node = itemList.item(i)
            if (itemNode.nodeType == Node.ELEMENT_NODE) {
                val elem = itemNode as Element
                val alternate = getNodeValue("ALTERNATE", elem)
                if (alternate != "N") {
                    continue
                }
                val itemType = getNodeValue("ITEMTYPE", elem)
                val itemId = getNodeValue("ITEMID", elem)
                val quantity = getNodeValue("QTY", elem)
                val color = getNodeValue("COLOR", elem)
                // TODO: Extra seems to have value 'N' and not int
//                val extra = getNodeValue("EXTRA", elem)

                val downloadedPart = DownloadedPart(itemType, itemId, quantity.toInt(), color.toInt())
                parts.add(downloadedPart)
            }
        }
        return parts
    }

    private fun getNodeValue(tag: String, element: Element): String {
        val nodeList = element.getElementsByTagName(tag)
        val node = nodeList.item(0)
        if (node != null) {
            if (node.hasChildNodes()) {
                val child = node.firstChild
                while (child != null) {
                    if (child.nodeType == Node.TEXT_NODE) {
                        return child.nodeValue
                    }
                }
            }
        }
        return ""
    }
}
