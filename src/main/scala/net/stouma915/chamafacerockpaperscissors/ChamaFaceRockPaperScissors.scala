package net.stouma915.chamafacerockpaperscissors

import akka.actor.{ActorSystem, Props}
import com.jagrosh.jdautilities.command.CommandClientBuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.{AccountType, JDA, JDABuilder, OnlineStatus}
import net.stouma915.chamafacerockpaperscissors.channelsetting.ChannelSetting
import net.stouma915.chamafacerockpaperscissors.configuration.Configuration
import net.stouma915.chamafacerockpaperscissors.timer.{Timer, TimerActor}

import scala.concurrent.duration._
import scala.language.postfixOps

object ChamaFaceRockPaperScissors {
  var jda: JDA = _

  def main(args: Array[String]): Unit = {
    if (!Configuration.saveDefaultConfig) {
      println("config.ymlのコピーに失敗しました。起動処理を中止します。")
      sys.exit(1)
      return
    }
    if (!ChannelSetting.saveDefaultSetting) {
      println("channel.ymlのコピーに失敗しました。起動処理を中止します。")
      sys.exit(1)
      return
    }
    import actorSystem.dispatcher

    val actorSystem = ActorSystem("TestActor")
    val timerActor = actorSystem.actorOf(Props(new TimerActor))
    actorSystem.scheduler.scheduleAtFixedRate(0 seconds,
                                              1 seconds,
                                              timerActor,
                                              Timer)
    import net.stouma915.chamafacerockpaperscissors.command._
    val commands = Seq(
      new SetChannelCommand
    )
    val commandClient = new CommandClientBuilder()
      .setPrefix("/")
      .setOwnerId("566817854616240128")
      .addCommands(commands: _*)
      .setStatus(OnlineStatus.ONLINE)
      .useHelpBuilder(false)
      .setActivity(Activity.playing("ちゃま顔じゃんけん"))
      .build()
    val token = Configuration.getToken match {
      case Some(string) => string
      case None =>
        println("Tokenが設定されていません。")
        sys.exit(1)
        return
    }
    try jda = new JDABuilder(AccountType.BOT)
      .setToken(token)
      .addEventListeners(commandClient)
      .build()
    catch {
      case e: Exception =>
        e.printStackTrace()
        println("Botの起動に失敗しました。")
        sys.exit(1)
    }
  }
}
