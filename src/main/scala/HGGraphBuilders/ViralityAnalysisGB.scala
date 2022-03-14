package HGGraphBuilders

import com.raphtory.core.components.graphbuilder.GraphBuilder
import com.raphtory.core.components.graphbuilder.{ImmutableProperty, Properties, Type}

class ViralityAnalysisGB extends GraphBuilder[String]{
  override def parseTuple(tuple: String): Unit = {
    val dataLine = tuple.split(",").map(_.trim)

    val vertex = dataLine(1).toLong
    val parent = dataLine(1).toLong
    val timestamp = dataLine(1)
    val index = dataLine(1).toLong
    val level = dataLine(1)
    val hateful = dataLine(1)
    val user = dataLine(1)

    addVertex(index,vertex,Properties(
      ImmutableProperty("level",level),
      ImmutableProperty("hateful", hateful),
      ImmutableProperty("user", user)
    ))
    if (level != "0") addEdge(index, vertex, parent)
  }
}
