package commons.email

import commons.logger.Logger
import org.scalatest.FunSuite

// sbt "~commons/testOnly commons.email.EmailTest"
class EmailTest
  extends FunSuite
  with Logger {

  test("Receive some emails") {
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
    e.subjectRaw = "Test message"
    e.text = Some("X!!!")
    e.senderAddress = "wextmail@gmail.com"
    e.recipients = Seq("odwrotnie@gmail.com")
    e.send
  }
}
