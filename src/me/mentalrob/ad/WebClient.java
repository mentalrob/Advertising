package me.mentalrob.ad;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class WebClient {
	private Main plugin;
	public int task1;
	public WebClient(Main instance){
		this.plugin = instance;
	}
	
	public void Start(String creditsUrl , String removerUrl , String key){
		task1 = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				CreditsMake(creditsUrl , removerUrl , key);
			}
		}, 0L, 200L);
	}
	public String GET(String urlToRead) throws Exception{
		StringBuilder out = new StringBuilder();
		URL url = new URL(urlToRead);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0");
		conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "tr-TR,tr;q=0.8,en-US;q=0.5,en;q=0.3");
		conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
		
		conn.connect();
		BufferedReader read = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = read.readLine()) != null){
			out.append(line);
		}
		read.close();
		conn.disconnect();
		return out.toString();
	}
	public void CreditsMake(String creditsUrl , String removerUrl , String key){
		try {
			String oku = GET(creditsUrl+"?key="+URLEncoder.encode(plugin.getConfig().getString("key")));
			if(!oku.equals(null) && oku.length() > 0){
				Bukkit.getLogger().info("Okudum...");
				String[] split1 = oku.split(",");
				for(String veri : split1){
					String[] split2 = veri.split(":");
					String sahip = split2[0].trim();
					String ip = split2[1].trim();
					String cost = split2[2].trim();
					if(plugin.credits.playerip.containsKey(sahip)){
						List<String> ips = plugin.credits.playerip.get(sahip);
						if(ips.contains(ip)){
							continue;
						}
						ips.add(ip);
						plugin.credits.playerip.put(sahip, ips);
						plugin.creditFunc.addCredit(sahip, Double.parseDouble(cost));
					}else{
						List<String> ips = new ArrayList<String>();
						ips.add(ip);
						plugin.credits.playerip.put(sahip, ips);
						plugin.creditFunc.addCredit(sahip, Double.parseDouble(cost));
					}
				}
			}
			GET(removerUrl+"?key="+key);
		} catch (Exception e) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[Advertising] There is an error occured ! [CreditsUrl is wrong ? or RemoverUrl ? " + creditsUrl +" "+removerUrl+"?key="+key+ "]\n"+e.getMessage()+"\nWebClient is disabling... You can start it again with /adv admin reload");
			plugin.getServer().getScheduler().cancelTask(task1);
		}
		
	}
}
	