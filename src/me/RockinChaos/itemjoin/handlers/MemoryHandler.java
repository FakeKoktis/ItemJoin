package me.RockinChaos.itemjoin.handlers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import me.RockinChaos.itemjoin.utils.Reflection;
import me.RockinChaos.itemjoin.utils.VaultAPI;
import me.RockinChaos.itemjoin.utils.sqlite.SQLData;
import me.RockinChaos.itemjoin.Commands;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.listeners.LimitSwitch;
import me.RockinChaos.itemjoin.giveitems.listeners.PlayerJoin;
import me.RockinChaos.itemjoin.giveitems.listeners.PlayerQuit;
import me.RockinChaos.itemjoin.giveitems.listeners.RegionEnter;
import me.RockinChaos.itemjoin.giveitems.listeners.Respawn;
import me.RockinChaos.itemjoin.giveitems.listeners.WorldSwitch;
import me.RockinChaos.itemjoin.giveitems.utils.ItemDesigner;
import me.RockinChaos.itemjoin.listeners.Consumes;
import me.RockinChaos.itemjoin.listeners.Drops;
import me.RockinChaos.itemjoin.listeners.Interact;
import me.RockinChaos.itemjoin.listeners.InvClickSurvival;
import me.RockinChaos.itemjoin.listeners.InventoryClose;
import me.RockinChaos.itemjoin.listeners.Legacy_Pickups;
import me.RockinChaos.itemjoin.listeners.Legacy_Storable;
import me.RockinChaos.itemjoin.listeners.Pickups;
import me.RockinChaos.itemjoin.listeners.Placement;
import me.RockinChaos.itemjoin.listeners.Recipes;
import me.RockinChaos.itemjoin.listeners.Storable;
import me.RockinChaos.itemjoin.listeners.SwitchHands;

public class MemoryHandler {
	
	private static FileConfiguration configYML;
	
	private static boolean multiverseCore = false;
	private static boolean multiverseInventories = false;
	private static boolean placeHolderAPI = false;
	private static boolean perWorldPlugins = false;
	private static boolean perWorldInventory = false;
	private static boolean betterNick = false;
	private static boolean authMe = false;
	private static boolean myWorlds = false;
	private static boolean xInventories = false;
	private static boolean tokenEnchant = false;
	private static boolean headDatabase = false;
	private static boolean worldGuard = false;
	private static int worldGuardVers = 0;
	private static boolean dataTags = true;
	private static boolean logCommands = true;
	private static boolean logColoration = true;
	private static boolean oldMapMethod = false;
	private static boolean oldMapViewMethod = false;
	private static boolean Debugging = false;
	private static int heldItemSlot = -1;
	private static SQLData sqlData;
	private static UpdateHandler updater;
	private static ItemDesigner itemDesigner;

	public static void generateData() {
		configYML = ConfigHandler.getConfig("config.yml");
		ItemHandler.initializeItemID();
		setHeldItemSlot();
		setDebugging();
		setLoggable();
		setLogColor();
		newSoftDepends();
		newDataTags();
		newSQLData();
		newItemDesigner();
		ConfigHandler.loadClearDelay();
		ConfigHandler.loadDelay();
		ConfigHandler.loadGetItemPermissions();
		ConfigHandler.loadOPCommandPermissions();
		PlayerJoin.setRunCommands();
	}
	
	private static void newSoftDepends() {
		newVault();
		newPlaceholderAPI();
		newMultiverseCore();
		newMultiverseInv();
		newPerWorldPlugins();
		newPerWorldInventory();
		newBetterNick();
		newAuthMe();
		newWorldGuard();
		newMyWorlds();
		newXInventories();
		newTokenEnchant();
		newHeadDatabase();
	}
	
