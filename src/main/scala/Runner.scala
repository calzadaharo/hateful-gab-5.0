import Algorithms.{EfficientVirality, GraphDepthIndex, Virality,OrderByCascade}
import HGGraphBuilders.{FirstAnalysisGB, ViralityAnalysisGB, NoRepostGB}
import com.raphtory.deployment.Raphtory
import com.raphtory.output.FileOutputFormat
import com.raphtory.spouts.ResourceSpout

object Runner extends App {
  val firstTest = 39316
  val lastTimestampPart0: Long = 3062658
  val lastTimestamp: Long = 46417964
  val maxIndex = 2692
  val maxIndexNoRepost = 1687


  val source = ResourceSpout("no-repost-preVirality.csv")
  val builder = new NoRepostGB()
  val graph = Raphtory.batchLoad(spout = source, graphBuilder = builder)
  val output = FileOutputFormat("/home/rodrigo/output-5.0")
  val outputServer = FileOutputFormat("/home/rcalzada/output-5.0")
  val queryHandler = graph
    .at(maxIndexNoRepost)
    .past()
    .execute(EfficientVirality())
    .writeTo(outputServer)
}