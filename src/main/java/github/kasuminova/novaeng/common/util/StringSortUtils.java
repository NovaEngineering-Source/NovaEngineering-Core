package github.kasuminova.novaeng.common.util;

import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class StringSortUtils {

    /**
     * <p>根据给定的字符串的匹配率，排序给定的数组。</p>
     * <p>在开头被匹配的字符越多，匹配率越高，越往后越低。</p>
     * <p>算法相对暴力...</p>
     *
     * @param source 要排序的数组
     * @param filter      字符串
     * @return 排序后的列表
     */
    public static List<String> sortWithMatchRate(final Collection<String> source, final String filter) {
        PriorityQueue<MatchResult> sorted = new PriorityQueue<>();

        char[] filterCharArr = filter.replace(" ", "").toLowerCase().toCharArray();

        for (final String s : source) {
            String str = s.replace(" ", "").toLowerCase();

            char[] targetCharArr = str.toCharArray();

            int matchRate = getMatchRate(filterCharArr, targetCharArr);
            if (matchRate > 0) {
                sorted.add(new MatchResult(s, matchRate));
            }
        }

        return sorted.stream().map(e -> e.str).collect(Collectors.toList());
    }

    private static int getMatchRate(final char[] filterCharArr, final char[] targetCharArr) {
        int matchRate = 0;
        int matchedCharCount = 0;
        int targetIndex = 0;

        filter:
        for (final char c : filterCharArr) {
            for (int i = targetIndex; i < targetCharArr.length; i++) {
                char tc = targetCharArr[i];
                if (c == tc) {
                    matchRate += targetCharArr.length - targetIndex + 1;

                    targetIndex++;
                    matchedCharCount++;
                    continue filter;
                }
            }
        }

        if (targetCharArr.length >= filterCharArr.length && matchedCharCount == filterCharArr.length) {
            return Integer.MAX_VALUE;
        }
        return matchRate;
    }

    public static class MatchResult implements Comparable<MatchResult> {
        private final String str;
        private final int matchRate;

        public MatchResult(final String str, final int matchRate) {
            this.str = str;
            this.matchRate = matchRate;
        }

        @Override
        public int compareTo(final MatchResult o) {
            return Integer.compare(o.matchRate, matchRate);
        }
    }
}
