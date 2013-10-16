package mesosphere.chaos.http

import org.rogach.scallop.ScallopConf

/**
 * @author Florian Leibert (flo@leibert.de)
 */

trait HttpConf extends ScallopConf {
  lazy val httpPort = opt[Int]("http_port",
    descr = "The port to listen on for HTTP requests", default = Some(8080),
    noshort = true)

  lazy val httpsPort = opt[Int]("https_port",
    descr = "The port to listen on for HTTPS requests", default = Some(8443),
    noshort = true)

  lazy val sslKeystorePath = opt[String]("ssl_keystore_path",
    descr = "Provides the keystore, if supplied, SSL is enabled",
    default = None, noshort = true)

  lazy val sslKeystorePassword = opt[String]("ssl_keystore_password",
    descr = "The password for the keystore", default = None, noshort = true)

  lazy val httpCredentials = opt[String]("http_credentials",
    descr = "Credentials for accessing the http service." +
      "If empty, anyone can access the HTTP endpoint. A username:password" +
      "is expected where the username must not contain ':'",
    default = None, noshort = true)

}
