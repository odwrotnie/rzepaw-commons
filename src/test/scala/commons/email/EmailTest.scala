package commons.email

import commons.files.Files
import commons.logger.Logger
import org.scalatest.FunSuite

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

  test("Send an email") {
    val s = SMTPServer("smtp.gmail.com", "wextmail@gmail.com", "wextmail2014")
    val e = EmailOut(s)
    e.subjectRaw = "WEXT - Scaffold email"
    e.text = Some("PLAIN TEXT")
    e.html = Some(html)
    e.senderAddress = "wextmail@gmail.com"
    e.recipients = Seq("wext@subeli.com")
    e.send
  }

  lazy val html = <html>
    <head>
      <meta name="viewport" content="width=device-width"/>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
      <title>Really Simple HTML Email Template</title>
      { style }
    </head>

    <body bgcolor="#f6f6f6">

      <!-- body -->
      <table class="body-wrap" bgcolor="#f6f6f6">
        <tr>
          <td></td>
          <td class="container" bgcolor="#FFFFFF">

            <!-- content -->
            <div class="content">
              <table>
                <tr>
                  <td>
                    <p>Hi there,</p>
                    <p>Sometimes all you want is to send a simple HTML email with a basic design.</p>
                    <h1>Really simple HTML email template</h1>
                    <p>This is a really simple email template. Its sole purpose is to get you to click the button below.</p>
                    <h2>How do I use it?</h2>
                    <p>All the information you need is on GitHub.</p>
                    <!-- button -->
                    <table class="btn-primary" cellpadding="0" cellspacing="0" border="0">
                      <tr>
                        <td>
                          <a href="https://github.com/leemunroe/html-email-template">View the source and instructions on GitHub</a>
                        </td>
                      </tr>
                    </table>
                    <!-- /button -->
                    <p>Feel free to use, copy, modify this email template as you wish.</p>
                    <p>Thanks, have a lovely day.</p>
                    <p><a href="http://twitter.com/leemunroe">Follow @leemunroe on Twitter</a></p>
                  </td>
                </tr>
              </table>
            </div>
            <!-- /content -->

          </td>
          <td></td>
        </tr>
      </table>
      <!-- /body -->

      <!-- footer -->
      <table class="footer-wrap">
        <tr>
          <td></td>
          <td class="container">

            <!-- content -->
            <div class="content">
              <table>
                <tr>
                  <td align="center">
                    <p>Don't like these annoying emails? <a href="#"><unsubscribe>Unsubscribe</unsubscribe></a>.
                    </p>
                  </td>
                </tr>
              </table>
            </div>
            <!-- /content -->

          </td>
          <td></td>
        </tr>
      </table>
      <!-- /footer -->

    </body>
  </html>

  lazy val style = {
    val css = Files.textFromResources("email", "style.css").get
    <style>{ css }</style>
  }
}
