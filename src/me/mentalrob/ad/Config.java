package me.mentalrob.ad;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
	private Main plugin;
	public Config(Main instance){
		this.plugin = instance;
	}
	public File creditShop;
	public FileConfiguration creditShopConfig;
	public File player;
	public FileConfiguration playerConfig;
	public File ads;
	public FileConfiguration adsConfig;
	public void load() throws IOException{
		creditShop = new File(plugin.getDataFolder() , "creditShop.yml");
		creditShopConfig = YamlConfiguration.loadConfiguration(creditShop);
		player = new File(plugin.getDataFolder() , "playerData.yml");
		playerConfig = YamlConfiguration.loadConfiguration(player);
		ads = new File(plugin.getDataFolder() , "ads.yml");
		adsConfig = YamlConfiguration.loadConfiguration(ads);
		if(!creditShop.exists()){
			creditShopConfig.set("Shops.tool1.CreditCost" , 100.0);
			creditShopConfig.set("Shops.tool1.Items.322.Data" , 1 );
			creditShopConfig.set("Shops.tool1.Items.322.Amount", 8);
			creditShopConfig.set("Shops.tool1.Items.322.Name", "&4Advertising Gift");
			creditShopConfig.set("Shops.tool1.Items.276.Amount", 1);
			creditShopConfig.set("Shops.tool1.Items.276.Name", "&4Advertising Gift");
			List<String> enchants = new ArrayList<String>();
			enchants.add("DAMAGE_ALL:2");
			creditShopConfig.set("Shops.tool1.Items.276.Enchantments", enchants);
			List<String> commands = new ArrayList<String>();
			commands.add("say %player% just bought the tool1 !");
			creditShopConfig.set("Shops.tool1.Commands", commands);
			creditShopConfig.save(creditShop);
		}
		if(!player.exists()){
			List<String> ip = new ArrayList<String>();
			ip.add("127.0.0.1");
			playerConfig.set("Players.mentalrob.ip",ip);
			playerConfig.set("Players.mentalrob.Credits", 0.0);
			playerConfig.save(player);
		}
		if(!ads.exists()){
			adsConfig.set("Ads.anadolumc.Cost", 1.0);
			adsConfig.set("Ads.anadolumc.Link", "http://www.anadolumc.com");
			adsConfig.save(ads);
		}
	}
	public FileConfiguration tools(){
		return creditShopConfig;
	}
	public void saveTools() throws IOException{
		creditShopConfig.save(creditShop);
	}
	public FileConfiguration playerConf(){
		return playerConfig;
	}
	public void savePlayer() throws IOException{
		playerConfig.save(player);
	}
	public FileConfiguration Advert(){
		return adsConfig;
	}
	public void saveAdv() throws IOException{
		adsConfig.save(ads);
	}
}
