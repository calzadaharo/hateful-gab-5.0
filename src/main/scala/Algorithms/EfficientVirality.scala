package Algorithms

import com.raphtory.core.algorithm.GraphPerspective

import com.raphtory.core.algorithm.{GraphAlgorithm,Row,Table}
import com.raphtory.core.graph.visitor.Vertex

class EfficientVirality  extends GraphAlgorithm {
  override def apply(graph: GraphPerspective): GraphPerspective = {
    graph
      .step{(vertex: Vertex) => {
        vertex.getProperty[String]("index") match {
          case Some(i) => {i match {
            case "0" => {
              vertex.setState("contribution",1)
              vertex.setState("sum",0.toLong)
              vertex.setState("index",1)
            }
            case "1" => {
              vertex.setState("contribution",1)
              vertex.setState("sum",2.toLong)
              vertex.setState("index",1)
            }
            case "2" => {
              vertex.setState("index",i)
              vertex.messageOutNeighbours(SendMeParent(vertex.ID(),2))
            }
            case _ => {
              vertex.setState("index",0)
              vertex.setState("contribution",0)
            }
          }}
          case None => logger.error("ERROR")
        }
      }}
      .iterate({(vertex:Vertex) =>
        vertex.messageQueue[Message].foreach(_ match {
            case SendMeParent(id, index) => {
              val contribution = vertex.getState[Int]("contribution")
              vertex.setState("contribution",contribution+1)
              vertex.setState("index",index)
              vertex.messageVertex(id,MyContribution(contribution))
              vertex.messageAllNeighbours(UpdateDistance(2,index))
            }
          case MyContribution(value) => {
            val index: String = vertex.getProperty[String]("index") match {
              case Some(i) => i
              case None => "ERROR"
            }
            val totalSum: Long = 2*(value.toLong+index.toLong)
//            println(s"Total sum of ${index}: ${totalSum}")
            vertex.setState("sum",totalSum)
            vertex.setState("index",index.toInt)
            vertex.setState("contribution",value+index.toInt)
          }
          case UpdateDistance(distance, receivedIndex) => {
            val myIndex: String = vertex.getProperty[String]("index") match {
              case Some(i) => i
              case None => "ERROR"
            }
            val mySituation = vertex.getState[Int]("index")
            if (receivedIndex > myIndex.toInt && receivedIndex > mySituation) {
              val newContribution: Int = (vertex.getState[Int]("contribution")
                + distance)
              vertex.setState("index",receivedIndex)
              vertex.setState("contribution",newContribution)
              vertex.messageAllNeighbours(
                UpdateDistance(newContribution,receivedIndex))
            } else if (myIndex.toInt - 1 == receivedIndex) {
//              println(s"FOUND NEXT: ${myIndex}")
//              vertex.setState("index", myIndex.toInt)
              vertex.messageOutNeighbours(SendMeParent(vertex.ID(),myIndex.toInt))
            }
          }
          case _ => println("ERROR")
        })
      }, iterations = 100000, executeMessagedOnly = true)
  }
  override def tabularise(graph: GraphPerspective): Table = {
    graph
      .select(vertex=> {
        Row(vertex.ID(),
          vertex.getPropertyOrElse("cascade","null"),
          vertex.getPropertyOrElse("index","null"),
          vertex.getStateOrElse("sum", 0))
      })
  }
  sealed trait Message {}
  case class SendMeParent(vertexId: Long, index: Int)     extends Message
  case class MyContribution(contribution: Int)            extends Message
  case class UpdateDistance(newDistance: Int, index: Int) extends Message
}

object EfficientVirality {
  def apply() = new EfficientVirality()
}