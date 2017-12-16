package me.mentalrob.ad;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.json.JSONArray;
import org.json.JSONObject;

import net.md_5.bungee.api.ChatColor;

public class Commands implements CommandExecutor{
	private Main plugin;
	public Commands(Main instance){
		this.plugin = instance;
	}
	String helpMessage = "&6----Advertising----\n"+
						 "&2-HOW IT WORKS: \n"+
						 "&2You can buy things with credits\n"+
						 "&2But you need to earn it\n"+
						 "&2You can earn it with this command /adv generate <adname>\n"+
						 "&2This command gives you a link. If somebody click it you earn some credits :)\n"+
						 "&4/adv credit &6 -> &2Shows your balance\n" +
						 "&4/adv buy <toolname> &6 -> &2Buy the tool with your credits\n" +
						 "&4/adv tools &6 -> &2Lists the tools\n"+
						 "&4/adv list &6 -> &2Lists the advertisements\n"+
						 "&4/adv generate <adname> &6 -> &2Generates the advertise link";
	String helpAdminMessage = "&6----Advertising----\n"+
			 "&2-HOW IT WORKS: \n"+
			 "&2You can buy things with credits\n"+
			 "&2But you need to earn it\n"+
			 "&2You can earn it with this command /adv generate <adname>\n"+
			 "&2This command gives you a link. If somebody click it you earn some credits :)\n"+
			 "&4/adv credit &6 -> &2Shows your balance\n" +
			 "&4/adv buy <toolname> &6 -> &2Buy the tool with your credits\n" +
			 "&4/adv tools &6 -> &2Lists the tools\n"+
			 "&4/adv list &6 -> &2Lists the advertisements\n"+
			 "&4/adv generate <adname> &6 -> &2Generates the advertise link\n"+
			 "&4/adv admin setCredit <player> <credit>\n"+
			 "&4/adv admin removeCredit <player> <credit>\n"+
			 "&4/adv admin giveTool <player> <tool>\n"+
			 "&4/adv admin credit <player>\n"+
			 "&4/adv admin creditBal\n"+
			 "&4/adv admin reload";
			 
	
	@Override
	public boolean onCommand(CommandSender sender, Command com, String label, String[] args) {
		if(sender instanceof Player){
			Player player = (Player) sender;
			if(label.equalsIgnoreCase("adv")){
				if(player.hasPermission(plugin.perm1) || player.isOp()){
					if(args.length == 1){
						if(args[0].equals("credit")){
							String str = plugin.getConfig().getString("creditMessage");
							Double d = plugin.creditFunc.getCredit(player.getName());
							String credit = Double.toString(d);
							str = str.replaceAll("%credit%", credit);
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
						}else if(args[0].equals("tools")){
							String s = listTools();
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
						}else if(args[0].equals("list")){
							String s = listAdv();
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
						}else if(args[0].equals("admin")){
							if(player.hasPermission(plugin.admin) || player.isOp()){
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', helpAdminMessage));
							}
						}
						else{
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', helpMessage));
						}
					}else if(args.length == 2){
						if(args[0].equals("buy")){
							String requestedTool = args[1];
							List<String> toolz = Tools();
							if(toolz.contains(requestedTool)){
								String PlayerName = player.getName();
								Double gerekliMiktar = plugin.conf.tools().getDouble("Shops."+requestedTool+".CreditCost");
								Double oyuncuKredisi = plugin.creditFunc.getCredit(PlayerName);
								if(oyuncuKredisi >= gerekliMiktar){
									toolver(player, requestedTool);
								}else{
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("notenoughcredit")));
								}
							}else{
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("toolnf")));
							}
							
						}else if(args[0].equals("generate")){
							String requestedAd = args[1];
							List<String> ads = Ads();
							if(ads.contains(requestedAd)){
								AES aes = new AES();
								String redirectUrl = plugin.conf.Advert().getString("Ads."+requestedAd+".Link");
								Double cost = plugin.conf.Advert().getDouble("Ads."+requestedAd+".Cost");
								String PlayerName = player.getName();
								String enPlayerName = aes.encrypt(plugin.getConfig().getString("key"), plugin.getConfig().getString("key2"), PlayerName);
								String enCost = aes.encrypt(plugin.getConfig().getString("key"), plugin.getConfig().getString("key2"), Double.toString(cost));
								String scriptURL = plugin.getConfig().getString("script");
								redirectUrl = aes.encrypt(plugin.getConfig().getString("key"), plugin.getConfig().getString("key2"), redirectUrl);
								scriptURL = scriptURL + "?u=" + URLEncoder.encode(enPlayerName) + "&url=" + URLEncoder.encode(redirectUrl) + "&cost=" + URLEncoder.encode(enCost);
								long USERID = plugin.getConfig().getLong("USERID");
									String PUBLICAPIKEY = plugin.getConfig().getString("PUBLICAPIKEY");
									String SECRETAPIKEY = plugin.getConfig().getString("SECRETAPIKEY");
									AdflyApiWrapper.PUBLIC_KEY = PUBLICAPIKEY;
									AdflyApiWrapper.SECRET_KEY = SECRETAPIKEY;
									AdflyApiWrapper.USER_ID = USERID;
									generete(scriptURL, player);
								
							}
							else{
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("adnf")));
							}
						}else if(args[0].equals("admin")){
							if(player.hasPermission(plugin.admin) || player.isOp()){
								if(args[1].equals("reload")){
									for(HashMap.Entry<String , Double> entry : plugin.credits.playercredit.entrySet()){
										String oyuncuIsmi = entry.getKey();
										Double kredisi = entry.getValue();
										plugin.conf.playerConf().set("Players."+oyuncuIsmi+".Credits", kredisi);
										try {plugin.conf.savePlayer();} catch (IOException e){e.printStackTrace();}
									}
									for(Entry<String, List<String>> entry : plugin.credits.playerip.entrySet()){
										String oyuncuIsmi = entry.getKey();
										List<String> ip = entry.getValue();
										plugin.conf.playerConf().set("Players."+oyuncuIsmi+".ip", ip);
										try {plugin.conf.savePlayer();} catch (IOException e) {e.printStackTrace();}
									}
									plugin.loadVariables();
									plugin.reloadConfig();
									Bukkit.getServer().getScheduler().cancelTask(plugin.webclient.task1);
									plugin.webclient.Start(plugin.getConfig().getString("credits"), plugin.getConfig().getString("remover"), plugin.getConfig().getString("key"));
									player.sendMessage(ChatColor.GREEN + "Reloaded.");
								}else if(args[1].equalsIgnoreCase("creditBal")){
									String s = "&6---Advertisories---\n";
									for(HashMap.Entry<String , Double> entry : plugin.credits.playercredit.entrySet()){
										String oyuncuIsmi = entry.getKey();
										Double kredisi = entry.getValue();
										s = s + "&2" + oyuncuIsmi + " -> " + kredisi;
									}
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
								}
								else{
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', helpAdminMessage));
								}
							}
						}
					}else if(args.length == 3){
						if(args[0].equalsIgnoreCase("admin")){
							if(player.hasPermission(plugin.perm1) || player.isOp()){
								if(args[1].equalsIgnoreCase("credit")){
									if(!args[2].equals(null)){
										Player p = null;
										for(Player pc : Bukkit.getServer().getOnlinePlayers()){
											if(args[2].equals(pc.getName())){
												p = pc;
												break;
											}
										}
										if(p == null){
											player.sendMessage(ChatColor.RED + "Target player is not found.");
											return false;
										}
										Double d = 0.0;
										if(plugin.credits.playercredit.containsKey(args[2])){
											d = plugin.credits.playercredit.get(args[2]);
										}
										player.sendMessage(ChatColor.GREEN + args[2] + ": "+d);
									}
								}else{
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', helpAdminMessage));
								}
							}
						}else{
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', helpMessage));
						}
					}else if(args.length == 4){
						if(args[0].equalsIgnoreCase("admin")){
							if(player.hasPermission(plugin.admin) || player.isOp()){
								if(args[1].equalsIgnoreCase("setCredit")){
										if(!args[2].equals(null) && !args[3].equals(null)){
								Player p = null;
						for(Player pc : Bukkit.getServer().getOnlinePlayers()){
									if(args[2].equals(pc.getName())){
														p = pc;
												break;
												}
														}
													if(p == null){
																player.sendMessage(ChatColor.RED + "Target player is not found.");
																return false;
														}
															Double d = 0.0;
														try{
																d = Double.parseDouble(args[3]);
															}catch(Exception e){
															player.sendMessage(ChatColor.RED + "Type a double number like 2.0 or 3.5");
															}
															plugin.creditFunc.setCredit(args[2], d);
															player.sendMessage(ChatColor.GREEN + "Credit is setted.");
														}else{
															player.sendMessage(ChatColor.RED + "Please type a player and credit amount(double)");
														}
													}else if(args[1].equalsIgnoreCase("removeCredit")){
														if(!args[2].equals(null) && !args[3].equals(null)){
															Player p = null;
															for(Player pc : Bukkit.getServer().getOnlinePlayers()){
																if(args[2].equals(pc.getName())){
																	p = pc;
																	break;
																}
															}
															if(p == null){
																player.sendMessage(ChatColor.RED + "Target player is not found.");
																return false;
															}
															Double d = 0.0;
															try{
																d = Double.parseDouble(args[3]);
															}catch(Exception e){
																player.sendMessage(ChatColor.RED + "Type a double number like 2.0 or 3.5");
															}
															plugin.creditFunc.removeCredit(args[2], d);
															player.sendMessage(ChatColor.GREEN + "Credit is removed.");
														}else{
															player.sendMessage(ChatColor.RED + "Please type a player and credit amount(double)");
														}
													}else if(args[1].equalsIgnoreCase("giveTool")){
														if(!args[2].equals(null) && !args[3].equals(null)){
															List<String> sitr = Tools();
															Player p = null;
															for(Player pc : Bukkit.getServer().getOnlinePlayers()){
																if(args[2].equals(pc.getName())){
																	p = pc;
																	break;
																}
															}
															if(p == null){
																player.sendMessage(ChatColor.RED + "Target player is not found.");
																return false;
															}
															if(sitr.contains(args[3])){
																toolverx(player , p , args[3]);
																player.sendMessage(ChatColor.GREEN + "Tool is gived");
															}else{
																player.sendMessage(ChatColor.RED + "Tool not found");
															}
														}else{
															player.sendMessage(ChatColor.RED + "Please type a player and credit amount(double)");
														}
													}else{
														player.sendMessage(ChatColor.translateAlternateColorCodes('&', helpAdminMessage));
													}
												}
											}
					}
					else{
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', helpMessage));
					}
				}
			}
		}
		return false;
		}
	public List<String> Ads(){
		List<String> ads = new ArrayList<String>();
		ConfigurationSection sec = plugin.conf.Advert().getConfigurationSection("Ads");
		if(!sec.equals(null)){
			for(String key : sec.getKeys(false)){
				ads.add(key);
			}
		}
		return ads;
	}
	public List<String> Tools(){
		List<String> toolz = new ArrayList<String>();
		ConfigurationSection sec = plugin.conf.tools().getConfigurationSection("Shops");
		if(!sec.equals(null)){
			for(String key : sec.getKeys(false)){
				toolz.add(key);
			}
			
		}
		return toolz;
	}
	public String listTools(){
		String s = "&6---Advertisement---\n";
		ConfigurationSection sec = plugin.conf.tools().getConfigurationSection("Shops");
		if(!sec.equals(null)){
			for(String key : sec.getKeys(false)){
				s = s + "&6" + key + " &2Cost : " + plugin.conf.tools().getDouble("Shops."+key+".CreditCost") + "\n";   
			}
		}
		return s;
	}
	public String listAdv(){
		String s = "&6---Advertisement---\n";
		ConfigurationSection sec = plugin.conf.Advert().getConfigurationSection("Ads");
		if(!sec.equals(null)){
			for(String key : sec.getKeys(false)){
				s = s + "&6" + key + " &2Credit : " + plugin.conf.Advert().getDouble("Ads."+key+".Cost");
			}
		}
		return s;
	}
	public void toolver(Player player , String tool){

		ConfigurationSection sec = plugin.conf.tools().getConfigurationSection("Shops."+tool+".Items");
		List<ItemStack> items = new ArrayList<ItemStack>();
		if(sec != null){
			Integer sz = sec.getKeys(false).size();
			Integer slots = bosSlotlar(player);
			if(sz > slots){
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("inventoryfull")));
				return;
			}
			for(String key : sec.getKeys(false)){
				int Data = plugin.conf.tools().getInt("Shops."+tool+".Items."+key+".Data");
				int Amount = plugin.conf.tools().getInt("Shops."+tool+".Items."+key+".Amount");
				if(Amount == 0) Amount = 1;
				String Name = "x";
				if(!plugin.conf.tools().getString("Shops."+tool+".Items."+key+".Name").equals(null)) Name = plugin.conf.tools().getString("Shops."+tool+".Items."+key+".Name"); 
				Name = ChatColor.translateAlternateColorCodes('&', Name);
				List<String> Enchants = new ArrayList<String>();
				if(!plugin.conf.tools().getStringList("Shops."+tool+".Items."+key+".Enchantments").equals(null)) Enchants = plugin.conf.tools().getStringList("Shops."+tool+".Items."+key+".Enchantments");
				ItemStack item = new ItemStack(Integer.parseInt(key) , Amount , (byte) Data);
				if(!Name.equalsIgnoreCase("x")){
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(Name);
					item.setItemMeta(meta);
				}
				if(!Enchants.isEmpty()){
					for(String ench : Enchants){
						String[] enci = ench.split(":");
						String encha = enci[0];
						String enchalevel = enci[1];
						Enchantment encx = Enchantment.getByName(encha);
						item.addUnsafeEnchantment(encx, Integer.parseInt(enchalevel));
					}
				}	
				player.getInventory().addItem(item);
			}
			Double cost = plugin.conf.tools().getDouble("Shops."+tool+".CreditCost");
			String PlayerName = player.getName();
			plugin.creditFunc.removeCredit(PlayerName, cost);
			List<String> commands = plugin.conf.tools().getStringList("Shops."+tool+".Commands");
			if(!commands.isEmpty()){
				for(String command : commands){
					command = command.replaceAll("%player%", player.getName());
					Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
				}
			}
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("successbuy")));
		}
	}
	public void toolverx(Player admin ,Player player , String tool){

		ConfigurationSection sec = plugin.conf.tools().getConfigurationSection("Shops."+tool+".Items");
		List<ItemStack> items = new ArrayList<ItemStack>();
		if(sec != null){
			Integer sz = sec.getKeys(false).size();
			Integer slots = bosSlotlar(player);
			if(sz > slots){
				admin.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Player has not enough empty slots"));
				return;
			}
			for(String key : sec.getKeys(false)){
				int Data = plugin.conf.tools().getInt("Shops."+tool+".Items."+key+".Data");
				int Amount = plugin.conf.tools().getInt("Shops."+tool+".Items."+key+".Amount");
				if(Amount == 0) Amount = 1;
				String Name = "x";
				if(!plugin.conf.tools().getString("Shops."+tool+".Items."+key+".Name").equals(null)) Name = plugin.conf.tools().getString("Shops."+tool+".Items."+key+".Name"); 
				Name = ChatColor.translateAlternateColorCodes('&', Name);
				List<String> Enchants = new ArrayList<String>();
				if(!plugin.conf.tools().getStringList("Shops."+tool+".Items."+key+".Enchantments").equals(null)) Enchants = plugin.conf.tools().getStringList("Shops."+tool+".Items."+key+".Enchantments");
				ItemStack item = new ItemStack(Integer.parseInt(key) , Amount , (byte) Data);
				if(!Name.equalsIgnoreCase("x")){
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(Name);
					item.setItemMeta(meta);
				}
				if(!Enchants.isEmpty()){
					for(String ench : Enchants){
						String[] enci = ench.split(":");
						String encha = enci[0];
						String enchalevel = enci[1];
						Enchantment encx = Enchantment.getByName(encha);
						item.addUnsafeEnchantment(encx, Integer.parseInt(enchalevel));
					}
				}	
				player.getInventory().addItem(item);
			}
			//Double cost = plugin.conf.tools().getDouble("Shops."+tool+".CreditCost");
			//String PlayerName = player.getName();
			//plugin.creditFunc.removeCredit(PlayerName, cost);
			List<String> commands = plugin.conf.tools().getStringList("Shops."+tool+".Commands");
			if(!commands.isEmpty()){
				for(String command : commands){
					command = command.replaceAll("%player%", player.getName());
					Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
				}
			}
			//player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("successbuy")));
		}
	}
	private static String formatJson(String jsonStr) {
		String jsonString = jsonStr;
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray newJSON = jsonObject.getJSONArray("data");
        JSONObject object = newJSON.getJSONObject(0);
        
       return object.getString("short_url");
	}
	public void generete(String scriptURL,Player player){
		final Player fplayer = player;
		final String fscriptURL = scriptURL;
		new BukkitRunnable() {
			
			@Override
			public void run() {
				try {
					String cscriptURL = fscriptURL;
					AdflyApiWrapper adfly = new AdflyApiWrapper();
					String s = adfly.shorten(cscriptURL);
					//fplayer.sendMessage(s);
					s = formatJson(s);
					String msg = plugin.getConfig().getString("adlinkmsg");
					msg = msg.replaceAll("%link%", s);
					fplayer.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
					this.cancel();
				} catch (Exception e) {e.printStackTrace();}
				this.cancel();
			}
		}.runTaskTimer(plugin, 20, 1);
		
	}
	public int bosSlotlar(Player player){
		int i = 0;
		for(ItemStack is : player.getInventory().getContents()){
			if(is == null){
				i++;
			}
		}
		return i;
	}

}
