package es.upm.dit

import com.raphtory.algorithms.generic.ConnectedComponents
import com.raphtory.deployment.Raphtory
import com.raphtory.output.FileOutputFormat
import com.raphtory.spouts.{FileSpout, ResourceSpout}
import com.raphtory.util.FileUtils
import es.upm.dit.algorithms.OrderByCascade
import es.upm.dit.graphbuilders.NoRepostGB

object Runner extends App {
  val firstTest = 39316
  val lastTimestampPart0: Long = 3062658
  val lastTimestamp: Long = 46417964
  val maxIndex = 2692
  val maxIndexNoRepost = 1687

  // LOTR test
//  val path = "/tmp/lotr.csv"
//  val url = "https://raw.githubusercontent.com/Raphtory/Data/main/lotr.csv"
//  FileUtils.curlFile(path, url)
//  val source = FileSpout(path)

  val source = ResourceSpout("pre-virality-no-repost-no-classi.csv")
  val builder = new NoRepostGB()
  val graph = Raphtory.batchLoad(spout = source, graphBuilder = builder)
  val output = FileOutputFormat("/home/rodrigo/output-5.0")
  val outputServer = FileOutputFormat("/home/rcalzada/output-5.0")
  val queryHandler = graph
    .execute(OrderByCascade())
    .writeTo(outputServer)

  queryHandler.waitForJob()
  System.exit(0)
}
