package me.RockinChaos.itemjoin.Listeners.JoinItem;

import java.io.File;
import java.io.IOException;
import java.util.List;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.WorldHandler;
import me.RockinChaos.itemjoin.utils.CheckItem;
import me.RockinChaos.itemjoin.utils.Registers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class FirstJoin implements Listener {

	public static int failCount = 0;

    public static void setFirstJoin(Player player, String world) {
    	Boolean FirstJoinMode = ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled");
   	   if (FirstJoinMode == true) {
    		 String FirstFindPlayer = ItemJoin.getSpecialConfig("FirstJoin.yml").getString(world + "." + player.getName().toString());
    		if (FirstFindPlayer == null) {
    		File playerFile =  new File (ItemJoin.pl.getDataFolder(), "FirstJoin.yml");
    		FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
    		playerData.set(world + "." + player.getName().toString() + "." + "UniqueId", player.getUniqueId().toString());
    		try {
    		playerData.save(playerFile);
    		} catch (IOException e1) {
    		ItemJoin.pl.getServer().getLogger().severe("Could not save " + player.getName() + " to the data file FirstJoin.yml!");
    		e1.printStackTrace();
     	   }
     	  }
      }
   }
	
    public static void setJoinItems(Player player)
    {
        ConfigurationSection selection = ItemJoin.getSpecialConfig("items.yml").getConfigurationSection(player.getWorld().getName() + ".items");
        final String world = WorldHandler.getWorld(player.getWorld().getName());
        for (String item : selection.getKeys(false)) 
        {
      	  ConfigurationSection items = selection.getConfigurationSection(item);
          String slot = items.getString(".slot");
         if (WorldHandler.isWorld(world)) {
       	  if (player.hasPermission(PermissionsHandler.customPermissions(items, item, world)) 
       			  || player.hasPermission("itemjoin." + world + ".*") 
       			  || player.hasPermission("itemjoin.*")) {
       		  if (slot.equalsIgnoreCase("Helmet") 
       				  || slot.equalsIgnoreCase("Arbitrary")
       				  || slot.equalsIgnoreCase("Chestplate") 
       				  || slot.equalsIgnoreCase("Leggings") 
       				  || slot.equalsIgnoreCase("Boots") 
       				  || slot.equalsIgnoreCase("Offhand")) {
       			CustomSlots(player, items, item);
       		  } else {
       		   InventorySlots(player, items, item);
       		  }
   	        }
         }
        }
        setFirstJoin(player, world);
    }
    
    public static void InventorySlots(Player player, ConfigurationSection items, String item)
    {
          final int slot = items.getInt(".slot");
    	  ItemStack[] inventory = player.getInventory().getContents();
          String WorldChanged = ((List<?>)items.getStringList(".itemflags")).toString();
          Boolean FirstJoinMode = ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled");
    	  ItemStack toSet = ItemJoin.pl.items.get(player.getWorld().getName() + "." + player.getName().toString() + ".items." + item);
    	  if (toSet != null) {
   		      if (slot >= 0 && slot <= 35 && inventory[slot] != null && !CheckItem.isSimilar(inventory[slot], toSet, items, player)) {
   		    	if (FirstJoinMode == true && WorldChanged.contains("first-join")) {
   		         player.getInventory().setItem(slot, toSet);
   		    	 }
   		        } else if (slot >= 0 && slot <= 35 && inventory[slot] == null && !CheckItem.ContainsItems(player, toSet, items)) {
   		        	if (FirstJoinMode == true && WorldChanged.contains("first-join")) {
				     player.getInventory().setItem(slot, toSet);
   		          }
   		        }
   	         }
    }
    
    public static void CustomSlots(Player player, ConfigurationSection items, String item)
    {
          final String slot = items.getString(".slot");
          Boolean FirstJoinMode = ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled");
          EntityEquipment Equip = player.getEquipment();
          String WorldChanged = ((List<?>)items.getStringList(".itemflags")).toString();
    	  ItemStack toSet = ItemJoin.pl.items.get(player.getWorld().getName() + "." + player.getName().toString() + ".items." + item);
    	  if (toSet != null) {
   		      if (slot.equalsIgnoreCase("Arbitrary")) {
  		    	   if (!CheckItem.ContainsItems(player, toSet, items)) {
   		    	    if (FirstJoinMode != true 
   		    			|| !WorldChanged.contains("first-join")) {
   		    	    	if (player.getInventory().firstEmpty() == -1) {
   		    	    		failCount = failCount + 1;
   		    	       } else {
   		    	    	player.getInventory().addItem(toSet);
   		    	    }
   		          }
   		    	}
   		      }
   		      if (slot.equalsIgnoreCase("Helmet") 
   		    		  && Equip.getHelmet() != null 
   		    		  && !CheckItem.isSimilar(Equip.getHelmet(), toSet, items, player)) {
   		    	if (FirstJoinMode == true 
   		    			&& WorldChanged.contains("first-join")) {
   		    		Equip.setHelmet(toSet);
   		    	 }
   		        } else if (slot.equalsIgnoreCase("Helmet") 
   		        		&& Equip.getHelmet() == null && !CheckItem.ContainsItems(player, toSet, items)) {
   		        	if (FirstJoinMode == true 
   		        			&& WorldChanged.contains("first-join")) {
   		        		Equip.setHelmet(toSet);
   		          }
   		        }
   		      if (slot.equalsIgnoreCase("Chestplate") 
   		    		  && Equip.getChestplate() != null 
   		    		  && !CheckItem.isSimilar(Equip.getChestplate(), toSet, items, player)) {
   		    	if (FirstJoinMode == true 
   		    			&& WorldChanged.contains("first-join")) {
   		    		Equip.setChestplate(toSet);
   		    	 }
   		        } else if (slot.equalsIgnoreCase("Chestplate") 
   		        		&& Equip.getChestplate() == null && !CheckItem.ContainsItems(player, toSet, items)) {
   		        	if (FirstJoinMode == true 
   		        			&& WorldChanged.contains("first-join")) {
   		        		Equip.setChestplate(toSet);
   		          }
   		        }
   		      if (slot.equalsIgnoreCase("Leggings") 
   		    		  && Equip.getLeggings() != null 
   		    		  && !CheckItem.isSimilar(Equip.getLeggings(), toSet, items, player)) {
   		    	if (FirstJoinMode == true 
   		    			&& WorldChanged.contains("first-join")) {
   		    		Equip.setLeggings(toSet);
   		    	 }
   		        } else if (slot.equalsIgnoreCase("Leggings")
   		        		&& Equip.getHelmet() == null && !CheckItem.ContainsItems(player, toSet, items)) {
   		        	if (FirstJoinMode == true 
   		        			&& WorldChanged.contains("first-join")) {
   		        		Equip.setLeggings(toSet);
   		          }
   		        }
   		      if (slot.equalsIgnoreCase("Boots") 
   		    		  && Equip.getBoots() != null 
   		    		  && !CheckItem.isSimilar(Equip.getBoots(), toSet, items, player)) {
   		    	if (FirstJoinMode == true 
   		    			&& WorldChanged.contains("first-join")) {
   		    		Equip.setBoots(toSet);
   		    	 }
   		        } else if (slot.equalsIgnoreCase("Boots") 
   		        		&& Equip.getBoots() == null && !CheckItem.ContainsItems(player, toSet, items)) {
   		        	if (FirstJoinMode == true 
   		        			&& WorldChanged.contains("first-join")) {
   		        		Equip.setBoots(toSet);
   		          }
   		        }
   		       if (Registers.hasCombatUpdate() && slot.equalsIgnoreCase("Offhand") 
   		    		  && player.getInventory().getItemInOffHand() != null 
   		    		  && !CheckItem.isSimilar(player.getInventory().getItemInOffHand(), toSet, items, player)) {
   		    	if (FirstJoinMode == true 
   		    			&& WorldChanged.contains("first-join")) {
   		    		player.getInventory().setItemInOffHand(toSet);
   		    	 }
   		        } else if (Registers.hasCombatUpdate() && slot.equalsIgnoreCase("Offhand") 
   		        		&& player.getInventory().getItemInOffHand() == null && !CheckItem.ContainsItems(player, toSet, items)) {
   		        	if (FirstJoinMode == true 
   		        			&& WorldChanged.contains("first-join")) {
   		        		player.getInventory().setItemInOffHand(toSet);
   		          }
   		       }
   	      }
    }
	
    public static void setWorldChangedItems(Player player)
    {
        ConfigurationSection selection = ItemJoin.getSpecialConfig("items.yml").getConfigurationSection(player.getWorld().getName() + ".items");
        final String world = WorldHandler.getWorld(player.getWorld().getName());
        for (String item : selection.getKeys(false)) 
        {
      	  ConfigurationSection items = selection.getConfigurationSection(item);
          String WorldChanged = ((List<?>)items.getStringList(".itemflags")).toString();
		   if (WorldChanged.contains("world-changed")) {
          final int slot = items.getInt(".slot");
    	  ItemStack[] inventory = player.getInventory().getContents();
          Boolean FirstJoinMode = ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled");
    	  ItemStack toSet = ItemJoin.pl.items.get(player.getWorld().getName() + "." + player.getName().toString() + ".items." + item);
         if (WorldHandler.isWorld(world)) {
       	  if (player.hasPermission(PermissionsHandler.customPermissions(items, item, world)) || player.hasPermission("itemjoin." + world + ".*") || player.hasPermission("itemjoin.*")) {
   	        if (toSet != null) {
   		      if (slot >= 0 && slot <= 35 && inventory[slot] != null && !CheckItem.isSimilar(inventory[slot], toSet, items, player)) {
   		    	if (FirstJoinMode == true && WorldChanged.contains("first-join")) {
   		         player.getInventory().setItem(slot, toSet);
   		    	 }
   		        } else if (slot >= 0 && slot <= 35 && inventory[slot] == null) {
   		        	if (FirstJoinMode == true && WorldChanged.contains("first-join")) {
				     player.getInventory().setItem(slot, toSet);
   		          }
   	         }
       	   }
         }
		 }
         }
        }
        setFirstJoin(player, world);
    }
}