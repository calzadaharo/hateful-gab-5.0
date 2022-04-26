package Algorithms

import com.raphtory.algorithms.api.GraphAlgorithm
import com.raphtory.algorithms.api.GraphPerspective
import com.raphtory.algorithms.api.Row
import com.raphtory.algorithms.api.Table

class EfficientVirality  extends GraphAlgorithm {
  override def apply(graph: GraphPerspective): GraphPerspective = {
    graph
      .step{(vertex) => {
        vertex.getProperty[String]("index") match {
          case Some(i) => {i match {
            case "0" => {
              vertex.setState("contribution",1.toLong)
              vertex.setState("sum",0.toLong)
              vertex.setState("index",1)
            }
            case "1" => {
              vertex.setState("contribution",1.toLong)
              vertex.setState("sum",2.toLong)
              vertex.setState("index",1)
            }
            case "2" => {
              vertex.setState("index",0)
              vertex.setState("contribution",0.toLong)
              vertex.messageOutNeighbours(SendMeParent(vertex.ID(),2))
            }
            case _ => {
              vertex.setState("index",0)
              vertex.setState("contribution",0.toLong)
            }
          }}
          case None => println(s"STEP ERROR ${vertex.ID()}")
        }
      }}
      .iterate({(vertex) =>
        vertex.messageQueue[Message[vertex.IDType]].foreach(_ match {
            case SendMeParent(id, index) => {
              val contribution = vertex.getState[Long]("contribution")
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
            val totalSum: Long = 2*(value+index.toLong)
//            println(s"Index: ${index},total sum: ${totalSum}")
            vertex.setState("sum",totalSum)
            vertex.setState("contribution",value+index.toLong)
            vertex.messageInNeighbours(UpdateDistance(1,index.toInt))
          }
          case UpdateDistance(distance, receivedIndex) => {
            val myIndex: String = vertex.getProperty[String]("index") match {
              case Some(i) => i
              case None => "ERROR"
            }
            val mySituation = vertex.getState[Int]("index")
            if (receivedIndex > myIndex.toInt && receivedIndex > mySituation) {
              val newContribution: Long = (vertex.getState[Long]("contribution")
                + distance.toLong)
//              println(s"Index: ${myIndex}, contrib: ${newContribution}")
              vertex.setState("index",receivedIndex)
              vertex.setState("contribution",newContribution)
              vertex.messageAllNeighbours(
                UpdateDistance(distance+1,receivedIndex))
            } else if (myIndex.toInt - 1 == receivedIndex) {
              vertex.messageOutNeighbours(SendMeParent(vertex.ID(),myIndex.toInt))
            }
          }
          case _ => println(s"ITERATE ERROR: ${vertex.ID()}")
        })
      }, iterations = 100000, executeMessagedOnly = true)
  }
  override def tabularise(graph: GraphPerspective): Table = {
    graph
      .select(vertex=> {
        Row(vertex.ID(),
          vertex.getPropertyOrElse("cascade","null"),
          vertex.getPropertyOrElse("index","null"),
          vertex.getStateOrElse("contribution", 0),
          vertex.getStateOrElse("sum", 0))
      })
  }
  sealed trait Message[VertexId] {}
    case class SendMeParent[VertexId](vertexId: VertexId, index: Int) extends Message[VertexId]
    case class MyContribution[VertexId](contribution: Long) extends Message[VertexId]
    case class UpdateDistance[VertexId](newDistance: Int, index: Int) extends Message[VertexId]
}

object EfficientVirality {
  def apply() = new EfficientVirality()
}