package com.gmc.core;

public class LogUtils {
    public static String format(String msg, Object... args) {
        if (args == null || args.length == 0) {
            return msg;
        }
        StringBuilder builder = new StringBuilder();
        int index;
        int lastIndex = 0;
        int i = 0;
        while ((index = msg.indexOf("{}", lastIndex)) >= 0) {
            builder.append(msg.substring(lastIndex, index));

            builder.append(args[i]);
            i++;
            lastIndex = index + 2;
            if (i > args.length) {
                break;
            }
        }
        if (lastIndex < msg.length()) {
            builder.append(msg.substring(lastIndex));
        }
        if (args.length > i) {
            for (; i < args.length; i++) {
                builder.append(" ").append(args[i].toString());
            }
        }
        return builder.toString();

    }
}
