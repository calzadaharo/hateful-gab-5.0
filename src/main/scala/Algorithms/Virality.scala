package Algorithms

import com.raphtory.core.algorithm.{GraphAlgorithm, GraphPerspective, Row, Table}

class Virality extends GraphAlgorithm {
  override def apply(graph: GraphPerspective): GraphPerspective = {
    graph
      .step(vertex => {
        val index = vertex.getPropertyOrElse("index","-1").toLong
        if (index == 0) vertex.setState("sent",2)
        if (index == 1) {
          vertex.setState("sum", 2.toLong)
          vertex.setState("parent_sum",0.toLong)
          vertex.setState("sent",2)
        } else if (index == 2) {
          vertex.setState("sum", 8.toLong)
          vertex.setState("parent_sum",2.toLong)
          vertex.messageAllNeighbours(0.toLong,index+1,0.toLong,3.toLong, false)
        }
      })
      .iterate({vertex =>
        val messages = vertex.messageQueue[(Long,Long,Long,Long,Boolean)]
        messages.foreach( message => {
          val received_id = message._1
          val received_index = message._2
          val distance = message._3
          val parent_sum = message._4
          val direct = message._5

          val my_index = vertex.getPropertyOrElse("index","-1").toLong
          val my_sent = vertex.getStateOrElse("sent",0)
          val my_new_sent = vertex.getStateOrElse("new_sent",0)

          // Check if I am the main vertex
          if (my_index == received_index) {
            // Check if it comes from a new vertex
            if (parent_sum != 0 && vertex.getStateOrElse("parent_sum",0.toLong) == 0) {
              println("CAMINO 1")
              vertex.setState("sum", 0.toLong)
              vertex.setState("parent_sum", parent_sum)
              vertex.setState("storage", 0)
              vertex.messageOutNeighbours(vertex.ID(), my_index, 1.toLong, 0.toLong, false)
            } else {
              println("CAMINO X")
              // Check if it comes from an specific vertex
              if (direct) {
                println("CAMINO Y")
                val my_new_storage = vertex.getStateOrElse("storage",0) + 1
                val sum: Long = vertex.getState("sum")
                val my_new_sum = sum + distance
                vertex.setState("sum",my_new_sum)
                // Check if I have received all previous posts
                if (my_new_storage == my_index) {
                  println("CAMINO 2")
                  vertex.setState("new_sent",my_index.toInt+1)
                  val total_sum = 2*my_new_sum + vertex.getStateOrElse("parent_sum",0.toLong)
                  vertex.setState("sum",total_sum)
                  println("TOTAL SUM -> "+total_sum)
                  vertex.messageAllNeighbours(0.toLong, my_index + 1, 0.toLong, total_sum, false)
                } else {
                  println("CAMINO 3")
                  vertex.setState("storage",my_new_storage)
                }
              }
            }
          } else {
            if (parent_sum != 0.toLong) {
              println("CAMINO 4")
              if (my_new_sent < received_index) {
                println("CAMINO 5")
                vertex.setState("new_sent",received_index.toInt)
                vertex.messageAllNeighbours(received_id,received_index,distance,parent_sum,false)
              }
            } else {
              // Check if I am previous to the sender message and if I had already been considered
              if (received_index > my_index && received_index > my_sent) {
                // Check
                println("CAMINO 6")
                vertex.setState("sent",received_index.toInt)
                vertex.messageVertex(received_id,
                  (received_id, received_index, distance, parent_sum,true))
                vertex.messageAllNeighbours(received_id,received_index,distance+1,parent_sum,false)
                println("DISTANCE =" + distance +" VERTEX = " + my_index)
              }
              println("CAMINO 8")
            }
          }
        })
      }, iterations = 100000, executeMessagedOnly = true)
  }
  override def tabularise(graph: GraphPerspective): Table = {
    def virality(index: Int, total_sum: Long): Double = index match {
      case 0 => 1
      case _ => 1/(index.toDouble*(index.toDouble+1))*(total_sum.toDouble)
     }

    graph
      .select(vertex => {
        val index = vertex.getPropertyOrElse("index", "-1").toInt
        val sum = vertex.getStateOrElse("sum",0.toLong)
        val parent_sum = vertex.getStateOrElse("parent_sum",0.toLong)
        Row(
          vertex.getPropertyOrElse("cascade",null),
          index,
          vertex.getPropertyOrElse("level", null),
          sum,
          parent_sum,
          virality(index,sum)
        )
      })
  }
}

object Virality {
  def apply() = new Virality()
}
