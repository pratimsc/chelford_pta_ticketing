

import java.io.{File, FileOutputStream, OutputStream, PrintWriter}
import java.nio.charset.{Charset, StandardCharsets}
import java.util.UUID

import net.glxn.qrgen.core.image.ImageType
import net.glxn.qrgen.javase.QRCode

val _filePath = """/tmp/chelford_pta/qrcodes/"""
val _qrcodeImageType = """.png"""


for (i <- 1 to 1) {
  val uuid = UUID.randomUUID()
  println(uuid.toString)
  //val fout = Some(new FileOutputStream(filePath + uuid + qrcodeImageType))
  //fout.map(QRCode.from(uuid.toString).withCharset("UTF-8").to(ImageType.PNG).writeTo(_))
}

case class Ticket(id: UUID = UUID.randomUUID(),
                  label: String = "Adult",
                  price: Int = 5,
                  date: String = "11/07/2017")

def generateQrCode(data: String, charset: Charset): QRCode = QRCode.from(data).withCharset(charset.name()).to(ImageType.PNG)

def writeQrCodeToFile(qr: QRCode)(implicit out: OutputStream) = qr.writeTo(out)

def ticketToJsonString(ticket: Ticket): String =
  s"""
     |{
     |  "id" : "${ticket.id.toString}",
     |  "label" : "${ticket.label}",
     |  "price": "${ticket.price}",
     |  "date" : "${ticket.date}"
     |}
   """.stripMargin

def generateTickets(numberOfTicket: Int, label: String, price: Int, date: String): List[Ticket] = (for(i <- 1 to numberOfTicket) yield {
  println("---------------------------->"+i)
 Ticket(UUID.randomUUID(), label, price, date)
}).toList

def writeTicketToFile(tickets: List[(Ticket, File)], out: PrintWriter): List[Unit] = tickets.map { ticket =>
  val record: String =
    s""""${ticket._1.id.toString}","${ticket._1.label}","${ticket._1.price}","${ticket._1.date}","${ticket._2.getAbsolutePath}
       |"""".stripMargin
  out.write(record)
}

def writeTicketQrCodeToFile(tickets: List[Ticket], directory: File): List[(Ticket, File)] = tickets.map { t =>
  val qrCodeFIle = new File(directory.getCanonicalFile + File.separator + t.id.toString + ".png")
  println(s"Printing QrCode to file : ${qrCodeFIle.getAbsoluteFile}")
  implicit val fout = new FileOutputStream(qrCodeFIle)
  writeQrCodeToFile(generateQrCode(ticketToJsonString(t), StandardCharsets.UTF_8))
  fout.close()
  (t, qrCodeFIle)
}

val tickets = generateTickets(5, "Adult", 10, "21/11/2018")

val ticketsWithQrCode = writeTicketQrCodeToFile(tickets, new File(_filePath))

val ticketDataFile = new File(_filePath + UUID.randomUUID() + "___.csv")
val out = new PrintWriter(ticketDataFile)
writeTicketToFile(ticketsWithQrCode, out)
out.close()