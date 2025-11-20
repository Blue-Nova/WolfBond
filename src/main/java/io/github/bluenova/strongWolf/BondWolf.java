package io.github.bluenova.strongWolf;


import io.github.bluenova.strongWolf.ui.UIManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Objects;

public class BondWolf {

    public static final int RESPAWN_COOLDOWN = 40; // in seconds
    private static final float BASE_SPEED = 0.25f;
    private static final float BASE_STRENGTH = 1f;
    private static final float BASE_HEALTH = 2f;
    private static final float BASE_WATER_MOVEMENT_EFFICIENCY = 1.0f;
    private static final float BASE_SCALE = 0.06f;


    private static final float SPEED_INCREMENT = 0.15f;
    private static final float STRENGTH_INCREMENT = 0.33f;
    private static final float HEALTH_INCREMENT = 1.5f;
    private static final float SCALE_INCREMENT = 0.02f;

    private int level = 0;
    private int deathCooldown = 0;
    private float strength;
    private float max_health;
    private float speed;
    private float scale;

    private int nextUpgradeIndex = 0;

    private org.bukkit.entity.Wolf wolfEntity;

    private final WolfOwner owner;

    public BondWolf(WolfOwner owner) {
        this.owner = owner;
        this.strength = BASE_STRENGTH;
        this.max_health = BASE_HEALTH*2;
        this.speed = BASE_SPEED;
        this.scale = BASE_SCALE;
        spawnWolf();
    }

    public void respawn() {
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
    }

    public void reapplyAttributes() {
        if (wolfEntity != null && !wolfEntity.isDead()) {
            Objects.requireNonNull(wolfEntity.getAttribute(Attribute.MAX_HEALTH))
                    .setBaseValue(this.max_health);

            Objects.requireNonNull(wolfEntity.getAttribute(Attribute.ATTACK_DAMAGE))
                    .setBaseValue(this.strength);

            Objects.requireNonNull(wolfEntity.getAttribute(Attribute.MOVEMENT_SPEED))
                    .setBaseValue(BASE_SPEED + (this.speed * 0.06));

            Objects.requireNonNull(wolfEntity.getAttribute(Attribute.WATER_MOVEMENT_EFFICIENCY))
                    .setBaseValue(BASE_WATER_MOVEMENT_EFFICIENCY + (this.speed * 0.02));

            Objects.requireNonNull(wolfEntity.getAttribute(Attribute.SCALE))
                    .setBaseValue(this.scale);

            Objects.requireNonNull(wolfEntity.getAttribute(Attribute.STEP_HEIGHT))
                    .setBaseValue(0.5 + (this.scale * 1.5));

            Objects.requireNonNull(wolfEntity.getAttribute(Attribute.SAFE_FALL_DISTANCE))
                    .setBaseValue(1.5 + this.scale * 6);

            Objects.requireNonNull(wolfEntity.getAttribute(Attribute.JUMP_STRENGTH))
                    .setBaseValue(0.5 + (this.scale));
        }
    }

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

    public synchronized int getDeathCooldown() {
        return deathCooldown;
    }

    public void death() {
        if (wolfEntity != null) {
            wolfEntity.remove();
        }
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
        return strength;
    }
    public int getHealth() {
        return wolfEntity != null ? (int) wolfEntity.getHealth() : 0;
    }
    public float getSpeed() {
        return speed;
    }
    public float getScale() {
        return scale;
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

    public double getMaxHealth() {
        return max_health;
    }

    public String getState() {
        if (wolfEntity == null || wolfEntity.isDead()) {
            return "Dead";
        } else if (wolfEntity.getTarget() != null) {
            return "Attacking";
        } else {
            return "Idle";
        }
    }

    public void upgradeAttributes() {
        if (nextUpgradeIndex >= 2) {
            nextUpgradeIndex = 0;
            upgradeHealth();
            upgradeSpeed();
            upgradeStrength();
            upgradeWaterEfficiency();
            UIManager.sendWolfUpgradeMessage(owner.getPlayer(), this);
            level++;
        }else nextUpgradeIndex++;
        generalUpgrade();
    }

    private void generalUpgrade() {
        // general upgrade that applies to all upgrades
        upgradeScale();
        wolfEntity.setHealth(wolfEntity.getHealth() + 2); // heal 1 heart on upgrade
    }

    public void remove() {
        if (wolfEntity != null && !wolfEntity.isDead()) {
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
}
