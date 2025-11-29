package io.github.bluenova.strongWolf;


import io.github.bluenova.strongWolf.ui.UIManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;

public class BondWolf {

    /**
     * WOLF'S BASE VANILLA ATTRIBUTES (tamed, normal difficulty):
     * - Health: 40
     * - Speed: 0.3
     * - Strength: 4 <- (level 10 buffed to 8)
     *
     *  At LEVEL 10, the will should be on par with the base attributes of a tamed wolf.
     *  Current calculations:
     *  - Health: 5 + (3.5 * 10) = 40
     *  - Speed: 0.2 + (0.01 * 10) = 0.3
     *  - Strength: 2 + (0.6 * 10) = 8
     *  - Scale: 0.01 + (0.033 * 30) = 1 <- scale is different, it needs 40 bones to reach level 10
     *  so X should be 0.02
     *  - Water Movement Efficiency: 1.0 + (0.025 * 10) = 1.2
     *  -----------------------------
     */

    public static final int RESPAWN_COOLDOWN = 30; // in seconds
    private static final float BASE_SPEED = 0.2f;
    private static final float BASE_STRENGTH = 2f;
    private static final float BASE_HEALTH = 5f;
    private static final float BASE_SCALE = 0.01f;


    private static final float SPEED_INCREMENT = 0.01f;
    private static final float STRENGTH_INCREMENT = 0.6f;
    private static final float HEALTH_INCREMENT = 3.5f;
    private static final float SCALE_INCREMENT = 0.033f;

    private int level = 0;
    private int deathCooldown = 0;

    private Wolf wolfEntity;

    private final WolfOwner owner;

    private int max_level = 0;
    private boolean wolf_capped = false;

    // Bucket scheduled task (empty for now) that runs while the wolf is alive/respawned
    private BukkitTask bucketTask;

    public BondWolf(WolfOwner owner) {
        this.owner = owner;

        // initialize a boilerplate bucket task that runs every 15 ticks
        // This is a harmless placeholder — actual behavior can be implemented inside run().
        bucketTask = new BukkitRunnable() {
            @Override
            public void run() {
            }
        }.runTaskTimer(StrongWolfPlugin.getPlugin(), 0L, 20L);

        spawnWolf();
    }

    public void respawn() {
        // ensure any running bucket task is stopped before respawning
        stopBucketTask();
        if (wolfEntity != null && !wolfEntity.isDead()) {
            wolfEntity.remove();
        }
        spawnWolf();
    }

    public void attack(LivingEntity target) {
        if (wolfEntity != null && !wolfEntity.isDead()) {
            if (target.equals(wolfEntity))
                return;

            if (wolfEntity.isSitting())
                wolfEntity.setSitting(false);

            wolfEntity.setTarget(target);
        }
    }

    private void spawnWolf() {
        Location spawnLocation = owner.getPlayer().getLocation();
        // Spawn wolf
        wolfEntity = (org.bukkit.entity.Wolf) spawnLocation.getWorld()
                .spawnEntity(spawnLocation, org.bukkit.entity.EntityType.WOLF);

        // Make sure the wolf belongs to the player
        wolfEntity.setOwner(owner.getPlayer());
        wolfEntity.setTamed(true);
        wolfEntity.setAdult();
        wolfEntity.setSitting(false);

        // ---- CUSTOM ATTRIBUTES ----

        reapplyAttributes();

        UIManager.sendWolfRespawnMessage(owner.getPlayer(), this);

        // start the bucket scheduled task when the wolf respawns
        startBucketTask();
    }

