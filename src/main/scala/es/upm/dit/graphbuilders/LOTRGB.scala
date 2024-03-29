package es.upm.dit.graphbuilders

import com.raphtory.components.graphbuilder.GraphBuilder
import com.raphtory.components.graphbuilder.Properties.{ImmutableProperty, Properties, Type}

class LOTRGB extends GraphBuilder[String] {

  override def parseTuple(tuple: String): Unit = {
    //    val line = new String(tuple,"UTF-8")
    val fileLine   = tuple.split(",").map(_.trim)
    val sourceNode = fileLine(0)
    val srcID      = assignID(sourceNode)
    val targetNode = fileLine(1)
    val tarID      = assignID(targetNode)
    val timeStamp  = fileLine(2).toLong

    addVertex(
      timeStamp,
      srcID,
      Properties(ImmutableProperty("name", sourceNode)),
      Type("Character")
    )
    addVertex(
      timeStamp,
      tarID,
      Properties(ImmutableProperty("name", targetNode)),
      Type("Character")
    )
    addEdge(timeStamp, srcID, tarID, Type("Character Co-occurence"))
  }

}