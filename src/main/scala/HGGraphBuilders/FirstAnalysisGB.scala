package HGGraphBuilders

//import com.raphtory.core.components.graphbuilder.{GraphBuilder, Properties, ImmutableProperty, Type}
//import com.raphtory.core.model.graph.{ImmutableProperty, Properties, Type}
import com.raphtory.components.graphbuilder.GraphBuilder
import com.raphtory.components.graphbuilder.Properties.ImmutableProperty
import com.raphtory.components.graphbuilder.Properties.Properties
import com.raphtory.components.graphbuilder.Properties.Type

class FirstAnalysisGB extends GraphBuilder[String]{
  override def parseTuple(tuple: String): Unit = {
    val dataLine = tuple.split(",").map(_.trim)

    val vertex = dataLine(0).toLong
    val timeString = dataLine(1)
    val timestamp = dataLine(1).toLong
    val user = dataLine(2)
    val hateful = dataLine(5)
    val parent = dataLine(3).toLong
    val initial = dataLine(4).toLong

    if (parent == 0 && initial == 0) {
      addVertex(timestamp, vertex,
        Properties(
          ImmutableProperty("timestamp",timeString),
          ImmutableProperty("user",user),
          ImmutableProperty("hateful",hateful),
          ImmutableProperty("type","original")),
        Type("Post")
      )
    } else if (parent != 0) {
      addVertex(timestamp, vertex,
        Properties(
          ImmutableProperty("timestamp",timeString),
          ImmutableProperty("user",user),
          ImmutableProperty("hateful",hateful),
          ImmutableProperty("type","answer"),
          ImmutableProperty("parent",parent.toString)),
        Type("Post")
      )
      addVertex(timestamp, parent, Type("Post"))
      addEdge(timestamp,vertex,parent,Type("Answer"))
    } else {
      addVertex(timestamp, vertex,
        Properties(
          ImmutableProperty("timestamp",timeString),
          ImmutableProperty("user",user),
          ImmutableProperty("hateful",hateful),
          ImmutableProperty("type","initial"),
          ImmutableProperty("parent",initial.toString)),
        Type("Post")
      )
      addVertex(timestamp, initial, Type("Post"))
      addEdge(timestamp,vertex, initial, Type("Answer"))
    }
  }
}
