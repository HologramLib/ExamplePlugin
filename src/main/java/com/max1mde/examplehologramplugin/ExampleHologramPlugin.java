package com.max1mde.examplehologramplugin;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.maximde.hologramlib.HologramLib;
import com.maximde.hologramlib.hologram.*;
import com.maximde.hologramlib.utils.Vector3F;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.awt.Color;

public final class ExampleHologramPlugin extends JavaPlugin {

    private HologramManager hologramManager;
    private final Map<String, Hologram<?>> activeHolograms = new HashMap<>();

    @Override
    public void onEnable() {
        hologramManager = HologramLib.getManager().orElse(null);
        if (hologramManager == null) {
            getLogger().severe("Failed to initialize HologramLib manager. Plugin will not function correctly.");
            return;
        } else {
            getLogger().info("Successfully initialized HologramLib!");
        }

        Objects.requireNonNull(getCommand("testholos")).setExecutor(new HologramCommand());
    }

    private class HologramCommand implements TabExecutor {
        private final String[] MAIN_COMMANDS = {
                "spawn", "remove", "list", "modify", "attach", "viewer"
        };

        private final String[] HOLOGRAM_TYPES = {
                "text", "text-viewers", "item", "block", "leaderboard"
        };

        private final String[] MODIFY_OPTIONS = {
                "scale", "translation", "billboard", "text", "item", "block", "glow", "rotation"
        };

        private final String[] VIEWER_OPTIONS = {
                "add", "remove", "clear"
        };

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
                return true;
            }

            if (args.length == 0) {
                sendHelp(player);
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "spawn" -> handleSpawn(player, args);
                case "remove" -> handleRemove(player, args);
                case "list" -> handleList(player);
                case "modify" -> handleModify(player, args);
                case "attach" -> handleAttach(player, args);
                case "viewer" -> handleViewer(player, args);
                default -> sendHelp(player);
            }

