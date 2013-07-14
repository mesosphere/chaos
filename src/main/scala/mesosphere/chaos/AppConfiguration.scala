package mesosphere.chaos

import org.rogach.scallop.ScallopConf

/**
 * @author Florian Leibert (flo@leibert.de)
 */
trait AppConfiguration extends ScallopConf {
  //TODO(*): Move this into chaos!
  lazy val logConfigFile = opt[String]("log_config",
    descr = "The path to the log config",
    required = false,
    noshort = true)
}
