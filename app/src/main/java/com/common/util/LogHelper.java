package com.common.util;

/**
 * Created by MHL on 2016/6/29.
 */

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Pattern;

import ma.com.locklock.BuildConfig;

public final class LogHelper {

    /**
     * A print stream which logs output line by line.
     *
     * {@hide}
     */
    static abstract class LoggingPrintStream extends PrintStream {

        private final StringBuilder builder = new StringBuilder();

        protected LoggingPrintStream() {
            super(new OutputStream() {
                @Override
                public void write(final int oneByte) throws IOException {
                    throw new AssertionError();
                }
            });
        }

        /**
         * Logs the given line.
         */
        protected abstract void log(String line);

        @Override
        public synchronized void flush() {
            flush(true);
        }

        /**
         * Searches buffer for line breaks and logs a message for each one.
         *
         * @param completely
         *            true if the ending chars should be treated as a line even
         *            though they don't end in a line break
         */
        private void flush(final boolean completely) {
            final int length = builder.length();

            int start = 0;
            int nextBreak;

            // LogHelper one line for each line break.
            while (start < length
                    && (nextBreak = builder.indexOf("\n", start)) != -1) {
                log(builder.substring(start, nextBreak));
                start = nextBreak + 1;
            }

            if (completely) {
                // LogHelper the remainder of the buffer.
                if (start < length) {
                    log(builder.substring(start));
                }
                builder.setLength(0);
            } else {
                // Delete characters leading up to the next starting point.
                builder.delete(0, start);
            }
        }

        /*
         * We have no idea of how these bytes are encoded, so just ignore them.
         */
        /** Ignored. */
        @Override
        public void write(final int oneByte) {
        }

        /** Ignored. */
        @Override
        public void write(final byte buffer[]) {
        }

        /** Ignored. */
        @Override
        public void write(final byte bytes[], final int start, final int count) {
        }

        /** Always returns false. */
        @Override
        public boolean checkError() {
            return false;
        }

        /** Ignored. */
        @Override
        protected void setError() { /* ignored */
        }

        /** Ignored. */
        @Override
        public void close() { /* ignored */
        }

        @Override
        public PrintStream format(final String format, final Object... args) {
            return format(Locale.getDefault(), format, args);
        }

        @Override
        public PrintStream printf(final String format, final Object... args) {
            return format(format, args);
        }

        @Override
        public PrintStream printf(final Locale l, final String format,
                                  final Object... args) {
            return format(l, format, args);
        }

        private final Formatter formatter = new Formatter(builder, null);

        @Override
        public synchronized PrintStream format(final Locale l,
                                               final String format, final Object... args) {
            if (format == null) {
                throw new NullPointerException("format");
            }

            formatter.format(l, format, args);
            flush(false);
            return this;
        }

        @Override
        public synchronized void print(final char[] charArray) {
            builder.append(charArray);
            flush(false);
        }

        @Override
        public synchronized void print(final char ch) {
            builder.append(ch);
            if (ch == '\n') {
                flush(false);
            }
        }

        @Override
        public synchronized void print(final double dnum) {
            builder.append(dnum);
        }

        @Override
        public synchronized void print(final float fnum) {
            builder.append(fnum);
        }

        @Override
        public synchronized void print(final int inum) {
            builder.append(inum);
        }

        @Override
        public synchronized void print(final long lnum) {
            builder.append(lnum);
        }

        @Override
        public synchronized void print(final Object obj) {
            builder.append(obj);
            flush(false);
        }

        @Override
        public synchronized void print(final String str) {
            builder.append(str);
            flush(false);
        }

        @Override
        public synchronized void print(final boolean bool) {
            builder.append(bool);
        }

        @Override
        public synchronized void println() {
            flush(true);
        }

        @Override
        public synchronized void println(final char[] charArray) {
            builder.append(charArray);
            flush(true);
        }

        @Override
        public synchronized void println(final char ch) {
            builder.append(ch);
            flush(true);
        }

        @Override
        public synchronized void println(final double dnum) {
            builder.append(dnum);
            flush(true);
        }

        @Override
        public synchronized void println(final float fnum) {
            builder.append(fnum);
            flush(true);
        }

