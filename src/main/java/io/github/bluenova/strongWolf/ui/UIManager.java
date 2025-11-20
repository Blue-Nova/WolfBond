package io.github.bluenova.strongWolf.ui;

import io.github.bluenova.strongWolf.BondKeeper;
import io.github.bluenova.strongWolf.BondWolf;
import io.github.bluenova.strongWolf.StrongWolfPlugin;
import io.github.bluenova.strongWolf.WolfOwner;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.text.DecimalFormat;
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

    public static void sendWolfDeathMessage(Player player, BondWolf bondWolf) {
        // send action bar message
        float pitch = Math.max(0.01f , 6f - bondWolf.getScale()*5f);
        player.sendActionBar(Component.text("§cYour wolf has died!"));
        player.playSound(player.getLocation(), Sound.ENTITY_WOLF_DEATH, 0.5f, pitch);
    }

    public static void sendWolfRespawnMessage(Player player, BondWolf bondWolf) {
        // send action bar message
        // the bigger the scale of the wolf, the lower the pitch
        float pitch = Math.max(0.01f , 6f - bondWolf.getScale()*5f);
        player.sendActionBar(Component.text("§aYour wolf has respawned!"));
        player.playSound(player.getLocation(), Sound.ENTITY_WOLF_HOWL, 0.5f, pitch);
    }

    public static void sendWolfUpgradeMessage(Player player, BondWolf bondWolf) {
        float pitch = Math.max(0.01f , 6f - bondWolf.getScale()*5f);
        // send action bar message
        player.sendActionBar(Component.text("§aYour wolf has upgraded!"));
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_STEP, 0.5f, pitch);
    }

    public static void removeUIFromPlayer(Player player) {
        // Remove scoreboard
        ScoreboardManager mgr = Bukkit.getScoreboardManager();
        if (mgr == null) return;

        Scoreboard board = mgr.getNewScoreboard();
        player.setScoreboard(board);

        // Remove boss bar
        BossBar emptyBar = BossBar.bossBar(
                Component.text(""),
                0f,
                BossBar.Color.WHITE,
                BossBar.Overlay.PROGRESS
        );
        player.hideBossBar(emptyBar);
    }

    synchronized private void updateScoreboard(WolfOwner owner, BondWolf wolf) {
        Player player = owner.getPlayer();
        if (!player.isOnline()) {
            return;
        }

        // Schedule scoreboard work on the main server thread (Bukkit API must run on main thread)
        StrongWolfPlugin.getPlugin().getServer().getScheduler().runTask(StrongWolfPlugin.getPlugin(), () -> {
            ScoreboardManager mgr = Bukkit.getScoreboardManager();
            if (mgr == null) return;

            Scoreboard board = mgr.getNewScoreboard();

            // Objective name must be unique per scoreboard; 'wolf_bond' is internal id
            Objective obj;
            try {
                obj = board.registerNewObjective("wolf_bond", "dummy", "§6Wolf Bond");
            } catch (IllegalArgumentException ex) {
                // fallback in case of API differences
                obj = board.registerNewObjective("wolf_bond", "dummy");
                obj.setDisplayName("§6Wolf Bond");
            }
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);

            DecimalFormat df = new DecimalFormat("#0.0");
            String level = Integer.toString(wolf.getLevel());
            String health = df.format(wolf.getHealth()/2);
            String maxHealth = df.format(wolf.getMaxHealth()/2);
            String strength = df.format(wolf.getStrength());
            String speed = df.format(wolf.getSpeed());

            // Build a neat layout (higher score appears higher on the sidebar)
            obj.getScore("§7---- §fLvL:" + "§e"+level + " §7----").setScore(6);
            obj.getScore("§eHealth: §a" + health + " / " + maxHealth).setScore(5);
            obj.getScore("§eStrength: §c" + strength).setScore(4);
            obj.getScore("§eSpeed: §b" + speed).setScore(3);
            obj.getScore(" ").setScore(2);
            obj.getScore("§eState: §f" +wolf.getState()).setScore(1);

            player.setScoreboard(board);
        });
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
        if (wolf.getState().equals("Dead")) {
            bossBar = BossBar.bossBar(
                    Component.text("§cRespawning in " + wolf.getDeathCooldown() + "s"),
                    (float) wolf.getDeathCooldown() / BondWolf.RESPAWN_COOLDOWN,
                    BossBar.Color.YELLOW,
                    BossBar.Overlay.PROGRESS
            );
        }
        owner.updateBossBar(bossBar);
    }

    synchronized public void start() {
        uiUpdater.runTaskTimerAsynchronously(StrongWolfPlugin.getPlugin(), 0L, 5L);
    }

}
