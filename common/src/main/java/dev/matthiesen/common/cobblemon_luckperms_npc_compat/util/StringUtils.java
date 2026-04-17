package dev.matthiesen.common.cobblemon_luckperms_npc_compat.util;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class StringUtils {
    public static long convertToSeconds(String input) {
        // Splits "4 days" into ["4", "days"]
        String[] parts = input.split(" ");
        long amount = Long.parseLong(parts[0]);
        String unit = parts[1].toLowerCase();

        return switch (unit) {
            case "w", "week", "weeks" -> amount * 604800;
            case "d", "day", "days" -> amount * 86400;
            case "h", "hour", "hours" -> amount * 3600;
            case "m", "minute", "minutes" -> amount * 60;
            case "s", "second", "seconds" -> amount;
            default -> throw new IllegalArgumentException("Unknown unit: " + unit);
        };
    }

    public static long convertToSecondsFromNow(String input) {
        long seconds = convertToSeconds(input);
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Duration duration = checkPastDate(Duration.between(now, Instant.ofEpochSecond(seconds)));
        return duration.getSeconds();
    }

    private static Duration checkPastDate(Duration duration) throws RuntimeException {
        if (duration.isNegative()) {
            throw new RuntimeException("The provided time is in the past");
        }
        return duration;
    }
}
