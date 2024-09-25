package org.flomik.flomiksFactions.clan.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HelpCommandHandler {

    // Основной обработчик команды
    public boolean handleCommand(Player player, String[] args) {
        int page = 1; // Страница по умолчанию

        // Проверка, есть ли аргумент, и попытка его преобразовать в число
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Неверный номер страницы. Показываю первую страницу.");
            }
        }

        return showCommands(player, page);
    }

    // Метод для отображения команд с постраничной навигацией
    private boolean showCommands(Player player, int page) {
        String[][] commands = {
                {"/menu", "Открыть главное меню"},
                {"/clanmenu", "Открыть клановое меню"},
                {"/clan create <название>", "Создать новый клан"},
                {"/clan name <название>", "Поменять название клана"},
                {"/clan desc <описание>", "Поменять описание клана"},
                {"/clan claim", "Заприватить чанк"},
                {"/clan unclaim", "Убрать приват чанка"},
                {"/clan disband", "Распустить клан"},
                {"/clan ally <название>", "Предложить альянс клану"},
                {"/clan leader <игрок>", "Сделать игрока лидером"},
                {"/clan moder <игрок>", "Повысить игрока до Заместителя"},
                {"/clan promote <игрок>", "Повысить игрока"},
                {"/clan demote <игрок>", "Понизить игрока"},
                {"/clan invite <игрок>", "Пригласить игрока в ваш клан"},
                {"/clan join <название клана>", "Присоединиться к клану"},
                {"/clan leave", "Покинуть клан"},
                {"/clan kick <игрок>", "Выгнать игрока из клана"},
                {"/clan info <игрок>", "Информация о клане игрока"},
                {"/clan info <название>", "Информация о клане"},
                {"/clan info", "Информация о вашем клане"},
                {"/clan home", "Телепорт на точку дома"},
                {"/clan sethome", "Добавить точку дома"},
                {"/clan delhome", "Удалить точку дома"},
                {"/clan list", "Показать список всех кланов"},
                {"/сс <текст>", "Написать сообщение в чат клана"},
                {"/сlanchunks ", "Список чанков клана"}
        };

        // Определяем количество команд на странице
        int commandsPerPage = 15;
        int totalCommands = commands.length;
        int totalPages = (int) Math.ceil((double) totalCommands / commandsPerPage);

        // Проверяем корректность номера страницы
        if (page > totalPages || page <= 0) {
            player.sendMessage(ChatColor.RED + "Страница не найдена. Введите число от 1 до " + totalPages + ".");
            return false;
        }

        TextComponent commandsInfo = new TextComponent(ChatColor.GREEN + "**** " + ChatColor.WHITE + "Доступные команды" + ChatColor.GREEN + " ****");

        // Определяем начальный и конечный индекс команд на текущей странице
        int startIndex = (page - 1) * commandsPerPage;
        int endIndex = Math.min(startIndex + commandsPerPage, totalCommands);

        // Добавляем команды текущей страницы
        for (int i = startIndex; i < endIndex; i++) {
            // Убираем всё, что в угловых скобках
            String cleanCommand = commands[i][0].replaceAll("<.*?>", "");
            addCommand(commandsInfo, commands[i][0], cleanCommand, commands[i][1]);
        }

        // Добавляем навигацию по страницам
        TextComponent navigation = new TextComponent("\n");
        TextComponent prevPage = new TextComponent(ChatColor.GREEN + "[<< Пред]");
        TextComponent pageNumber = new TextComponent(ChatColor.RESET + " Страница " + page + "/" + totalPages + " ");
        TextComponent nextPage = new TextComponent(ChatColor.GREEN + "[След >>]");

        // Настройка событий для навигации
        if (page > 1) {
            prevPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan help " + (page - 1)));
        } else {
            prevPage.setColor(net.md_5.bungee.api.ChatColor.GRAY); // Если страница первая, стрелка неактивна
        }

        if (page < totalPages) {
            nextPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan help " + (page + 1)));
        } else {
            nextPage.setColor(net.md_5.bungee.api.ChatColor.GRAY); // Если последняя страница, стрелка неактивна
        }

        navigation.addExtra(prevPage);
        navigation.addExtra(pageNumber);
        navigation.addExtra(nextPage);

        // Отправляем сообщение игроку
        player.spigot().sendMessage(new ComponentBuilder(commandsInfo).append(navigation).create());
        return true;
    }

    // Метод добавления команды с описанием
    void addCommand(TextComponent parent, String commandText, String cleanCommand, String description) {
        TextComponent cmdComponent = new TextComponent("\n" + ChatColor.YELLOW + commandText);
        cmdComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cleanCommand)); // Убираем всё, что в <>
        TextComponent descComponent = new TextComponent(ChatColor.WHITE + " - " + description);
        parent.addExtra(cmdComponent);
        parent.addExtra(descComponent);
    }
}
