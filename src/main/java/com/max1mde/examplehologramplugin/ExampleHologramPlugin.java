package com.max1mde.examplehologramplugin;


import com.maximde.hologramapi.HologramAPI;
import com.maximde.hologramapi.hologram.TextAnimation;
import com.maximde.hologramapi.hologram.TextHologram;
import com.maximde.hologramapi.utils.Vector3F;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import org.bukkit.plugin.java.JavaPlugin;

public final class ExampleHologramPlugin extends JavaPlugin implements Listener {

    private TextHologram hologram;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if(!event.getMessage().startsWith("!")) return;
        event.setCancelled(
                onTestCommand(event.getMessage().toLowerCase().replace("!",""), event.getPlayer(), event))
        ;
    }

    private boolean onTestCommand(String command, Player player, AsyncPlayerChatEvent event) {
        boolean isCommand = true;
        switch (command) {
            case "spawn" -> {
                if(hologram != null && !hologram.isDead()) hologram.kill();
                hologram = new TextHologram("test")
                        .setMiniMessageText("<aqua>Hello world!")
                        .setSeeThroughBlocks(false)
                        .setBillboard(Display.Billboard.VERTICAL)
                        .setShadow(true)
                        .setScale(1.5F,1.5F,1.5F)
                        .setTextOpacity((byte) 200)
                        .setBackgroundColor(Color.fromARGB(20, 255, 236, 222).asARGB());
                HologramAPI.getHologram().spawn(hologram, player.getLocation());
            }
            case "kill" -> {
                hologram.kill();
            }
            case "animate" -> {
                TextAnimation animation = new TextAnimation()
                        .addFrame( "<red>First frame")
                        .addFrame("<green>Second frame")
                        .addFrame("Third frame\n" +
                                "Second line in third frame")
                        .addFrame("Last frame");
                animation.setDelay(20L); // 1 second
                animation.setDelay(20L * 2);
                HologramAPI.getHologram().applyAnimation(this.hologram, animation);
            }
            case "stopanimation" -> {
                HologramAPI.getHologram().cancelAnimation(this.hologram);
                hologram.setMiniMessageText("<aqua>Hello world!");
                hologram.update();
            }
            case "killall" -> {
                HologramAPI.getHologram().removeAll();
            }
            case "bigger" -> {
                hologram.setScale(new Vector3F(5,5,5));
                hologram.update();
            }
            case "smaller" -> {
                hologram.setScale(new Vector3F(0.5F,0.5F,0.5F));
                hologram.update();
            }
            case "normal" -> {
                hologram.setScale(new Vector3F(1.5F,1.5F,1.5F));
                hologram.update();
            }
            default -> isCommand = false;
        }
        if(isCommand) player.sendMessage(ChatColor.GREEN + "Success!");
        return isCommand;
    }


}
