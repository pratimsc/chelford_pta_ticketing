

import java.io.{File, FileOutputStream}
import java.util.UUID

import net.glxn.qrgen.core.image.ImageType
import net.glxn.qrgen.javase.QRCode

//val f:File = QRCode.from("Hello momy").file()

val filePath = """/tmp/qrcodes/"""
val qrcodeImageType = """.png"""


for (i <- 1 to 10){
  val uuid = UUID.randomUUID()
  println(uuid.toString)
  val fout = Some(new FileOutputStream(filePath+uuid+qrcodeImageType))
  fout.map(QRCode.from(uuid.toString).withCharset("UTF-8").to(ImageType.PNG).writeTo(_))
}




