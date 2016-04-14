package mesosphere.chaos.examples.trains

import akka.actor.{ActorSystem, Props}
import com.softwaremill.macwire.aop.ProxyingInterceptor
import com.softwaremill.macwire.wiredInModule
import com.softwaremill.tagging._
import mesosphere.chaos.examples.trains.modules.impl.loading.LoadListenerActor
import mesosphere.chaos.examples.trains.modules.impl.station.{Load, LoadListener}
import mesosphere.chaos.examples.trains.modules.{StationModule, ModernShuntingModule, TraditionalShuntingModule, LoadingModule}

object TrainStation extends App {
  val system = ActorSystem("trainSystem")

  val traditionalModules = new TraditionalShuntingModule
    with LoadingModule
    with StationModule {

    lazy val logEvents = ProxyingInterceptor { ctx =>
      println(s"${ctx.target} calling method: ${ctx.method.getName}")
      ctx.proceed()
    }
    lazy val actorSystem = system
  }

  val modernModules = new ModernShuntingModule
    with LoadingModule
    with StationModule {
    lazy val logEvents = ProxyingInterceptor { ctx =>
      println(s"${ctx.target} calling method: ${ctx.method.getName}")
      ctx.proceed()
    }
    lazy val actorSystem = system
  }

  println("# Traditional station:")
  println(traditionalModules.trainStation("Old school station.").prepareAndDispatchNextTrain())
  println("\n# Modern station:")
  println(modernModules.trainStation("Futuristic teleporting station.").prepareAndDispatchNextTrain())


  // Dynamically wired in PlugIns
  val wired = wiredInModule(modernModules)

  val pluginList = Seq(classOf[SlackPlugin])

  val plugins = pluginList.map { pluginClass =>
    wired
      .wireClassInstance(pluginClass)
      .asInstanceOf[TrainStationPlugin]
  }

  plugins.foreach(_.init())


  // Actor injection

  // usage; statically checked ActorRef types!
  val loadListener = system
    .actorOf(Props[LoadListenerActor])
    .taggedWith[LoadListener]

  val reactiveTrainDispatch = modernModules.createReactiveTrainDispatch(loadListener)

  reactiveTrainDispatch ! Load
}

trait TrainStationPlugin {
  def init(): Unit
}

class SlackPlugin extends TrainStationPlugin {
  override def init() = { println("Slack plugin initialized.") }
}
