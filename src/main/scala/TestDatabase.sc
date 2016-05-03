import java.io.{File, FileOutputStream}
import java.nio.charset.StandardCharsets
import java.text.DecimalFormat
import java.util.UUID

import chelford.ticketing.{Ticket, TicketManager, Tickets, TicketsDao}
import com.typesafe.config.ConfigFactory
import net.glxn.qrgen.core.image.ImageType
import net.glxn.qrgen.javase.QRCode
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import slick.driver.PostgresDriver.api._
import slick.driver.PostgresDriver.backend.DatabaseDef

import scala.None
import scala.concurrent.Await
import scala.concurrent.duration.Duration


val tl_ac: List[Ticket] = TicketManager.generateTickets(250, "AC", "Adult Hog Roast (Pre-Book)", 4.00, new DateTime(2016, 6, 11, 0, 0, 0)).map(TicketManager.enrichTicketWithQrCode)
val tl_an: List[Ticket] = TicketManager.generateTickets(250, "AN", "Adult Hog Roast", 5.00, new DateTime(2016, 6, 11, 0, 0, 0)).map(TicketManager.enrichTicketWithQrCode)
val tl_cc1: List[Ticket] = TicketManager.generateTickets(250, "CC1", "Child Hog Roast (Pre-Book)", 1.00, new DateTime(2016, 6, 11, 0, 0, 0)).map(TicketManager.enrichTicketWithQrCode)
val tl_cc2: List[Ticket] = TicketManager.generateTickets(250, "CC2", "Child Hot Dog (Pre-Book)", 1.00, new DateTime(2016, 6, 11, 0, 0, 0)).map(TicketManager.enrichTicketWithQrCode)
val tl_cn1: List[Ticket] = TicketManager.generateTickets(250, "CN1", "Child Hog Roast", 2.00, new DateTime(2016, 6, 11, 0, 0, 0)).map(TicketManager.enrichTicketWithQrCode)
val tl_cn2: List[Ticket] = TicketManager.generateTickets(250, "CN2", "Child Hot Dog", 2.00, new DateTime(2016, 6, 11, 0, 0, 0)).map(TicketManager.enrichTicketWithQrCode)
val tl_cm: List[Ticket] = TicketManager.generateTickets(250, "CM", "Child (< 4 yrs) Free", 0.00, new DateTime(2016, 6, 11, 0, 0, 0)).map(TicketManager.enrichTicketWithQrCode)

//Enrich tickets with QrCode
//val enrichedTickets = TicketManager.enrichedTicketsWithQrCode(tl_ac)
//enrichedTickets.map(t => println(s"""Adding ticket to database with details [${t}]"""))

val _filePath = """/tmp/chelford_pta/scribus/qrcodes/"""
TicketManager.writeTicketQrCode(tl_ac, new File(_filePath))
TicketManager.writeTicketQrCode(tl_an, new File(_filePath))
TicketManager.writeTicketQrCode(tl_cc1, new File(_filePath))
TicketManager.writeTicketQrCode(tl_cc2, new File(_filePath))
TicketManager.writeTicketQrCode(tl_cn1, new File(_filePath))
TicketManager.writeTicketQrCode(tl_cn2, new File(_filePath))
TicketManager.writeTicketQrCode(tl_cm, new File(_filePath))

/**
  * All database operations start below.
  */
val parsedConfig = ConfigFactory.parseFile(new File("/home/pratimsc/IdeaProjects/chelford_ticketing/src/main/resources/application.conf"))
val conf = ConfigFactory.load(parsedConfig)

implicit lazy val db: slick.driver.PostgresDriver.backend.DatabaseDef = Database.forConfig("postgresql-ticket-database", conf)


println("Add tickets to database")
TicketsDao.create(tl_ac)
TicketsDao.create(tl_an)
TicketsDao.create(tl_cc1)
TicketsDao.create(tl_cc2)
TicketsDao.create(tl_cn1)
TicketsDao.create(tl_cn2)
TicketsDao.create(tl_cm)
println("Tickets added to database")

implicit val fout1 = new FileOutputStream(new File("/tmp/chelford_pta/scribus/temp_tl_ac.csv"))
TicketManager.writeToCSVFile(tl_ac)(fout1)
fout1.close()
implicit val fout2 = new FileOutputStream(new File("/tmp/chelford_pta/scribus/temp_tl_an.csv"))
TicketManager.writeToCSVFile(tl_an)(fout2)
fout2.close()
implicit val fout3 = new FileOutputStream(new File("/tmp/chelford_pta/scribus/temp_tl_cc1.csv"))
TicketManager.writeToCSVFile(tl_cc1)(fout3)
fout3.close()
implicit val fout4 = new FileOutputStream(new File("/tmp/chelford_pta/scribus/temp_tl_cc2.csv"))
TicketManager.writeToCSVFile(tl_cc2)(fout4)
fout4.close()
implicit val fout5 = new FileOutputStream(new File("/tmp/chelford_pta/scribus/temp_tl_cn1.csv"))
TicketManager.writeToCSVFile(tl_cn1)(fout5)
fout5.close()
implicit val fout6 = new FileOutputStream(new File("/tmp/chelford_pta/scribus/temp_tl_cn2.csv"))
TicketManager.writeToCSVFile(tl_cn2)(fout6)
fout6.close()
implicit val fout7 = new FileOutputStream(new File("/tmp/chelford_pta/scribus/temp_tl_cm.csv"))
TicketManager.writeToCSVFile(tl_cm)(fout7)
fout7.close()


