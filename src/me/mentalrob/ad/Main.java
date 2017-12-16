package me.mentalrob.ad;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin{
	public Permission admin = new Permission("advertising.admin");
	public Permission perm1 = new Permission("advertising.advertise");
	public Commands com = new Commands(this);
	public Config conf = new Config(this);
	public CrediFunc creditFunc = new CrediFunc(this);
	public Credits credits = new Credits();
	public JoinListener jlst = new JoinListener(this);
	public WebClient webclient = new WebClient(this);
	public PluginManager pm;
	@Override
	public void onEnable(){
		pm = getServer().getPluginManager();
		pm.addPermission(admin);
		pm.addPermission(perm1);
		pm.registerEvents(jlst, this);
		ConsoleCommandSender cons = getServer().getConsoleSender();
		getCommand("adv").setExecutor(com);
		cons.sendMessage(ChatColor.GREEN + "[Advertising] Starting...");
		cons.sendMessage(ChatColor.GREEN + "[Advertising] Config is setting up");
		try {conf.load();} catch (IOException e1) {e1.printStackTrace();}
		loadVariables();
		saveDefaultConfig();
		try {conf.load();}catch(IOException e) {cons.sendMessage(ChatColor.RED + "[Advertising] There is an error occured : "+e.getMessage() + " Shutting down");pm.disablePlugin(this);}
		webclient.Start(getConfig().getString("credits"), getConfig().getString("remover"), getConfig().getString("key"));
	}
	public void onDisable(){
		for(HashMap.Entry<String , Double> entry : credits.playercredit.entrySet()){
			String oyuncuIsmi = entry.getKey();
			Double kredisi = entry.getValue();
			conf.playerConf().set("Players."+oyuncuIsmi+".Credits", kredisi);
			try {conf.savePlayer();} catch (IOException e){e.printStackTrace();}
		}
		for(Entry<String, List<String>> entry : credits.playerip.entrySet()){
			String oyuncuIsmi = entry.getKey();
			List<String> ip = entry.getValue();
			conf.playerConf().set("Players."+oyuncuIsmi+".ip", ip);
			try {conf.savePlayer();} catch (IOException e) {e.printStackTrace();}
		}
		getServer().getScheduler().cancelTask(webclient.task1);
	}
	public void loadVariables(){
		ConfigurationSection sec = conf.playerConf().getConfigurationSection("Players");
		if(sec != null){
			for(String key : sec.getKeys(false)){
				List<String> ip = conf.playerConf().getStringList("Players."+key+".ip");
				Double kredi = conf.playerConf().getDouble("Players."+key+".Credits");
				credits.playerip.put(key, ip);
				credits.playercredit.put(key, kredi);
			}
		}
	}
}