    /**
     * Starts a simple scheduled bucket task. The body is intentionally empty for now.
     * The task is run every 20 ticks (1 second).
     */
    private void startBucketTask() {
        // stop any existing task first
        stopBucketTask();
        bucketTask = new BukkitRunnable() {
            @Override
            public void run() {
                // If the wolf doesn't exist or is dead, nothing to do
                if (wolfEntity == null || wolfEntity.isDead()) return;

                // Damage any Ender Dragon within a 4-block radius by the wolf's strength
                double radius = 4.0;
                double damage = getStrength();
                for (Entity e : wolfEntity.getNearbyEntities(radius, radius, radius)) {
                    if (e instanceof EnderDragon) {
                        try {
                            // attribute the damage to the wolf entity
                            ((EnderDragon) e).setHealth(Math.max(((EnderDragon) e).getHealth() - damage, 0) );
                        } catch (Exception notIgnored) {
                            StrongWolfPlugin.getPlugin().getLogger().warning("Failed to apply damage to Ender Dragon:" + notIgnored.getMessage());
                        }
                    }
                }
            }
        }.runTaskTimer(StrongWolfPlugin.getPlugin(), 0L, 15L);
    }

    private void stopBucketTask() {
        if (bucketTask != null) {
            try {
                bucketTask.cancel();
            } catch (IllegalStateException ignored) {
                // If the scheduler is shutting down or task already cancelled, ignore
            }
            bucketTask = null;
        }
    }

    public void reapplyAttributes() {
        if (wolfEntity != null && !wolfEntity.isDead()) {
            // divide levels by 3
            int effectiveLevel = (int) Math.floor(this.level / 3.0);
            Objects.requireNonNull(wolfEntity.getAttribute(Attribute.MAX_HEALTH))
                    .setBaseValue(BASE_HEALTH + (HEALTH_INCREMENT * effectiveLevel));

            // every three levels, increase strength
            Objects.requireNonNull(wolfEntity.getAttribute(Attribute.ATTACK_DAMAGE))
                    .setBaseValue(BASE_STRENGTH + (STRENGTH_INCREMENT * effectiveLevel));

            Objects.requireNonNull(wolfEntity.getAttribute(Attribute.MOVEMENT_SPEED))
                    .setBaseValue(BASE_SPEED + (SPEED_INCREMENT * effectiveLevel));

            Objects.requireNonNull(wolfEntity.getAttribute(Attribute.WATER_MOVEMENT_EFFICIENCY))
                    .setBaseValue(BASE_SPEED * (SPEED_INCREMENT * effectiveLevel));

            Objects.requireNonNull(wolfEntity.getAttribute(Attribute.SCALE))
                    .setBaseValue(BASE_SCALE + (SCALE_INCREMENT * this.level));

            Objects.requireNonNull(wolfEntity.getAttribute(Attribute.STEP_HEIGHT))
                    .setBaseValue(0.5 + (this.level * 0.1));

            Objects.requireNonNull(wolfEntity.getAttribute(Attribute.SAFE_FALL_DISTANCE))
                    .setBaseValue(1.5 + (this.level * 0.3));

            Objects.requireNonNull(wolfEntity.getAttribute(Attribute.JUMP_STRENGTH))
                    .setBaseValue(0.5 + (this.level * 0.1));

            // past level 20, start increasing knockback resistance
            Objects.requireNonNull(wolfEntity.getAttribute(Attribute.KNOCKBACK_RESISTANCE))
                    .setBaseValue(Math.max((this.level-20) * 0.02, 0f));
        }
    }

    /**
    public void upgradeStrength() {
        this.strength += STRENGTH_INCREMENT;
        if (wolfEntity != null && !wolfEntity.isDead()) {
            reapplyAttributes();
        }
    }

    public void upgradeHealth() {
        this.max_health += HEALTH_INCREMENT;
        if (wolfEntity != null && !wolfEntity.isDead()) {
            reapplyAttributes();
        }
    }

    public void upgradeSpeed() {
        this.speed += SPEED_INCREMENT;
        if (wolfEntity != null && !wolfEntity.isDead()) {
            reapplyAttributes();
        }
    }

    public void upgradeWaterEfficiency() {
        if (wolfEntity != null && !wolfEntity.isDead()) {
            reapplyAttributes();
        }
    }

    public void upgradeScale() {
        this.scale += SCALE_INCREMENT;
        if (wolfEntity != null && !wolfEntity.isDead()) {
            reapplyAttributes();
        }
    }
    */

