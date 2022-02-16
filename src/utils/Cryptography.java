package utils;

import dataclasses.Data;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Cryptography {
    public static MessageDigest messageDigest;

    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static BigInteger getSHA256HashFromString(String string){
        return new BigInteger(1, messageDigest.digest(string.getBytes(StandardCharsets.UTF_8)));
    }

    public static String getHexFromHash(BigInteger hash){
        StringBuilder builder = new StringBuilder(hash.toString(16));

        // add 0's padding
        while(builder.length() < 32)
            builder.insert(0, '0');

        return builder.toString();
    }

    public static BigInteger getSHA256HashUsingMerkelTree(List<? extends Data> data){
        List<String> hashes = new ArrayList<>();

        for(Object d : data) {
            hashes.add(((Data) d).getStringHash());
        }

        String lastHash = null;
        if((hashes.size() % 2) == 1){
            // the number of "leaves" is odd.
            // we remove the last one to get it even,
            // and we will combine it at the end of the process.
            lastHash = hashes.remove(hashes.size() - 1);
        }

        while(hashes.size() > 1){

            for(int i = 0; i <= (hashes.size() / 2); ++i){
                BigInteger newHash = Cryptography.getSHA256HashFromString(hashes.remove(i) + hashes.remove(i));
                hashes.add(i, Cryptography.getHexFromHash(newHash));
            }
        }

        if(lastHash != null)
            return Cryptography.getSHA256HashFromString(hashes.get(0) + lastHash);

        return new BigInteger(hashes.get(0), 16);
    }

    public static boolean isSignatureValid(BigInteger computedHash, String signedHash, String publicKey){
        // todo implement method
        // should tell if the "signedHash" correspond to the "computedHash" signed with the given key ("publicKey").
        return true;
    }

    public static String signMessage(String message, String privateKey){
        // todo implement method
        // returned the "message" signed with the given key ("privateKey").
        return null;
    }
}
