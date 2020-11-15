package net.stouma915.chamafacerockpaperscissors.timer

import java.util.{Calendar, TimeZone}

import akka.actor.Actor
import net.stouma915.chamafacerockpaperscissors.ChamaFaceRockPaperScissors
import net.stouma915.chamafacerockpaperscissors.channelsetting.ChannelSetting

import scala.util.Random

class TimerActor extends Actor {
  override def receive: Receive = {
    case Timer =>
      val calender = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"))
      val hour = calender.get(Calendar.HOUR_OF_DAY)
      val minute = calender.get(Calendar.MINUTE)
      val second = calender.get(Calendar.SECOND)
      if (hour == 23 && minute == 59 && second == 0) {
        ChannelSetting.getAll.foreach {
          case (serverId: Long, channelId: Long) =>
            if (ChamaFaceRockPaperScissors.jda != null) {
              if (ChamaFaceRockPaperScissors.jda.getGuildById(serverId) != null) {
                val server =
                  ChamaFaceRockPaperScissors.jda.getGuildById(serverId)
                if (server.getTextChannelById(channelId) != null) {
                  val channel = server.getTextChannelById(channelId)
                  val imageName = new Random().nextInt(3) match {
                    case 0 => "paper"
                    case 1 => "rock"
                    case 2 => "scissors"
                  }
                  val hand = imageName match {
                    case "paper"    => "パー"
                    case "rock"     => "グー"
                    case "scissors" => "チョキ"
                  }
                  val inputStream =
                    ChamaFaceRockPaperScissors.getClass.getClassLoader
                      .getResourceAsStream(s"${imageName}.png")
                  val win = imageName match {
                    case "paper"    => "チョキ"
                    case "rock"     => "パー"
                    case "scissors" => "グー"
                  }
                  channel
                    .sendFile(inputStream, s"${imageName}.png")
                    .queue()
                  channel
                    .sendMessage(s"僕が${hand}だから、${win}の勝ち！ 明日もまた整地してね！")
                    .queue()
                }
              }
            }
        }
      }
  }
}
