import akka.actor.{Props, ActorSystem}
import akka.routing.FromConfig
import com.typesafe.config.{ConfigFactory, Config}

/**
 * A simple factory for building ``ActorSystem``s that represent nodes in the cluster.
 */
object ClusterBuilder {
  // Shared configuration for all nodes.
  private val appConfig: Config = ConfigFactory.defaultApplication()
  private val commonConfig = appConfig.getConfig("common")

  /**
   * Start an `ActorSystem` to represent the specified cluster node.
   * @param nodeId The cluster node Id (used as short-hand to differentiate actors on different nodes).
   * @return The configured `ActorSystem`.
   */
  def startNode(nodeId: Int): ActorSystem = {
    // Composite configuration with node-specific overrides.
    val nodeConfiguration =
      appConfig.getConfig(s"node${nodeId}")
          .withFallback(commonConfig)

    val clusterNode = ActorSystem("ClusterSystem", nodeConfiguration)

    // Shouters used to display broadcast messages.
    val shouterProps = Props(classOf[Shouter], nodeId)
    clusterNode.actorOf(shouterProps, s"shouter${nodeId}")
    clusterNode.actorOf(shouterProps, s"shouter${nodeId + 1}")

    // The clustered broadcaster (see application.conf).
    clusterNode.actorOf(
      FromConfig.props(),
      "broadcaster"
    )

    // The BrokenRecord actor periodically emits messages for broadcast.
    clusterNode.actorOf(
      Props(classOf[BrokenRecord], nodeId),
      "broken-record"
    )

    clusterNode
  }
}
