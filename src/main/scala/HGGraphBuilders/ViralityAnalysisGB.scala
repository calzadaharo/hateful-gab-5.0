package HGGraphBuilders

import com.raphtory.core.components.graphbuilder.{GraphBuilder, Properties, ImmutableProperty}
//import com.raphtory.core.model.graph.{ImmutableProperty, Properties, Type}

class ViralityAnalysisGB extends GraphBuilder[String]{
  override def parseTuple(tuple: String): Unit = {
    val dataLine = tuple.split(",").map(_.trim)

    val vertex = dataLine(2).toLong
    val parent = dataLine(3)
    val cascade = dataLine(1)
    //    val timestamp = dataLine(4)
    val index = dataLine(5).toLong
    val level = dataLine(6)
    val hateful = dataLine(7)
//    val user = dataLine(8)

      addVertex(index,vertex,Properties(
        ImmutableProperty("index",index.toString),
        ImmutableProperty("cascade",cascade),
        ImmutableProperty("level",level),
        ImmutableProperty("hateful", hateful),
      ))
      if (level != "0") {addEdge(index, vertex, parent.toLong)}
  }
}
