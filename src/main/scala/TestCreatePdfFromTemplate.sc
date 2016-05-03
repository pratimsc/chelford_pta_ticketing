import java.io.{File, FileOutputStream}

import chelford.ticketing.{Ticket, Tickets, TicketsDao}
import com.itextpdf.text.pdf.{PdfContentByte, PdfPCell, PdfPTable, PdfWriter}
import com.itextpdf.text._
import com.typesafe.config.ConfigFactory
import slick.driver.PostgresDriver.api._

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.io.{Codec, Source}
import scala.util.{Failure, Success}

/**
  * All different functions goes here
  */

def createImageCell(img:Array[Byte]) = {
  val image = Image.getInstance(img)
  val imgCell = new PdfPCell(image,true)
  imgCell.setPadding(0)
  imgCell
}


/**
  * All database operations start below.
  */
val parsedConfig = ConfigFactory.parseFile(new File("/home/pratimsc/IdeaProjects/chelford_ticketing/src/main/resources/application.conf"))
val conf = ConfigFactory.load(parsedConfig)
implicit lazy val db: slick.driver.PostgresDriver.backend.DatabaseDef = Database.forConfig("postgresql-ticket-database", conf)
//Get all the tickets of Adult type
val ticketsFuture: Future[Seq[Ticket]] = TicketsDao.findByTicketType('A')
ticketsFuture.onComplete {
  case Success(tl) => println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>The number of records successfully fectched is " + tl.length)
  case Failure(ex) => println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>Some exception has occured")
    ex.printStackTrace()
}
val tickets: Seq[Ticket] = Await.result(ticketsFuture, Duration.Inf)

/**
  * Following the example at -
  * http://developers.itextpdf.com/examples/itext-action-second-edition/chapter-3#162-movieposters.java
  */
val _pdfFileName =
  """/tmp/chelford_pta/itext/1.pdf"""
val _ticketBackGroundImage = """/earth/peak/Chelford PTA Accounts/Kakoli_Chaudhuri PTA/Apr 2016 ⁄ Mar 2017/Queen's 90th Birthday/images/Union Jack.jpg"""

//Step 1
val ticketSize = new Rectangle(PageSize.A4.getWidth, PageSize.A4.getHeight / 6)
val document: Document = new Document(new Rectangle(PageSize.A4.getWidth, PageSize.A4.getHeight/3))

//Step 2
val pdfWriter: PdfWriter = PdfWriter.getInstance(document, new FileOutputStream(_pdfFileName))
//pdfWriter.setCompressionLevel(0)

//Step 3
document.open()

//Step 4
// Loop over and add images
val font = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD, BaseColor.BLACK)
for {
  t <- tickets
} {
  println("Printing ticket >>>>>>>" + t)

  //Add a table
  val table = new PdfPTable(4)
  table.setTotalWidth(ticketSize.getWidth)
  table.setLockedWidth(true)

  //Add ticket id
  val cellTicketId = new PdfPCell(new Phrase(t.ticket_id.toString, font))
  val cellTicketDesciption = new PdfPCell(new Phrase(t.description, font))
  val cellEventTiming = new PdfPCell(new Phrase("On 11 June 2016 from 3:00PM until 8:00PM", font))
  val cellPrice = new PdfPCell(new Phrase("£" + t.price, font))
  val cellVenue = new PdfPCell(new Phrase("Chelford Primary School, Oak Road, SK11 9AY",font))
  val cellQrCode = createImageCell(t.qrCode.getOrElse(Array[Byte](0)))
  cellQrCode.setRowspan(5)
  cellTicketId.setFixedHeight(ticketSize.getHeight/6)
  cellTicketId.setPadding(0)
  cellTicketDesciption.setFixedHeight(ticketSize.getHeight/6)
  cellTicketDesciption.setPadding(0)
  cellEventTiming.setFixedHeight(ticketSize.getHeight/6)
  cellEventTiming.setPadding(0)
  cellPrice.setFixedHeight(ticketSize.getHeight/6)
  cellPrice.setPadding(0)
  cellVenue.setFixedHeight(ticketSize.getHeight/6)
  cellVenue.setPadding(0)

  table.addCell(cellTicketId)
  table.addCell(cellQrCode)
  table.addCell(cellTicketId)
  table.addCell(cellQrCode)

  table.addCell(cellEventTiming)
  table.addCell(cellEventTiming)

  table.addCell(cellPrice)
  table.addCell(cellPrice)

  table.addCell(cellTicketDesciption)
  table.addCell(cellTicketDesciption)

  table.addCell(cellVenue)
  table.addCell(cellVenue)

  document.add(table)
  document.newPage()
}

println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>FINISHED>>>>>>>>>>>>>>>>>>>>>>>>>")
//Step 5
document.close()