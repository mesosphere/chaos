package mesosphere.chaos.examples.trains.modules

import com.softwaremill.macwire.aop.NoOpInterceptor
import mesosphere.chaos.examples.trains.modules.impl.shunting.PointSwitcher
import org.mockito.Mockito._
import org.scalatest.FlatSpec

class ShuntingModuleTest extends FlatSpec {
  it should "work" in {
    // given
    val mockPointSwitcher = mock(classOf[PointSwitcher])

    // when
    val moduleToTest = new TraditionalShuntingModule {
      // the mock implementation will be used to wire the graph
      override lazy val pointSwitcher = mockPointSwitcher
      lazy val logEvents = NoOpInterceptor
    }
    moduleToTest.trainShunter.shunt()

    // then
    verify(mockPointSwitcher).switch()
  }
}