        @Override
        public synchronized void println(final int inum) {
            builder.append(inum);
            flush(true);
        }

        @Override
        public synchronized void println(final long lnum) {
            builder.append(lnum);
            flush(true);
        }

        @Override
        public synchronized void println(final Object obj) {
            builder.append(obj);
            flush(true);
        }

        @Override
        public synchronized void println(final String s) {
            if (builder.length() == 0) {
                // Optimization for a simple println.
                final int length = s.length();

                int start = 0;
                int nextBreak;

                // LogHelper one line for each line break.
                while (start < length
                        && (nextBreak = s.indexOf('\n', start)) != -1) {
                    log(s.substring(start, nextBreak));
                    start = nextBreak + 1;
                }

                if (start < length) {
                    log(s.substring(start));
                }
            } else {
                builder.append(s);
                flush(true);
            }
        }

        @Override
        public synchronized void println(final boolean bool) {
            builder.append(bool);
            flush(true);
        }

        @Override
        public synchronized PrintStream append(final char c) {
            print(c);
            return this;
        }

        @Override
        public synchronized PrintStream append(final CharSequence csq) {
            builder.append(csq);
            flush(false);
            return this;
        }

        @Override
        public synchronized PrintStream append(final CharSequence csq,
                                               final int start, final int end) {
            builder.append(csq, start, end);
            flush(false);
            return this;
        }
    }

    /**
     * Print stream which log lines using Android's logging system.
     *
     * {@hide}
     */
    static class AndroidPrintStream extends LoggingPrintStream {

        private final int priority;
        private final String tag;

        /**
         * Constructs a new logging print stream.
         *
         * @param priority
         *            from {@link android.util.Log}
         * @param tag
         *            to log
         */
        public AndroidPrintStream(final int priority, final String tag) {
            if (tag == null) {
                throw new NullPointerException("tag");
            }

            this.priority = priority;
            this.tag = tag;
        }

        @Override
        protected void log(final String line) {
            LogHelper.println(priority, tag, line);
        }
    }

    private static final String MESSAGE_TEMPLATE = "[%s]%s";

    private static final Object sSyncObject = new Object();

    private static final Pattern sNewLinePattern = Pattern
            .compile("\r|\r\n|\n");

    /**
     * Priority constant for enable all loggings.
     */
    public static final int ALL = -1;

    /**
     * Priority constant for {@link #println(int, String, String)} or
     * {@link #setFilterLevel(int)} methods; use LogHelper.v.
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the {@link #println(int, String, String)} or
     * {@link #setFilterLevel(int)} method; use LogHelper.d.
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the {@link #println(int, String, String)} or
     * {@link #setFilterLevel(int)} method; use LogHelper.i.
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the {@link #println(int, String, String)} or
     * {@link #setFilterLevel(int)} method; use LogHelper.w.
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the {@link #println(int, String, String)} or
     * {@link #setFilterLevel(int)} method; use LogHelper.e.
     */
    public static final int ERROR = 6;

    /**
     * Priority constant for the {@link #println(int, String, String)} or
     * {@link #setFilterLevel(int)} method.
     */
    public static final int ASSERT = 7;

    /**
     * Priority constant for disable all loggings.
     */
    public static final int NONE = Integer.MAX_VALUE;

    /**
     * New error output.
     */
    public static final PrintStream err = new AndroidPrintStream(ERROR, "");
    /**
     * System's default err output.
     */
    public static final PrintStream systemErr = System.err;

    /**
     * Filter level of logs. Only levels greater or equals this level will be
     * output to LogCat.
     */
    private static int sFilterLevel = ALL;

    private static String sApplicationTag;

    static {
        if (System.err != err) {
            System.setErr(err);
        }
    }

    /**
     * Set the default tag for this application.
     *
     * @param tag
     *            The tag of the application.
     */
    public static final void setApplicationTag(final String tag) {
        sApplicationTag = tag;
    }

    /**
     * Gets the default tag of the application.
     *
     * @return The default tag of the application.
     */
    public static final String getApplicationTag() {
        return sApplicationTag;
    }

