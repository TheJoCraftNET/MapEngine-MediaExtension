package de.pianoman911.mapengine.media;

import de.pianoman911.mapengine.media.updater.MapEngineUpdater;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MapEngineMedia extends JavaPlugin {

    @Override
    public void onLoad() {
        new Metrics(this, 18145);
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        if (this.getConfig().getBoolean("updater.enabled", true)) {
            MapEngineUpdater updater = new MapEngineUpdater(this);
            Bukkit.getPluginManager().registerEvents(updater, this);
        }
    }
}
