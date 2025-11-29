package io.github.bluenova.strongWolf;

import io.github.bluenova.strongWolf.ui.UIManager;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.entity.Boss;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WolfOwner {

    private final Player player;
    private final BondWolf wolf;
    private BossBar lastBossBar;

    public WolfOwner(Player player) {
        this.player = player;
        this.wolf = new BondWolf(this);
    }

    public BondWolf getWolf() {
        return wolf;
    }

    public Player getPlayer() {
        return player;
    }

    public void markTarget(@NotNull LivingEntity entity) {
        wolf.attack(entity);
    }

    public void updateBossBar(BossBar bossBar) {
        if (lastBossBar != null) {
            player.hideBossBar(lastBossBar);
        }
        lastBossBar = bossBar;
        player.showBossBar(lastBossBar);
    }

    public void remove() {
        if (lastBossBar != null) {
            player.hideBossBar(lastBossBar);
        }
        // remove scoreboard
        UIManager.removeUIFromPlayer(player);
    }
}
