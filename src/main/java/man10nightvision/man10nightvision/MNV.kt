package man10nightvision.man10nightvision

import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class MNV : JavaPlugin(),Listener {

    var mode = true
    var light = 0
    var lightitem : ItemStack? = null
    private val prefix = "[§a§lMNV§f]"
    override fun onEnable() {
        saveDefaultConfig()
        server.logger.info("$prefix is Enable~~~")
        server.pluginManager.registerEvents(this,this)
        getCommand("mnv")?.setExecutor(this)
        light = config.getInt("light")
        lightitem = config.getItemStack("lightitem")
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

    @EventHandler
    fun move(e : PlayerMoveEvent){
        if (!mode)return
        if (e.player.inventory.helmet == null)return
        if (e.player.inventory.helmet != lightitem)return
        if (e.player.location.block.lightLevel > light){
            e.player.removePotionEffect(PotionEffectType.BLINDNESS)
            e.player.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION,10,1,true))
        }else{
            e.player.removePotionEffect(PotionEffectType.NIGHT_VISION)
            e.player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS,10,1,true))
        }
        return
    }
}