    /**
     * Sets the filter level of logs. Only levels greater or equals this level
     * will be output to LogCat.
     *
     * @param level
     *            The filter level.
     */
    public static final void setFilterLevel(final int level) {
        synchronized (sSyncObject) {
            sFilterLevel = level;
        }
    }

    /**
     * Gets the filter level of logs. Only levels greater or equals this level
     * will be output to LogCat.
     *
     * @return Current filter level.
     */
    public static final int getFilterLevel() {
        return sFilterLevel;
    }

    /**
     * Retrieve a default tag for an class.
     *
     * @param cls
     *            The default tag for the specified class.
     * @return The simple name of the class.
     */
    public static final String getDefaultTag(final Class<?> cls) {
        if (null == cls) {
            return "";
        }
        return cls.getSimpleName();
    }

    private LogHelper() {
    }

    /**
     * Send a {@link #VERBOSE} log message.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     */
    public static int v(final String tag, final String msg) {
        return println(VERBOSE, tag, msg);
    }

    /**
     * Send a {@link #VERBOSE} log message.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param format
     *            The format of the message you would like logged.
     * @param args
     *            The arguments used to format the message.
     */
    public static int v(final String tag, final String format,
                        final Object... args) {
        final String msg = String.format(format, args);
        return println(VERBOSE, tag, msg);
    }

