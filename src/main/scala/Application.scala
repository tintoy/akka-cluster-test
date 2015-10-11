import com.typesafe.config.{ConfigFactory, Config}
import concurrent.Await
import concurrent.duration._
import io.StdIn

/**
 * The main application entry-point.
 */
object Application extends App {
  val appConfig: Config = ConfigFactory.defaultApplication()
  val commonConfig = appConfig.getConfig("common")
  val node1Config = appConfig.getConfig("node1").withFallback(commonConfig)
  val node2Config = appConfig.getConfig("node2").withFallback(commonConfig)

  val clusterNode1 = ClusterNodes.startNode(node1Config)
  val clusterNode2 = ClusterNodes.startNode(node2Config)

  println("Press enter to terminate.")
  StdIn.readLine

  clusterNode1.terminate()
  clusterNode2.terminate()

  // AF: Task.WhenAll is still a little simpler.
  implicit val executionContext = concurrent.ExecutionContext.global
  Await.result(
    for {
      node1Terminated <- clusterNode1.whenTerminated
      node2Terminated <- clusterNode2.whenTerminated
    } yield 0,
    5.seconds
  )
}

