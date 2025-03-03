package org.flomik.FlomiksFactions.clan.commands.handlers.clanInteractions; //NOPMD - suppressed CouplingBetweenObjects - TODO explain reason for suppression //NOPMD - suppressed CouplingBetweenObjects - TODO explain reason for suppression //NOPMD - suppressed CouplingBetweenObjects - TODO explain reason for suppression

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.flomik.FlomiksFactions.clan.Beacon;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.BeaconManager;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;
import org.flomik.FlomiksFactions.database.BeaconDao;
import org.flomik.FlomiksFactions.worldEvents.shrine.event.ShrineEvent;

import java.util.ArrayList;
import java.util.List;

public class ClaimRegionHandler { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    private final ClanManager clanManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final UnclaimRegionHandler unclaimRegionCommandHandler; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final ShrineEvent shrineEvent; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final BeaconDao beaconDao; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final BeaconManager beaconManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public ClaimRegionHandler(ClanManager clanManager, UnclaimRegionHandler unclaimRegionCommandHandler, ShrineEvent shrineEvent, //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
                              BeaconDao beaconDao, BeaconManager beaconManager) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        this.clanManager = clanManager;
        this.unclaimRegionCommandHandler = unclaimRegionCommandHandler;
        this.shrineEvent = shrineEvent;
        this.beaconDao = beaconDao;
        this.beaconManager = beaconManager;
    }

    public boolean handleCommand(Player sender, String[] args) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Эту команду может выполнить только игрок.");
            return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        if (args.length < 1 || !args[0].equalsIgnoreCase("claim")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
            sender.sendMessage(ChatColor.RED + "Использование: /c claim");
            return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        Player player = (Player) sender; //NOPMD - suppressed UnnecessaryCast - TODO explain reason for suppression //NOPMD - suppressed UnnecessaryCast - TODO explain reason for suppression //NOPMD - suppressed UnnecessaryCast - TODO explain reason for suppression
        Clan clan = clanManager.getPlayerClan(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
            return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }
        if (!clan.getRole(player.getName()).equalsIgnoreCase("Лидер")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
            player.sendMessage(ChatColor.RED + "Только лидер клана может использовать эту команду.");
            return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        ItemStack nexusBeacon = new ItemStack(Material.BEACON, 1); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        ItemMeta meta = nexusBeacon.getItemMeta(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Нексус");
            List<String> lore = new ArrayList<>(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            lore.add(ChatColor.GRAY + "Приватит чанк клана");
            lore.add(ChatColor.GRAY + "Имеет 5 'hp' против взрывов");
            meta.setLore(lore);

            meta.setCustomModelData(12345); //NOPMD - suppressed UseUnderscoresInNumericLiterals - TODO explain reason for suppression //NOPMD - suppressed UseUnderscoresInNumericLiterals - TODO explain reason for suppression //NOPMD - suppressed UseUnderscoresInNumericLiterals - TODO explain reason for suppression
            nexusBeacon.setItemMeta(meta);
        }

        player.getInventory().addItem(nexusBeacon); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
        player.sendMessage(ChatColor.GREEN + "Вы получили Маяк-нексус!");
        return true;
    }

    public boolean claimChunkWithBeacon(Player player, Block beaconBlock) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        Clan clan = clanManager.getPlayerClan(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
            return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        Chunk chunk = beaconBlock.getChunk(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        String chunkId = getChunkId(chunk); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

        if (isShrineChunk(chunk)) {
            player.sendMessage(ChatColor.RED + "Этот чанк является точкой Святилища и не может быть приватизирован.");
            return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        if (isChunkClaimed(chunkId, clan)) {
            player.sendMessage(ChatColor.YELLOW + "Этот чанк уже принадлежит вашему клану.");
            return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        if (isNotEnoughStrength(clan)) {
            player.sendMessage(ChatColor.YELLOW + "У вашего клана недостаточно силы для привата.");
            return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        if (isChunkClaimedByAnotherClan(chunkId, clan)) {
            for (Clan oldClan : clanManager.clans.values()) { //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                if (!oldClan.equals(clan) && oldClan.hasClaimedChunk(chunkId)) { //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                    if (oldClan.getLands() > oldClan.getStrength()) { //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                        oldClan.removeClaimedChunk(chunkId); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                        unclaimRegionCommandHandler.removeWorldGuardRegion(chunk, oldClan.getName()); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression

                        addWorldGuardRegion(chunk, clan.getName(), player, beaconBlock);
                        clan.addClaimedChunk(chunkId);

                        clanManager.sendClanMessage(clan,
                                ChatColor.GREEN + "Игрок " + ChatColor.YELLOW + player.getName() + ChatColor.GREEN
                                        + " захватил территорию клана " + ChatColor.YELLOW + oldClan.getName() + ChatColor.GREEN + "!"); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                        clanManager.sendClanMessage(oldClan,
                                ChatColor.RED + "Клан " + ChatColor.GOLD + clan.getName() + ChatColor.RED
                                        + " захватил один чанк вашей территории!");
                        return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
                    } else {
                        player.sendMessage(ChatColor.RED + "Этот чанк уже занят кланом " + oldClan.getName() + "."); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                        return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
                    }
                }
            }
            return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        addWorldGuardRegion(chunk, clan.getName(), player, beaconBlock);
        return true;
    }

    private void addWorldGuardRegion(Chunk chunk, String clanName, Player player, Block beaconBlock) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        String chunkId = getChunkId(chunk); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        Clan clan = clanManager.getPlayerClan(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        WorldGuard wg = WorldGuard.getInstance(); //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression
        RegionContainer container = wg.getPlatform().getRegionContainer(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
        RegionManager regions = container.get(BukkitAdapter.adapt(chunk.getWorld())); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

        String regionId = "clan_" + clanName + "_" + chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

        BlockVector3 min = BlockVector3.at(chunk.getX() << 4, 0, chunk.getZ() << 4); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        BlockVector3 max = BlockVector3.at((chunk.getX() << 4) + 15, chunk.getWorld().getMaxHeight(), (chunk.getZ() << 4) + 15); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(regionId, min, max); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

        region.setFlag(Flags.INTERACT, StateFlag.State.ALLOW);
        region.setFlag(Flags.PVP, StateFlag.State.ALLOW);
        region.setFlag(Flags.TNT, StateFlag.State.ALLOW);
        region.setFlag(Flags.CREEPER_EXPLOSION, StateFlag.State.DENY);
        region.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);

        if (regions != null) {
            regions.addRegion(region);
            addMembers(clan, region);
        }

        clan.addClaimedChunk(chunkId);

        beaconDao.insertBeacon(clan, beaconBlock.getLocation(), regionId, 5);
        Beacon beacon = new Beacon(clan.getName(), beaconBlock.getLocation(), 5, regionId); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        beaconManager.addBeacon(beacon);
    }

    private String getChunkId(Chunk chunk) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        return chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
    }

    private boolean isNotEnoughStrength(Clan clan) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        if (clan.getStrength() <= clan.getLands()){ //NOPMD - suppressed SimplifyBooleanReturns - TODO explain reason for suppression //NOPMD - suppressed SimplifyBooleanReturns - TODO explain reason for suppression
            return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }
        return false;
    }

    private boolean isChunkClaimed(String chunkId, Clan currentClan) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        for (Clan clan : clanManager.clans.values()) { //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            if (clan.equals(currentClan) && clan.hasClaimedChunk(chunkId)) {
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }
        }
        return false;
    }

    public void addMembers(Clan clan, ProtectedRegion region) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        for (String member : clan.getMembers()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            String playerRole = clan.getRole(member); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            Player player = Bukkit.getPlayerExact(member); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(member); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

            if (player != null && (playerRole.equals("Лидер") || playerRole.equals("Заместитель"))) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
                if (!region.getOwners().contains(player.getUniqueId())) { //NOPMD - suppressed CollapsibleIfStatements - TODO explain reason for suppression //NOPMD - suppressed CollapsibleIfStatements - TODO explain reason for suppression
                    DefaultDomain owners = region.getOwners(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    owners.addPlayer(player.getUniqueId());
                    region.setOwners(owners);
                }
            } if (player != null &&  playerRole.equals("Воин")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
                if (!region.getMembers().contains(player.getUniqueId())) { //NOPMD - suppressed CollapsibleIfStatements - TODO explain reason for suppression //NOPMD - suppressed CollapsibleIfStatements - TODO explain reason for suppression //NOPMD - suppressed CollapsibleIfStatements - TODO explain reason for suppression
                    DefaultDomain members = region.getMembers(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    members.addPlayer(player.getUniqueId());
                    region.setMembers(members);
                }
            } if (offlinePlayer != null && (playerRole.equals("Лидер") || playerRole.equals("Заместитель"))) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
                if (!region.getOwners().contains(offlinePlayer.getUniqueId())) { //NOPMD - suppressed CollapsibleIfStatements - TODO explain reason for suppression //NOPMD - suppressed CollapsibleIfStatements - TODO explain reason for suppression //NOPMD - suppressed CollapsibleIfStatements - TODO explain reason for suppression
                    DefaultDomain owners = region.getOwners(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    owners.addPlayer(offlinePlayer.getUniqueId());
                    region.setOwners(owners);
                }
            } if (offlinePlayer != null && playerRole.equals("Воин")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
                if (!region.getMembers().contains(offlinePlayer.getUniqueId())) { //NOPMD - suppressed CollapsibleIfStatements - TODO explain reason for suppression //NOPMD - suppressed CollapsibleIfStatements - TODO explain reason for suppression //NOPMD - suppressed CollapsibleIfStatements - TODO explain reason for suppression
                    DefaultDomain members = region.getMembers(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    members.addPlayer(offlinePlayer.getUniqueId());
                    region.setMembers(members);
                }
            }
        }
    }

    private boolean isChunkClaimedByAnotherClan(String chunkId, Clan currentClan) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        for (Clan clan : clanManager.clans.values()) { //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            if (!clan.equals(currentClan) && clan.hasClaimedChunk(chunkId)) {
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }
        }
        return false;
    }

    // Пример проверки, является ли чанк точкой святилища.
    private boolean isShrineChunk(Chunk chunk) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        World world = chunk.getWorld(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        WorldGuard wg = WorldGuard.getInstance(); //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression
        RegionContainer container = wg.getPlatform().getRegionContainer(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
        RegionManager regions = container.get(BukkitAdapter.adapt(world)); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

        if (regions == null) {
            return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        for (ProtectedRegion region : regions.getRegions().values()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (region.getId().startsWith("shrine_")) {
                BlockVector3 min = region.getMinimumPoint(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                BlockVector3 max = region.getMaximumPoint(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

                int chunkMinX = chunk.getX() << 4; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                int chunkMinZ = chunk.getZ() << 4; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                int chunkMaxX = chunkMinX + 15; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                int chunkMaxZ = chunkMinZ + 15; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

                if (min.getX() <= chunkMaxX && max.getX() >= chunkMinX &&
                        min.getZ() <= chunkMaxZ && max.getZ() >= chunkMinZ) {
                    return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
                }
            }
        }
        return false;
    }
}
