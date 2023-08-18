package github.kasuminova.novaeng.common.util;

import java.util.ArrayList;
import java.util.List;

public class StringSortUtils {

    /**
     * <p>根据给定的字符串的匹配率，排序给定的数组。</p>
     * <p>网上抄的（）</p>
     *
     * @param source 要排序的数组
     * @param filter 字符串
     * @return 排序后的列表
     */
    public static List<String> sortWithMatchRate(String[] source, String filter) {
        char[] charArr = filter.toLowerCase().toCharArray();

        String[] result = new String[source.length];
        int[] B = new int[source.length];
        int[] b = new int[source.length];

        int num = 0;
        for (String s : source) {
            char[] charSource = s.toLowerCase().toCharArray();

            for (int x = 0; x < 9; x++) {
                b[x] = 1;
            }

            int m = 0;
            int t = 0;
            for (int j = 0; j < charSource.length; j++) {
                if (j >= charArr.length) {
                    j = charArr.length - 1;
                }
                if (m >= charSource.length) {
                    break;
                }
                for (; m < charSource.length; m++) {
                    if (charSource[m] == charArr[j]) {
                        t = t + 1;

                        b[m] = 1;
                        m = m + 1;
                        break;
                    }
                    if (charSource[m] != charArr[j]) {

                        b[m] = 0;

                    }
                }
            }
            if (t == charArr.length) {
                result[num] = s;
                B[num] = num(b);
                num++;
            }
        }

        String t;
        int tt;
        for (int i = 0; i < result.length; i++) {
            for (int j = i + 1; j < result.length; j++) {
                if (B[j] > B[i]) {
                    t = result[j];
                    result[j] = result[i];
                    result[i] = t;

                    tt = B[j];
                    B[j] = B[i];
                    B[i] = tt;
                }
            }
        }

        return filterEmptyElements(result);
    }

    private static List<String> filterEmptyElements(String[] source) {
        List<String> list = new ArrayList<>();
        for (String s : source) {
            if (s != null && !s.isEmpty()) {
                list.add(s);
            }
        }
        return list;
    }

    private static int num(int[] b) {
        int byteb;
        int num = 0;
        for (int y = 0; y < 9; y++) {
            byteb = (int) (b[y] * Math.pow(10, 8 - y));
            num += byteb;
        }
        return num;
    }
}
