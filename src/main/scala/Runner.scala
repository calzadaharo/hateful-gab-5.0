import Algorithms.{GraphDepthIndex,Virality}
import HGGraphBuilders.{FirstAnalysisGB,ViralityAnalysisGB}
import com.raphtory.core.components.spout.instance.ResourceSpout
import com.raphtory.core.deploy.Raphtory
import com.raphtory.output.FileOutputFormat

object Runner extends App {
  val firstTest = 39316
  val lastTimestampPart0: Long = 3062658
  val lastTimestamp: Long = 46417964
  val maxIndex = 2698

  val source = ResourceSpout("pre-virality-2-9.csv")
  val builder = new ViralityAnalysisGB()
  val graph = Raphtory.createGraph(spout = source, graphBuilder = builder)
  val output = FileOutputFormat("/home/rodrigo/output-5.0")
  val outputServer = FileOutputFormat("/home/rcalzada/output-5.0")
  val queryHandler = graph.pointQuery(Virality(), outputServer, 9)
  queryHandler.waitForJob()
}
