import java.io.{File, FileOutputStream}
import java.util.UUID

import com.itextpdf.text.{Document, Paragraph, Rectangle, Utilities, PageSize, Font, BaseColor}
import com.itextpdf.text.pdf.PdfWriter

import scala.io.Source


val filePath = """/tmp/itext/"""

val doc1 = new Document()
val rectangle = new Rectangle(
  Utilities.millimetersToPoints(50), //Width
  Utilities.millimetersToPoints(100) //Height
)

val pageSize = PageSize.A1.rotate()

val fonts = List(new Font(),
  new Font(Font.FontFamily.COURIER),
  new Font(Font.FontFamily.HELVETICA),
  new Font(Font.FontFamily.COURIER,36),
  new Font(Font.FontFamily.COURIER,18, Font.BOLD),
  new Font(Font.FontFamily.HELVETICA,50, Font.STRIKETHRU,new BaseColor(175, 34, 79))
)

val pdfWriter1 = PdfWriter.getInstance(doc1, new FileOutputStream(filePath + s"""${UUID.randomUUID().toString}.pdf"""))
val paragraph = new Paragraph()
paragraph.add("This is the coolest text ever!!!")
//paragraph.add(google.mkString(""))
paragraph.add("----My Message Ends Here !!!----")
doc1.setPageSize(pageSize)
doc1.open()
fonts.map{ f=>

  doc1.add(new Paragraph(
    s"""
      |-------------------------------------------
      |This belongs to a quite cool Font.
      |Font family : ${f.getFamilyname}
      |Font name : ${f.getBaseFont}
      |Font style : ${f.getStyle}
      |Font size : ${f.getSize}
      |Font color : ${f.getColor}
      |-------------------------------------------
    """.stripMargin, f))
}
doc1.add(paragraph)
doc1.close()

/**
  * Reading and writing HTML to PDF
  */
implicit val codec = scala.io.Codec.UTF8
val google = Source.fromURL("""http://www.ruoka.cafe/""").getLines().toList
google.map(println(_))
val html = google.mkString(" ")

val pdf2 = new FileOutputStream(filePath + s"""${UUID.randomUUID().toString}.pdf""")
val doc2 = new Document()
val pdfWriter2 = PdfWriter.getInstance(doc2,pdf2)
doc2.setPageSize(PageSize.A5.rotate())
doc2.open()