    public synchronized int getDeathCooldown() {
        return deathCooldown;
    }

    public void death() {
        if (wolfEntity != null) {
            wolfEntity.remove();
        }
        // stop bucket task when the wolf dies
        stopBucketTask();
        this.deathCooldown = RESPAWN_COOLDOWN;
        UIManager.sendWolfDeathMessage(owner.getPlayer(), this);
        // You might want to implement a scheduler to decrease this cooldown over time
        Bukkit.getScheduler().runTaskTimer(StrongWolfPlugin.getPlugin(), task -> {
            deathCooldown--;
            if (deathCooldown <= 0) {
                respawn();
                task.cancel();
            }
        }, 20L, 20L); // Runs every second
    }

    public float getStrength() {
        return wolfEntity != null ? (float) Objects.requireNonNull(wolfEntity.getAttribute(Attribute.ATTACK_DAMAGE)).getBaseValue() : 0f;
    }
    public int getHealth() {
        return wolfEntity != null ? (int) wolfEntity.getHealth() : 0;
    }
    public float getSpeed() {
        return wolfEntity != null ? (float) Objects.requireNonNull(wolfEntity.getAttribute(Attribute.MOVEMENT_SPEED)).getBaseValue() : 0f;
    }
    public float getScale() {
        return wolfEntity != null ? (float) Objects.requireNonNull(wolfEntity.getAttribute(Attribute.SCALE)).getBaseValue() : 0f;
    }

    public double getMaxHealth() {
        return wolfEntity != null ? Objects.requireNonNull(wolfEntity.getAttribute(Attribute.MAX_HEALTH)).getBaseValue() : 0f;
    }

    public Entity getWolfEntity() {
        return wolfEntity;
    }

    public int getLevel() {
        return level;
    }

    public double getHealthPercent() {
        if (wolfEntity != null && !wolfEntity.isDead()) {
            return (wolfEntity.getHealth() / Objects.requireNonNull(wolfEntity.getAttribute(Attribute.MAX_HEALTH)).getBaseValue());
        }
        return 0f;
    }

    public String getState() {
        if (wolfEntity == null || wolfEntity.isDead()) {
            return "Dead";
        } else if (wolfEntity.getTarget() != null) {
            return "Attacking";
        } else if (wolfEntity.isSitting()) {
            return "Sitting";
        } else {
            return "Following";
        }
    }

    public void upgradeAttributes() {
        if (!wolf_capped){
            level++;
        }
        max_level++;
        UIManager.sendWolfUpgradeMessage(owner.getPlayer(), this);
        reapplyAttributes();
        wolfEntity.heal(4.0); // Heal 4 health points on upgrade
    }

    public void remove() {
        if (wolfEntity != null && !wolfEntity.isDead()) {
            // stop the bucket task when explicitly removing the wolf
            stopBucketTask();
            wolfEntity.remove();
            wolfEntity.setHealth(0);
        }
    }

    public void sit() {
        if (wolfEntity != null && !wolfEntity.isDead()) {
            wolfEntity.setSitting(true);
            wolfEntity.setTarget(null);
        }
    }

    public void setLevel(int level) {
        if (level < 0) {
            level = 0;
        }
        if (level > max_level) {
            level = max_level;
        }
        // if level is lower than max_level, set wolf_capped to true
        if (level < max_level) {
            wolf_capped = true;
        }
        // if level is equal to max_level, set wolf_capped to false
        if (level == max_level) {
            wolf_capped = false;
        }
        this.level = level;
        reapplyAttributes();
    }

    public int getMaxLevel() {
        return max_level;
    }

    public boolean isWolfCapped() {
        return wolf_capped;
    }

}
