package fsexample.fsexample.common;

import android.graphics.Color;

import androidx.core.graphics.ColorUtils;

import java.util.ArrayList;

/**
 * @author Syntarou YOSHIDA
 */
public class MXUtil {

    public static String toHexString2(int i) {
        String str = Integer.toHexString(i).toUpperCase();
        if (str.length() == 1) {
            return "0" + str;
        }
        /*
        if (str.length() >= 3) {
            return str.substring(str.length() - 2, str.length());
        }*/
        return str;
    }

    public static String toHexString4(int i) {
        int hi = (i >> 7) & 0x7f;
        int lo = i & 0x7f;
        return toHexString2(hi) + ":" + toHexString2(lo);
    }

    public static String dumpHex(int[] data) {
        return dumpHex(data, 0, data.length);
    }

    public static String dumpHex(int[] data, int offset, int count) {
        StringBuffer str = new StringBuffer();
        for (int i = offset; i < offset + count; ++i) {
            if (i != 0) {
                str.append(", ");
            }
            str.append(Integer.toHexString(data[i]));
        }
        return str.toString();
    }

    public static String dumpHex(byte[] data) {
        if (data == null) {
            return "nullptr";
        }
        return dumpHex(data, 0, data.length);
    }

    public static String dumpHex(byte[] data, int offset, int count) {
        StringBuffer str = new StringBuffer();
        for (int i = offset; i < offset + count; ++i) {
            if (i != 0) {
                str.append(" ");
            }
            str.append(toHexString2(data[i] & 0xff));
        }
        return str.toString();
    }

    public static String dumpDword(int dword) {
        byte[] data = new byte[4];
        data[0] = (byte) ((dword >> 24) & 0xff);
        data[1] = (byte) ((dword >> 16) & 0xff);
        data[2] = (byte) ((dword >> 8) & 0xff);
        data[3] = (byte) ((dword) & 0xff);
        return dumpHex(data);
    }

    public static boolean isNumberFormat(String text) {
        try {
            MXUtil.numberFromText(text);
            return true;
        } catch (NumberFormatException e) {
        }
        return false;
    }

    public static final int numberFromText(String text) throws NumberFormatException {
        int mum = 10;
        boolean negative = false;

        if (text == null) {
            throw new NumberFormatException("text null cant be number");
        }
        if (text.startsWith("-")) {
            negative = true;
            text = text.substring(1);
        }
        if (text.startsWith("0x")) {
            text = text.substring(2);
            mum = 16;
        }
        if (text.endsWith("h") || text.endsWith("H")) {
            text = text.substring(0, text.length() - 1);
            mum = 16;
        }

        int start = 0;
        int end = text.length();

        if (start >= end) {
            throw new NumberFormatException("length 0");
        }

        int x = 0;
        for (int pos = start; pos < end; ++pos) {
            int ch = text.charAt(pos);
            if (ch >= '0' && ch <= '9') {
                x *= mum;
                x += ch - (char) '0';
            } else if (ch >= 'A' && ch <= 'F' && mum == 16) {
                x *= mum;
                x += ch - (char) 'A' + 10;
            } else if (ch >= 'a' && ch <= 'f' && mum == 16) {
                x *= mum;
                x += ch - (char) 'a' + 10;
            } else {
                throw new NumberFormatException("Format Error '" + text + "'");
            }
        }
        if (negative) {
            return -x;
        }
        return x;
    }

    public static boolean searchTextIgnoreCase(String text, String words) {
        text = text.toLowerCase();
        words = words.toLowerCase();
        if (words.indexOf(' ') < 0) {
            return text.indexOf(words) >= 0;
        }
        ArrayList<String> cells = new ArrayList<>();
        split(words, cells, ' ');
        for (String parts : cells) {
            if (text.indexOf(parts) < 0) {
                return false;
            }
        }
        return true;
    }

    public static void split(String str, ArrayList<String> list, char splitter) {
        list.clear();
        int len = str.length();
        int from = 0;
        for (int i = 0; i < len; ++i) {
            char ch = str.charAt(i);
            if (ch == splitter) {
                list.add(str.substring(from, i));
                from = i + 1;
                continue;
            }
        }
        if (from < len) {
            list.add(str.substring(from, len));
        }
    }