	public static void registerEvents() {
	    ItemJoin.getInstance().getCommand("itemjoin").setExecutor(new Commands());
		ItemJoin.getInstance().getCommand("ij").setExecutor(new Commands());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new PlayerJoin(), ItemJoin.getInstance());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new PlayerQuit(), ItemJoin.getInstance());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new InventoryClose(), ItemJoin.getInstance());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new WorldSwitch(), ItemJoin.getInstance());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new LimitSwitch(), ItemJoin.getInstance());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Respawn(), ItemJoin.getInstance());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new InvClickSurvival(), ItemJoin.getInstance());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Drops(), ItemJoin.getInstance());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Interact(), ItemJoin.getInstance());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Placement(), ItemJoin.getInstance());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Consumes(), ItemJoin.getInstance());
		
		if (!ServerHandler.hasSpecificUpdate("1_8")) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Legacy_Storable(), ItemJoin.getInstance());
		} else { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Storable(), ItemJoin.getInstance()); }
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Recipes(), ItemJoin.getInstance());

		if (ServerHandler.hasSpecificUpdate("1_12") && Reflection.getEventClass("entity.EntityPickupItemEvent") != null) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Pickups(), ItemJoin.getInstance());
		} else { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Legacy_Pickups(), ItemJoin.getInstance()); }

		if (ServerHandler.hasCombatUpdate() && Reflection.getEventClass("player.PlayerSwapHandItemsEvent") != null) {
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new SwitchHands(), ItemJoin.getInstance());
		}

		if (worldGuard == true) { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new RegionEnter(), ItemJoin.getInstance()); }
	}
	
	public static boolean isDataTags() {
		if (ServerHandler.hasSpecificUpdate("1_8")) {
			return dataTags;
		}
		return false;
	}
	
	public static boolean isMultiverseCore() {
		return multiverseCore;
	}
	
	public static boolean isMultiverseInventories() {
		return multiverseInventories;
	}
	
	public static boolean isPlaceholderAPI() {
		return placeHolderAPI;
	}
	
	public static boolean isPerWorldPlugins() {
		return perWorldPlugins;
	}
	
	public static boolean isPerWorldInventory() {
		return perWorldInventory;
	}
	
	public static boolean isBetterNick() {
		return betterNick;
	}
	
	public static boolean isAuthMe() {
		return authMe;
	}
	
	public static boolean isMyWorlds() {
		return myWorlds;
	}
	
	public static boolean isXInventories() {
		return xInventories;
	}
	
	public static boolean isTokenEnchant() {
		return tokenEnchant;
	}
	
	public static boolean isHeadDatabase() {
		return headDatabase;
	}
	
	public static boolean isWorldGuard() {
		return worldGuard;
	}
	
	public static int getWorldGuardVersion() {
		return worldGuardVers;
	}
	
	public static UpdateHandler getUpdater() {
		return updater;
	}
	
	public static boolean isDebugging() {
		return Debugging;
	}
	
	public static boolean isLoggable() {
		return logCommands;
	}
	
	public static boolean isLogColor() {
		return logColoration;
	}
	
	public static int getHeldItemSlot() {
		return heldItemSlot;
	}
	
	public static boolean getMapMethod() {
		return oldMapMethod;
	}
	
	public static boolean getMapViewMethod() {
		return oldMapViewMethod;
	}
	
	public static SQLData getSQLData() {
		return sqlData;
	}
	
	public static ItemDesigner getItemDesigner() {
		return itemDesigner;
	}
	
	public static void setUpdater(UpdateHandler update) {
		updater = update;
	}
	
	public static void setMapMethod(boolean bool) {
		oldMapMethod = bool;
	}
	
	public static void setMapViewMethod(boolean bool) {
		oldMapViewMethod = bool;
	}
	
	public static void newSQLData() {
		sqlData = new SQLData();
	}
	
	public static void newItemDesigner() {
		itemDesigner = new ItemDesigner();
	}
	
	public static void newDataTags() {
		dataTags = configYML.getBoolean("Settings.DataTags");
	}
	
	public static void setLoggable() {
		logCommands = configYML.getBoolean("General.Log-Commands");
	}
	
	public static void setLogColor() {
		logColoration = configYML.getBoolean("General.Log-Coloration");
	}
	
	public static void setDebugging() {
		Debugging = ConfigHandler.getConfig("config.yml").getBoolean("General.Debugging");
	}
	
	public static void setHeldItemSlot() {
		heldItemSlot = ConfigHandler.getConfig("config.yml").getInt("Settings.HeldItem-Slot");
	}
	
	public static void newMyWorlds() {
		if (Bukkit.getServer().getPluginManager().getPlugin("My_Worlds") != null && configYML.getBoolean("softDepend.MyWorlds") == true) {
			ServerHandler.sendConsoleMessage("&aHooked into MyWorlds!");
			myWorlds = true;
		} else if (configYML.getBoolean("softDepend.MyWorlds") == true) {
			ServerHandler.sendConsoleMessage("&4Could not find MyWorlds.");
		}
	}
	
	public static void newXInventories() {
		if (Bukkit.getServer().getPluginManager().getPlugin("xInventories") != null && configYML.getBoolean("softDepend.xInventories") == true) {
			ServerHandler.sendConsoleMessage("&aHooked into xInventories!");
			xInventories = true;
		} else if (configYML.getBoolean("softDepend.xInventories") == true) {
			ServerHandler.sendConsoleMessage("&4Could not find xInventories.");
		}
	}
	
	public static void newTokenEnchant() {
		if (Bukkit.getServer().getPluginManager().getPlugin("TokenEnchant") != null && configYML.getBoolean("softDepend.TokenEnchant") == true) {
			ServerHandler.sendConsoleMessage("&aHooked into TokenEnchant!");
			tokenEnchant = true;
		} else if (configYML.getBoolean("softDepend.TokenEnchant") == true) {
			ServerHandler.sendConsoleMessage("&4Could not find TokenEnchant.");
		}
	}
	
	public static void newHeadDatabase() {
		if (Bukkit.getServer().getPluginManager().getPlugin("HeadDatabase") != null && configYML.getBoolean("softDepend.HeadDatabase") == true) {
			ServerHandler.sendConsoleMessage("&aHooked into HeadDatabase!");
			headDatabase = true;
		} else if (configYML.getBoolean("softDepend.HeadDatabase") == true) {
			ServerHandler.sendConsoleMessage("&4Could not find HeadDatabase.");
		}
	}

	public static void newVault() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null && configYML.getBoolean("softDepend.Vault") == true) {
			ServerHandler.sendConsoleMessage("&aHooked into Vault!");
			VaultAPI.enableEconomy();
			VaultAPI.setVaultStatus(true);
		} else if (configYML.getBoolean("softDepend.Vault") == true) {
			ServerHandler.sendConsoleMessage("&4Could not find Vault or no economy plugin is attached.");
			VaultAPI.setVaultStatus(false);
		}
	}

	public static void newPlaceholderAPI() {
		if (Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null && configYML.getBoolean("softDepend.PlaceholderAPI") == true) {
			ServerHandler.sendConsoleMessage("&aHooked into PlaceholderAPI!");
			placeHolderAPI = true;
		} else if (configYML.getBoolean("softDepend.PlaceholderAPI") == true) {
			ServerHandler.sendConsoleMessage("&4Could not find PlaceholderAPI.");
		}
	}

	public static void newMultiverseCore() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core") != null && configYML.getBoolean("softDepend.Multiverse-Core") == true) {
			ServerHandler.sendConsoleMessage("&aHooked into Multiverse-Core!");
			multiverseCore = true;
		} else if (configYML.getBoolean("softDepend.Multiverse-Core") == true) {
			ServerHandler.sendConsoleMessage("&4Could not find Multiverse-Core.");
		}
	}

	public static void newMultiverseInv() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Inventories") != null && configYML.getBoolean("softDepend.Multiverse-Inventories") == true) {
			ServerHandler.sendConsoleMessage("&aHooked into Multiverse-Inventories!");
			multiverseInventories = true;
		} else if (configYML.getBoolean("softDepend.Multiverse-Inventories") == true) {
			ServerHandler.sendConsoleMessage("&4Could not find Multiverse-Inventories.");
		}
	}

	public static void newPerWorldPlugins() {
		if (Bukkit.getServer().getPluginManager().getPlugin("PerWorldPlugins") != null && configYML.getBoolean("softDepend.PerWorldPlugins") == true) {
			ServerHandler.sendConsoleMessage("&aHooked into PerWorldPlugins!");
			perWorldPlugins = true;
		} else if (configYML.getBoolean("softDepend.PerWorldPlugins") == true) {
			ServerHandler.sendConsoleMessage("&4Could not find PerWorldPlugins.");
		}
	}

	public static void newPerWorldInventory() {
		if (Bukkit.getServer().getPluginManager().getPlugin("PerWorldInventory") != null && configYML.getBoolean("softDepend.PerWorldInventory") == true) {
			ServerHandler.sendConsoleMessage("&aHooked into PerWorldInventory!");
			perWorldInventory = true;
		} else if (configYML.getBoolean("softDepend.PerWorldInventory") == true) {
			ServerHandler.sendConsoleMessage("&4Could not find PerWorldInventory.");
		}
	}

	public static void newAuthMe() {
		if (Bukkit.getServer().getPluginManager().getPlugin("AuthMe") != null && configYML.getBoolean("softDepend.AuthMe") == true) {
			ServerHandler.sendConsoleMessage("&aHooked into AuthMe!");
			authMe = true;
		} else if (configYML.getBoolean("softDepend.AuthMe") == true) {
			ServerHandler.sendConsoleMessage("&4Could not find AuthMe.");
		}
	}
	
	public static void newBetterNick() {
		if (Bukkit.getServer().getPluginManager().getPlugin("BetterNick") != null && configYML.getBoolean("softDepend.BetterNick") == true) {
			ServerHandler.sendConsoleMessage("&aHooked into BetterNick!");
			betterNick = true;
		} else if (configYML.getBoolean("softDepend.BetterNick") == true) {
			ServerHandler.sendConsoleMessage("&4Could not find BetterNick.");
		}
	}
	
	public static void newWorldGuard() {
		if (configYML.getBoolean("softDepend.WorldGuard") == true && Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null && Bukkit.getServer().getPluginManager().getPlugin("WorldEdit") != null) {
			String fetchVersion = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard").getDescription().getVersion();
			try { worldGuardVers = Integer.parseInt(fetchVersion.replace(".", "").substring(0, 3));
			} catch (Exception e) { worldGuardVers = 622; }
			ServerHandler.sendConsoleMessage("&aHooked into WorldGuard!");
			worldGuard = true;
		} else if (configYML.getBoolean("softDepend.WorldGuard") == true && Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null && Bukkit.getServer().getPluginManager().getPlugin("WorldEdit") == null) {
			ServerHandler.sendConsoleMessage("&4Error; Found WorldGuard but WorldEdit is not found!");
			ServerHandler.sendConsoleMessage("&4WorldEdit is required for WorldGuard to function.");
		} else if (configYML.getBoolean("softDepend.WorldGuard") == true && Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") == null) {
			ServerHandler.sendConsoleMessage("&4Could not find WorldGuard.");
		}
	}
}