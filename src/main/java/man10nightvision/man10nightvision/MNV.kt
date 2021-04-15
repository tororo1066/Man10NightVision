package man10nightvision.man10nightvision

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class MNV : JavaPlugin() {

    private var mode = true
    private var light = 0
    private var lightitem : ItemStack? = null
    private val prefix = "[§a§lMNV§f]"
    override fun onEnable() {
        saveDefaultConfig()
        server.logger.info("$prefix is Enable~~~")
        getCommand("mnv")?.setExecutor(this)
        light = config.getInt("light")
        lightitem = config.getItemStack("lightitem")
        runtask()

    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player)return true
        if (!sender.isOp)return true
        if (args.isEmpty())return true
        when(args[0]){
            "help"->{
                sender.sendMessage("§a===============Man10NightVision===============")
                sender.sendMessage("§a/mnv (on or off) modeを切り替えます")
                sender.sendMessage("§a/mnv light 暗視がつくlightlevelを設定します")
                sender.sendMessage("§a/mnv lightitem 手に持っているアイテムをゴーグルにします")
                sender.sendMessage("§a===============Man10NightVision===============")
                return true
            }
            "on"->{
                if (mode){
                    sender.sendMessage(prefix + "すでにonです！")
                    return true
                }
                mode = true
                runtask()
                sender.sendMessage(prefix + "モードをonにしました")
                return true
            }
            "off"->{
                if (!mode){
                    sender.sendMessage(prefix + "すでにoffです！")
                    return true
                }
                mode = false
                sender.sendMessage(prefix + "モードをoffにしました")
                return true
            }
            "light"->{
                if (args.size != 2)return true
                if (args[1].toIntOrNull() == null){
                    sender.sendMessage(prefix + "args1は数字にしてください")
                    return true
                }
                config.set("light",args[1].toInt())
                saveConfig()
                light = args[1].toInt()
                sender.sendMessage(prefix + "コンフィグを設定しました")
                return true

            }
            "lightitem"->{
                if (sender.inventory.itemInMainHand.type == Material.AIR){
                    sender.sendMessage(prefix + "手にアイテムを持ってください")
                    return true
                }
                config.set("lightitem",sender.inventory.itemInMainHand)
                saveConfig()
                lightitem = sender.inventory.itemInMainHand
                sender.sendMessage(prefix + "コンフィグを設定しました")
                return true
            }
        }
        return true
    }

    private fun runtask(){
        Bukkit.getScheduler().runTaskTimer(this, Runnable {
            if (!mode)return@Runnable
            for (p in Bukkit.getOnlinePlayers()){
                if (p.inventory.helmet == null)continue
                if (p.inventory.helmet != lightitem)continue
                if (p.location.block.lightLevel < light){
                    p.removePotionEffect(PotionEffectType.BLINDNESS)
                    p.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION,400,1,true))
                }else{
                    p.removePotionEffect(PotionEffectType.NIGHT_VISION)
                    p.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS,400,1,true))
                }
            }
        },0,config.getLong("checktimer") * 20)
    }
}


