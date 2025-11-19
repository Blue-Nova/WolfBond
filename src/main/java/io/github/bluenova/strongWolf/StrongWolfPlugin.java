package io.github.bluenova.strongWolf;

import io.github.bluenova.strongWolf.commands.MainCommand;
import io.github.bluenova.strongWolf.events.EventManager;
import io.github.bluenova.strongWolf.ui.UIManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class StrongWolfPlugin extends JavaPlugin {

    static StrongWolfPlugin plugin;
    private BondKeeper bondKeeper;
    private EventManager eventManager;
    private MainCommand mainCommand;
    private UIManager uiManager;

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info("StrongWolf Plugin Enabled");
        bondKeeper = new BondKeeper();
        eventManager = new EventManager();
        Objects.requireNonNull(getPlugin().getCommand("strongwolf")).setExecutor(new MainCommand());
        uiManager = new UIManager();
        uiManager.start();
    }

    @Override
    public void onDisable() {

    }

    static public StrongWolfPlugin getPlugin() {
        return plugin;
    }

    public BondKeeper getBondKeeper() {
        return bondKeeper;
    }

}
