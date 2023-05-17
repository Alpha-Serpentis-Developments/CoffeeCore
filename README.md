[maven-central]: https://img.shields.io/maven-central/v/dev.alphaserpentis/CoffeeCore?color=blue

[![Maven Central][maven-central]](https://search.maven.org/artifact/dev.alphaserpentis/CoffeeCore)
# Coffee Core

A Discord bot framework built on top of [JDA](https://github.com/DV8FromTheWorld/JDA) designed to be easy to use. The
framework is designed to be modular, allowing you to only use the features you need and to focus on your bot's
functionality.

## Support

- **Discord Server**: https://asrp.dev/discord
- **Twitter**: [https://twitter.com/AlphaSerpentis_](https://twitter.com/AlphaSerpentis_)
- **GitHub Issues**: https://github.com/AlphaSerpentis/CoffeeCore/issues

## Getting Started

### Adding Coffee Core to your project with Maven

Latest Release:

```xml
<dependency>
    <groupId>dev.alphaserpentis</groupId>
    <artifactId>CoffeeCore</artifactId>
    <version>0.3.0-alpha</version>
</dependency>
```

Latest Snapshot:

```xml
<dependency>
    <groupId>dev.alphaserpentis</groupId>
    <artifactId>CoffeeCore</artifactId>
    <version>0.4.0-alpha-SNAPSHOT</version>
</dependency>
```

### Creating a bot

**Note**: By default, Coffee Core will enable `GUILD_MEMBERS`, a privileged intent. Additionally,
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

    // Start the bot
    core = builder.build(dotenv.get("DISCORD_BOT_TOKEN"));

    // Register your commands with core.registerCommands(...)
    core.registerCommands(new MyEpicCommand(), new QuackCommand());
    }
```

## Writing a Command

Currently, Coffee Core supports three types of commands:
- `BotCommand`: Purely uses the slash command to execute an action
- `ButtonCommand`: Extends off of `BotCommand` and adds the ability to use buttons
- `ModalCommand`: An interface that enables the use of modal dialogs

When using `BotCommand` or `ButtonCommand`, `BotCommand` has a generic type that represents the type of data that will be
returned when the command is executed. For example, if you want to return a `MessageEmbed` when the command is executed,
you would use `BotCommand<MessageEmbed>`. Otherwise, if you want to respond back with only a message, you would use
`BotCommand<String>`.

### Using `BotCommand`

```java
public class ExampleCommand extends BotCommand<MessageEmbed> {
    public ExampleCommand() {
        super(
                new BotCommand.BotCommandOptions()
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

Check out an example [here](https://github.com/AlphaSerpentis/CoffeeCore/blob/main/src/examples/java/hello/HelloCommandButton.java)

```java
public class ExampleCommand extends ButtonCommand<MessageEmbed> {
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
        return Arrays.asList(new ItemComponent[] {
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

- [JDA - 5.0.0-beta.9](https://github.com/DV8FromTheWorld/JDA)
- [Gson - 2.10.1](https://github.com/google/gson)
- [RxJava - 3.1.6](https://github.com/ReactiveX/RxJava)
- [Dotenv - 2.3.2](https://github.com/cdimascio/dotenv-java)