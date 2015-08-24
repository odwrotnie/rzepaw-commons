//package commons.sound
//
//import javax.sound.midi.MidiSystem
//
//import com.googlecode.scala.sound.midi.Notes._
//import com.googlecode.scala.sound.midi.message._
//
//object Beep {
//
//  val timeStamp: Long = -1
//  val rcvr = MidiSystem.getReceiver
//
//  def thin {
//    rcvr.send(NoteOn(0, C6, 93), timeStamp)
//  }
//
//  def fat {
//    rcvr.send(NoteOn(0, C3, 93), timeStamp)
//  }
//}
