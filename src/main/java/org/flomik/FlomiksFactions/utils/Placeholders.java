package org.flomik.FlomiksFactions.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;
import org.flomik.FlomiksFactions.player.PlayerDataHandler;

/**
 * Класс отвечает за регистрацию плейсхолдеров PlaceholderAPI.
 * Обеспечивает динамическое отображение информации о кланах и игроках.
 */
public class Placeholders extends PlaceholderExpansion {
    private final PlayerDataHandler playerDataHandler;
    private final ClanManager clanManager;

    /**
     * Конструктор принимает зависимости (менеджер игроков и кланов).
     */
    public Placeholders(FlomiksFactions plugin, PlayerDataHandler playerDataHandler, ClanManager clanManager) {
        this.playerDataHandler = playerDataHandler;
        this.clanManager = clanManager;
        plugin.getEconomy();
    }

    /**
     * Получает количество онлайн-участников клана.
     */
    private int getOnlineClanMembersCount(Clan clan) {
        return (int) clan.getMembers().stream()
                .map(Bukkit::getPlayer)
                .filter(player -> player != null && player.isOnline())
                .count();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "flomiksfactions";
    }

    @Override
    public String getAuthor() {
        return "Flomik";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    /**
     * Обрабатывает запрос на получение плейсхолдера.
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        if (clanManager == null) {
            return "Ошибка: ClanManager не инициализирован";
        }

        // Получаем клан игрока один раз, чтобы не дублировать код
        Clan clan = clanManager.getPlayerClan(player.getName());

        switch (identifier) {
            case "clan_name":
                return clan != null ? clan.getName() : "Нет клана";

            case "clan_role":
                return clan != null ? clan.getRole(player.getName()) : "Нет";

            case "clan_lands":
                return String.valueOf(clan != null ? clan.getLands() : 0);

            case "clan_level":
                return String.valueOf(clan != null ? clan.getLevel() : 0);

            case "clan_strength":
                return String.valueOf(clan != null ? clan.getStrength() : 0);

            case "clan_online_members":
                return String.valueOf(clan != null ? getOnlineClanMembersCount(clan) : 0);

            case "player_level":
                return String.valueOf(playerDataHandler.getPlayerLevel(player.getName()));

            case "kills":
                return String.valueOf(playerDataHandler.getKills(player));

            case "deaths":
                return String.valueOf(playerDataHandler.getDeaths(player));

            default:
                return null;
        }
    }
}