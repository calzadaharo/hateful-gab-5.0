package Algorithms

import com.raphtory.core.algorithm.{GraphAlgorithm, GraphPerspective, Row, Table}

class Virality extends GraphAlgorithm {
  override def apply(graph: GraphPerspective): GraphPerspective = {
    graph
  }
}

object Virality {
  def apply() = new Virality()
}
