package io.github.bluenova.strongWolf.events;

import io.github.bluenova.strongWolf.BondKeeper;
import io.github.bluenova.strongWolf.StrongWolfPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class WolfKillEntityEvent implements Listener {

    @EventHandler
    public void onWolfKillEntity(EntityDamageByEntityEvent event) {
        // This method will be called when any entity dies.
        // You can add your custom logic here to handle when a wolf kills an entity.
        Entity damager = event.getDamager();
        Entity victim = event.getEntity();

        StrongWolfPlugin plugin = StrongWolfPlugin.getPlugin();
        plugin.getLogger().info("EntityDamageByEntityEvent triggered: Damager = " + damager.getType() + ", Victim = " + victim.getType());
        if (!(damager instanceof Wolf wolf)) return;
        if (!(victim instanceof LivingEntity livingVictim)) return;
        plugin.getLogger().info("Damager is a Wolf and Victim is a LivingEntity.");
        // proceed only if the damage is strictly greater than the victim's current health
        // (i.e. the attack will kill the victim)
        double finalDamage = event.getFinalDamage();
        double currentHealth = livingVictim.getHealth();
        if (finalDamage < currentHealth) {
            return;
        }
        plugin.getLogger().info("Wolf is about to kill the entity.");
        BondKeeper bondKeeper = io.github.bluenova.strongWolf.StrongWolfPlugin.getPlugin().getBondKeeper();
        if (bondKeeper.isWolfBonded(wolf)) {
            // The wolf is bonded, you can add your custom logic here
            plugin.getLogger().info("Wolf is bonded, executing special kill actions...");
            Location loc = livingVictim.getLocation();
            World world = loc.getWorld();
            if (world != null) {
                // Chance to drop a bone upon killing an entity
                plugin.getLogger().info("Bonded wolf has killed an entity, checking for bone drop...");
                plugin.getLogger().info("Bone drop succeeded!");
                world.dropItem(loc, new ItemStack(Material.BONE, 1));
                float dropChance = 0.50f; // 25% chance
                if (Math.random() < dropChance) {
                    plugin.getLogger().info("Bone drop succeeded!");
                    world.dropItem(loc, new ItemStack(Material.BONE, 1));
                }
                float rareDropChance = 0.20f; // 10% chance for rare drop
                if (Math.random() < rareDropChance) {
                    plugin.getLogger().info("Rare drop (Bone) succeeded!");
                    world.dropItem(loc, new ItemStack(Material.BONE, 1));
                }
            }
        }else {
            plugin.getLogger().info("Wolf is not bonded, no special actions taken.");
        }
    }
}
