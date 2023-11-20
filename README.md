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

### Adding Coffee Core to Your Project

#### Latest Release:

**Maven**
```xml
<dependency>
    <groupId>dev.alphaserpentis</groupId>
    <artifactId>CoffeeCore</artifactId>
    <version>0.6.0-alpha</version>
</dependency>
```

**Gradle**
```groovy
implementation 'dev.alphaserpentis:CoffeeCore:0.6.0-alpha'
```

#### Latest Snapshot:

**Maven**
```xml
<dependency>
    <groupId>dev.alphaserpentis</groupId>
    <artifactId>CoffeeCore</artifactId>
    <version>0.6.0-alpha-111023-SNAPSHOT</version>
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

- - -
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
                Long.parseLong(dotenv.get("BOT_OWNER_ID")), // the bot owner's user ID
                dotenv.get("SERVER_DATA_PATH"), // the path to the server data file
                Boolean.parseBoolean(dotenv.get("UPDATE_COMMANDS_AT_LAUNCH")), // whether to update commands at launch
                Boolean.parseBoolean(dotenv.get("REGISTER_DEFAULT_COMMANDS")) // whether to register default commands
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

#### Updating Commands At Launch
When you update commands at launch, any changes made to the structure of the command (e.g., name, description, subcommands)
will be reflected in Discord. However, if this is not toggled to `true`, it will not update the changes!

#### Register Default Commands
If you wish to use the default commands that Coffee Core offers (e.g., `help`, `about`, `shutdown`, `settings`) you can
set this true to have them added to your bot.

Default commands can still be accessed without toggling the setting to `true` if needed, and can even be extended off of
to grant more functionality.

## Writing a Command

Coffee Core offers a flexible command system, catering to various interaction types.
At its core is the `BotCommand` class, which provides the foundational behavior for defining and executing commands.

Types of Commands:
- **Slash Commands (`SLASH`)**: The default type of command. Invoked by typing `/` in Discord.
- **User Context Menu Options (`USER`)**: Invoked by right-clicking on a user in Discord.
- **Message Context Menu Options (`MESSAGE`)**: Invoked by right-clicking on a message in Discord.

Regardless of the command type, these types can be used in conjunction with `ButtonCommand` or `ModalCommand` to add
buttons or modals to your commands.

### Using `CommandResponse`
The `CommandResponse` class provides a structured way to define the output of a command. Depending on the nature of the
command, you can choose to send a single string, embed, or multiple embeds.

**Note**: If you're using `String` as your response, you can only return one string!

**Default Constructor**:
```java
return new CommandResponse<>(
        true, // Is the message an ephemeral response
        true, // If the user's ratelimit should be forgiven after running this command,
        new EmbedBuilder().setDescription("Hello world!").build() // Takes in generic type T which can either be a String or MessageEmbed
);
```

The following constructors omit the `forgiveRatelimit` parameter, which defaults to `false`.

**Single String**:
```java
return new CommandResponse<>(true, "Hello world!");
```

**Multiple Embeds**:
```java
return new CommandResponse<>(true, embed1, embed2, embed3);
```

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

        return new CommandResponse<>(true, builder.build());
    }
}
```

### Using `ButtonCommand`

To add buttons, you can use the `addButton(...)` method. When adding buttons, you must provide a unique name/key for the
button, a `ButtonStyle`, a label, and whether the button is disabled. Optionally, there's a fifth parameter that allows
you to provide an `Emoji`.

Check out an example [here](src/test/dev/alphaserpentis/examples/coffeecore/java/hello/HelloCommandButton.java)

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

- [JDA - 5.0.0-beta.15](https://github.com/DV8FromTheWorld/JDA)
- [Gson - 2.10.1](https://github.com/google/gson)
- [RxJava - 3.1.7](https://github.com/ReactiveX/RxJava)
