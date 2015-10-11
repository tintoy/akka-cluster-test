import concurrent.Await
import concurrent.duration._
import io.StdIn

/**
 * The main application entry-point.
 */
object Application extends App {
  val clusterNode1 = ClusterBuilder.startNode(1)
  val clusterNode2 = ClusterBuilder.startNode(2)
  val clusterNode3 = ClusterBuilder.startNode(3)

  println("Press enter to terminate.")
  StdIn.readLine

  clusterNode1.terminate()
  clusterNode2.terminate()
  clusterNode3.terminate()

  // AF: Task.WhenAll is still a little simpler.
  implicit val executionContext = concurrent.ExecutionContext.global
  Await.result(
    for {
      node1Terminated <- clusterNode1.whenTerminated
      node2Terminated <- clusterNode2.whenTerminated
      node3Terminated <- clusterNode3.whenTerminated
    } yield (node1Terminated, node2Terminated, node3Terminated),
    5.seconds
  )
}

