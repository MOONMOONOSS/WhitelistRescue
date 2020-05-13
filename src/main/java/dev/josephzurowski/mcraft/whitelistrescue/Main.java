package dev.josephzurowski.mcraft.whitelistrescue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public final class Main extends JavaPlugin {

  @Override
  public void onEnable() {
    // Plugin startup logic
    this.getCommand("recoverwl").setExecutor(this::onCommand);
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    final boolean isPlayer = sender instanceof Player;

    if (isPlayer) {
      final Player ply = (Player)sender;
      if (ply.hasPermission("whitelistrescue.recover")) {
        final String path = this.getDataFolder().getAbsolutePath();
        final Set<OfflinePlayer> whitelist = Bukkit.getWhitelistedPlayers();
        ArrayList<JsonObject> recovered = new ArrayList<>();
        for(final OfflinePlayer player: whitelist) {
          JsonObject listing = new JsonObject();
          listing.addProperty("uuid", player.getUniqueId().toString());
          listing.addProperty("name", player.getName());

          recovered.add(listing);
        }

        final Gson g = new GsonBuilder().create();
        final String jsonStr = g.toJson(recovered);
        try {
          final FileWriter fw = new FileWriter(path+"/recovery.json");
          fw.write(jsonStr);
          fw.flush();
          fw.close();

          this.getServer().broadcast(ChatColor.GREEN + "[WhitelistRescue] The whitelist has been saved to the server's data folder.", this.getServer().BROADCAST_CHANNEL_ADMINISTRATIVE);

          return true;
        } catch (IOException e) {
          this.getServer().getLogger().warning("Unable to open the recovery file for writing!");

          return false;
        }
      } else return false;
    } else {
      return true;
    }
  }
}
