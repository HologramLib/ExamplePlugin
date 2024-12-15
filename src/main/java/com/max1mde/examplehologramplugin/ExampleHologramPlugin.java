package com.max1mde.examplehologramplugin;

import com.maximde.hologramapi.HologramAPI;
import com.maximde.hologramapi.hologram.HologramManager;
import com.maximde.hologramapi.hologram.TextAnimation;
import com.maximde.hologramapi.hologram.TextHologram;
import com.maximde.hologramapi.utils.Vector3F;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ExampleHologramPlugin extends JavaPlugin implements Listener {

    private TextHologram testHologram;
    private TextHologram leaderboard;
    private HologramManager hologramManager;

    @Override
    public void onEnable() {
        hologramManager = HologramAPI.getManager().orElse(null);
        if (hologramManager == null) {
            getLogger().severe("Failed to initialize HologramAPI manager. Plugin will not function correctly.");
            return;
        }
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!event.getMessage().startsWith("!")) return;

        String command = event.getMessage().substring(1).toLowerCase();
        boolean isCommand = onTestCommand(command, event.getPlayer());

        if (isCommand) {
            event.setCancelled(true);
        }
    }

    private boolean onTestCommand(String command, Player player) {
        if (hologramManager == null) {
            player.sendMessage(ChatColor.RED + "HologramAPI manager is not initialized.");
            return false;
        }

        switch (command) {
            case "spawn" -> {
                if (testHologram != null) hologramManager.remove(testHologram);

                testHologram = new TextHologram("test")
                        .setMiniMessageText("<aqua>Hello world!")
                        .setSeeThroughBlocks(false)
                        .setBillboard(Display.Billboard.VERTICAL)
                        .setShadow(true)
                        .setScale(1.5F, 1.5F, 1.5F)
                        .setTextOpacity((byte) 200)
                        .setBackgroundColor(Color.fromARGB(60, 255, 236, 222).asARGB());
                hologramManager.spawn(testHologram, player.getLocation());
            }

            case "passenger" -> {
                if (testHologram != null) hologramManager.remove(testHologram);

                testHologram = new TextHologram("test")
                        .setMiniMessageText("<aqua>Passenger hologram!")
                        .setSeeThroughBlocks(false)
                        .setBillboard(Display.Billboard.VERTICAL)
                        .setShadow(true)
                        .setScale(1.5F, 1.5F, 1.5F)
                        .setTextOpacity((byte) 200)
                        .setBackgroundColor(Color.fromARGB(60, 255, 236, 222).asARGB());
                hologramManager.spawn(testHologram, player.getLocation());
                hologramManager.attach(testHologram, player.getEntityId());
            }

            case "leaderboard" -> {
                if (leaderboard != null) hologramManager.remove(leaderboard);

                Map<Integer, String> leaderboardData = new LinkedHashMap<>() {{
                    put(1, "PlayerOne:1000");
                    put(2, "PlayerTwo:950");
                    put(3, "PlayerThree:900");
                    put(4, "PlayerFour:850");
                    put(5, "PlayerFive:800");
                    put(6, "PlayerOne:500");
                    put(7, "PlayerTwo:400");
                    put(8, "PlayerThree:300");
                    put(9, "PlayerFour:200");
                    put(10, "PlayerFive:100");
                }};

                leaderboard = hologramManager.generateLeaderboard(
                        player.getLocation().add(0, 2, 0),
                        leaderboardData,
                        HologramManager.LeaderboardOptions.builder()
                                .title("Top Players")
                                .suffix("kills")
                                .titleFormat("<gradient:#ff6000:#ffa42a>▛▀▀▀▀▀▀ {title} ▀▀▀▀▀▀▜</gradient>")
                                .build()
                );
            }

            case "leaderboard-half" -> {
                if (leaderboard != null) hologramManager.remove(leaderboard);

                Map<Integer, String> leaderboardData = new LinkedHashMap<>() {{
                    put(1, "PlayerOne:1000");
                    put(2, "PlayerTwo:950");
                    put(3, "");
                    put(4, "");
                    put(5, "");
                }};

                leaderboard = hologramManager.generateLeaderboard(
                        player.getLocation().add(0, 2, 0),
                        leaderboardData,
                        HologramManager.LeaderboardOptions.builder()
                                .title("Top Players")
                                .titleFormat("<gradient:#ff6000:#ffa42a>▛▀▀▀▀▀▀ {title} ▀▀▀▀▀▀▜</gradient>")
                                .showEmptyPlaces(true)
                                .scale(1.2f)
                                .build()
                );
            }

            case "leaderboard-update" -> {
                if (leaderboard == null) {
                    player.sendMessage(ChatColor.RED + "No leaderboard exists to update.");
                    return true;
                }

                Map<Integer, String> updatedData = new LinkedHashMap<>() {{
                    put(1, "NewChamp:1200");
                    put(2, "PlayerOne:1100");
                    put(3, "PlayerTwo:1050");
                    put(4, "PlayerThree:1000");
                    put(5, "PlayerFour:950");
                }};

                hologramManager.updateLeaderboard(
                        leaderboard,
                        updatedData,
                        HologramManager.LeaderboardOptions.builder()
                                .title("Updated Rankings")
                                .build()
                );
            }

            case "kill" -> {
                removeHolograms(testHologram, leaderboard);
            }

            case "animate" -> {
                if (testHologram == null) {
                    player.sendMessage(ChatColor.RED + "No hologram to animate.");
                    return true;
                }

                TextAnimation animation = new TextAnimation()
                        .addFrame("<red>First frame")
                        .addFrame("<green>Second frame")
                        .addFrame("Third frame\nSecond line in third frame")
                        .addFrame("Last frame")
                        .setDelay(20L)
                        .setSpeed(20L);

                hologramManager.applyAnimation(testHologram, animation);
            }

            case "stopanimation" -> {
                if (testHologram != null) {
                    hologramManager.cancelAnimation(testHologram);
                    testHologram.setMiniMessageText("<aqua>Hello world!");
                    testHologram.update();
                }
            }

            case "bigger" -> updateHologramScale(testHologram, new Vector3F(5, 5, 5));

            case "smaller" -> updateHologramScale(testHologram, new Vector3F(0.5F, 0.5F, 0.5F));

            case "normal" -> updateHologramScale(testHologram, new Vector3F(1.5F, 1.5F, 1.5F));

            default -> {
                return false;
            }
        }

        player.sendMessage(ChatColor.GREEN + "Command executed successfully!");
        return true;
    }

    private void removeHolograms(TextHologram... holograms) {
        if (holograms == null) return;

        for (TextHologram hologram : holograms) {
            if (hologram != null) {
                hologramManager.remove(hologram);
            }
        }
    }

    private void updateHologramScale(TextHologram hologram, Vector3F scale) {
        if (hologram == null) return;

        hologram.setScale(scale);
        hologram.update();
    }
}