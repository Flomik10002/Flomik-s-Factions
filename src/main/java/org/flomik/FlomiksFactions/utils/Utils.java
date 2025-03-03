package org.flomik.FlomiksFactions.utils; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    public static String hex(String message) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        if (message == null) {
            return ""; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }
        Pattern pattern = Pattern.compile("(#[a-fA-F0-9]{6})"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            String replaceSharp = hexCode.replace('#', 'x'); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

            char[] ch = replaceSharp.toCharArray(); //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression
            StringBuilder builder = new StringBuilder(); //NOPMD - suppressed AvoidInstantiatingObjectsInLoops - TODO explain reason for suppression //NOPMD - suppressed AvoidInstantiatingObjectsInLoops - TODO explain reason for suppression //NOPMD - suppressed AvoidInstantiatingObjectsInLoops - TODO explain reason for suppression
            for (char c : ch) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                builder.append("&").append(c); //NOPMD - suppressed AppendCharacterWithChar - TODO explain reason for suppression //NOPMD - suppressed AppendCharacterWithChar - TODO explain reason for suppression //NOPMD - suppressed AppendCharacterWithChar - TODO explain reason for suppression
            }

            message = message.replace(hexCode, builder.toString()); //NOPMD - suppressed AvoidReassigningParameters - TODO explain reason for suppression //NOPMD - suppressed AvoidReassigningParameters - TODO explain reason for suppression //NOPMD - suppressed AvoidReassigningParameters - TODO explain reason for suppression
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message).replace('&', '§');
    }
}
