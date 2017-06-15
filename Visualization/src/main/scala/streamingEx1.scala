import java.io.File
import java.nio.charset.Charset
import com.google.common.io.Files
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Seconds, StreamingContext, Time}
import org.apache.spark.util.IntParam
import org.apache.spark.util.LongAccumulator
import org.apache.spark.storage.StorageLevel


object RecoverableNetworkWordCount {
  def createContext(ip: String, port: Int, checkpointDirectory: String)
    : StreamingContext = {
    println("Creating new context")

    val sparkConf = new SparkConf().setAppName("RecoverableNetworkWordCount").set("spark.storage.memoryFraction", "0.8")
    val ssc = new StreamingContext(sparkConf, Seconds(1))
    ssc.checkpoint(checkpointDirectory)

    ssc
  }

  def main(args: Array[String]) {
    if (args.length != 3) {
      System.err.println("Your arguments were " + args.mkString("[", ", ", "]"))
      System.err.println(
        """
          |Usage: RecoverableNetworkWordCount <hostname> <port> <checkpoint-directory>
          |     <output-file>. <hostname> and <port> describe the TCP server that Spark
          |     Streaming would connect to receive data. <checkpoint-directory> directory to
          |     HDFS-compatible file system which checkpoint data <output-file> file to which the
          |     word counts will be appended
          |
          |In local mode, <master> should be 'local[n]' with n > 1
          |Both <checkpoint-directory> and <output-file> must be absolute paths
        """.stripMargin
      )
      System.exit(1)
    }
    val Array(ip, port, checkpointDirectory) = args

    val typeFile = new File("Type")
    val subTypeFile = new File("SubType")
    val statusFile = new File("Status")
    val serviceFile = new File("Service")
    val protoFile = new File("Proto")
    val priFile = new File("Pri")
    val log_IdFile = new File("Log_Id")
    val dangerFile = new File("danger")
    if (typeFile.exists()) typeFile.delete()
    if (subTypeFile.exists()) subTypeFile.delete()
    if (statusFile.exists()) statusFile.delete()
    if (serviceFile.exists()) serviceFile.delete()
    if (protoFile.exists()) protoFile.delete()
    if (priFile.exists()) priFile.delete()
    if (log_IdFile.exists()) log_IdFile.delete()
    if (dangerFile.exists()) dangerFile.delete()


    val ssc = StreamingContext.getOrCreate(checkpointDirectory,
      () => createContext(ip, port.toInt, checkpointDirectory))
    val logAll = ssc.sparkContext.parallelize(List(("proto","0"),("proto","6"),("proto","17"),("service","DNS"),("service","HTTP"),("service","HTTPS"),("service","IRC"),("service","UDP"),("type","event"),("type","traffic"),("type","virus"),("type","webfilter"),("subtype","other"),("subtype","allowed"),("pri","notice"),("pri","warning"),("pri","info"),("pri","critical"),("pri","debug"),("pri","error"),("log_id","4"),("log_id","2"),("log_id","7"),("status","accept"),("status","start"),("status","deny"))).map(x=>((x._1,x._2),0))
/*
val logAll = sc.parallelize(List(("proto","0"),("proto","6"),("proto","17"),("service","DNS"),("service","HTTP"),("service","HTTPS"),("service","IRC"),("service","UDP"),("type","event"),("type","traffic"),("type","virus"),("type","webfilter"),("subtype","other"),("subtype","allowed"),("pri","notice"),("pri","warning"),("pri","info"),("pri","critical"),("pri","debug"),("pri","error"),("log_id","4"),("log_id","2"),("log_id","7"),("status","accept"),("status","start"),("status","deny"))).map(x=>((x._1,x._2),0))
*/
    logAll.persist(StorageLevel.MEMORY_AND_DISK_SER)

    val logs = ssc.socketTextStream(ip, port.toInt, StorageLevel.MEMORY_AND_DISK_SER)
    val log = logs.flatMap(x=>x.split(" ")).filter(x=>x.contains("=")).map(str=>str.replace("\"","").replace(",","")).map(x=>(x.split("=")(0),x.split("=")(1)))
    log.persist(StorageLevel.MEMORY_ONLY)
    val logIdConvert = log.filter(x=>x._1.contains("log_id")).map(x=>(x._1,(x._2.toInt%100).toString))
    val logOtherConvert = log.filter{case (x,y)=>x=="type"||x=="subtype"||x=="pri"||x=="status"||x=="service"||x=="proto"}
    val unionLog = logIdConvert.union(logOtherConvert)
    val log1 = unionLog.map(x=>(x,1)).reduceByKey((x,y)=>x+y)
    log1.foreachRDD { (rdd: RDD[((String,String),Int)], time: Time) =>
      val result = logAll.leftOuterJoin(rdd).map(x=>(x._1._1,(x._1._2,x._2._2))).sortByKey(false)
      val resultToken = result.map(x=>(x._2._2.getOrElse(0).toString)).collect()
      val dangerStr = result.filter{case (x,y)=>y._1=="virus"||y._1=="deny"||y._1=="critical"||y._1=="error"||y._1=="warning"}.map(x=>(x._2._2.getOrElse(0).toString)).collect().mkString(",")
      val typeStr = resultToken.slice(0,4).mkString(",")
      val subTypeStr = resultToken.slice(4,6).mkString(",")
      val statusStr = resultToken.slice(6,9).mkString(",")
      val serviceStr = resultToken.slice(9,14).mkString(",")
      val protoStr = resultToken.slice(14,17).mkString(",")
      val priStr = resultToken.slice(17,23).mkString(",")
      val log_IdStr = resultToken.slice(23,26).mkString(",")

      val output = resultToken.mkString(",")
      println(output+"/"+dangerStr)
      Files.append(typeStr + "\n", typeFile, Charset.defaultCharset())
      Files.append(subTypeStr + "\n", subTypeFile, Charset.defaultCharset())
      Files.append(statusStr + "\n", statusFile, Charset.defaultCharset())
      Files.append(serviceStr + "\n", serviceFile, Charset.defaultCharset())
      Files.append(protoStr + "\n", protoFile, Charset.defaultCharset())
      Files.append(priStr + "\n", priFile, Charset.defaultCharset())
      Files.append(log_IdStr + "\n", log_IdFile, Charset.defaultCharset())
      Files.append(dangerStr + "\n", dangerFile, Charset.defaultCharset())
    }
    logs.foreachRDD { rdd =>
      rdd.unpersist()
    }

    ssc.start()
    ssc.awaitTermination()
    logAll.unpersist()
  }
}

//sudo $SPARK_HOME/bin/spark-submit --master local[4] --class RecoverableNetworkWordCount target/scala-2.11/simple-project_2.11-1.0.jar localhost 7777 /home/comso/sparkEx/streamingEx1/check
