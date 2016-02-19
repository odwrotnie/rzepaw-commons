package commons.email

import commons.logger.Logger
import org.scalatest.FunSuite

/*
sbt "~rzepawCommons/testOnly commons.email.EmailTest"
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

  test("Send an email") {
    val s = SMTPServer("smtp.gmail.com", "wextmail@gmail.com", "wextmail2014")
    val e = EmailOut(s)
    e.subjectRaw = "WEXT - Test message"
    e.text = Some("PLAIN TEXT")
    e.html = Some(<h1>HTML node sequence</h1>)
    e.senderAddress = "wextmail@gmail.com"
    e.recipients = Seq("wext@subeli.com")
    e.send
  }
}
