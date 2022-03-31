//package Algorithms
//
//import com.raphtory.core.algorithm.GraphAlgorithm
//import com.raphtory.core.algorithm.GraphPerspective
//import com.raphtory.core.algorithm.GraphState
//import com.raphtory.core.algorithm.GraphStateImplementation
//import com.raphtory.core.algorithm.Row
//import com.raphtory.core.algorithm.Table
//import com.raphtory.core.graph.visitor.Vertex
//
//class CountNodes extends GraphAlgorithm {
//
//  override def apply(graph: GraphPerspective): GraphPerspective =
//    graph
//      .setGlobalState { globalState: GraphState =>
//        globalState.newAdder[Int]("nodeCount")
//      }
//      .step { (vertex: Vertex, globalState: GraphState) =>
//        globalState("nodeCount") += 1
//      }
//
//  override def tabularise(graph: GraphPerspective): Table =
//    graph
//      .globalSelect(graphState => Row(graphState("nodeCount").value))
//}
//
//object CountNodes {
//  def apply() = new CountNodes
//}
//
//class CountNodesTwice extends GraphAlgorithm {
//
//  override def apply(graph: GraphPerspective): GraphPerspective =
//    graph
//      .setGlobalState { globalState: GraphState =>
//        globalState.newAdder[Int]("nodeCount")
//        globalState.newAdder[Int]("nodeCountDoubled", retainState = true)
//      }
//      .step { (vertex: Vertex, globalState: GraphState) =>
//        globalState("nodeCount") += 1
//        globalState("nodeCountDoubled") += 1
//      }
//      .step { (vertex: Vertex, globalState: GraphState) =>
//        globalState("nodeCount") += 1
//        globalState("nodeCountDoubled") += 1
//      }
//
//  override def tabularise(graph: GraphPerspective): Table =
//    graph
//      .globalSelect { graphState: GraphState =>
//        Row(graphState("nodeCount").value, graphState("nodeCountDoubled").value)
//      }
//
//}
//
//object CountNodesTwice {
//  def apply() = new CountNodesTwice
//}
//