    /**
     * Send a {@link #VERBOSE} log message and log the exception.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     * @param tr
     *            An exception to log
     */
    public static int v(final String tag, final String msg, final Throwable tr) {
        return println(VERBOSE, tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * Send a {@link #DEBUG} log message.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     */
    public static int d(final String tag, final String msg) {
        return println(DEBUG, tag, msg);
    }

    /**
     * Send a {@link #DEBUG} log message.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param format
     *            The format of the message you would like logged.
     * @param args
     *            The arguments used to format the message.
     */
    public static int d(final String tag, final String format,
                        final Object... args) {
        final String msg = String.format(format, args);
        return println(DEBUG, tag, msg);
    }

    /**
     * Send a {@link #DEBUG} log message and log the exception.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     * @param tr
     *            An exception to log
     */
    public static int d(final String tag, final String msg, final Throwable tr) {
        return println(DEBUG, tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * Send an {@link #INFO} log message.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     */
    public static int i(final String tag, final String msg) {
        return println(INFO, tag, msg);
    }

    /**
     * Send an {@link #INFO} log message.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param format
     *            The format of the message you would like logged.
     * @param args
     *            The arguments used to format the message.
     */
    public static int i(final String tag, final String format,
                        final Object... args) {
        final String msg = String.format(format, args);
        return println(INFO, tag, msg);
    }

    /**
     * Send a {@link #INFO} log message and log the exception.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     * @param tr
     *            An exception to log
     */
    public static int i(final String tag, final String msg, final Throwable tr) {
        return println(INFO, tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * Send a {@link #WARN} log message.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     */
    public static int w(final String tag, final String msg) {
        return println(WARN, tag, msg);
    }

    /**
     * Send a {@link #WARN} log message.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param format
     *            The format of the message you would like logged.
     * @param args
     *            The arguments used to format the message.
     */
    public static int w(final String tag, final String format,
                        final Object... args) {
        final String msg = String.format(format, args);
        return println(WARN, tag, msg);
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     * @param tr
     *            An exception to log
     */
    public static int w(final String tag, final String msg, final Throwable tr) {
        return println(WARN, tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * Checks to see whether or not a log for the specified tag is loggable at
     * the specified level.
     *
     * The default level of any tag is set to INFO. This means that any level
     * above and including INFO will be logged. Before you make any calls to a
     * logging method you should check to see if your tag should be logged. You
     * can change the default level by setting a system property: 'setprop
     * log.tag.&lt;YOUR_LOG_TAG> &lt;LEVEL>' Where level is either VERBOSE,
     * DEBUG, INFO, WARN, ERROR, ASSERT, or SUPPRESS. SUPRESS will turn off all
     * logging for your tag. You can also create a local.prop file that with the
     * following in it: 'log.tag.&lt;YOUR_LOG_TAG>=&lt;LEVEL>' and place that in
     * /data/local.prop.
     *
     * @param tag
     *            The tag to check.
     * @param level
     *            The level to check.
     * @return Whether or not that this is allowed to be logged.
     * @throws IllegalArgumentException
     *             is thrown if the tag.length() > 23.
     */
    public static boolean isLoggable(final String tag, final int level) {
        return android.util.Log.isLoggable(tag, level);
    }

    /*
     * Send a {@link #WARN} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually
     * identifies the class or activity where the log call occurs.
     *
     * @param tr An exception to log
     */
    public static int w(final String tag, final Throwable tr) {
        return println(WARN, tag, getStackTraceString(tr));
    }

    /**
     * Send an {@link #ERROR} log message.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param format
     *            The format of the message you would like logged.
     * @param args
     *            The arguments used to format the message.
     */
    public static int e(final String tag, final String format,
                        final Object... args) {
        final String msg = String.format(format, args);
        return println(ERROR, tag, msg);
    }

    /**
     * Send an {@link #ERROR} log message.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     */
    public static int e(final String tag, final String msg) {
        return println(ERROR, tag, msg);
    }

    /**
     * Send a {@link #ERROR} log message and log the exception.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     * @param tr
     *            An exception to log
     */
    public static int e(final String tag, final String msg, final Throwable tr) {
        final int r = println(ERROR, tag, msg + '\n' + getStackTraceString(tr));
        return r;
    }

    /**
     * Handy function to get a loggable stack trace from a Throwable
     *
     * @param tr
     *            An exception to log
     */
    public static String getStackTraceString(final Throwable tr) {
        if (tr == null) {
            return "";
        }
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Low-level logging call.
     *
     * @param priority
     *            The priority/type of this log message
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     * @return The number of bytes written.
     */
    public static int println(final int priority, final String tag,
                              final String msg) {
        if (priority < sFilterLevel || TextUtils.isEmpty(msg)) {
            return 0;
        }
        final String[] messageLines = sNewLinePattern.split(msg);
        final String logTag;
        final String format;
        if (TextUtils.isEmpty(sApplicationTag) || sApplicationTag.equals(tag)) {
            logTag = tag;
            format = null;
        } else if (TextUtils.isEmpty(tag)
                && !TextUtils.isEmpty(sApplicationTag)) {
            logTag = sApplicationTag;
            format = null;
        } else {
            logTag = sApplicationTag;
            format = MESSAGE_TEMPLATE;
        }
        // return android.util.Log.println(priority, sApplicationTag, message);
        int bytesWritten = 0;
        if (TextUtils.isEmpty(format)) {
            for (final String message : messageLines) {
                bytesWritten += android.util.Log.println(priority, logTag,
                        message);
            }
        } else {
            for (String message : messageLines) {
                message = String.format(MESSAGE_TEMPLATE, tag, message);
                bytesWritten += android.util.Log.println(priority, logTag,
                        message);
            }
        }
        return bytesWritten;
    }

    /**
     * Configure the global log settings.
     *
     * @param context
     *            the context of the application.
     * @param debug
     *            true to enable debug and verbose logging, false to disable
     *            them.
     */
    public static void init(final Context context) {
        boolean debug = BuildConfig.DEBUG;
        setApplicationTag(generateApplicationTag(context));
        setFilterLevel(debug ? ALL : ASSERT);

    }

    /**
     * Generate the application tag with application name and version name.
     *
     * @param context
     *            the context of the application.
     */
    public static String generateApplicationTag(final Context context) {
        if (context != null) {
            final PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo;
            try {
                packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
                final ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                final String applicationTag = String.format("%s/%s",
                        context.getString(applicationInfo.labelRes),
                        packageInfo.versionName);
                return applicationTag;
            } catch (final PackageManager.NameNotFoundException e) {
                // Never get here.
            }
        }
        return null;
    }

    /**
     * Generate the application tag with application name.
     *
     * @param context
     *            the context of the application.
     */
    public static String generateApplicationTagWithoutVersionName(
            final Context context) {
        if (context != null) {
            final ApplicationInfo applicationInfo = context
                    .getApplicationInfo();
            final String applicationTag = context
                    .getString(applicationInfo.labelRes);
            return applicationTag;
        }
        return null;
    }

}

