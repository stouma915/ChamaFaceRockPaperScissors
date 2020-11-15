package net.stouma915.chamafacerockpaperscissors.command

import com.jagrosh.jdautilities.command.{Command, CommandEvent}
import net.stouma915.chamafacerockpaperscissors.channelsetting.ChannelSetting

class SetChannelCommand extends Command {
  this.name = "setchannel"

  override def execute(event: CommandEvent): Unit = {
    ChannelSetting.set(event.getGuild.getIdLong, event.getChannel.getIdLong)
    event.getChannel.sendMessage("このチャンネルをちゃま顔じゃんけんのチャンネルに設定しました。").queue()
  }
}
