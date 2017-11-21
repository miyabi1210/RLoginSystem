package kazuki.rlogin;

import java.io.File;
import java.util.HashMap;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerPreLoginEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

public class RLoginSystem extends PluginBase implements Listener {
	private Config player;
	@SuppressWarnings("rawtypes")
	private HashMap<String, HashMap> playerdata = new HashMap<String, HashMap>();
	@SuppressWarnings("rawtypes")
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getLogger().info("§a起動しました §bby kazuki102812");
		this.getDataFolder().mkdirs();
		this.player = new Config(new File(this.getDataFolder(), "player.yml"),Config.YAML);
		this.player.getAll().forEach((name, data) -> this.playerdata.put(name, (HashMap) data));
	}
	public void onDisable() {
		this.playerdata.forEach((name, data) -> this.player.set(name, data));
		this.player.save();
	}
	@SuppressWarnings({ "rawtypes", "deprecation" })
	@EventHandler
	public void onJoin(PlayerPreLoginEvent event) {
		Player player = event.getPlayer();
		String name = player.getName();
		String ip = player.getAddress();
		String cid = String.valueOf(player.getClientId());
		if(this.playerdata.containsKey(name)) {
            HashMap data = this.playerdata.get(name);
            HashMap pdata = (HashMap) data.get("main");
            String pip = (String) pdata.get("ip");
            String pcid = (String) pdata.get("cid");
            if(ip.equals(pip) && cid.equals(pcid)) {
            	player.sendMessage("§f>>>§a[RL]§fログイン認証完了しました");
            }else {
            	event.setCancelled();
            	event.setKickMessage("§a[RL]§fアカウント情報が一致しませんでした");
            }
		}else {
			HashMap<String, HashMap> data = new HashMap<String, HashMap>();
			HashMap<String, String> datas = new HashMap<String, String>();
			datas.put("ip", ip);
			datas.put("cid", cid);
			data.put("main", datas);
			this.playerdata.put(name, data);
			player.sendMessage("§f>>>§a[RL]§fアカウント登録が完了しました");
		}
	}
}
