package me.mentalrob.ad;

import java.io.IOException;

import org.bukkit.Bukkit;

public class CrediFunc {
	private Main plugin;
	private Credits credit = new Credits();
	public CrediFunc(Main instance){
		this.plugin = instance;
	}
	public Double getCredit(String PlayerName){
		Double d = 0.0;
		if(credit.playercredit.containsKey(PlayerName)){
			d = credit.playercredit.get(PlayerName);
		}
		return d;
	}
	public void setCredit(String PlayerName , Double Credit){
		credit.playercredit.put(PlayerName, Credit);
	}
	public void addCredit(String PlayerName , Double Credit){
		Double d = 0.0;
		PlayerName = PlayerName.trim();
		if(credit.playercredit.containsKey(PlayerName)){
			d = credit.playercredit.get(PlayerName);

		}
		d = d + Credit;
		credit.playercredit.put(PlayerName, d);
		
	}
	public void removeCredit(String PlayerName , Double Credit){
		Double d = 0.0;
		if(credit.playercredit.containsKey(PlayerName)){
			d = credit.playercredit.get(PlayerName);
			d = d - Credit;
			credit.playercredit.put(PlayerName, d);
		}
	}
}
