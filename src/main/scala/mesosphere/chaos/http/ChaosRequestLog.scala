package mesosphere.chaos.http

import org.eclipse.jetty.server.NCSARequestLog
import java.util.logging.Logger
import javax.inject.Inject
import org.eclipse.jetty.util.StringUtil

/**
 * @author Tobi Knaup
 */

class ChaosRequestLog @Inject()(log: Logger) extends NCSARequestLog {

  val lineSepLength = StringUtil.__LINE_SEPARATOR.length

  override def write(requestEntry: String) {
    // Remove line separator because jul will add it
    log.info(requestEntry.substring(0, requestEntry.length - lineSepLength))
  }
}