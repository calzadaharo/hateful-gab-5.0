package HGGraphBuilders

import com.raphtory.components.graphbuilder.GraphBuilder
import com.raphtory.components.graphbuilder.Properties.ImmutableProperty
import com.raphtory.components.graphbuilder.Properties.Properties
import com.raphtory.components.graphbuilder.Properties.Type

class NoRepostGB extends GraphBuilder[String]{
  override def parseTuple(tuple: String): Unit = {
    val dataLine = tuple.split(",").map(_.trim)

    val vertex = dataLine(0).toLong
    val timestamp = dataLine(1)
    val user = dataLine(2)
    val hateful = dataLine(5)
    val parent = dataLine(3).toLong

    if (parent == 0) {
      addVertex(timestamp.toLong, vertex,
        Properties(
          ImmutableProperty("timestamp",timestamp),
          ImmutableProperty("user",user),
          ImmutableProperty("hateful",hateful),
          ImmutableProperty("type","original")),
        Type("Post")
      )
    } else {
      addVertex(timestamp.toLong, vertex,
        Properties(
          ImmutableProperty("timestamp",timestamp),
          ImmutableProperty("user",user),
          ImmutableProperty("hateful",hateful),
          ImmutableProperty("type","answer"),
          ImmutableProperty("parent",parent.toString)),
        Type("Post")
      )
      addVertex(timestamp.toLong, parent, Type("Post"))
      addEdge(timestamp.toLong,vertex,parent,Type("Answer"))
    }
  }
}

