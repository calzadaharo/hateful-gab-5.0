package Algorithms

import com.raphtory.core.algorithm.GraphPerspective

import com.raphtory.core.algorithm.{GraphAlgorithm,Row,Table,
  GraphStateImplementation,GraphState}
import com.raphtory.core.graph.visitor.Vertex

class EfficientVirality  extends GraphAlgorithm {
  override def apply(graph: GraphPerspective): GraphPerspective = {
    graph
      .setGlobalState({ globalState: GraphState =>
        println("SET GLOBAL STATE")
        globalState.newAdder[Int]("generalIndex", retainState = true)
      })
      .step{(vertex: Vertex ,globalState: GraphState) => {
        println("STEP")
        vertex.getProperty[String]("index") match {
          case Some(i) => {i match {
            case "0" => {
              vertex.setState("contribution",1)
              vertex.setState("sum",0)
              vertex.setState("index",1)
            }
            case "1" => {
              vertex.setState("contribution",1)
              vertex.setState("sum",2)
              vertex.setState("index",1)
            }
            case "2" => {
//              vertex.setState("index",vertex.getProperty[Int]("index"))
              vertex.messageOutNeighbours(vertex.ID().toString)
            }
            case _ => {
//              vertex.setState("index",vertex.getProperty[Int]("index"))
            }
          }}
          case None => logger.error("ERROR")
        }
      }}
      .iterate({(vertex:Vertex, globalState: GraphState) =>
        println("ITERATE")
        val messages = vertex.messageQueue[(Any)]
        println("Message: " + messages)
        messages.foreach(_ match {
          case m: String => println(s"STRING: $m")
          case m: Long => println(s"LONG: $m")
          case m: Int => println(s"INT: $m")
          case _ => println("TAKE A LOOK!")
        })
      }, iterations = 100000, executeMessagedOnly = true)
  }
  override def tabularise(graph: GraphPerspective): Table = {
    graph
      .select(vertex=> {
        println("TABULARISE")
        Row(vertex.ID(),
          vertex.getStateOrElse("contribution", 0))
      })
  }
}
object EfficientVirality {
  def apply() = new EfficientVirality()
}

