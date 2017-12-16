package me.mentalrob.ad;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener{
	private Main plugin;
	public JoinListener(Main instance){
		this.plugin = instance;
	}
	@EventHandler
	public void onJ(PlayerJoinEvent e) throws IOException{
		Player player = e.getPlayer();
		String PlayerName = player.getName();
		if(!plugin.credits.playercredit.containsKey(PlayerName)){
			Double d = 0.0;
			if(plugin.conf.playerConf().get("Players."+PlayerName+".Credits") == null){
				plugin.conf.playerConf().set("Players."+PlayerName+".Credits", d);
				plugin.conf.savePlayer();
			}else{
				d = plugin.conf.playerConf().getDouble("Players."+PlayerName+".Credits");
			}
			plugin.credits.playercredit.put(PlayerName, d);
		}
		
	}
	
}
