package net.stouma915.chamafacerockpaperscissors.channelsetting

import java.io.{File, FileInputStream, FileOutputStream, FileWriter}
import java.util

import net.stouma915.chamafacerockpaperscissors.ChamaFaceRockPaperScissors
import org.yaml.snakeyaml.Yaml

import scala.collection.mutable
import scala.reflect.ClassTag

object ChannelSetting {
  private var settingObject = mutable.Map[String, Object]()

  def saveDefaultSetting: Boolean = {
    val configFile = new File(System.getProperty("user.dir"), "channel.yml")
    if (!configFile.exists()) {
      try {
        val inputStream = ChamaFaceRockPaperScissors.getClass.getClassLoader
          .getResourceAsStream("channel.yml")
        val outputStream = new FileOutputStream(configFile)
        var read = 0
        val bytes = new Array[Byte](1024)
        while ({
          read = inputStream.read(bytes)
          read
        } != -1) outputStream.write(bytes, 0, read)
        inputStream.close()
        outputStream.close()
        return true
      } catch {
        case e: Exception =>
          e.printStackTrace()
          return false
      }
    }
    true
  }

  private def load: Boolean =
    try {
      import scala.jdk.CollectionConverters._
      settingObject = new Yaml()
        .load(new FileInputStream(
          new File(System.getProperty("user.dir"), "channel.yml")))
        .asInstanceOf[util.LinkedHashMap[String, Object]]
        .asScala
      true
    } catch {
      case e: Exception =>
        e.printStackTrace()
        false
    }

  private def get[T: ClassTag](path: String*): Option[T] = {
    if (!load) return None
    if (path.isEmpty) return None
    try {
      if (path.length == 1)
        settingObject.getOrElse(path.head, None) match {
          case obj: Object => Some(obj.asInstanceOf[T])
          case None        => None
        } else {
        var previousValue = settingObject.getOrElse(path.head, None)
        import scala.jdk.CollectionConverters._
        path
          .drop(1)
          .dropRight(1)
          .foreach(
            p =>
              previousValue = previousValue
                .asInstanceOf[util.LinkedHashMap[String, Object]]
                .asScala
                .getOrElse(p, None))
        previousValue
          .asInstanceOf[util.LinkedHashMap[String, Object]]
          .asScala
          .getOrElse(path.last, None) match {
          case obj: Object => Some(obj.asInstanceOf[T])
          case None        => None
        }
      }
    } catch {
      case _: NullPointerException => None
      case _: ClassCastException   => None
    }
  }

  def set(serverId: Long, channelId: Long): Unit = {
    if (!load) return
    get[Long](serverId.toString) match {
      case Some(_) =>
        settingObject.remove(serverId.toString)
        settingObject += serverId.toString -> channelId.asInstanceOf[Object]
      case None =>
        settingObject += serverId.toString -> channelId.asInstanceOf[Object]
    }
    import scala.jdk.CollectionConverters._
    new Yaml().dump(
      settingObject.asJava,
      new FileWriter(new File(System.getProperty("user.dir"), "channel.yml")))
  }

  def get(serverId: Long): Option[Long] = get[Long](serverId.toString)

  def getAll: Seq[(Long, Long)] = {
    if (!load) return Seq()
    var channels = Seq[(Long, Long)]()
    settingObject.foreach {
      case (key: String, value: Object) =>
        channels = channels.appended((key.toLong, value.toString.toLong))
    }
    channels
  }

}
