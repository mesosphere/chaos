package mesosphere.chaos.http

import org.eclipse.jetty.server.{RequestLog, AbstractNCSARequestLog}
import java.util.logging.{Level, Logger}
import javax.inject.Inject

/**
 * @author Tobi Knaup
 */

class ChaosRequestLog @Inject()(log: Logger) extends AbstractNCSARequestLog with RequestLog {
  def isEnabled: Boolean = {
    log.isLoggable(Level.INFO)
  }

  def write(requestEntry: String) {
    log.info(requestEntry)
  }
}