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
        getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                if(!event.getMessage().startsWith("!")) return;
                onTestCommand(event.getMessage().toLowerCase().replace("!",""), event.getPlayer());
            }
        });
    }

    private void onTestCommand(String command, Player player) {
        switch (command) {
            case "spawn" -> {
                if(hologram != null && !hologram.isDead()) hologram.kill();
                hologram = new TextHologram("test_hologram")
                        .setText(ChatColor.AQUA + "Hello world!")
                        .setBillboard(Display.Billboard.VERTICAL)
                        .setShadow(true)
                        .setScale(2,2,2)
                        .setTextOpacity((byte) 200)
                        .setBackgroundColor(Color.fromARGB(0, 255, 236, 222).asARGB());
                HologramAPI.getHologram().spawn(hologram, player.getLocation());
                player.sendMessage("spawned");
            }
            case "kill" -> {
                hologram.kill();
            }
            case "animate" -> {
                TextAnimation animation = new TextAnimation()
                        .addFrame( ChatColor.RED + "First frame")
                        .addFrame("Second frame")
                        .addFrame("Third frame\nSecond line")
                        .addFrame("Last frame");
                HologramAPI.getHologram().applyAnimation(this.hologram, animation);
            }
            case "stopanimation" -> {
                HologramAPI.getHologram().cancelAnimation(this.hologram);
            }
            case "killall" -> {
                HologramAPI.getHologram().remove("test_hologram");
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
        }
    }


}
