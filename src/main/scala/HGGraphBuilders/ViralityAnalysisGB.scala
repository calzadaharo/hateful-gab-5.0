package HGGraphBuilders

import com.raphtory.core.components.graphbuilder.{GraphBuilder, Properties, ImmutableProperty}
//import com.raphtory.core.model.graph.{ImmutableProperty, Properties, Type}

class ViralityAnalysisGB extends GraphBuilder[String]{
  override def parseTuple(tuple: String): Unit = {
    val dataLine = tuple.split(",").map(_.trim)

    val vertex = dataLine(1).toLong
    val parent = dataLine(2)
    val cascade = dataLine(0)
    //    val timestamp = dataLine(3)
    val index = dataLine(4).toLong
    val level = dataLine(5)
    val hateful = dataLine(6)
//    val user = dataLine(7)

      addVertex(index,vertex,Properties(
        ImmutableProperty("index",index.toString),
        ImmutableProperty("cascade",cascade),
        ImmutableProperty("level",level),
        ImmutableProperty("hateful", hateful),
      ))
      if (level != "0") {addEdge(index, vertex, parent.toLong)}
  }
}
