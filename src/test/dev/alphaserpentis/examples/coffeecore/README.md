# Examples

## Hello World (hello/HelloWorld)
This program implements `BotCommand<MessageEmbed>`, `BotCommand<String>`, and `ButtonCommand<MessageEmbed>` showing
the basic functionality of Coffee Core.

By default, it also adds four of the default commands:
- `help` - Shows a list of commands
- `about` - Shows information about the bot
- `settings` - Configure the bot for the server
- `shutdown` - Shuts down the bot
## Custom Server Data (custom/CustomServerDataExample)
This program includes a custom class - `CustomServerData` - to extend `ServerData` and another to extend `ServerDataHandler<T>`
with a type of `CustomServerData`.

It highlights how to use Coffee Core's builder for a custom `ServerDataHandler`
