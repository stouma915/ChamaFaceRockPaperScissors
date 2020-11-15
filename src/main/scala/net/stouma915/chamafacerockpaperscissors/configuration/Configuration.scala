package net.stouma915.chamafacerockpaperscissors.configuration

import java.io.{File, FileInputStream, FileOutputStream}
import java.util

import net.stouma915.chamafacerockpaperscissors.ChamaFaceRockPaperScissors
import org.yaml.snakeyaml.Yaml

import scala.collection.mutable
import scala.reflect.ClassTag

object Configuration {
  private var configObject = mutable.Map[String, Object]()

  def saveDefaultConfig: Boolean = {
    val configFile = new File(System.getProperty("user.dir"), "config.yml")
    if (!configFile.exists()) {
      try {
        val inputStream = ChamaFaceRockPaperScissors.getClass.getClassLoader
          .getResourceAsStream("config.yml")
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
      configObject = new Yaml()
        .load(new FileInputStream(
          new File(System.getProperty("user.dir"), "config.yml")))
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
        configObject.getOrElse(path.head, None) match {
          case obj: Object => Some(obj.asInstanceOf[T])
          case None        => None
        } else {
        var previousValue = configObject.getOrElse(path.head, None)
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

  def getToken: Option[String] = get[String]("Token")
}