            return true;
        }

        private void handleSpawn(Player player, String[] args) {
            if (args.length < 3) {
                player.sendMessage(ChatColor.RED + "Usage: /testholos spawn <type> <id>");
                return;
            }

            String type = args[1].toLowerCase();
            String id = args[2];

            if (activeHolograms.containsKey(id)) {
                player.sendMessage(ChatColor.RED + "A hologram with that ID already exists!");
                return;
            }

            Location loc = player.getLocation();
            Hologram<?> hologram = switch (type) {
                case "text-viewers" -> {

                    TextHologram textHolo = new TextHologram(id, RenderMode.VIEWER_LIST)
                            .setMiniMessageText("<gradient:red:blue>Test Text Hologram</gradient>")
                            .setScale(1.0F, 1.0F, 1.0F)
                            .setBillboard(Display.Billboard.CENTER);
                    yield hologramManager.spawn(textHolo, loc);
                }
                case "text" -> {

                    TextHologram textHolo = new TextHologram(id)
                            .setMiniMessageText("<gradient:red:blue>Test Text Hologram</gradient>")
                            .setScale(1.0F, 1.0F, 1.0F)
                            .setBillboard(Display.Billboard.CENTER);
                    yield hologramManager.spawn(textHolo, loc);
                }
                case "item" -> {
                    ItemHologram itemHolo = new ItemHologram(id)
                            .setItem(new ItemStack.Builder().type(ItemTypes.DIAMOND_SWORD).build())
                            .setScale(1.0F, 1.0F, 1.0F)
                            .setGlowing(true);
                    itemHolo.setGlowColor(Color.CYAN);
                    yield hologramManager.spawn(itemHolo, loc);
                }
                case "block" -> {
                    BlockHologram blockHolo = new BlockHologram(id, RenderMode.NEARBY)
                            .setBlock(Material.DIAMOND_BLOCK.ordinal())
                            .setScale(1.0F, 1.0F, 1.0F)
                            .setGlowing(true);
                    yield hologramManager.spawn(blockHolo, loc);
                }
                case "leaderboard" -> {
                    Map<Integer, String> leaderboardData = new LinkedHashMap<>() {{
                        put(1, "MaximDe:1000");
                        put(2, "dream:950");
                        put(3, "BastiGHG:500");
                        put(4, "Wichtiger:400");
                        // ... more entries
                    }};
                    LeaderboardHologram leaderboard = hologramManager.generateLeaderboard(
                            loc,
                            leaderboardData,
                            LeaderboardHologram.LeaderboardOptions.builder()
                                    .title("Top Players - Kills")
                                    .showEmptyPlaces(true)
                                    .scale(1.2f)
                                    .maxDisplayEntries(10)
                                    .suffix("kills")
                                    .topPlayerHead(true)
                                    .build()
                    );
                    yield leaderboard.getTextHologram();
                }
                default -> null;
            };

            if (hologram != null) {
                activeHolograms.put(id, hologram);
                player.sendMessage(ChatColor.GREEN + "Spawned " + type + " hologram with ID: " + id);
            }
        }

        private void handleModify(Player player, String[] args) {
            if (args.length < 4) {
                player.sendMessage(ChatColor.RED + "Usage: /testholos modify <id> <option> <value>");
                return;
            }

            String id = args[1];
            String option = args[2].toLowerCase();
            Hologram<?> hologram = activeHolograms.get(id);

            if (hologram == null) {
                player.sendMessage(ChatColor.RED + "No hologram found with ID: " + id);
                return;
            }

            switch (option) {
                case "scale" -> {
                    if (args.length < 5) {
                        player.sendMessage(ChatColor.RED + "Usage: /testholos modify <id> scale <x> <y> <z>");
                        return;
                    }
                    float x = Float.parseFloat(args[3]);
                    float y = Float.parseFloat(args[4]);
                    float z = Float.parseFloat(args[5]);
                    hologram.setScale(new Vector3F(x, y, z));
                }
                case "translation" -> {
                    if (args.length < 5) {
                        player.sendMessage(ChatColor.RED + "Usage: /testholos modify <id> translation <x> <y> <z>");
                        return;
                    }
                    float x = Float.parseFloat(args[3]);
                    float y = Float.parseFloat(args[4]);
                    float z = Float.parseFloat(args[5]);
                    hologram.setTranslation(new Vector3F(x, y, z));
                }
                case "rotation" -> {
                    if (args.length < 5) {
                        player.sendMessage(ChatColor.RED + "Usage: /testholos modify <id> rotation <x> <y> <z> <w>");
                        return;
                    }
                    float x = Float.parseFloat(args[3]);
                    float y = Float.parseFloat(args[4]);
                    float z = Float.parseFloat(args[5]);
                    float w = Float.parseFloat(args[6]);
                    hologram.setLeftRotation(x, y, z, w);
                }
                case "billboard" -> {
                    Display.Billboard billboard = Display.Billboard.valueOf(args[3].toUpperCase());
                    hologram.setBillboard(billboard);
                }
                case "text" -> {
                    if (hologram instanceof TextHologram textHologram) {
                        String text = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
                        textHologram.setMiniMessageText(text);
                    }
                }
                case "item" -> {
                    if (hologram instanceof ItemHologram itemHologram) {
                        ItemType itemType = ItemTypes.getByName(args[3].toUpperCase());
                        itemHologram.setItem(new ItemStack.Builder().type(itemType).build());
                    }
                }
                case "block" -> {
                    if (hologram instanceof BlockHologram blockHologram) {
                        Material material = Material.valueOf(args[3].toUpperCase());
                        blockHologram.setBlock(material.ordinal());
                    }
                }
                case "glow" -> {
                    boolean glow = Boolean.parseBoolean(args[3]);
                    if (hologram instanceof ItemHologram itemHologram) {
                        itemHologram.setGlowing(glow);
                    } else if (hologram instanceof BlockHologram blockHologram) {
                        blockHologram.setGlowing(glow);
                    }
                }
            }

            hologram.update();
            player.sendMessage(ChatColor.GREEN + "Modified hologram " + id);
        }

        private void handleRemove(Player player, String[] args) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /testholos remove <id>");
                return;
            }

            String id = args[1];
            Hologram<?> hologram = activeHolograms.remove(id);

            if (hologram != null) {
                hologramManager.remove(hologram);
                player.sendMessage(ChatColor.GREEN + "Removed hologram with ID: " + id);
            } else {
                player.sendMessage(ChatColor.RED + "No hologram found with ID: " + id);
            }
        }

        private void handleList(Player player) {
            if (activeHolograms.isEmpty()) {
                player.sendMessage(ChatColor.YELLOW + "No active holograms.");
                return;
            }

            player.sendMessage(ChatColor.GREEN + "Active holograms:");
            for (Map.Entry<String, Hologram<?>> entry : activeHolograms.entrySet()) {
                player.sendMessage(ChatColor.YELLOW + "- " + entry.getKey() + " (" + entry.getValue().getClass().getSimpleName() + ")");
            }
        }

        private void handleAttach(Player player, String[] args) {
            if (args.length < 3) {
                player.sendMessage(ChatColor.RED + "Usage: /testholos attach <id> <target>");
                return;
            }

            String id = args[1];
            String target = args[2];

            Hologram<?> hologram = activeHolograms.get(id);
            if (hologram == null) {
                player.sendMessage(ChatColor.RED + "No hologram found with ID: " + id);
                return;
            }

            Player targetPlayer = getServer().getPlayer(target);
            if (targetPlayer == null) {
                player.sendMessage(ChatColor.RED + "Target player not found!");
                return;
            }

            hologramManager.attach(hologram, targetPlayer.getEntityId());
            player.sendMessage(ChatColor.GREEN + "Attached hologram to " + target);
        }

        private void handleViewer(Player player, String[] args) {
            if (args.length < 4) {
                player.sendMessage(ChatColor.RED + "Usage: /testholos viewer <id> <add/remove/clear> [player]");
                return;
            }

            String id = args[1];
            String action = args[2].toLowerCase();
            Hologram<?> hologram = activeHolograms.get(id);

            if (hologram == null) {
                player.sendMessage(ChatColor.RED + "No hologram found with ID: " + id);
                return;
            }

            switch (action) {
                case "add" -> {
                    Player target = getServer().getPlayer(args[3]);
                    if (target != null) {
                        hologram.addViewer(target);
                        player.sendMessage(ChatColor.GREEN + "Added " + target.getName() + " as viewer");
                    }
                }
                case "remove" -> {
                    Player target = getServer().getPlayer(args[3]);
                    if (target != null) {
                        hologram.removeViewer(target);
                        player.sendMessage(ChatColor.GREEN + "Removed " + target.getName() + " as viewer");
                    }
                }
                case "clear" -> {
                    hologram.removeAllViewers();
                    player.sendMessage(ChatColor.GREEN + "Cleared all viewers");
                }
            }
            hologram.update();
        }

        private void sendHelp(Player player) {
            player.sendMessage(ChatColor.GREEN + "Hologram Test Commands:");
            player.sendMessage(ChatColor.YELLOW + "/testholos spawn <type> <id> - Spawn a hologram");
            player.sendMessage(ChatColor.YELLOW + "/testholos remove <id> - Remove a hologram");
            player.sendMessage(ChatColor.YELLOW + "/testholos list - List all active holograms");
            player.sendMessage(ChatColor.YELLOW + "/testholos modify <id> <option> <value> - Modify a hologram");
            player.sendMessage(ChatColor.YELLOW + "/testholos attach <id> <player> - Attach hologram to player");
            player.sendMessage(ChatColor.YELLOW + "/testholos viewer <id> <add/remove/clear> [player] - Manage viewers");
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            if (args.length == 1) {
                return filterStartsWith(Arrays.asList(MAIN_COMMANDS), args[0]);
            }

            if (args.length == 2) {
                switch (args[0].toLowerCase()) {
                    case "spawn":
                        return filterStartsWith(Arrays.asList(HOLOGRAM_TYPES), args[1]);
                    case "remove":
                    case "modify":
                    case "attach":
                    case "viewer":
                        return filterStartsWith(new ArrayList<>(activeHolograms.keySet()), args[1]);
                }
            }

            if (args.length == 3) {
                switch (args[0].toLowerCase()) {
                    case "modify":
                        return filterStartsWith(Arrays.asList(MODIFY_OPTIONS), args[2]);
                    case "viewer":
                        return filterStartsWith(Arrays.asList(VIEWER_OPTIONS), args[2]);
                }
            }

            if (args.length == 4) {
                if (args[0].equals("viewer") && (args[2].equals("add") || args[2].equals("remove"))) {
                    return null;
                }
            }

            return Collections.emptyList();
        }

        private List<String> filterStartsWith(List<String> list, String prefix) {
            return list.stream()
                    .filter(s -> s.toLowerCase().startsWith(prefix.toLowerCase()))
                    .toList();
        }
    }
}