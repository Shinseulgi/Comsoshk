import java.io.File
import java.nio.charset.Charset
import com.google.common.io.Files
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.Accumulator
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Milliseconds, Seconds, Minutes, StreamingContext, Time, State, StateSpec}
import org.apache.spark.util.IntParam
import org.apache.spark.storage.StorageLevel

object RecoverableNetworkWordCount {
  val protoMap = Map(
	"80+9999"->"MS FrontPage Server Extension Buffer Overflow",
	"135+9191"->"MS Messenger Heap Overflow",
	"445+4444"->"LSASS.DLL RPC Buffer Overflow",
	"389+31337"->"IPswitch IMAIL LDAP",
	"23+2001"->"Splaris /bin/login Remote Root Exploit",
	"21+19800"->"WFTPD STAT Command Remote Exploit",
	"135+7175"->"Windows XP/2000 Return into Libc"
  )

  def trackStateFunc(batchTime: Time, key: String, value: Option[String], state: State[String]): Option[(String, String)] = {
    var sum : String = new String()
    if(state.getOption.getOrElse(0)==0){
      sum = value.getOrElse("")
    }
    else if(state.get().contains(value.get)){
      sum = state.getOption.getOrElse("")
    }
    else{ 
      sum = state.getOption.getOrElse("") + "+" + value.getOrElse("")
    }
    val output = (key, sum)
    state.update(sum)
    Some(output)
  }

  def createContext(ip: String, port: Int, checkpointDirectory: String)
    : StreamingContext = {
    println("Creating new context")

    val sparkConf = new SparkConf().setAppName("RecoverableNetworkWordCount").set("spark.storage.memoryFraction", "0.8")
    val ssc = new StreamingContext(sparkConf, Milliseconds(500))
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

    val resultFile1 = new File("result1.txt")
    val resultFile2 = new File("result2.txt")
    val resultFile3 = new File("result3.txt")
    if (resultFile1.exists()) resultFile1.delete()
    if (resultFile2.exists()) resultFile2.delete()
    if (resultFile3.exists()) resultFile3.delete()

    val ssc = StreamingContext.getOrCreate(checkpointDirectory,
      () => createContext(ip, port.toInt, checkpointDirectory))

    val initialRDD : RDD[(String,String)] = ssc.sparkContext.parallelize(Seq())
    initialRDD.persist(StorageLevel.MEMORY_AND_DISK_SER)

    val stateSpec = StateSpec.function(trackStateFunc _).initialState(initialRDD).numPartitions(2).timeout(Minutes(120))

    val logs = ssc.socketTextStream(ip, port.toInt, StorageLevel.MEMORY_AND_DISK_SER_2)
    val initial_logs = logs.filter{case x => !x.contains("icmp")}
    initial_logs.persist(StorageLevel.MEMORY_ONLY)


    val windowLen_12 = 20
    val slidingInterval_12 = 20
    val window_12 = initial_logs.window(Seconds(windowLen_12),Seconds(slidingInterval_12))
    val windowLen_3 = 1000
    val slidingInterval_3 = 500
    val window_3 = initial_logs.window(Milliseconds(windowLen_3),Milliseconds(slidingInterval_3))

    val win_1 = window_12.map(x=>(x.split("=")(10).split(" ")(0)+"/"+x.split("=")(24).split(" ")(0),x.split("=")(13).split(" ")(0))).transform(rdd=>rdd.distinct()).reduceByKey((x,y) => x+"+"+y)
    val wordCountStateStream1 = win_1.mapWithState(stateSpec)
    val stateSnapshotStream1 = wordCountStateStream1.stateSnapshots()  
    stateSnapshotStream1.foreachRDD { rdd =>
      Files.write("" , resultFile1, Charset.defaultCharset())
      rdd.collect().foreach{ x=>
        Files.append(x + "\n", resultFile1, Charset.defaultCharset())
      }
    }

    val win_2 = window_12.map{rdd => 
      if(rdd.contains("/"))
        (rdd.split("=")(10).split(" ")(0)+"/"+rdd.split("=")(13).split(" ")(0),rdd.split("/")(0).split("=").last+"/"+rdd.split("/")(1).split(" ")(0))
      else
        (rdd.split("=")(10).split(" ")(0)+"/"+rdd.split("=")(13).split(" ")(0),rdd.split("proto")(1).split("=")(1).split(" ")(0)+"/"+rdd.split("proto")(0).split("=").last.replace(" ",""))
     }.transform(x=>x.distinct()).reduceByKey((x,y)=>x+"+"+y)
    val wordCountStateStream2 = win_2.mapWithState(stateSpec)
    val stateSnapshotStream2 = wordCountStateStream2.stateSnapshots()  
    stateSnapshotStream2.foreachRDD { rdd =>
      Files.write("" , resultFile2, Charset.defaultCharset())
      rdd.collect().foreach{ x=>
        Files.append(x + "\n", resultFile2, Charset.defaultCharset())
      }
    }
    
    val win_3 = window_3.filter{case rdd => rdd.contains("/")}.map(rdd=>rdd.split("/")(0).split("=").last).reduce((proto1,proto2)=>proto1+"+"+proto2)
    win_3.foreachRDD { rdd =>
      rdd.collect().foreach{x=>
        val hackingPattern = protoMap.get(x).getOrElse("")
        println(hackingPattern)
        if(hackingPattern!="")
          Files.append(x + "\n", resultFile3, Charset.defaultCharset())
      }
    }

    ssc.start()
    ssc.awaitTermination()
    initial_logs.foreachRDD { rdd =>
      rdd.unpersist()
    }
    initialRDD.unpersist()
  }
}

//sudo $SPARK_HOME/bin/spark-submit --master local[4] --class RecoverableNetworkWordCount target/scala-2.11/simple-project_2.11-1.0.jar localhost 7777 /home/comso/sparkEx/streamingEx1/check
