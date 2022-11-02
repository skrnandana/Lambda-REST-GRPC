import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.funsuite.AnyFunSuite
import gRPCServer.{config, url}

class gRPCTests extends AnyFunSuite {

  val config: Config = ConfigFactory.load()
  //Test to assert the port from configuration
  test("PORT TEST") {
    val actual = config.getString("port")
    val expected = "50051"
    //Assert to check the port
    assert(expected == actual)
  }


  //Test to assert the time from configuration
  test("Time Test") {
    val actual = config.getString("T")
    val expected = "17:38:04"
    //Assert to check the time stamp
    assert(expected == actual)
  }

  //Test to assert the delta time from configuration
  test("Delta Time test") {
    val actual = config.getString("dT")
    //Assert to check the time delta
    val expected = "00:00:20"
    assert(expected == actual)
  }

}
