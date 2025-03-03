package org.flomik.FlomiksFactions.clan.commands.handlers.playerInteractions; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HelpHandler { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    public boolean handleCommand(Player player, String[] args) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        int page = 1;


        if (args.length > 1) { //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Неверный номер страницы. Показываю первую страницу.");
            }
        }

        return showCommands(player, page);
    }

    private boolean showCommands(Player player, int page) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        String[][] commands = { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                {"/menu", "Открыть главное меню"},
                {"/clanmenu", "Открыть клановое меню"},
                {"/clan create <название>", "Создать новый клан"},
                {"/clan rename <название>", "Поменять название клана"},
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


        int commandsPerPage = 15; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        int totalCommands = commands.length; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        int totalPages = (int) Math.ceil((double) totalCommands / commandsPerPage); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression


        if (page > totalPages || page <= 0) {
            player.sendMessage(ChatColor.RED + "Страница не найдена. Введите число от 1 до " + totalPages + ".");
            return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        TextComponent commandsInfo = new TextComponent(ChatColor.GREEN + "**** " + ChatColor.WHITE + "Доступные команды" + ChatColor.GREEN + " ****"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression


        int startIndex = (page - 1) * commandsPerPage; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        int endIndex = Math.min(startIndex + commandsPerPage, totalCommands); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression


        for (int i = startIndex; i < endIndex; i++) {

            String cleanCommand = commands[i][0].replaceAll("<.*?>", ""); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            addCommand(commandsInfo, commands[i][0], cleanCommand, commands[i][1]);
        }


        TextComponent navigation = new TextComponent("\n"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        TextComponent prevPage = new TextComponent(ChatColor.GREEN + "[<< Пред]"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        TextComponent pageNumber = new TextComponent(ChatColor.RESET + " Страница " + page + "/" + totalPages + " "); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        TextComponent nextPage = new TextComponent(ChatColor.GREEN + "[След >>]"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression


        if (page > 1) { //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression
            prevPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan help " + (page - 1)));
        } else {
            prevPage.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        }

        if (page < totalPages) {
            nextPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan help " + (page + 1)));
        } else {
            nextPage.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        }

        navigation.addExtra(prevPage);
        navigation.addExtra(pageNumber);
        navigation.addExtra(nextPage);


        player.spigot().sendMessage(new ComponentBuilder(commandsInfo).append(navigation).create());
        return true;
    }


    void addCommand(TextComponent parent, String commandText, String cleanCommand, String description) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        TextComponent cmdComponent = new TextComponent("\n" + ChatColor.YELLOW + commandText); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        cmdComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cleanCommand));
        TextComponent descComponent = new TextComponent(ChatColor.WHITE + " - " + description); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        parent.addExtra(cmdComponent);
        parent.addExtra(descComponent);
    }
}
