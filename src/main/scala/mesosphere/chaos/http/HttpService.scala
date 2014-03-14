package mesosphere.chaos.http

import com.google.common.util.concurrent.AbstractIdleService
import com.google.inject.Inject
import org.eclipse.jetty.server.Server
import java.util.logging.{Level, Logger}
import scala.util.{Failure, Try}

/**
 * Wrapper for starting and stopping the HttpServer.
 * @author Florian Leibert (flo@leibert.de)
 * @author Tobi Knaup (tobi@knaup.me)
 */
class HttpService @Inject()(val server: Server, val log: Logger) extends AbstractIdleService {

  def startUp() {
    log.fine("Starting up HttpServer.")
    Try(
      server.start()
    ).recoverWith {
      case e: java.net.BindException => {
        log.log(Level.SEVERE, "Failed to start HTTP service", e)
        Try(server.stop())
      }
    }
  }

  def shutDown() {
    log.fine("Shutting down HttpServer.")
    server.stop()
  }
}
