package chelford.ticketing

import java.io.{File, FileOutputStream, OutputStream, Writer}
import java.nio.charset.StandardCharsets
import java.text.DecimalFormat
import java.util.UUID

import org.joda.time.DateTime
import slick.driver.PostgresDriver.api._
import com.github.tototoshi.slick.PostgresJodaSupport._
import net.glxn.qrgen.core.image.ImageType
import net.glxn.qrgen.javase.QRCode
import org.joda.time.format.DateTimeFormat
import slick.driver.PostgresDriver.backend.DatabaseDef

import scala.collection.immutable.IndexedSeq
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class Ticket(ticket_id: UUID = UUID.randomUUID(),
                  ticket_type: String = "AN",
                  description: String = "Adult",
                  price: Double = 5.00,
                  event_date: DateTime = new DateTime(2017, 7, 11),
                  qrCode: Option[Array[Byte]] = None)

class Tickets(tag: Tag) extends Table[Ticket](tag, "TICKETS") {
  def ticket_id = column[UUID]("TICKET_ID", O.PrimaryKey)

  def ticket_type = column[String]("TICKET_TYPE")

  def description = column[String]("TICKET_DESCRIPTION")

  def price = column[Double]("PRICE")

  def event_date = column[DateTime]("EVENT_TIME")

  def qrCode = column[Option[Array[Byte]]]("QRCODE")

  def * = (ticket_id, ticket_type, description, price, event_date, qrCode) <>(Ticket.tupled, Ticket.unapply _)
}


object TicketsDao {

  lazy val tickets = TableQuery[Tickets]

  //val db = Database.forConfig("postgresql-ticket-database")

  def findById(id: UUID)(implicit db: DatabaseDef): Future[Option[Ticket]] =
    db.run(tickets.filter(_.ticket_id === id).result).map(_.headOption)


  def findByTicketType(t: String)(implicit db: DatabaseDef): Future[Seq[Ticket]] =
    db.run(tickets.filter(_.ticket_type === t).result)


  def create(ticket: Ticket)(implicit db: DatabaseDef): Future[Unit] =
    db.run(DBIO.seq(tickets.insertOrUpdate(ticket)))

  def create(tickets: List[Ticket])(implicit db: DatabaseDef): List[Future[Unit]] =
    tickets.map(create)


  def deleteById(id: UUID)(implicit db: DatabaseDef): Future[Int] =
    db.run(tickets.filter(_.ticket_id === id).delete)

}

object TicketManager {

  /**
    * Generate a number of tickets
    *
    * @param numberOfTicket
    * @param ticket_type
    * @param description
    * @param price
    * @param event_date
    * @return
    */
  def generateTickets(numberOfTicket: Int, ticket_type: String, description: String, price: Double, event_date: DateTime): List[Ticket] = (for (i <- 1 to numberOfTicket) yield {
    println("---------------------------->" + i)
    Ticket(UUID.randomUUID(), ticket_type, description, price, event_date, None)
  }).toList

  /**
    * Convert ticket to Json
    *
    * @param ticket
    * @return
    */
  def ticketToJsonString(ticket: Ticket): String =
    s"""
       |{
       |  "id" : "${ticket.ticket_id.toString}",
       |  "label" : "${ticket.ticket_type}",
       |  "description":"${ticket.description}",
       |  "price": "${f"${ticket.price}%1.2f"}",
       |  "date" : "${DateTimeFormat.forPattern("MM/dd/yyyy").print(ticket.event_date)}"
       |}
   """.stripMargin

  /**
    * Encrich a single ticket with QrCode
    *
    * @param ticket
    * @return
    */
  def enrichTicketWithQrCode(ticket: Ticket): Ticket = {
    val qrCode: Array[Byte] = QRCode.from(ticketToJsonString(ticket))
      .withCharset(StandardCharsets.UTF_8.name()).withSize(450, 450)
      .to(ImageType.PNG).stream().toByteArray

    ticket.copy(qrCode = Some(qrCode))
  }

  /**
    * Enrich all tickets in a list of tickets with QrCode
    *
    * @param tickets
    * @return
    */
  def enrichedTicketsWithQrCode(tickets: List[Ticket]): List[Ticket] = tickets.map(enrichTicketWithQrCode)

  def writeTicketQrCode(ticket: Ticket)(implicit out: OutputStream) =
    out.write(ticket.qrCode.getOrElse(new Array[Byte](0)))

  def writeTicketQrCode(tickets: List[Ticket], directory: File): Unit = tickets.map { ticket =>
    val qrCodeFIle = new File(directory.getAbsolutePath + File.separator + ticket.ticket_id.toString + ".png")
    println(s"Printing QrCode to file : ${qrCodeFIle.getAbsoluteFile}")
    implicit val fout = new FileOutputStream(qrCodeFIle)
    writeTicketQrCode(ticket)
    fout.close()
  }

  def writeToCSVFile(tickets: List[Ticket], groupQuantity: Int = 5)(implicit out: Writer) = {

    def ticketToCsv(ticket: Ticket): String =
      s""""${ticket.ticket_id}","${ticket.ticket_type}","${ticket.description}","${f"${ticket.price}%1.2f"}","${DateTimeFormat.forPattern("MM/dd/yyyy").print(ticket.event_date)}","/tmp/chelford_pta/scribus/qrcodes/${ticket.ticket_id}.png""""
    //Write Header
    def ticketToCsvHeader(groupIndex: Int) =
      s""""uuid_${groupIndex}","t_type_${groupIndex}","t_desc_${groupIndex}", "t_price_${groupIndex}","e_date_${groupIndex}","qrcode_${groupIndex}""""

    //Get the full header list
    val hl: IndexedSeq[String] = for (i <- 1 to groupQuantity) yield ticketToCsvHeader(i)
    val header: String = hl.mkString(",")

    val groupedTckt: List[List[Ticket]] = tickets.foldLeft(List[List[Ticket]]()) { (gl, t) =>
      gl match {
        case h :: tail =>
          if (h.length < groupQuantity) (t :: h) :: tail
          else
            List(t) :: (h :: tail)
        case Nil => List(List(t))
      }
    }

    val data = groupedTckt.map(t => t.map(ticketToCsv).mkString(",")) //Converts a list of tickets to horizontal comma separated data

    //Write the data to output stream
    out.write(header)
    out.write("\n")
    data.map{ d =>
      out.write(d)
      out.write("\n")
      //out.write(0x0A)
    }
  }
}
