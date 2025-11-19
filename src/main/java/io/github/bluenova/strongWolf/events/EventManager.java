package io.github.bluenova.strongWolf.events;

import io.github.bluenova.strongWolf.StrongWolfPlugin;
import org.bukkit.plugin.PluginManager;

public class EventManager {

    public EventManager() {
        registerEvents();
    }

    private void registerEvents() {
        PluginManager pluginManager = StrongWolfPlugin.getPlugin().getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerDamageEntityEvent(), StrongWolfPlugin.getPlugin());
        pluginManager.registerEvents(new FeedWolfEvent(), StrongWolfPlugin.getPlugin());
        pluginManager.registerEvents(new RightClickEvent(), StrongWolfPlugin.getPlugin());
        pluginManager.registerEvents(new WolfDeathEvent(), StrongWolfPlugin.getPlugin());
    }
}
