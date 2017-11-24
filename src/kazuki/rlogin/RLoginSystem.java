package kazuki.rlogin;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

public class RLoginSystem extends PluginBase implements Listener {
	private Config player;
	private Config banip;
	private Config bancid;
	private Config ban;
	@SuppressWarnings("rawtypes")
	private HashMap<String, HashMap> playerdata = new HashMap<String, HashMap>();
	@SuppressWarnings({ "rawtypes" })
	private HashMap<String, HashMap> bandata = new HashMap<String, HashMap>();
	@SuppressWarnings("rawtypes")
	private HashMap<String, HashMap> banipdata = new HashMap<String, HashMap>();
	@SuppressWarnings("rawtypes")
	private HashMap<String, HashMap> banciddata = new HashMap<String, HashMap>();
	@SuppressWarnings("rawtypes")
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getLogger().info("§a起動しました §bby kazuki102812");
		this.getDataFolder().mkdirs();
		this.player = new Config(new File(this.getDataFolder(), "player.yml"),Config.YAML);
		this.banip = new Config(new File(this.getDataFolder(), "banip.yml"),Config.YAML);
		this.bancid = new Config(new File(this.getDataFolder(), "bancid.yml"),Config.YAML);
		this.ban = new Config(new File(this.getDataFolder(), "ban.yml"),Config.YAML);
		this.player.getAll().forEach((name, data) -> this.playerdata.put(name, (HashMap) data));
		this.banip.getAll().forEach((name, data) -> this.banipdata.put(name, (HashMap) data));
		this.bancid.getAll().forEach((name, data) -> this.banciddata.put(name, (HashMap) data));
		this.ban.getAll().forEach((name, data) -> this.bandata.put(name, (HashMap) data));
	}
	public void onDisable() {
		this.playerdata.forEach((name, data) -> this.player.set(name, data));
		this.player.save();
		this.banipdata.forEach((name, data) -> this.banip.set(name, data));
		this.banip.save();
		this.banciddata.forEach((name, data) -> this.bancid.set(name, data));
		this.bancid.save();
		this.bandata.forEach((name, data) -> this.ban.set(name, data));
		this.ban.save();
	}
	@SuppressWarnings({ "rawtypes", "deprecation" })
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String name = player.getName();
		String ip = player.getAddress();
		String cid = String.valueOf(player.getClientId());
		if(this.playerdata.containsKey(name)) {
            HashMap data = this.playerdata.get(name);
            HashMap pdata = (HashMap) data.get("main");
            String pip = (String) pdata.get("ip");
            String pcid = (String) pdata.get("cid");
            if(this.banipdata.containsKey(pip) && this.banciddata.containsKey(pcid)) {
            	player.kick("§l§aRL§fあなたはRBANされています", false);
            	event.setJoinMessage("§l§aRL§l>>>§f"+name+"がログイン認証に失敗しました");
            }else {
            	if(ip.equals(pip) || cid.equals(pcid)) {
                	event.setJoinMessage("§l§aRL§l>>>§f"+name+"が§aLogin§fしました");
                	player.sendMessage("§l§aRL§l>>>§fログイン認証完了しました");
                }else {
                	player.kick("§l§aRL§fアカウント情報と一致しません", false);
                	event.setJoinMessage("§l§aRL§l>>>§f"+name+"がログイン認証に失敗しました");
                }
            }
		}else {
			String host;
			try {
				InetAddress ia = InetAddress.getByName(ip);
				host = ia.getHostName();
			} catch (UnknownHostException e) {
				host = ip;
			}
			HashMap<String, HashMap> data = new HashMap<String, HashMap>();
			HashMap<String, String> datas = new HashMap<String, String>();
			datas.put("ip", ip);
			datas.put("cid", cid);
			datas.put("host", host);
			data.put("main", datas);
			this.playerdata.put(name, data);
			event.setJoinMessage("§l§aRL§l>>>§f"+name+"が§aLogin§fしました");
			player.sendMessage("§l§aRL§l>>>§fアカウント登録が完了しました");
		}
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		String name = player.getName();
		event.setQuitMessage("§l§aRL§l>>>§f"+name+"が§dQuit§fしました");
	}
	@SuppressWarnings({ "rawtypes" })
	public boolean onCommand(final CommandSender sender, Command command, String label, String[] args){
		switch(command.getName()){
		case "rban":
			try {
				if(args[0] != null){
					switch (args[0]) {
					  case "add":
						  try {
							  if(args[1] != null){
								  if(args[2] !=null) {
									  String name = args[1];
									  if (sender instanceof Player){
										  Player player = (Player)sender;
										  if(player.isOp()) {
			                                    if(this.playerdata.containsKey(name)) {
			                                    	if(getServer().getPlayer(args[1]) != null) {
			                                    		Player pl = getServer().getPlayer(args[1]);
			                                    		String message = args[2];
			                                    		pl.kick("§a[RL]§fあなたは接続禁止になりました 理由 : "+message, false);
			                                    		getServer().broadcastMessage("§l§bRBAN§l>>>§f"+name+"が§bRBAN§fされました\n§l§bRBAN§l>>>§f理由 : "+message+"\n§l§bRBAN§l>>>§fコマンド使用者 : "+player.getName());
			                                            HashMap data = this.playerdata.get(name);
			                                            HashMap pdata = (HashMap) data.get("main");
			                                            String pip = (String) pdata.get("ip");
			                                            String pcid = (String) pdata.get("cid");
			                                			HashMap<String, HashMap> ipdata = new HashMap<String, HashMap>();
			                                			HashMap<String, String> ipdatas = new HashMap<String, String>();
			                                			ipdatas.put("ip", pip);
			                                			ipdata.put(pip, ipdatas);
			                                            this.banipdata.put(pip, ipdata);
			                                			HashMap<String, HashMap> ciddata = new HashMap<String, HashMap>();
			                                			HashMap<String, String> ciddatas = new HashMap<String, String>();
			                                			ciddatas.put("cid", pcid);
			                                			ciddata.put(pcid, ciddatas);
			                                            this.banciddata.put(pcid, ciddata);
			                                			HashMap<String, HashMap> bdata = new HashMap<String, HashMap>();
			                                			HashMap<String, String> bdatas = new HashMap<String, String>();
			                                			bdatas.put("ip", pip);
			                                			bdatas.put("cid", pcid);
			                                			bdatas.put("理由", message);
			                                			bdata.put("情報", bdatas);
			                                			this.bandata.put(name, bdata);
			                                    	}else {
			                                    		String message = args[2];
			                                    		getServer().broadcastMessage("§l§bRBAN§l>>>§f"+name+"が§bRBAN§fされました\n§l§bRBAN§l>>>§f理由 : "+message+"\n§l§bRBAN§l>>>§fコマンド使用者 : "+player.getName());
			                                            HashMap data = this.playerdata.get(name);
			                                            HashMap pdata = (HashMap) data.get("main");
			                                            String pip = (String) pdata.get("ip");
			                                            String pcid = (String) pdata.get("cid");
			                                			HashMap<String, HashMap> ipdata = new HashMap<String, HashMap>();
			                                			HashMap<String, String> ipdatas = new HashMap<String, String>();
			                                			ipdatas.put("ip", pip);
			                                			ipdata.put(pip, ipdatas);
			                                            this.banipdata.put(pip, ipdata);
			                                			HashMap<String, HashMap> ciddata = new HashMap<String, HashMap>();
			                                			HashMap<String, String> ciddatas = new HashMap<String, String>();
			                                			ciddatas.put("cid", pcid);
			                                			ciddata.put(pcid, ciddatas);
			                                            this.banciddata.put(pcid, ciddata);
			                                			HashMap<String, HashMap> bdata = new HashMap<String, HashMap>();
			                                			HashMap<String, String> bdatas = new HashMap<String, String>();
			                                			bdatas.put("ip", pip);
			                                			bdatas.put("cid", pcid);
			                                			bdatas.put("理由", message);
			                                			bdata.put("情報", bdatas);
			                                			this.bandata.put(name, bdata);
			                                    	}
			                                    }else {
			                                    	player.sendMessage("§l§bRBAN§l>>>§fそのplayerは存在しません");
			                                    }
										  }else {
											  player.sendMessage("§l§bRBAN§l>>>§f使用する権限がありません");
										  }
									  }else if(sender instanceof ConsoleCommandSender) {
		                                    if(this.playerdata.containsKey(name)) {
		                                    	if(getServer().getPlayer(args[1]) != null) {
		                                    		Player pl = getServer().getPlayer(args[1]);
		                                    		String message = args[2];
		                                    		pl.kick("§a[RL]§fあなたは接続禁止になりました 理由 : "+message, false);
		                                    		getServer().broadcastMessage("§l§bRBAN§l>>>§f"+name+"が§bRBAN§fされました\n§l§bRBAN§l>>>§f理由 : "+message+"\n§l§bRBAN§l>>>§fコマンド使用者 : 管理者");
		                                            HashMap data = this.playerdata.get(name);
		                                            HashMap pdata = (HashMap) data.get("main");
		                                            String pip = (String) pdata.get("ip");
		                                            String pcid = (String) pdata.get("cid");
		                                			HashMap<String, HashMap> ipdata = new HashMap<String, HashMap>();
		                                			HashMap<String, String> ipdatas = new HashMap<String, String>();
		                                			ipdatas.put("ip", pip);
		                                			ipdata.put(pip, ipdatas);
		                                            this.banipdata.put(pip, ipdata);
		                                			HashMap<String, HashMap> ciddata = new HashMap<String, HashMap>();
		                                			HashMap<String, String> ciddatas = new HashMap<String, String>();
		                                			ciddatas.put("cid", pcid);
		                                			ciddata.put(pcid, ciddatas);
		                                            this.banciddata.put(pcid, ciddata);
		                                			HashMap<String, HashMap> bdata = new HashMap<String, HashMap>();
		                                			HashMap<String, String> bdatas = new HashMap<String, String>();
		                                			bdatas.put("ip", pip);
		                                			bdatas.put("cid", pcid);
		                                			bdatas.put("理由", message);
		                                			bdata.put("情報", bdatas);
		                                			this.bandata.put(name, bdata);
		                                    	}else {
		                                    		String message = args[2];
		                                    		getServer().broadcastMessage("§l§bRBAN§l>>>§f"+name+"が§bRBAN§fされました\n§l§bRBAN§l>>>§f理由 : "+message+"\n§l§bRBAN§l>>>§fコマンド使用者 : 管理者");
		                                            HashMap data = this.playerdata.get(name);
		                                            HashMap pdata = (HashMap) data.get("main");
		                                            String pip = (String) pdata.get("ip");
		                                            String pcid = (String) pdata.get("cid");
		                                			HashMap<String, HashMap> ipdata = new HashMap<String, HashMap>();
		                                			HashMap<String, String> ipdatas = new HashMap<String, String>();
		                                			ipdatas.put("ip", pip);
		                                			ipdata.put(pip, ipdatas);
		                                            this.banipdata.put(pip, ipdata);
		                                			HashMap<String, HashMap> ciddata = new HashMap<String, HashMap>();
		                                			HashMap<String, String> ciddatas = new HashMap<String, String>();
		                                			ciddatas.put("cid", pcid);
		                                			ciddata.put(pcid, ciddatas);
		                                            this.banciddata.put(pcid, ciddata);
		                                			HashMap<String, HashMap> bdata = new HashMap<String, HashMap>();
		                                			HashMap<String, String> bdatas = new HashMap<String, String>();
		                                			bdatas.put("ip", pip);
		                                			bdatas.put("cid", pcid);
		                                			bdatas.put("理由", message);
		                                			bdata.put("情報", bdatas);
		                                			this.bandata.put(name, bdata);
		                                    	}
		                                    }else {
		                                    	sender.sendMessage("§l§bRBAN§l>>>§fそのplayerは存在しません");
		                                    }
									  }
								  }
							  }
						  }
						  catch(ArrayIndexOutOfBoundsException e){
								sender.sendMessage("§l§bRBAN§l>>>§f/rban add [名前] [理由]");
							}
						break;

					  case "del":
					      try {
							  if(args[1] != null){
								  String name = args[1];
								  if(sender instanceof Player){
									  Player player = (Player)sender;
									  if(player.isOp()) {
										  if(this.playerdata.containsKey(name)) {
	                                          HashMap data = this.playerdata.get(name);
	                                          HashMap pdata = (HashMap) data.get("main");
	                                          String pip = (String) pdata.get("ip");
	                                          String pcid = (String) pdata.get("cid");
	                                          if(this.banipdata.containsKey(pip) && this.banciddata.containsKey(pcid)) {
	                                              this.banipdata.remove(pip);
	                                              this.banciddata.remove(pcid);
	                                              this.bandata.remove(name);
	                                              getServer().broadcastMessage("§l§bRBAN§l>>>§f"+name+"の§bRBAN§f解除されました\n§l§bRBAN§l>>>§fコマンド使用者 : "+player.getName());
	                                          }else {
	                                             player.sendMessage("§l§bRBAN§l>>>§fそのplayerは§bRBAN§fされていません");
	                                          }
										  }else {
											  player.sendMessage("§l§bRBAN§l>>>§fそのplayerは存在しません");
										  }
									  }else {
										  player.sendMessage("§l§bRBAN§l>>>§f使用する権限がありません");
									  }
								  }else if(sender instanceof ConsoleCommandSender){
									  if(this.playerdata.containsKey(name)) {
                                          HashMap data = this.playerdata.get(name);
                                          HashMap pdata = (HashMap) data.get("main");
                                          String pip = (String) pdata.get("ip");
                                          String pcid = (String) pdata.get("cid");
                                          if(this.banipdata.containsKey(pip) && this.banciddata.containsKey(pcid)) {
                                              this.banipdata.remove(pip);
                                              this.banciddata.remove(pcid);
                                              this.bandata.remove(name);
                                              getServer().broadcastMessage("§l§bRBAN§l>>>§f"+name+"の§bRBAN§f解除されました\n§l§bRBAN§l>>>§fコマンド使用者 : 管理者");
                                          }else {
                                        	  sender.sendMessage("§l§bRBAN§l>>>§fそのplayerは§bRBAN§fされていません");
                                          }
									  }else {
										  sender.sendMessage("§l§bRBAN§l>>>§fそのplayerは存在しません");
									  }
								  }
					    	  }
					      }
						  catch(ArrayIndexOutOfBoundsException e){
								sender.sendMessage("§l§bRBAN§l>>>§f/rban del [名前]");
							}
						break;

					  case "list":
					      try {
                               sender.sendMessage("§l§bRBAN§l>>>§f未実装");
					      }
						  catch(ArrayIndexOutOfBoundsException e){
								sender.sendMessage("§l§bRBAN§l>>>§f/rban list | リスト");
							}
						break;

					}
				}
			}
			catch(ArrayIndexOutOfBoundsException e){
				sender.sendMessage("§l§bRBAN§l>>>§f/rban add [名前] [理由]");
				sender.sendMessage("§l§bRBAN§l>>>§f/rban del [名前]");
				sender.sendMessage("§l§bRBAN§l>>>§f/rban list | リスト");
			}
		break;

		  case "delete":
		      try {
		    	  if(sender instanceof Player){
		    		  Player player = (Player)sender;
		    		  if(player.isOp()) {
		    			  if(args[0] != null){
		    				  String name = args[0];
		    				  if(this.playerdata.containsKey(name)) {
		    					  HashMap data = this.playerdata.get(name);
                                  HashMap pdata = (HashMap) data.get("main");
                                  String pip = (String) pdata.get("ip");
                                  String pcid = (String) pdata.get("cid");
                                  if(this.banipdata.containsKey(pip) && this.banciddata.containsKey(pcid)) {
                                      this.banipdata.remove(pip);
                                      this.banciddata.remove(pcid);
                                      this.bandata.remove(name);
                                  }
                                  this.playerdata.remove(name);
                                  player.sendMessage("§l§aRL§l>>>§f"+name+"のアカウント情報を削除しました");
		    				  }else {
		    					  player.sendMessage("§l§aRL§l>>>§fそのplayerは存在しません");
		    				  }
		    			  }
		    		  }else {
		    			  player.sendMessage("§l§aRL§l>>>§f使用する権限がありません");
		    		  }
		    	  }else if(sender instanceof ConsoleCommandSender){
		    		  if(args[0] != null){
	    				  String name = args[0];
	    				  if(this.playerdata.containsKey(name)) {
	    					  HashMap data = this.playerdata.get(name);
                              HashMap pdata = (HashMap) data.get("main");
                              String pip = (String) pdata.get("ip");
                              String pcid = (String) pdata.get("cid");
                              if(this.banipdata.containsKey(pip) && this.banciddata.containsKey(pcid)) {
                                  this.banipdata.remove(pip);
                                  this.banciddata.remove(pcid);
                                  this.bandata.remove(name);
                              }
                              this.playerdata.remove(name);
                              sender.sendMessage("§l§aRL§l>>>§f"+name+"のアカウント情報を削除しました");
	    				  }else {
	    					  sender.sendMessage("§l§aRL§l>>>§fそのplayerは存在しません");
	    				  }
	    			  }
		    	  }
		      }
			  catch(ArrayIndexOutOfBoundsException e){
					sender.sendMessage("§l§aRL§l>>>§f/delete [名前]");
				}
			break;

		}
		return false;
	}
}

