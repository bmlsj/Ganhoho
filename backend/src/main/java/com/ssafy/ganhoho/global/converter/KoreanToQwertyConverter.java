package com.ssafy.ganhoho.global.converter;

public class KoreanToQwertyConverter {
    private static final String[] CHOSEONG = {
            "r", "R", "s", "e", "E", "f", "a", "q", "Q", "t",
            "T", "d", "w", "W", "c", "z", "x", "v", "g"
    };

    private static final String[] JUNGSEONG = {
            "k", "o", "i", "O", "j", "p", "u", "P", "h", "hk",
            "ho", "hl", "y", "n", "nj", "np", "nl", "b", "m", "ml", "l"
    };

    private static final String[] JONGSEONG = {
            "", "r", "R", "rt", "s", "sw", "sg", "e", "f", "fr",
            "fa", "fq", "ft", "fx", "fv", "fg", "a", "q", "qt", "t",
            "T", "d", "w", "c", "z", "x", "v", "g"
    };

    public static String convert(String input) {
        StringBuilder result = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (c >= 0xAC00 && c <= 0xD7A3) { // 한글 범위 확인
                int code = c - 0xAC00;
                int jong = code % 28;
                int jung = (code / 28) % 21;
                int cho = (code / (28 * 21));

                result.append(CHOSEONG[cho])
                        .append(JUNGSEONG[jung])
                        .append(JONGSEONG[jong]);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
