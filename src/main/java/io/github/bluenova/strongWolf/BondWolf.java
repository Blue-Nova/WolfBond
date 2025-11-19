package io.github.bluenova.strongWolf;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Objects;

public class BondWolf {

    static final int RESPAWN_COOLDOWN = 40; // in seconds

    private int deathCooldown = 0;
    private int strength;
    private int health;
    private int speed;

    private org.bukkit.entity.Wolf wolfEntity;

    private final WolfOwner owner;

    public BondWolf(WolfOwner owner) {
        this.owner = owner;
        this.strength = 2;
        this.health = 20;
        this.speed = 0;
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
        // Health
        Objects.requireNonNull(wolfEntity.getAttribute(Attribute.MAX_HEALTH))
                .setBaseValue(this.health);
        wolfEntity.setHealth(this.health);

        // Strength → Attack damage
        Objects.requireNonNull(wolfEntity.getAttribute(Attribute.ATTACK_DAMAGE))
                .setBaseValue(this.strength);

        // Speed
        Objects.requireNonNull(wolfEntity.getAttribute(Attribute.MOVEMENT_SPEED))
                .setBaseValue(0.35 + (this.speed * 0.06));
    }

    public void upgradeStrength(int amount) {
        this.strength += amount;
        if (wolfEntity != null && !wolfEntity.isDead()) {
            Objects.requireNonNull(wolfEntity.getAttribute(Attribute.ATTACK_DAMAGE))
                    .setBaseValue(this.strength);
        }
    }

    public void upgradeHealth(int amount) {
        this.health += amount;
        if (wolfEntity != null && !wolfEntity.isDead()) {
            Objects.requireNonNull(wolfEntity.getAttribute(Attribute.MAX_HEALTH))
                    .setBaseValue(this.health);
            wolfEntity.setHealth(this.health);
        }
    }

    public void upgradeSpeed(int amount) {
        this.speed += amount;
        if (wolfEntity != null && !wolfEntity.isDead()) {
            Objects.requireNonNull(wolfEntity.getAttribute(Attribute.MOVEMENT_SPEED))
                    .setBaseValue(0.25 + (this.speed * 0.02));
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
        // You might want to implement a scheduler to decrease this cooldown over time
        Bukkit.getScheduler().runTaskTimer(StrongWolfPlugin.getPlugin(), task -> {
            deathCooldown--;
            owner.getPlayer().sendMessage("Your wolf will respawn in " + deathCooldown + " seconds.");
            if (deathCooldown <= 0) {
                respawn();
                task.cancel();
            }
        }, 20L, 20L); // Runs every second
    }

    public int getStrength() {
        return strength;
    }
    public int getHealth() {
        return health;
    }
    public int getSpeed() {
        return speed;
    }

    public Entity getWolfEntity() {
        return wolfEntity;
    }

    public double getHealthPercent() {
        if (wolfEntity != null && !wolfEntity.isDead()) {
            return (wolfEntity.getHealth() / wolfEntity.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
        }
        owner.getPlayer().sendMessage("§cYour wolf is dead!");
        return 0f;
    }
}
