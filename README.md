[maven-central]: https://img.shields.io/maven-central/v/dev.alphaserpentis/CoffeeCore?color=blue

# Coffee Core

A Discord bot framework built on top of [JDA](https://github.com/DV8FromTheWorld/JDA) designed to be easy to use. The
framework is designed to be modular, allowing you to only use the features you need and to focus on your bot's
functionality.

## Features

- Primarily uses slash commands, but can also be interacted with through:
  - Buttons
  - Modal dialogs
- Modular design if needed
- Default implementation for handling server data, slash commands, buttons, and dialogs
- Sharding support!

## Getting Started

### Adding Coffee Core to your project with Maven

Latest Release:

```xml
<dependency>
    <groupId>dev.alphaserpentis</groupId>
    <artifactId>CoffeeCore</artifactId>
    <version>0.2.0-alpha.1</version>
</dependency>
```

Latest Snapshot:

```xml
<dependency>
    <groupId>dev.alphaserpentis</groupId>
    <artifactId>CoffeeCore</artifactId>
    <version>0.3.0-alpha-SNAPSHOT</version>
</dependency>
```

### Creating a bot

**Note**: By default, Coffee Core will enable two privileged intents: `GUILD_MEMBERS`. Additionally,
when Coffee Core is being created, it uses JDA's default configuration via [`JDABuilder.createDefault`](https://ci.dv8tion.net/job/JDA5/javadoc/net/dv8tion/jda/api/JDABuilder.html#createDefault(java.lang.String)) 
then applies the provided settings. In addition, the Coffee Core builder has its own default settings to configure JDA.

The default settings are as follows:

```java
public class CoffeeCoreBuilder {
    // Some properties omitted...
    protected ChunkingFilter chunkingFilter = ChunkingFilter.ALL;
    protected Collection<CacheFlag> disabledCacheFlags = new ArrayList<>() {
            {
            add(CacheFlag.MEMBER_OVERRIDES);
            add(CacheFlag.VOICE_STATE);
            }
            };
    protected Collection<GatewayIntent> enabledGatewayIntents = new ArrayList<>() {
            {
            add(GatewayIntent.GUILD_MEMBERS);
            }
            };
    protected JDABuilderConfiguration jdaBuilderConfiguration = JDABuilderConfiguration.DEFAULT;
    protected MemberCachePolicy memberCachePolicy = MemberCachePolicy.NONE;
    
    // Methods omitted...
}
```

1. Create a new `.env` file in the root of your project and fill it out with the following (adjust to your liking):

```env
DISCORD_BOT_TOKEN=YOUR_DISCORD_BOT_TOKEN
BOT_OWNER_ID=YOUR_DISCORD_USER_ID
SERVER_DATA_PATH=PATH_TO_SERVER_DATA_FOLDER
UPDATE_COMMANDS_AT_LAUNCH=true
REGISTER_DEFAULT_COMMANDS=true
```

2. Create a new CoffeeCore instance using CoffeeCoreBuilder and load in your bot settings from the `.env` file:

```java
public static void main(String[] args) throws Exception {
        Dotenv dotenv = Dotenv.load(); // Load a local .env file
        CoffeeCoreBuilder builder = new CoffeeCoreBuilder();
        CoffeeCore core;

        // Load in the settings from the .env file
        builder
            .setSettings(
                new BotSettings(
                    Long.parseLong(dotenv.get("BOT_OWNER_ID")),
                    dotenv.get("SERVER_DATA_PATH"),
                    Boolean.parseBoolean(dotenv.get("UPDATE_COMMANDS_AT_LAUNCH")),
                    Boolean.parseBoolean(dotenv.get("REGISTER_DEFAULT_COMMANDS"))
                )
            )
        .setServerDataHandler(new MyServerDataHandler()) // (Experimental!) Optionally, assign your own ServerDataHandler
        .setCommandsHandler(new MyCommandsHandler()); // (Experimental!) Optionally, also assign your own CommandsHandler

        // (Experimental!) Optionally, you can also assign your own CommandsHandler
        builder.setCommandsHandler(new MyCommandsHandler());
        
        // Start the bot
        core = builder.build(dotenv.get("DISCORD_BOT_TOKEN"));
        
        // Register your commands with core.registerCommands(...)
        core.registerCommands(new MyEpicCommand(), new QuackCommand());
}
```

## Support

- Discord Server (TBD)
- [Twitter](https://twitter.com/AlphaSerpentis_)
- [GitHub Issues](https://github.com/AlphaSerpentis/CoffeeCore/issues)

## Dependencies

- [JDA - 5.0.0-beta.8](https://github.com/DV8FromTheWorld/JDA)
- [Gson - 2.10.1](https://github.com/google/gson)
- [RxJava - 3.1.6](https://github.com/ReactiveX/RxJava)
- [Dotenv - 2.3.2](https://github.com/cdimascio/dotenv-java)