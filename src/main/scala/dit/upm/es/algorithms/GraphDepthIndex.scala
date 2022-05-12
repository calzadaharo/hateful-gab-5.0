package dit.upm.es.algorithms

//import com.raphtory.core.algorithm.{GraphAlgorithm, GraphPerspective, Row, Table}
import com.raphtory.algorithms.api.{GraphAlgorithm, GraphPerspective, Row, Table}

class GraphDepthIndex() extends GraphAlgorithm  {
  override def apply(graph: GraphPerspective): GraphPerspective = {
    graph
      .step(vertex => {
        if (vertex.getPropertyOrElse("type", null) == "original" &&
          vertex.getAllNeighbours().nonEmpty) {
          vertex.setState("cascade", vertex.ID)
          vertex.setState("level",0)
          vertex.setState("index", 0)
          val timestamp = vertex.getPropertyOrElse("timestamp","0").toLong
          vertex.messageInNeighbours(vertex.ID,timestamp,0,true)
        } else {
          val timestamp = vertex.getPropertyOrElse("timestamp","0").toLong
          vertex.setState("index", 0)
          vertex.messageOutNeighbours(0.toLong,timestamp,0,false)
        }
      })
      .iterate({vertex =>
        val messages = vertex.messageQueue[(Long,Long,Int,Boolean)]
        messages.foreach(message => {
          val cascade = message._1
          val timestamp = message._2
          val level = message._3 + 1
          val flag = message._4
          val index = vertex.getStateOrElse[Int]("index", 0) + 1

          // Check if message is already descending
          if (flag) {
            // Check if the message was born in the original
            if (cascade != 0) {
              vertex.setState("cascade",cascade)
              vertex.setState("level",level)
              vertex.setState("index",index)
              vertex.messageInNeighbours(cascade,timestamp,level,true)
            } else {
              // Check if vertex timestamp is higher than the message one
              if (timestamp < vertex.getPropertyOrElse("timestamp","0").toLong) {
                vertex.setState("index",index)
              }
              // Common for both higher and lower than timestamp
              vertex.messageInNeighbours(0.toLong,timestamp,0,true)
            }
            // Message ascending
          } else {
            // Check is message has reached the original
            if (vertex.getPropertyOrElse("type",null) == "original"){
              vertex.messageInNeighbours(0.toLong,timestamp,0,true)
            } else {
              vertex.messageOutNeighbours(0.toLong,timestamp,0,false)
            }
          }
        })
        }, iterations = 100000, executeMessagedOnly = true)
  }

  override def tabularise(graph: GraphPerspective): Table = {
    graph
      .select(vertex =>
        Row(
          vertex.ID(),
          vertex.getPropertyOrElse("parent",null),
          vertex.getStateOrElse("cascade",null),
          vertex.getPropertyOrElse("timestamp",null),
          vertex.getStateOrElse("index",null),
          vertex.getStateOrElse("level",null),
          vertex.getPropertyOrElse("hateful",null),
          vertex.getPropertyOrElse("user",null)
        )
      )
      .filter(row => row.get(5) != null)
  }
}

object GraphDepthIndex {
  def apply() = new GraphDepthIndex()
}
