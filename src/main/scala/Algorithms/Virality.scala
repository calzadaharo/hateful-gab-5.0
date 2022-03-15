package Algorithms

import com.raphtory.core.algorithm.{GraphAlgorithm, GraphPerspective, Row, Table}

class Virality extends GraphAlgorithm {
  override def apply(graph: GraphPerspective): GraphPerspective = {
    graph
      .step(vertex => {
        val index = vertex.getPropertyOrElse[Long]("index",-1)
        if (index == 0) vertex.setState("sent",2)
        if (index == 1) {
          vertex.setState("sum", 2)
          vertex.setState("parent_sum",0)
          vertex.setState("sent",2)
        } else if (index == 2) {
          vertex.setState("sum", 3)
          vertex.setState("parent_sum",2)
          vertex.setState("sent",3)
          vertex.messageOutNeighbours(vertex.ID(),index+1,1,3)
        }
      })
      .iterate({vertex =>
        val messages = vertex.messageQueue[(String,Long,Long,Long)]
        messages.foreach( message => {
          val received_id = message._1
          val received_index = message._2
          val distance = message._3
          val parent_sum = message._4

          val my_index = vertex.getPropertyOrElse[Long]("index",-1)
          val my_sent = vertex.getStateOrElse[Long]("sent",-1)

          // Check if I am the main vertex
          if (my_index == received_index) {
            // Check if it comes from an specific vertex
            if (received_index == 0) {
              val my_new_storage = vertex.getStateOrElse[Long]("storage",-1) + 1
              val my_new_sum = vertex.getStateOrElse[Long]("sum",-1) + distance
              vertex.setState("sum",my_new_sum)
              // Check if I have received all previous posts
              if (my_new_storage == my_index) {
                vertex.messageAllNeighbours(0, my_index + 1, 0, my_new_sum)
              } else {
                vertex.setState("storage",my_new_storage)
              }
            }
          } else {
            // Check if it comes from a new vertex
            if (parent_sum != 0) {
              // Check if I am the new vertex
              if (my_index == received_index) {
                vertex.setState("sum",0)
                vertex.setState("parent_sum",parent_sum)
                vertex.setState("storage",0)
                vertex.messageOutNeighbours(vertex.ID(),my_index,1,0)
              }
              // Not a new vertex
            } else {
              // Check if I am previous to the sender message
              if (received_index < my_index) {
                // Check if I had already been considered
                if (received_index < my_sent) {
                  vertex.setState("index",received_index)
                  vertex.messageVertex(received_id.toLong, (received_id, 0, distance, parent_sum))
                  vertex.messageAllNeighbours(received_id,received_index,distance+1,parent_sum)
                }
              }
            }
          }
        })
      }, iterations = 100000, executeMessagedOnly = true)
  }
  override def tabularise(graph: GraphPerspective): Table = {
    graph
      .select(vertex => {
        Row(
          vertex.getPropertyOrElse("cascade",null),
          vertex.getPropertyOrElse("level", null),
          vertex.getStateOrElse("sum",null),
          vertex.getStateOrElse("parent_sum",null)
        )
      })
  }
}

object Virality {
  def apply() = new Virality()
}
