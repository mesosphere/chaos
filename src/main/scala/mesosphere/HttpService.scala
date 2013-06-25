package mesosphere

import com.google.common.util.concurrent.AbstractIdleService
import com.google.inject.Inject
import org.eclipse.jetty.server.Server
import java.util.logging.Logger

/**
 * @author Florian Leibert (flo@leibert.de)
 */

class HttpService @Inject()(val server : Server, val log : Logger) extends AbstractIdleService {

  def startUp() {
    log.fine("Starting up HttpServer.")
    server.start()
  }

  def shutDown() {
    log.fine("Shutting down HttpServer.")
    server.stop()
  }
}
