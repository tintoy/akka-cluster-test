import akka.actor.{ActorLogging, Cancellable, Actor}
import concurrent.duration._

/**
 * Actor that repeatedly sends messages to shouter(s), via broadcaster.
 */
class BrokenRecord(val nodeId: Int) extends Actor with ActorLogging {
  import context.dispatcher
  
  lazy val scheduler = context.system.scheduler
  var repeaterCancellation: Cancellable = null

  /**
   * Called when the actor is started.
   */
  override def preStart(): Unit = {
    super.preStart()

    log.debug("Starting broadcast reminder")
    repeaterCancellation = scheduler.schedule(
      initialDelay = 3.seconds,
      interval = 3.seconds,
      receiver = self,
      message = DoBroadcast
    )
  }

  /**
   * Called when the actor is stopped.
   */
  override def postStop(): Unit = {
    if (repeaterCancellation != null) {
      log.debug("Stopping broadcast reminder")
      repeaterCancellation.cancel()
    }

    super.postStop()
  }

  /**
   * Called when the actor receives a message.
   */
  override def receive: Receive = {
    case DoBroadcast =>
      log.debug("Broadcasting.")
      context.actorSelection("../broadcaster").tell(
        Shouter.Shout(
          s"Hello from ${self.path.name} on node ${nodeId}"
        ),
        sender = self
      )
  }

  /**
   * Message telling a `BrokenRecord` to broadcast another message.
   */
  private case object DoBroadcast
}
