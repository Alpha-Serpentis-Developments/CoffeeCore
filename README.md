[maven-central]: https://img.shields.io/maven-central/v/dev.alphaserpentis/CoffeeCore?color=blue

[![Maven Central][maven-central]](https://search.maven.org/artifact/dev.alphaserpentis/CoffeeCore)
# Coffee Core

A Discord bot framework built on top of [JDA](https://github.com/DV8FromTheWorld/JDA) designed to be easy to use. The
framework is designed to be modular, allowing you to only use the features you need and to focus on your bot's
functionality.

## Support

- **Discord Server**: https://asrp.dev/discord
- **Twitter**: [https://twitter.com/AlphaSerpentis_](https://twitter.com/AlphaSerpentis_)
- **GitHub Issues**: https://github.com/Alpha-Serpentis-Developments/CoffeeCore/issues

## Getting Started

**Notice**: Coffee Core requires Java 17+!

### Adding Coffee Core to your project with Maven

Latest Release:

```xml
<dependency>
    <groupId>dev.alphaserpentis</groupId>
    <artifactId>CoffeeCore</artifactId>
    <version>0.5.0-alpha</version>
</dependency>
```

Latest Snapshot:

```xml
<dependency>
    <groupId>dev.alphaserpentis</groupId>
    <artifactId>CoffeeCore</artifactId>
    <version>0.5.0-alpha-SNAPSHOT</version>
</dependency>
```

By default, JDA includes a package to handle voice connections. If you don't need voice support, you can exclude it:

```xml
<dependencies>
    <dependency>
        <groupId>dev.alphaserpentis</groupId>
        <artifactId>CoffeeCore</artifactId>
        <version>VERSION-HERE</version>
        <exclusions>
            <exclusion>
                <groupId>club.minnced</groupId>
                <artifactId>opus-java</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
</dependencies>
```

You can also exclude the `dotenv-java` package if you do not intend to use it.

```xml
<exclusions>
    <exclusion>
        <groupId>io.github.cdimascio</groupId>
        <artifactId>dotenv-java</artifactId>
    </exclusion>
</exclusions>
```

### Creating a bot

The default settings are as follows:

- **Chunking Filter**: `ChunkingFilter.NONE`
- **Enabled Cache Flags**: None
- **Disabled Cache Flags**: `MEMBER_OVERRIDES` and `VOICE_STATE`
- **Enabled Gateway Intents**: None
- **Disabled Gateway Intents**: None
- **Member Cache Policy**: `MemberCachePolicy.NONE`
- **Sharding**: Disabled
- **Shards Total**: -1 (set by Discord)
- **Builder Configuration**: `BuilderConfiguration.DEFAULT`

**Note**: `BuilderConfiguration` is the equivalent of `JDABuilder` or `DefaultShardManagerBuilder`, depending on if the bot is sharded or not

1. (Optional) Create a new `.env` file in the root of your project and fill it out with the following (adjust to your liking):

```env
DISCORD_BOT_TOKEN=YOUR_DISCORD_BOT_TOKEN
BOT_OWNER_ID=YOUR_DISCORD_USER_ID
SERVER_DATA_PATH=PATH_TO_SERVER_DATA_FOLDER
UPDATE_COMMANDS_AT_LAUNCH=true
REGISTER_DEFAULT_COMMANDS=true
```

2. Create a new CoffeeCore instance using CoffeeCoreBuilder and (optionally) load in your bot settings from the `.env` file (or other source):

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

    // Start the bot
    core = builder.build(dotenv.get("DISCORD_BOT_TOKEN"));

    // Register your commands with core.registerCommands(...)
    core.registerCommands(new MyEpicCommand(), new QuackCommand());
}
```

## Writing a Command

Currently, Coffee Core supports three types of commands:
- `BotCommand`: The base command type
- `ButtonCommand`: Extends off of `BotCommand` and adds the ability to use buttons
- `ModalCommand`: An interface that enables the use of modal dialogs

When using `BotCommand` or `ButtonCommand`, `BotCommand` has a generic type that represents the type of data that will be
returned when the command is executed. For example, if you want to return a `MessageEmbed` when the command is executed,
you would use `BotCommand<MessageEmbed, ...>`. Otherwise, if you want to respond back with only a message, you would use
`BotCommand<String, ...>`. For the second generic type, it represents the type of event that will be used to execute the
command. For example, if you want to use a `SlashCommandInteractionEvent`, you would use `BotCommand<..., SlashCommandInteractionEvent>`.

### Using `BotCommand`

```java
public class ExampleCommand extends BotCommand<MessageEmbed, SlashCommandInteractionEvent> {
    public ExampleCommand() {
        super(
                new BotCommandOptions()
                        .setName("example")
                        .setDescription("An example command")
                        .setOnlyEmbed(true) // Must match with the generic type (e.g., true if MessageEmbed, false if String)
        );
    }

    @Override
    @NonNull
    public CommandResponse<MessageEmbed> runCommand(long userId, @NonNull SlashCommandInteractionEvent event) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Example Command");
        builder.setDescription("This is an example command!");

        return new CommandResponse<>(builder.build());
    }
}
```

### Using `ButtonCommand`

To add buttons, you can use the `addButton(...)` method. When adding buttons, you must provide a unique name/key for the
button, a `ButtonStyle`, a label, and whether the button is disabled. Optionally, there's a fifth parameter that allows
you to provide an `Emoji`.

Check out an example [here](https://github.com/Alpha-Serpentis-Developments/CoffeeCore/blob/main/src/test/java/hello/HelloCommandButton.java)

```java
public class ExampleCommand extends ButtonCommand<MessageEmbed, SlashCommandInteractionEvent> {
    public ExampleCommand() {
        super(
                // set your BotCommandOptions
        );

        addButton("primary", ButtonStyle.PRIMARY, "Primary", false);
        addButton("secondary", ButtonStyle.SECONDARY, "Secondary", false);
        addButton("danger", ButtonStyle.DANGER, "Danger", false);
    }

    @Override
    @NonNull
    public CommandResponse<MessageEmbed> runCommand(long userId, @NonNull SlashCommandInteractionEvent event) {
        // Run stuff here
    }

    @Override
    public void runButtonInteraction(@NonNull ButtonInteractionEvent event) {
        String key = convertComponentIdToKey(event.getComponentId());

        // Do stuff here based on the key
    }

    @Override
    @NonNull
    public Collection<ItemComponent> addButtonsToMessage(@NonNull GenericCommandInteractionEvent event) {
        return Arrays.asList(new ItemComponent[]{
                getButton("primary"),
                getButton("secondary"),
                getButton("danger")
        });
    }
}
```

### Using `ModalCommand`

To be written...

## Dependencies

- [JDA - 5.0.0-beta.10](https://github.com/DV8FromTheWorld/JDA)
- [Gson - 2.10.1](https://github.com/google/gson)
- [RxJava - 3.1.6](https://github.com/ReactiveX/RxJava)
- (Optional) [Dotenv - 3.0.0](https://github.com/cdimascio/dotenv-java)
