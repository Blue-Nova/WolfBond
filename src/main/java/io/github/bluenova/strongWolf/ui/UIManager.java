package io.github.bluenova.strongWolf.ui;

import io.github.bluenova.strongWolf.BondKeeper;
import io.github.bluenova.strongWolf.BondWolf;
import io.github.bluenova.strongWolf.StrongWolfPlugin;
import io.github.bluenova.strongWolf.WolfOwner;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class UIManager {

    // this class creates a thread that will run each second.
    BukkitRunnable uiUpdater;
    public UIManager() {
        uiUpdater = new BukkitRunnable() {
            @Override
            public void run() {
                BondKeeper bondKeeper = StrongWolfPlugin.getPlugin().getBondKeeper();

                HashMap<WolfOwner, BondWolf> bondList = bondKeeper.getBondList();

                for (Map.Entry<WolfOwner, BondWolf> entry : bondList.entrySet()) {
                    WolfOwner owner = entry.getKey();
                    BondWolf wolf = entry.getValue();

                    updateBossBar(owner, wolf);
                    updateScoreboard(owner, wolf);
                }

            }
        };
    }

    synchronized private void updateScoreboard(WolfOwner owner, BondWolf wolf) {
        Player player = owner.getPlayer();
        if (!player.isOnline()) {
            return;
        }
        
    }

    synchronized private void updateBossBar(WolfOwner owner, BondWolf wolf) {
        Player player = owner.getPlayer();
        if (!player.isOnline()) {
            return;
        }
        BossBar bossBar = BossBar.bossBar(
                Component.text("§cWolf Health"),
                (float) wolf.getHealthPercent(),
                BossBar.Color.PINK,
                BossBar.Overlay.PROGRESS
        );
        owner.updateBossBar(bossBar);

    }

    synchronized public void start() {
        uiUpdater.runTaskTimerAsynchronously(StrongWolfPlugin.getPlugin(), 0L, 20L);
    }

}
