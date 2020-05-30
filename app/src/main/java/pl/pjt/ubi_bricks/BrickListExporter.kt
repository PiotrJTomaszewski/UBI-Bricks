package pl.pjt.ubi_bricks

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.w3c.dom.Document
import org.w3c.dom.Element
import pl.pjt.ubi_bricks.database.InventoryPart
import java.io.File
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class BrickListExporter {
    companion object {
        fun writeXml(parts: Array<*>, fileUri: Uri, context: Context) {
            val docBuilder: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val doc: Document = docBuilder.newDocument()
            val rootElement: Element = doc.createElement("INVENTORY")
            doc.appendChild(rootElement)
            for (part in parts) {
                part as InventoryPart.InventoryPartEntity
                val quantityInStore = part.quantityInStore ?: 0
                if (quantityInStore == part.quantityInSet) {
                    continue
                }
                val itemElement: Element = doc.createElement("ITEM")
                rootElement.appendChild(itemElement)
                if (part.typeEntity != null && part.typeEntity!!.code != null) {
                    val itemTypeElement: Element = doc.createElement("ITEMTYPE")
                    itemTypeElement.appendChild(doc.createTextNode(part.typeEntity!!.code))
                    itemElement.appendChild(itemTypeElement)
                }
                if (part.partId != null) {
                    val itemIdElement: Element = doc.createElement("ITEMID")
                    itemIdElement.appendChild(doc.createTextNode(part.partId.toString()))
                    itemElement.appendChild(itemIdElement)
                }
                if (part.colorEntity != null && part.colorEntity!!.legoId != null) {
                    val colorElement: Element = doc.createElement("COLOR")
                    colorElement.appendChild(doc.createTextNode(part.colorEntity!!.legoId.toString()))
                    itemElement.appendChild(colorElement)
                }
                if (part.quantityInStore != null) {
                    val qtyFilledElement: Element = doc.createElement("QTYFILLED")
                    qtyFilledElement.appendChild(doc.createTextNode(quantityInStore.toString()))
                    itemElement.appendChild(qtyFilledElement)
                }
            }
            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            val outputStream = context.contentResolver.openOutputStream(fileUri)
            transformer.transform(DOMSource(doc), StreamResult(outputStream))
        }

    }
}