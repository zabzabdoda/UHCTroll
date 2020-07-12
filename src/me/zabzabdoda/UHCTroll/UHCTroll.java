package me.zabzabdoda.UHCTroll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.entity.Vindicator;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class UHCTroll extends JavaPlugin implements Listener {

	private ArrayList<Creeper> fakeCreepers;
	private HashMap<Player, Integer> playerPoints;
	private HashMap<Player, Integer> creeperPoints;
	private HashMap<Player, Integer> fakePlayerPoints;
	private HashMap<Player, Integer> signPoints;
	private HashMap<Player, Boolean> pointStatus;
	private static final int TROLL_INVENTORY_SIZE = 9;
	private Inventory trollMenu, mainMenu, soundMenu, effectsMenu, spawnsMenu, spectatorInv;

	public void onEnable() {
		System.out.println("[UHCTroll] Starting UHCTroll Plugin...");
		Bukkit.getServer().getPluginManager().registerEvents((Listener) this, (Plugin) this);
		playerPoints = new HashMap<Player, Integer>();
		creeperPoints = new HashMap<Player, Integer>();
		fakeCreepers = new ArrayList<Creeper>();
		fakePlayerPoints = new HashMap<Player, Integer>();
		pointStatus = new HashMap<Player, Boolean>();
		signPoints = new HashMap<Player, Integer>();
		TrollItems.initializeMenuItems();
		TrollItems.initializeSoundItems();
		TrollItems.initializeEffectItems();
		TrollItems.initializeSpawnItems();
		givePoints();
	}

	public void onDisable() {
		System.out.println("[UHCTroll] Ending UHCTroll Plugin...");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("troll")) {
				if (args.length == 1) {
					if (player.getGameMode().equals(GameMode.SPECTATOR)) {
						Player gettingTrolled = Bukkit.getPlayer(args[0]);
						if (gettingTrolled != null && gettingTrolled.getGameMode() != GameMode.SPECTATOR
								&& gettingTrolled.getGameMode() != GameMode.CREATIVE) {
							openSubTrollMenu(gettingTrolled, player);
							return true;
						} else {
							player.sendMessage(ChatColor.RED + "Player does not exist");
							return true;
						}
					}
				}
			} else if (cmd.getName().equalsIgnoreCase("creeper")) {
				if (player.getGameMode().equals(GameMode.SPECTATOR)) {
					if (args.length == 1) {
						Player gettingTrolled = Bukkit.getPlayer(args[0]);
						if (gettingTrolled != null) {
							if (creeperPoints.containsKey(player) && creeperPoints.get(player) > 0) {
								// spawn creeper
								Creeper c = (Creeper) player.getWorld().spawnEntity(player.getLocation(),
										EntityType.CREEPER);
								c.setTarget(gettingTrolled);
								c.setNoDamageTicks(0);
								fakeCreepers.add(c);
								creeperPoints.put(player, creeperPoints.get(player) - 1);
								player.sendMessage(ChatColor.GREEN + "Spawned a creeper targeted at "
										+ gettingTrolled.getDisplayName());
								return true;
							} else {
								// not enough points
								player.sendMessage(ChatColor.RED
										+ "You don't have any creeper points, buy one in the /troll menu");
								return true;
							}
						} else {
							// player null
							player.sendMessage(ChatColor.RED + "Player does not exist");
							return true;
						}
					} else if (args.length == 0) {
						if (creeperPoints.containsKey(player) && creeperPoints.get(player) > 0) {
							// spawn creeper
							Creeper c = (Creeper) player.getWorld().spawnEntity(player.getLocation(),
									EntityType.CREEPER);
							c.setNoDamageTicks(0);
							fakeCreepers.add(c);
							creeperPoints.put(player, creeperPoints.get(player) - 1);
							player.sendMessage(ChatColor.GREEN + "Spawned a creeper");
							return true;
						} else {
							// not enough points
							player.sendMessage(
									ChatColor.RED + "You don't have any creeper points, buy one in the /troll menu");
							return true;
						}
					}
				}
			} else if (cmd.getName().equalsIgnoreCase("fakeplayer")) {
				if (player.getGameMode().equals(GameMode.SPECTATOR)) {
					if (args.length == 1) {
						Player gettingTrolled = Bukkit.getPlayer(args[0]);
						if (gettingTrolled != null) {
							if (fakePlayerPoints.containsKey(player) && fakePlayerPoints.get(player) > 0) {
								// spawn creeper
								Zombie z = (Zombie) player.getWorld().spawnEntity(player.getLocation(),
										EntityType.ZOMBIE);
								z.setTarget(gettingTrolled);
								z.setRemoveWhenFarAway(false);
								z.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
								z.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
								z.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
								z.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
								z.setCustomName(gettingTrolled.getDisplayName());
								z.setCustomNameVisible(true);
								// fakePlayers.add(c);
								fakePlayerPoints.put(player, creeperPoints.get(player) - 1);
								player.sendMessage(ChatColor.GREEN + "Spawned a creeper targeted at "
										+ gettingTrolled.getDisplayName());
								return true;
							} else {
								// not enough points
								player.sendMessage(ChatColor.RED
										+ "You don't have any creeper points, buy one in the /troll menu");
								return true;
							}
						} else {
							// player null
							player.sendMessage(ChatColor.RED + "Player does not exist");
							return true;
						}
					}
				}
			} else if (cmd.getName().equalsIgnoreCase("sign")) {
				if (player.getGameMode().equals(GameMode.SPECTATOR)) {
					if (args.length >= 3) {
						if (signPoints.containsKey(player) && signPoints.get(player) > 0) {
							signPoints.put(player, signPoints.get(player) - 1);
							Location loc = player.getLocation();
							loc.getBlock().setType(Material.OAK_SIGN);
							Sign s = (Sign) loc.getBlock().getState();
							String total = "";
							for (String str : args) {
								total += str + " ";
							}
							total = total.trim();
							System.out.println(total);
							System.out.println(total.length());
							if (total.length() <= 60 && total.length() > 45) {
								// 4
								System.out.println("4");
								s.setLine(0, total.substring(0, 16));
								s.setLine(1, total.substring(16, 31));
								s.setLine(2, total.substring(31, 46));
								s.setLine(3, total.substring(46, total.length()));

							} else if (total.length() <= 45 && total.length() > 30) {
								// 3
								System.out.println("3");
								s.setLine(0, total.substring(0, 16));
								s.setLine(1, total.substring(16, 31));
								s.setLine(2, total.substring(31, total.length()));

							} else if (total.length() <= 30 && total.length() > 15) {
								// 2
								System.out.println("2");
								s.setLine(0, total.substring(0, 16));
								s.setLine(1, total.substring(16, total.length()));

							} else if (total.length() <= 15 && total.length() > 0) {
								// 1
								System.out.println("1");
								s.setLine(0, total);

							} else {
								player.sendMessage(ChatColor.RED+"String too long for the sign, max length is 60 characters");
							}

							s.update();
							return true;
						} else {
							// not enough points
							player.sendMessage(
									ChatColor.RED + "You don't have any Sign Points, buy one in the /troll menu");
							return true;
						}
					}
				}
			} else if (cmd.getName().equalsIgnoreCase("points")) {
				if (args.length == 0) {
					Integer num = playerPoints.get(player);
					if (num == null) {
						num = new Integer(0);
					}
					player.sendMessage(ChatColor.GREEN + "You have " + ChatColor.DARK_GREEN + playerPoints.get(player)
							+ ChatColor.GREEN + " points");
					return true;
				} else if (args.length == 1) {
					if (args[0].equalsIgnoreCase("off")) {
						pointStatus.put(player, false);
						player.sendMessage(ChatColor.GREEN + "Messages disabled, do " + ChatColor.DARK_GREEN
								+ "/points on" + ChatColor.GREEN + " to enable");
					} else if (args[0].equalsIgnoreCase("on")) {
						pointStatus.put(player, true);
						player.sendMessage(ChatColor.GREEN + "Messages enabled, do " + ChatColor.DARK_GREEN
								+ "/points off" + ChatColor.GREEN + " to disable");
					} else {
						if(player.isOp()) {
							playerPoints.put(player, Integer.parseInt(args[0]));
						}
					}

					return true;
				}
			}
		}
		return false;
	}

	public void givePoints() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					if (player.getGameMode() == GameMode.SPECTATOR) {
						if (playerPoints.get(player) != null) {
							playerPoints.put(player, playerPoints.get(player) + 1);
							if (pointStatus.get(player) == true) {
								player.sendMessage(ChatColor.GREEN + "You now have " + ChatColor.DARK_GREEN
										+ playerPoints.get(player) + ChatColor.GREEN + " points");
							}
						} else {
							playerPoints.put(player, 1);
							if (pointStatus.get(player) == true) {
								player.sendMessage(ChatColor.GREEN + "You now have " + ChatColor.DARK_GREEN
										+ playerPoints.get(player) + ChatColor.GREEN + " point");
								player.sendMessage(ChatColor.GREEN + "Type " + ChatColor.DARK_GREEN + "/points off"
										+ ChatColor.GREEN + " to disable this message");
							}
						}
					}
				}
			}
		}.runTaskTimer(this, 0, 1200);
	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		if (pointStatus.get(e.getPlayer()) == null) {
			pointStatus.put(e.getPlayer(), true);
		}
		if (playerPoints.get(e.getPlayer()) == null) {
			playerPoints.put(e.getPlayer(), 0);
		}

	}

	@EventHandler
	public void creeperBlowUp(EntityExplodeEvent e) {
		if (e.getEntity() instanceof Creeper) {
			if (fakeCreepers.contains(e.getEntity())) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void entitydamageentity(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Creeper) {
			if (fakeCreepers.contains(e.getDamager())) {
				// fakeCreepers.remove(e.getEntity());
				e.setDamage(0);
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void playerInteractEntity(PlayerInteractEntityEvent e) {
		if (e.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
			if (e.getRightClicked() instanceof Player) {
				Player clicked = (Player) e.getRightClicked();
				openMainMenu(clicked, e.getPlayer());
			}
		}
	}

	/*
	 * public void mainMenuOpen(Player player) { mainMenu =
	 * Bukkit.getServer().createInventory(player, 9); mainMenu.setItem(6,
	 * TrollItems.get(1, TrollItems.menuItems)); mainMenu.setItem(4,
	 * TrollItems.get(0, TrollItems.menuItems)); player.openInventory(mainMenu); }
	 */

	@EventHandler
	public void inventoryInteract(InventoryClickEvent e) {
		if (e.getInventory().equals(spectatorInv)) {
			Player player = (Player) e.getWhoClicked();
			if (e.getCurrentItem() != null) {
				if (e.getCurrentItem().equals(TrollItems.back)) {
					openMainMenu((Player) e.getInventory().getHolder(), player);
				}
			}
		} else if (e.getInventory().equals(trollMenu)) {
			Player player = (Player) e.getInventory().getHolder();
			if (e.getCurrentItem() != null) {
				if (e.getCurrentItem().equals(TrollItems.get(0, TrollItems.trollSubMenu))) { // Give 4 Arrows
					openSoundMenu(player, (Player) e.getWhoClicked());
				} else if (e.getCurrentItem().equals(TrollItems.get(1, TrollItems.trollSubMenu))) { // Grow Tree
					openEffectMenu(player, (Player) e.getWhoClicked());
				} else if (e.getCurrentItem().equals(TrollItems.get(2, TrollItems.trollSubMenu))) { // Spawn dog
					openSpawnMenu(player, (Player) e.getWhoClicked());
				} else if (e.getCurrentItem().equals(TrollItems.back)) {
					openMainMenu(player, (Player) e.getWhoClicked());
				}
			}
			e.setCancelled(true);
		} else if (e.getInventory().equals(mainMenu)) {
			Player player = (Player) e.getInventory().getHolder();
			if (e.getCurrentItem() != null) {
				if (e.getCurrentItem().equals(TrollItems.get(1, TrollItems.menuItems))) {
					openSubTrollMenu(player, (Player) e.getWhoClicked());
				} else if (e.getCurrentItem().equals(TrollItems.get(0, TrollItems.menuItems))) {
					openPlayersInventory((Player) e.getWhoClicked(), player);
				} else if (e.getCurrentItem().equals(TrollItems.back)) {
					e.getWhoClicked().closeInventory();
				}
			}
			e.setCancelled(true);

		} else if (e.getInventory().equals(soundMenu)) {
			if (e.getCurrentItem() != null) {
				if (e.getSlot() == 0) {
					// zombie

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						playSound(Sound.ENTITY_ZOMBIE_AMBIENT, e.getWhoClicked().getLocation(), 5);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 1) {
					// witch

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						playSound(Sound.ENTITY_WITCH_AMBIENT, e.getWhoClicked().getLocation(), 5);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 2) {
					// enderman

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						playSound(Sound.ENTITY_ENDERMAN_STARE, e.getWhoClicked().getLocation(), 5);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 3) {
					// villager

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						playSound(Sound.ENTITY_VILLAGER_AMBIENT, e.getWhoClicked().getLocation(), 5);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 4) {
					// creeper

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						playSound(Sound.ENTITY_CREEPER_PRIMED, e.getWhoClicked().getLocation(), 5);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 5) {
					// sheep

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						playSound(Sound.ENTITY_SHEEP_AMBIENT, e.getWhoClicked().getLocation(), 5);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 6) {
					// chicken

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						playSound(Sound.ENTITY_CHICKEN_AMBIENT, e.getWhoClicked().getLocation(), 5);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 7) {
					// spider

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						playSound(Sound.ENTITY_SPIDER_AMBIENT, e.getWhoClicked().getLocation(), 5);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 8) {
					// pig

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						playSound(Sound.ENTITY_PIG_AMBIENT, e.getWhoClicked().getLocation(), 5);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 9) {
					// cow

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						playSound(Sound.ENTITY_COW_AMBIENT, e.getWhoClicked().getLocation(), 5);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 10) {
					// zombie-pigman

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						playSound(Sound.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, e.getWhoClicked().getLocation(), 5);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 11) {
					// bat

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						playSound(Sound.ENTITY_BAT_AMBIENT, e.getWhoClicked().getLocation(), 5);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 12) {
					// fox

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						playSound(Sound.ENTITY_FOX_SCREECH, e.getWhoClicked().getLocation(), 5);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 13) {
					// phantom

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						playSound(Sound.ENTITY_PHANTOM_SWOOP, e.getWhoClicked().getLocation(), 5);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 14) {
					// silverfish

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						playSound(Sound.ENTITY_SILVERFISH_AMBIENT, e.getWhoClicked().getLocation(), 5);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 15) {
					// skeleton

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						playSound(Sound.ENTITY_SKELETON_AMBIENT, e.getWhoClicked().getLocation(), 5);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 16) {
					// skeleton

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						playSound(Sound.ENTITY_WITHER_SPAWN, new Location(e.getWhoClicked().getWorld(), 0, 100, 0),
								1000);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getCurrentItem().equals(TrollItems.back)) {
					openSubTrollMenu((Player) e.getInventory().getHolder(), (Player) e.getWhoClicked());
				}
			}
		} else if (e.getInventory().equals(effectsMenu)) {
			Player player = (Player) e.getInventory().getHolder();
			if (e.getCurrentItem() != null) {
				if (e.getSlot() == 0) {
					// health

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						trollHealth(player);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 1) {
					// glowing

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						trollGlowing(player);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 2) {
					// weakness

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						trollWeakness(player);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 3) {
					// hunger

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						trollHunger(player);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 4) {
					// levitate

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						trollLevitate(player);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 5) {
					// mining fatigue

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						trollMiningFatigue(player);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 6) {

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						trollSaturation(player);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getCurrentItem().equals(TrollItems.back)) {
					openSubTrollMenu((Player) e.getInventory().getHolder(), (Player) e.getWhoClicked());
				}
			}
		} else if (e.getInventory().equals(spawnsMenu)) {
			Player player = (Player) e.getInventory().getHolder();
			if (e.getCurrentItem() != null) {
				if (e.getSlot() == 0) {
					// arrows
					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						trollArrows(player);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 1) {
					// grow

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						trollGrow(player, (Player) e.getWhoClicked());
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 2) {
					// spawn dog

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						trollSpawnDog(player);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 3) {
					// creeper

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						trollCreeper((Player) e.getWhoClicked());
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 4) {
					// water

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						trollWater(player);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 5) {
					// raid

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						trollRaid((Player) e.getWhoClicked());
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 6) {
					// firerworks

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						trollFirework(player);
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 7) {
					// fake player

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						trollFakePlayer((Player) e.getWhoClicked());
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getSlot() == 8) {
					// sign

					if (playerPoints.get((Player) e.getWhoClicked()) >= e.getCurrentItem().getAmount()) {
						trollSign(player, (Player) e.getWhoClicked());
						purchase(e.getCurrentItem().getAmount(), (Player) e.getWhoClicked());
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points");
					}
				} else if (e.getCurrentItem().equals(TrollItems.back)) {
					openSubTrollMenu((Player) e.getInventory().getHolder(), (Player) e.getWhoClicked());
				}
			}
		}
	}

	public void purchase(int price, Player player) {
		playerPoints.put(player, playerPoints.get(player) - price);
	}

	public void trollRaid(Player trolling) {
		World w = trolling.getWorld();
		Location loc = new Location(w, 0, 150, 0);
		PotionEffect pot = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 255);
		Pillager p1 = (Pillager) w.spawnEntity(loc, EntityType.PILLAGER);
		Pillager p2 = (Pillager) w.spawnEntity(loc, EntityType.PILLAGER);
		Pillager p3 = (Pillager) w.spawnEntity(loc, EntityType.PILLAGER);
		Ravager rav = (Ravager) w.spawnEntity(loc, EntityType.RAVAGER);
		Vindicator vin = (Vindicator) w.spawnEntity(loc, EntityType.VINDICATOR);
		p1.setCustomName(trolling.getDisplayName() + "'s Raid");
		p2.setCustomName(trolling.getDisplayName() + "'s Raid");
		p3.setCustomName(trolling.getDisplayName() + "'s Raid");
		rav.setCustomName(trolling.getDisplayName() + "'s Raid");
		vin.setCustomName(trolling.getDisplayName() + "'s Raid");
		p1.addPotionEffect(pot);
		p2.addPotionEffect(pot);
		p3.addPotionEffect(pot);
		rav.addPotionEffect(pot);
		rav.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).addModifier(
				new AttributeModifier("GENERIC_ATTACK_DAMAGE", -8, AttributeModifier.Operation.ADD_NUMBER));
		vin.addPotionEffect(pot);
	}

	public void trollFakePlayer(Player troll) {
		Integer num = fakePlayerPoints.get(troll);
		if (num != null) {
			num++;
		} else {
			num = new Integer(1);
		}
		fakePlayerPoints.put(troll, num);
		troll.sendMessage(ChatColor.GREEN + "You now have " + ChatColor.DARK_GREEN + fakePlayerPoints.get(troll)
				+ ChatColor.GREEN + " Fake Player Spawns");
		troll.sendMessage(ChatColor.GREEN + "Type" + ChatColor.BLUE + " /FakePlayer <player>" + ChatColor.GREEN
				+ " to spawn a Fake Player at your location");
		troll.sendMessage(ChatColor.GREEN + "with the name of " + ChatColor.BLUE + " <player>");
	}

	public void trollSign(Player trolled, Player trolling) {
		Integer num = signPoints.get(trolling);
		if (num != null) {
			num++;
		} else {
			num = new Integer(1);
		}
		signPoints.put(trolling, num);
		trolling.sendMessage(ChatColor.GREEN + "You now have " + ChatColor.DARK_GREEN + signPoints.get(trolling)
				+ ChatColor.GREEN + " Sign Spawns");
		trolling.sendMessage(ChatColor.GREEN + "Type" + ChatColor.BLUE + " /sign <String> <String> <String>"
				+ ChatColor.GREEN + " to spawn a Sign at your location");
		trolling.sendMessage(ChatColor.GREEN + "with the the lines " + ChatColor.BLUE + " <String>");
	}

	public void trollFirework(Player trolled) {
		Firework fw = (Firework) trolled.getWorld().spawnEntity(trolled.getLocation(), EntityType.FIREWORK);
		FireworkMeta fm = fw.getFireworkMeta();
		// fm.setPower(3);
		FireworkEffect effect = FireworkEffect.builder().withColor(Color.GREEN).build();
		fm.addEffect(effect);
		fw.setFireworkMeta(fm);
	}

	public void playSound(Sound sound, Location location, float volume) {
		location.getWorld().playSound(location, sound, volume, 1f);
	}

	public void trollArrows(Player trolled) {
		if (trolled.getInventory().firstEmpty() != -1) {
			trolled.getInventory().addItem(new ItemStack(Material.ARROW, 4));
		} else {
			trolled.getWorld().dropItem(trolled.getLocation(), new ItemStack(Material.ARROW, 4));
		}
	}

	public void trollGrow(Player trolled, Player troll) {
		String[] block = findNearSappling(trolled);
		if (block != null) {
			int x = Integer.parseInt(block[0]);
			int y = Integer.parseInt(block[1]);
			int z = Integer.parseInt(block[2]);
			TreeType t = TreeType.valueOf(block[3]);
			trolled.getWorld().getBlockAt(new Location(trolled.getWorld(), x, y, z))
					.breakNaturally(new ItemStack(Material.AIR));
			if (trolled.getWorld().generateTree(new Location(trolled.getWorld(), x, y, z), t)) {
				troll.sendMessage(ChatColor.GREEN + "Tree has been grown for " + trolled.getCustomName());
			} else {
				troll.sendMessage(ChatColor.RED + "Tree growth failed for a minecraft reason, ");
				troll.sendMessage(ChatColor.RED + "make sure that there is enough room around the sapling or mushroom");
			}
		} else {
			troll.sendMessage(ChatColor.RED + "No Sapling found near by");
		}
	}

	public void trollSpawnDog(Player trolled) {
		trolled.getWorld().spawnEntity(trolled.getLocation(), EntityType.WOLF);
	}

	public void trollHealth(Player trolled) {
		trolled.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 0));
	}

	public void trollGlowing(Player trolled) {
		trolled.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 0));
	}

	public void trollLevitate(Player trolled) {
		trolled.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 0));
	}

	public void trollHunger(Player trolled) {
		trolled.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 200, 1));
	}

	public void trollSaturation(Player trolled) {
		trolled.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 200, 1));
	}

	public void trollMiningFatigue(Player trolled) {
		trolled.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 200, 1));
	}

	public void trollCreeper(Player troll) {
		Integer num = creeperPoints.get(troll);
		if (num != null) {
			num++;
		} else {
			num = 1;
		}
		creeperPoints.put(troll, num);
		troll.sendMessage(ChatColor.GREEN + "You now have " + ChatColor.DARK_GREEN + creeperPoints.get(troll)
				+ ChatColor.GREEN + " Creeper Spawns");
		troll.sendMessage(ChatColor.GREEN + "Type" + ChatColor.BLUE + " /creeper <player> " + ChatColor.GREEN
				+ " to spawn a creeper at your location");
		troll.sendMessage(ChatColor.GREEN + "targeted at the player " + ChatColor.BLUE + " <player>");
		troll.sendMessage(ChatColor.GREEN + "or just " + ChatColor.BLUE + " /creeper " + ChatColor.GREEN
				+ " to spawn it at your location");
	}

	public void trollWater(Player trolled) {
		Location loc = new Location(trolled.getWorld(), trolled.getLocation().getBlockX(),
				trolled.getLocation().getBlockY(), trolled.getLocation().getBlockZ());
		trolled.getWorld().getBlockAt(trolled.getLocation()).setType(Material.WATER);
		new BukkitRunnable() {
			@Override
			public void run() {
				loc.getBlock().setType(Material.AIR);
			}
		}.runTaskLater(this, 40);
	}

	public void trollWeakness(Player trolled) {
		trolled.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1));
	}

	public String[] findNearSappling(Player player) {
		for (int i = -5; i < 5; i++) {
			for (int j = -5; j < 5; j++) {
				for (int k = -5; k < 5; k++) {
					Block b = player.getWorld().getBlockAt(player.getLocation().getBlockX() + i,
							player.getLocation().getBlockY() + j, player.getLocation().getBlockZ() + k);
					if (b.getType().equals(Material.ACACIA_SAPLING)) {
						return new String[] { b.getLocation().getBlockX() + "", b.getLocation().getBlockY() + "",
								b.getLocation().getBlockZ() + "", "ACACIA" };
					} else if (b.getType().equals(Material.BROWN_MUSHROOM)) {
						return new String[] { b.getLocation().getBlockX() + "", b.getLocation().getBlockY() + "",
								b.getLocation().getBlockZ() + "", "BROWN_MUSHROOM" };
					} else if (b.getType().equals(Material.RED_MUSHROOM)) {
						return new String[] { b.getLocation().getBlockX() + "", b.getLocation().getBlockY() + "",
								b.getLocation().getBlockZ() + "", "RED_MUSHROOM" };
					} else if (b.getType().equals(Material.BIRCH_SAPLING)) {
						return new String[] { b.getLocation().getBlockX() + "", b.getLocation().getBlockY() + "",
								b.getLocation().getBlockZ() + "", "BIRCH" };
					} else if (b.getType().equals(Material.DARK_OAK_SAPLING)) {
						return new String[] { b.getLocation().getBlockX() + "", b.getLocation().getBlockY() + "",
								b.getLocation().getBlockZ() + "", "DARK_OAK" };
					} else if (b.getType().equals(Material.JUNGLE_SAPLING)) {
						return new String[] { b.getLocation().getBlockX() + "", b.getLocation().getBlockY() + "",
								b.getLocation().getBlockZ() + "", "JUNGLE" };
					} else if (b.getType().equals(Material.OAK_SAPLING)) {
						return new String[] { b.getLocation().getBlockX() + "", b.getLocation().getBlockY() + "",
								b.getLocation().getBlockZ() + "", "TREE" };
					}
				}
			}
		}
		return null;
	}

	public void openSubTrollMenu(Player trolled, Player trolling) {
		trollMenu = Bukkit.getServer().createInventory(trolled, TROLL_INVENTORY_SIZE, trolled.getDisplayName());
		TrollItems.fillItems(trolled, trollMenu, TrollItems.trollSubMenu);
		trolling.openInventory(trollMenu);
	}

	public void openMainMenu(Player trolled, Player trolling) {
		mainMenu = Bukkit.getServer().createInventory(trolled, 9, trolled.getDisplayName());
		TrollItems.fillItems(trolled, mainMenu, TrollItems.menuItems);
		trolling.openInventory(mainMenu);
	}

	public void openSoundMenu(Player trolled, Player trolling) {
		soundMenu = Bukkit.getServer().createInventory(trolled, 27, trolled.getDisplayName());
		TrollItems.fillItems(trolled, soundMenu, TrollItems.soundItems);
		trolling.openInventory(soundMenu);
	}

	public void openEffectMenu(Player trolled, Player trolling) {
		effectsMenu = Bukkit.getServer().createInventory(trolled, 27, trolled.getDisplayName());
		TrollItems.fillItems(trolled, effectsMenu, TrollItems.effectItems);
		trolling.openInventory(effectsMenu);
	}

	public void openSpawnMenu(Player trolled, Player trolling) {
		spawnsMenu = Bukkit.getServer().createInventory(trolled, 27, trolled.getDisplayName());
		TrollItems.fillItems(trolled, spawnsMenu, TrollItems.spawnItems);
		trolling.openInventory(spawnsMenu);
	}

	public void giveRegen(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 3, 0));
	}

	public void openPlayersInventory(Player player, Player player2) {
		spectatorInv = Bukkit.createInventory(player2, 45, player2.getDisplayName());
		for (int i = 0; i < player2.getInventory().getSize() - 1; i++) {
			if (player2.getInventory().getItem(i) != null) {
				spectatorInv.setItem(i, player2.getInventory().getItem(i));
			} else {
				spectatorInv.setItem(i, new ItemStack(Material.AIR));
			}
		}

		ItemStack healthItem = new ItemStack(Material.POTION, (int) player2.getHealth());
		ItemMeta healthMeta = healthItem.getItemMeta();
		healthMeta.setDisplayName(ChatColor.GREEN + "Health");
		ArrayList<String> activeEffectsList = new ArrayList<String>();
		for (PotionEffect effect : player2.getActivePotionEffects()) {
			activeEffectsList.add(ChatColor.AQUA + effect.getType().getName() + ", " + effect.getDuration() + ", "
					+ effect.getAmplifier());
		}
		healthMeta.setLore(activeEffectsList);
		healthItem.setItemMeta(healthMeta);
		spectatorInv.setItem(40, healthItem);// health

		ItemStack foodItem = new ItemStack(Material.COOKED_BEEF, (int) player2.getFoodLevel());
		ItemMeta foodMeta = foodItem.getItemMeta();
		foodMeta.setDisplayName(ChatColor.GREEN + "Food");
		foodItem.setItemMeta(foodMeta);
		spectatorInv.setItem(41, foodItem);// food
		if (player2.getInventory().getItem(40) != null) {
			spectatorInv.setItem(42, player2.getInventory().getItem(40));// offhand
		}

		ItemStack xp = TrollItems.createItem(Material.EXPERIENCE_BOTTLE,
				ChatColor.DARK_GREEN + "" + player2.getLevel() + " Levels",
				Arrays.asList(ChatColor.GREEN + "Experience Levels"), 1);

		spectatorInv.setItem(43, xp);
		spectatorInv.setItem(44, TrollItems.back);
		player.openInventory(spectatorInv);
	}

}