    public static boolean isShrinkTarget(char c) {
        if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
            return true;
        }
        return false;
    }

    public static String shrinkText(String text) {
        if (text == null) {
            return null;
        }
        if (text.length() == 0) {
            return text;
        }
        int start = 0;
        int end = text.length() - 1;
        while (start <= end && isShrinkTarget(text.charAt(start))) {
            start++;
        }
        while (start <= end && isShrinkTarget(text.charAt(end))) {
            end--;
        }
        if (start > end) {
            return "";
        }
        return text.substring(start, end + 1);
    }

    /*
    public static File getAppBaseDirectory() {
         MainActivity.getCurrentActivity().openFileInput(fileName);
         MainActivity.getCurrentActivity().openFileOutput();
     }
     */
    public static String digitalClock(long time) {
        String hour = Long.toString(time / 60 / 60 / 1000);
        String min = Long.toString((time / 60 / 1000) % 60);
        String sec = Long.toString((time / 1000) % 60);
        if (hour.equals("0")) {
            hour = "";
            if (min.equals("0")) {
                min = "";
            }
        }

        if (min.length() >= 1) {
            if (sec.length() == 1) {
                sec = "0" + sec;
            }
        }
        if (hour.length() >= 1) {
            if (min.length() == 1) {
                min = "0" + sec;
            }
        }

        if (hour.length() >= 1) {
            return "" + hour + ":" + min + ":" + sec;
        }
        if (min.length() >= 1) {
            return "" + min + ":" + sec;
        }
        return "" + sec;
    }

    public static int mixtureColor(int left1, int leftPercent, int right1, int rightPercent) {
        Color left = Color.valueOf(left1);
        Color right = Color.valueOf(right1);
        int totalPercent = leftPercent + rightPercent;
        float lr = (left.red() * leftPercent / totalPercent);
        float lg = (left.green() * leftPercent / totalPercent);
        float lb = (left.blue() * leftPercent / totalPercent);
        float rr = (right.red() * rightPercent / totalPercent);
        float rg = (right.green() * rightPercent / totalPercent);
        float rb = (right.blue() * rightPercent / totalPercent);
        return Color.valueOf(lr + rr, lg + rg, lb + rb, 1.0f).toArgb();
    }

    public static int mixtureColor(int left1, int leftPercent, int center1, int centerPercent, int right1, int rightPercent) {
        Color left = Color.valueOf(left1);
        Color center = Color.valueOf(center1);
        Color right = Color.valueOf(right1);
        int totalPercent = leftPercent + centerPercent + rightPercent;
        float lr = (left.red() * leftPercent / totalPercent);
        float lg = (left.green() * leftPercent / totalPercent);
        float lb = (left.blue() * leftPercent / totalPercent);
        float cr = (center.red() * centerPercent / totalPercent);
        float cg = (center.green() * centerPercent / totalPercent);
        float cb = (center.blue() * centerPercent / totalPercent);
        float rr = (right.red() * rightPercent / totalPercent);
        float rg = (right.green() * rightPercent / totalPercent);
        float rb = (right.blue() * rightPercent / totalPercent);
        return Color.valueOf(lr + cr + rr, lg + cg + rg, lb + cb + rb, 1.0f).toArgb();
    }


    public static int mixedColorXYZ(int[] list) {
        double[] mixed = new double[3];
        int count = 0;
        for (int seek : list) {
            if (seek == Color.BLACK) {
                continue;
            }
            double[] conv = new double[3];
            ColorUtils.colorToXYZ(seek, conv);
            int x = ColorUtils.XYZToColor(conv[0], conv[1], conv[2]);

            Color colSeek = Color.valueOf(seek);
            Color colX = Color.valueOf(x);

            /*
            float diffR = colSeek.red() - colX.red();
            float diffG = colSeek.green() - colX.green();
            float diffB = colSeek.blue() - colX.blue();

            if (diffR != 0 || diffG != 0 || diffB != 0) {
                Log.e(TAG, "Color diff = " + diffR + ", " + diffG + ", " + diffB);
            }
            else {
                Log.e(TAG, "Color diff = " + diffR + ", " + diffG + ", " + diffB);
            }*/

            for (int i = 0; i < conv.length; ++i) {
                mixed[i] += conv[i];
            }
            count++;
        }
        for (int i = 0; i < mixed.length; ++i) {
            mixed[i] /= count;
        }
        return ColorUtils.XYZToColor(mixed[0], mixed[1], mixed[2]);
    }
}
