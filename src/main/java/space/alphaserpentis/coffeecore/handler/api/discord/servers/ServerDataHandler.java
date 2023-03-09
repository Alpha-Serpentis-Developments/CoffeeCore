package space.alphaserpentis.coffeecore.handler.api.discord.servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import space.alphaserpentis.coffeecore.core.CoffeeCore;
import space.alphaserpentis.coffeecore.data.server.ServerData;
import space.alphaserpentis.coffeecore.serialization.ServerDataDeserializer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerDataHandler extends ListenerAdapter {

    public static Path serverJson;
    public static HashMap<Long, ServerData> serverDataHashMap = new HashMap<>();

    public static void init(@NonNull CoffeeCore core) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(serverDataHashMap.getClass(), new ServerDataDeserializer())
                .create();
        serverJson = Path.of(core.settings.serverDataPath);

        Reader reader = Files.newBufferedReader(serverJson);
        serverDataHashMap = gson.fromJson(reader, new TypeToken<Map<Long, ServerData>>(){}.getType());

        // Check the current servers
        if(serverDataHashMap == null)
            serverDataHashMap = new HashMap<>();

        ArrayList<Long> serversActuallyJoined = new ArrayList<>();

        for(Guild g: CoffeeCore.api.getGuilds()) {
            if(!serverDataHashMap.containsKey(g.getIdLong())) {
                serverDataHashMap.put(g.getIdLong(), new ServerData());
            }

            serversActuallyJoined.add(g.getIdLong());
        }

        // Check if the bot left a server but data wasn't cleared
        serverDataHashMap.keySet().removeIf(id -> !serversActuallyJoined.contains(id));

        updateServerData();
    }

    public static void updateServerData() throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        writeToJSON(gson, serverDataHashMap);
    }

    private static void writeToJSON(@NonNull Gson gson, @NonNull Object data) throws IOException {
        Writer writer = Files.newBufferedWriter(serverJson);
        gson.toJson(data, writer);
        writer.close();
    }

    @Override
    public void onGuildJoin(@NonNull GuildJoinEvent event) {
        serverDataHashMap.put(event.getGuild().getIdLong(), new ServerData());
        try {
            updateServerData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onGuildLeave(@NonNull GuildLeaveEvent event) {
        serverDataHashMap.remove(event.getGuild().getIdLong());
        try {
            updateServerData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
