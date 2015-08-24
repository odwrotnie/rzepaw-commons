package commons.logger

import com.github.lalyos.jfiglet.FigletFont
import org.slf4j._

trait Logger {

  @transient
  private lazy val logger = LoggerFactory.getLogger(getClass)

  def trace(msg: => AnyRef) = if (logger.isTraceEnabled)
    logger.trace(String.valueOf(msg))
  def isTraceEnabled = logger.isTraceEnabled

  def debug(msg: => AnyRef) = if (logger.isDebugEnabled)
    logger.debug(String.valueOf(msg))
  def isDebugEnabled = logger.isDebugEnabled

  def info(msg: => AnyRef) = if (logger.isInfoEnabled)
    logger.info(String.valueOf(msg))
  def infoAsciiArt(msg: => AnyRef) = if (logger.isInfoEnabled) {
    logger.info(String.valueOf(msg))
    println("\n" + FigletFont.convertOneLine(String.valueOf(msg)))
  }

  def isInfoEnabled = logger.isInfoEnabled

  def warn(msg: => AnyRef) = if (logger.isWarnEnabled)
    logger.warn(String.valueOf(msg))
  def isWarnEnabled = logger.isWarnEnabled

  def error(msg: => AnyRef) = if (logger.isErrorEnabled)
    logger.error(String.valueOf(msg))
  def isErrorEnabled = logger.isErrorEnabled
}
