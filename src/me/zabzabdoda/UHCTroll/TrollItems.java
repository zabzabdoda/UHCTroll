package me.zabzabdoda.UHCTroll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TrollItems {
	
	public static ArrayList<ItemStack> menuItems,trollSubMenu,soundItems,effectItems,spawnItems;
	public static ItemStack back = createItem(Material.BARRIER,ChatColor.RED+"Back",Arrays.asList(ChatColor.GREEN+"Go back a menu"),1);

	public static ItemStack get(int index, ArrayList<ItemStack> list) {
		return list.get(index);
	}
	
	public static void fillItems(Player player, Inventory menu, ArrayList<ItemStack> list) {
		for(ItemStack is : list) {
			//is.setAmount(is.getAmount()*2);
			menu.addItem(is);
		}
		if(player.hasPermission("troll.expensive") && (list.equals(soundItems) || list.equals(effectItems) || list.equals(spawnItems))) {
			for(int i = 0; i < menu.getSize(); i++) {
				if(menu.getItem(i) != null) {
					ItemStack item = menu.getItem(i).clone();
					item.setAmount(item.getAmount()*2);
					menu.setItem(i,item);
				}
			}
		}
		menu.setItem(menu.getSize()-1,back);
	}
	
	public static void initializeMenuItems() {
		menuItems = new ArrayList<ItemStack>();
		trollSubMenu = new ArrayList<ItemStack>();
		
		menuItems.add(createItem(Material.CHEST,ChatColor.BLUE + "Player Inventory",Arrays.asList(ChatColor.GREEN + "Opens the players inventory"),1));//0
		menuItems.add(createItem(Material.TNT,ChatColor.RED + "Troll",Arrays.asList(ChatColor.GREEN + "Opens the Troll menu for this player"),1));//1
		
		trollSubMenu.add(createItem(Material.SLIME_BLOCK,ChatColor.BLUE + "Sounds",Arrays.asList(ChatColor.GREEN + "Plays a sound at your position"),1));//0
		trollSubMenu.add(createItem(Material.POTION,ChatColor.BLUE + "Effects",Arrays.asList(ChatColor.GREEN + "Gives this player an effect"),1));//1
		trollSubMenu.add(createItem(Material.GHAST_SPAWN_EGG,ChatColor.BLUE + "Spawns",Arrays.asList(ChatColor.GREEN + "Spawns items, mobs, and more"),1));//2

	}
	
	public static void initializeSoundItems() {
		soundItems = new ArrayList<ItemStack>();
		soundItems.add(createSoundItem(Material.ZOMBIE_SPAWN_EGG,"Zombie",1)); //0
		soundItems.add(createSoundItem(Material.WITCH_SPAWN_EGG,"Witch",1));//1
		soundItems.add(createSoundItem(Material.ENDERMAN_SPAWN_EGG,"Enderman",2));//2
		soundItems.add(createSoundItem(Material.VILLAGER_SPAWN_EGG,"Villager",1));//3
		soundItems.add(createSoundItem(Material.CREEPER_SPAWN_EGG,"Creeper",2));//4
		soundItems.add(createSoundItem(Material.SHEEP_SPAWN_EGG,"Sheep",1));//5
		soundItems.add(createSoundItem(Material.CHICKEN_SPAWN_EGG,"Chicken",1));//6
		soundItems.add(createSoundItem(Material.SPIDER_SPAWN_EGG,"Spider",1));//7
		soundItems.add(createSoundItem(Material.PIG_SPAWN_EGG,"Pig",1));//8
		soundItems.add(createSoundItem(Material.COW_SPAWN_EGG,"Cow",1));//9
		soundItems.add(createSoundItem(Material.ZOMBIFIED_PIGLIN_SPAWN_EGG,"Zombie Pigman",2));//10
		soundItems.add(createSoundItem(Material.BAT_SPAWN_EGG,"Bat",1));//11
		soundItems.add(createSoundItem(Material.FOX_SPAWN_EGG,"Fox",2));//12
		soundItems.add(createSoundItem(Material.PHANTOM_SPAWN_EGG,"Phantom",2));//13
		soundItems.add(createSoundItem(Material.SILVERFISH_SPAWN_EGG,"Silverfish",2));//14
		soundItems.add(createSoundItem(Material.SKELETON_SPAWN_EGG,"Skeleton",1));//15
		soundItems.add(createSoundItem(Material.WITHER_SKELETON_SKULL,"Wither",25));//15
	}
	
	public static void initializeEffectItems() {
		effectItems = new ArrayList<ItemStack>();

		effectItems.add(createItem(Material.POTION,ChatColor.BLUE + "Give Health",Arrays.asList(ChatColor.GREEN + "Give this player 1/2 a heart"),10));//0
		effectItems.add(createItem(Material.GLOWSTONE,ChatColor.BLUE + "Glowing",Arrays.asList(ChatColor.GREEN + "adds the glowing effect",ChatColor.GREEN + "to the player for 5 seconds"),10));//1
		effectItems.add(createItem(Material.WOODEN_SWORD,ChatColor.RED + "Weakness",Arrays.asList(ChatColor.GREEN + "Gives the player the weakness",ChatColor.GREEN + "status effect for 10 seconds"),5));//2
		effectItems.add(createItem(Material.ROTTEN_FLESH,ChatColor.RED + "Hunger",Arrays.asList(ChatColor.GREEN + "Give this player hunger for 5 seconds"),5));//3
		effectItems.add(createItem(Material.RABBIT_FOOT,ChatColor.RED + "Levitate",Arrays.asList(ChatColor.GREEN + "Make this player levitate for 2 seconds"),10));//4
		effectItems.add(createItem(Material.GOLDEN_PICKAXE,ChatColor.RED + "Mining Fatigue",Arrays.asList(ChatColor.GREEN + "Give this player mining fatigue for 10 seconds"),15));//5
		effectItems.add(createItem(Material.COOKED_BEEF,ChatColor.BLUE + "Saturation",Arrays.asList(ChatColor.GREEN + "Give this player saturation for 5 seconds"),10));//3

	}
	
	public static void initializeSpawnItems() {
		spawnItems = new ArrayList<ItemStack>();
		
		spawnItems.add(createItem(Material.ARROW,ChatColor.BLUE + "Arrows",Arrays.asList(ChatColor.GREEN + "Give this player 4 arrows."),8));//0
		spawnItems.add(createItem(Material.BONE_MEAL,ChatColor.BLUE + "Grow",Arrays.asList(ChatColor.GREEN + "Grows a tree or mushroom if",ChatColor.GREEN + "a sappling is near by"),15));//1
		spawnItems.add(createItem(Material.WOLF_SPAWN_EGG,ChatColor.BLUE + "Spawn Dog",Arrays.asList(ChatColor.GREEN + "Spawns an untamed dog",ChatColor.GREEN + "near the player"),15));//2
		spawnItems.add(createItem(Material.CREEPER_HEAD,ChatColor.RED + "Creeper",Arrays.asList(ChatColor.GREEN + "spawns a fake creeper that",ChatColor.GREEN + "doesn't harm the player when",ChatColor.GREEN + "it explodes, use /creeper after",ChatColor.GREEN + "buying to spawn it on your location"),10));//3
		spawnItems.add(createItem(Material.WATER_BUCKET,ChatColor.RED + "Water",Arrays.asList(ChatColor.GREEN + "spawns water below the player",ChatColor.GREEN + "picks it up after 2 seconds"),10));//4
		spawnItems.add(createItem(Material.BLACK_BANNER,ChatColor.RED + "Raid",Arrays.asList(ChatColor.GREEN + "Spawn a raid party at 0,0"),30));//5
		spawnItems.add(createItem(Material.FIREWORK_ROCKET,ChatColor.RED + "Firework",Arrays.asList(ChatColor.GREEN + "Spawns a firework at a player",ChatColor.GREEN + "....yes it will explode"),10));//6
		spawnItems.add(createItem(Material.ARMOR_STAND,ChatColor.RED + "Fake Player",Arrays.asList(ChatColor.GREEN + "Spawns a zombie with a players name and armor "),10));//7
		spawnItems.add(createItem(Material.OAK_SIGN,ChatColor.RED + "Sign",Arrays.asList(ChatColor.GREEN + "Leave your mark in the world"),5));//8

	}
	
	private static ItemStack createSoundItem(Material mat, String name, int price) {
		ItemStack item = new ItemStack(mat,price);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(ChatColor.RED + name + " Sound");
		im.setLore(Arrays.asList(ChatColor.GREEN + "Makes a " + name + " sound."));
		item.setItemMeta(im);
		return item;
	}
	
	
	public static ItemStack createItem(Material mat, String name, List<String> list, int price) {
		ItemStack item = new ItemStack(mat,price);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(name);
		im.setLore(list);
		item.setItemMeta(im);
		return item;
	}
	
}
