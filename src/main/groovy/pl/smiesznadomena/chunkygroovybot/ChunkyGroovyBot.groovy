package pl.smiesznadomena.chunkygroovybot

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent

import javax.annotation.Nonnull
import java.awt.Color

final token = System.getenv('DISCORD_TOKEN')
final renderChannel = System.getenv('DISCORD_RENDER_CHANNEL_ID') as Long
final informationEmbed = new EmbedBuilder().with {
    // Last time I checked I had to use direct calls to setters for some reason
    setTitle 'Error'
    setDescription 'You have to provide a direct link to your render or upload it directly as an attachment'
    setColor Color.RED

    build()
}

final urlSuffixes = ['jpg', 'jpeg', 'png', 'tif', 'tiff', 'webp'] as String[]

if (token == null || token.empty || renderChannel == null || renderChannel == 0) {
    println "Please provide valid environment!\nDISCORD_TOKEN = $token\nDISCORD_RENDER_CHANNEL_ID = $renderChannel\n"
    return
}

new JDABuilder().create(
        token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES
).build().addEventListener(new ListenerAdapter() {
    @Override
    void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (event.channel.idLong != renderChannel || event.webhookMessage || event.author.bot)
            return

        if (
            event.message.attachments
                .find { it.isImage() } == null
            &&
            // if the user didn't upload the render directly we'll check if there is anything that looks like
            // image url in the message
            event.message.contentRaw.split(' ')
                .find {
                    final str = it.toLowerCase()
                    str.startsWithAny('https://', 'http://') && str.split('\\?', 2)[0]?.endsWithAny(urlSuffixes)
                } == null
        ) {
            event.author.openPrivateChannel().queue {
                it.sendMessage(informationEmbed).queue()
                it.sendMessage(event.message.contentRaw).queue()
                event.message.attachments.each { att ->
                    it.sendMessage(att.url).queue()
                }
                // Delete the message __after__ we've copied its contents for the user
                event.message.delete().queue()
            }
        }
    }
})
