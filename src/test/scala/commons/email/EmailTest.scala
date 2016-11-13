package commons.email

import java.io.File

import commons.files.Files
import commons.logger.Logger
import org.scalatest.FunSuite

import scala.xml.{Elem, XML}

/*
sbt "~rzepaw-commons/testOnly commons.email.EmailTest"
*/

class EmailTest
  extends FunSuite
    with Logger {

  ignore("Receive some emails") {
    val i = IMAPServer("imap.gmail.com", "wextmail@gmail.com", "wextmail2014", "Inbox")
    println("Inbox:")
    i.messages foreach { m =>
      println("\n\n - " + m)
      println("Subject: " + m.subject)
      println("Subject thread: " + m.subjectThread)
      m.files foreach { f =>
        println(" File: " + f)
      }
    }
  }

  ignore("Send an email") {
    val s = SMTPServer("smtp.gmail.com", "wextmail@gmail.com", "wextmail2014")
    val e = new EmailOut(s)
    val html: String = Files.textFromResources("email", "email.html")
    e.subjectRaw = "WEXT - Scaffold email"
    e.text = Some("PLAIN TEXT")
    e.htmlString = Some(html)
    e.senderAddress = "wextmail@gmail.com"
    e.recipients = Seq("wext@subeli.com")
    e.send
  }
}
