
package com.common.util;

import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class CmdExecutor {

    public static String exec(String cmmand) {
        return exec(splitWhitespace(cmmand));
    }

    public static String exec(String[] cmmand) {
        return exec(cmmand, "/");
    }

    public static String exec(List<String> command) {
        StringBuffer sb = new StringBuffer();
        InputStream is = null;
        try {
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            is = process.getInputStream();
            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, read));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(is);
        }
        return sb.toString();
    }

    private static String exec(String[] cmmand, String directory) {
        StringBuilder sb = new StringBuilder();
        InputStream is = null;
        try {
            ProcessBuilder builder = new ProcessBuilder(cmmand);
            if (directory != null) {
                builder.directory(new File(directory));
            }
            builder.redirectErrorStream(true);
            Process process = builder.start();

            // 得到命令执行后的结果
            is = process.getInputStream();

            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                String temp = new String(buffer, 0, read);
                sb.append(temp);
                if (!TextUtils.isEmpty(temp) && temp.contains("device not found")) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(is);
        }
        return sb.toString().trim();
    }

    public static String execSu(String command) {
        String[] cmd = new String[] {
                "su",
                "-c",
                command,
        };
        return exec(cmd);
    }


    /**
     * 以空白符分隔字符串
     * @param text
     * @return
     */
    private static String[] splitWhitespace(String text) {
        return text.split("\\s{1,}");
    }

    private static void closeQuietly(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
    }

}
