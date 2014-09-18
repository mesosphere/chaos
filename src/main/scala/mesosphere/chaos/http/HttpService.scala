package mesosphere.chaos.http

import com.google.common.util.concurrent.AbstractIdleService
import com.google.inject.Inject
import org.eclipse.jetty.server.Server
import org.apache.log4j.Logger
import scala.util.Try

/**
  * Wrapper for starting and stopping the HttpServer.
  */
class HttpService @Inject() (val server: Server) extends AbstractIdleService {

  private[this] val log = Logger.getLogger(getClass.getName)

  def startUp() {
    log.debug("Starting up HttpServer.")
    Try(
      server.start()).recoverWith {
        case e: java.net.BindException => {
          log.fatal("Failed to start HTTP service", e)
          Try(server.stop())
        }
      }
  }

  def shutDown() {
    log.debug("Shutting down HttpServer.")
    server.stop()
  }
}
