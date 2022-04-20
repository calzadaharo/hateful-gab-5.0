import Algorithms.{EfficientVirality, GraphDepthIndex, Virality,OrderByCascade}
import HGGraphBuilders.{FirstAnalysisGB, ViralityAnalysisGB}
//import com.raphtory.core.deploy.Raphtory
//import com.raphtory.core.components.spout.instance.ResourceSpout
//import com.raphtory.output.FileOutputFormat
import com.raphtory.deployment.Raphtory
import com.raphtory.output.FileOutputFormat
import com.raphtory.spouts.ResourceSpout

object Runner extends App {
  val firstTest = 39316
  val lastTimestampPart0: Long = 3062658
  val lastTimestamp: Long = 46417964
  val maxIndex = 2692

  val source = ResourceSpout("correct-pre-virality.csv")
  val builder = new ViralityAnalysisGB()
  val graph = Raphtory.streamGraph(spout = source, graphBuilder = builder)
  val output = FileOutputFormat("/home/rodrigo/output-5.0")
  val outputServer = FileOutputFormat("/home/rcalzada/output-5.0")
  val queryHandler = graph.pointQuery(EfficientVirality(), outputServer, maxIndex)
}