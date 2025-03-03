package kg.attractor.java.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CodeGenerator {
    public static String makeCode(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return convertToString(md.digest(input.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String convertToString(byte[] array) {
        return IntStream.range(0, array.length / 4)
                .map(i -> array[i])
                .map(i -> (i < 0) ? i + 127 : i)
                .mapToObj(Integer::toHexString)
                .collect(Collectors.joining());
    }

}
