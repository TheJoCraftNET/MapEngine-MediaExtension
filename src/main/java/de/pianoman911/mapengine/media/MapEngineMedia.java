package de.pianoman911.mapengine.media;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class MapEngineMedia extends JavaPlugin {

    @Override
    public void onLoad() {
        new Metrics(this, 18145);
    }
}
