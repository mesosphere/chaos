package mesosphere.chaos.http

import org.rogach.scallop.ScallopConf
import java.net.URL

trait HttpConf extends ScallopConf {
  lazy val httpPort = opt[Int]("http_port",
    descr = "The port to listen on for HTTP requests", default = Some(8080),
    noshort = true)

  lazy val httpsPort = opt[Int]("https_port",
    descr = "The port to listen on for HTTPS requests", default = Some(8443),
    noshort = true)

  lazy val sslKeystorePath = opt[String]("ssl_keystore_path",
    descr = "Path to the keystore, if supplied, SSL is enabled",
    default = None, noshort = true)

  lazy val sslKeystorePassword = opt[String]("ssl_keystore_password",
    descr = "The password for the keystore", default = None, noshort = true)

  lazy val shiroIni = opt[String]("shiro_ini",
    descr = "The shiro.ini auth configuration file. See http://shiro.apache.org/",
    default = None, noshort = true)

  lazy val httpCredentials = opt[String]("http_credentials",
    descr = "Credentials for accessing the http service. " +
      "If empty, anyone can access the HTTP endpoint. A username:password " +
      "pair is expected where the username must not contain ':'",
    default = None, noshort = true)

  lazy val assetsFileSystemPath = opt[String]("assets_path",
    descr = "Set a local file system path to load assets from, " +
      "instead of loading them from the packaged jar.",
    default = None, noshort = true)

  def assetsUrl(): URL = assetsFileSystemPath.get match {
    case Some(path: String) => new URL(s"file:$path")
    // Default to the asset path in the jar
    case _                  => getClass.getClassLoader.getResource("assets")
  }
}
