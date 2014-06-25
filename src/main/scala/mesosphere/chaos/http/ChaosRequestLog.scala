package mesosphere.chaos.http

import org.eclipse.jetty.server.NCSARequestLog
import org.apache.log4j.Logger
import org.eclipse.jetty.util.StringUtil

class ChaosRequestLog extends NCSARequestLog {

  val lineSepLength = StringUtil.__LINE_SEPARATOR.length

  private[this] val log = Logger.getLogger(getClass.getName)

  override def write(requestEntry: String) {
    // Remove line separator because jul will add it
    log.info(requestEntry.substring(0, requestEntry.length - lineSepLength))
  }
}
