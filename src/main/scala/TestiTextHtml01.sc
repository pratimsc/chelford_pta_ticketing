import java.io.{FileInputStream, FileOutputStream}

import com.itextpdf.text.{Document, PageSize, Rectangle}
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.tool.xml.XMLWorkerHelper

val doc = new Document(new Rectangle(PageSize.A4.getWidth, PageSize.A4.getHeight/4))
val pdfWriter = PdfWriter.getInstance(doc,new FileOutputStream("""/tmp/chelford_pta/html/Queen_Cerebration_Ticket.pdf"""))
doc.open()
XMLWorkerHelper.getInstance().parseXHtml(pdfWriter,doc,new FileInputStream("""/tmp/chelford_pta/html/Queen_Cerebration_Ticket.html"""))
doc.close()