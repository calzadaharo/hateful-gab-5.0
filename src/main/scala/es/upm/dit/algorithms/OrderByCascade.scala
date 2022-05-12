package es.upm.dit.algorithms

//import com.raphtory.core.algorithm.{GraphAlgorithm, GraphPerspective, Row, Table}
import com.raphtory.algorithms.api.{GraphAlgorithm, GraphPerspective, Row, Table}

class OrderByCascade extends GraphAlgorithm {
  override def apply(graph: GraphPerspective): GraphPerspective = {
    graph
      .step(vertex => {
        if (vertex.getPropertyOrElse("type", null) == "original" &&
          vertex.getAllNeighbours().nonEmpty) {
          vertex.setState("cascade", vertex.ID)
          vertex.setState("level", 0)
          vertex.messageInNeighbours(1, vertex.ID())
        }
      })
      .iterate({ (vertex) => {
        val message = vertex.messageQueue[(Int, Long)]
        val level: Int = message(0)._1
        val cascade: Long = message(0)._2
        vertex.setState("cascade", cascade)
        vertex.setState("level", level)
        vertex.messageInNeighbours(level + 1, cascade)
      }
      }, iterations = 100000, executeMessagedOnly = true)
  }
  override def tabularise(graph: GraphPerspective): Table = {
    graph
      .select(vertex =>
        Row(vertex.ID(),
          vertex.getPropertyOrElse("parent",null),
          vertex.getStateOrElse("cascade",null),
          vertex.getPropertyOrElse("timestamp",null),
          vertex.getStateOrElse("level",null),
          vertex.getPropertyOrElse("hateful",null),
          vertex.getPropertyOrElse("user",null)
        )
      )
      .filter(_.get(2) != null)
  }
}

object OrderByCascade {
  def apply() = new OrderByCascade()
}