import akka.actor.{Props, ActorSystem}
import akka.routing.FromConfig
import com.typesafe.config.Config

object ClusterNodes {
  def startNode(nodeConfiguration: Config): ActorSystem = {
    val clusterNode = ActorSystem("Cluster", nodeConfiguration)

    clusterNode.actorOf(
      Props[Shouter],
      "shouter1"
    )
    clusterNode.actorOf(
      Props[Shouter],
      "shouter2"
    )

    clusterNode.actorOf(
      FromConfig.props(),
      "broadcaster"
    )

    clusterNode.actorOf(
      Props[Witness],
      "witness"
    )

    clusterNode
  }
}
