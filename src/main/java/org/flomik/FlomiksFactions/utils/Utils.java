package org.flomik.FlomiksFactions.utils;

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Набор вспомогательных методов (утилит) для работы с текстом.
 */
public class Utils {

    /**
     * Преобразует строку, содержащую HEX-коды (#RRGGBB),
     * в формат цветных кодов Minecraft (&x &r &g ...),
     * а затем с помощью ChatColor переводит их в цветные символы.
     * <p>
     * Пример: "#FF00FF" превратится в "§x§F§F§0§0§F§F".
     *
     * @param message исходная строка (может содержать текст с #RRGGBB)
     * @return обработанная строка, готовая к отображению цветов в Minecraft
     */
    public static String hex(final String message) {
        if (message == null) {
            return "";
        }

        // Шаблон поиска шестизначных HEX-кодов вида "#FFFFFF"
        final Pattern hexPattern = Pattern.compile("(#[a-fA-F0-9]{6})");

        String result = message;  // будем обновлять результат по мере замен
        Matcher matcher = hexPattern.matcher(result);

        // Пока в тексте есть совпадения с шаблоном (e.g. "#A1B2C3")
        while (matcher.find()) {
            // Выделяем HEX-код из строки
            final String hexCode = result.substring(matcher.start(), matcher.end());
            // Заменяем '#' на 'x', чтобы получить "xRRGGBB"
            final String replaced = hexCode.replace('#', 'x');

            // Генерируем строку вида "&x&F&F&0&0&F&F"
            final char[] chars = replaced.toCharArray();
            final StringBuilder builder = new StringBuilder();

            // Каждый символ добавляем с префиксом "&"
            // Пример: 'x','F','F','0','0','F','F' -> "&x&F&F&0&0&F&F"
            for (char c : chars) {
                builder.append("&").append(c);
            }

            // Заменяем исходный HEX-код ("#FF00FF") на "&x&F&F&0&0&F&F"
            result = result.replace(hexCode, builder.toString());

            // Обновляем matcher для новой строки
            matcher = hexPattern.matcher(result);
        }

        // Применяем ChatColor, чтобы заменить &x&F&F... на §x§F§F...
        // И окончательно возвращаем цветной текст
        return ChatColor.translateAlternateColorCodes('&', result).replace('&', '§');
    }
}
