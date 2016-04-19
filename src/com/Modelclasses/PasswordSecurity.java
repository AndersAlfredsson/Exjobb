package com.Modelclasses;

import com.sun.deploy.util.ArrayUtil;
import com.sun.org.apache.xpath.internal.SourceTree;
import com.sun.xml.internal.ws.commons.xmlutil.Converter;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by Gustav on 2016-04-14.
 * The class that takes a password and makes it secure for saving on the database
 * also does authentication on login attempt
 */
public class PasswordSecurity
{
    /**
     * Constructor that takes an ApplicationUser object
     */
    public PasswordSecurity()
    {

    }

    /**
     * Main function for hashing a password and adding a salt before hashing
     * @param user
     * @return
     */
    public static boolean hashPassword(ApplicationUser user)
    {
        try
        {
            byte[] password = user.getPassword().getBytes();
            byte[] salt = generateSalt();
            byte[] concatenated = concatenateArrays(password, salt);
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(concatenated);
            messageDigest.reset();

            String s = convertToString(salt);
            user.setPassword(convertToString(hash));
            user.setSalt(s);
            return true;
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Function for hashing a password with an already existing salt
     * @param user
     * @return
     */
    private static boolean hashPasswordWithExistingSalt(ApplicationUser user)
    {
        try
        {
            byte[] password = user.getPassword().getBytes();
            byte[] salt = convertToByte(user.getSalt());
            byte[] concatenated = concatenateArrays(password, salt);
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(concatenated);
            messageDigest.reset();

            String s = convertToString(salt);
            user.setPassword(convertToString(hash));
            user.setSalt(s);
            return true;
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Should get the salt from the database to convert it back to a byte array to be able to compare password
     * @param salt
     * @return
     */
    private static byte[] convertToByte(String salt)
    {
        char[] charSet = salt.toCharArray();
        byte[] byteArray = new byte[charSet.length/2];

        for(int i = 0; i < charSet.length; i+=2)
        {
            StringBuilder curr = new StringBuilder(2);
            curr.append(charSet[i]).append(charSet[i+1]);
            byteArray[i/2] = (byte) Integer.parseInt(curr.toString(), 16);
        }
        return byteArray;
    }

    /**
     * Converts an array of bytes to a readable string
     * @param bytes
     * @return
     */
    private static String convertToString(byte[] bytes)
    {
        StringBuffer sb = new StringBuffer();
        char[] charSet = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        for(int i = 0; i < bytes.length; i++)
        {
            byte b = bytes[i];
            sb.append(charSet[(b&0xF0) >> 4]);
            sb.append(charSet[b&0x0F]);
        }
        return sb.toString();
    }

    /**
     * Takes a user with the password in clear text, salt from DB and password from DB
     * it should hash the password from user with the salt from DB and then compare if it
     * is the same as the password from the DB
     * @param user
     * @param DbUser
     * @return
     */
    public static boolean authenticate(ApplicationUser user, ApplicationUser DbUser)
    {
        user.setSalt(DbUser.getSalt());
        hashPasswordWithExistingSalt(user);
        String pw = user.getPassword();
        if(DbUser.getPassword().equals(pw) && DbUser.getEmail().equals(user.getEmail()))
        {
            //System.out.println("Success!");
            return true;
        }
        else
        {
            //System.out.println("Failure!");
            return false;
        }
    }

    /**
     * Takes two byte arrays and puts one at the end of the other to make a new array with both and returns it
     * @param hash
     * @param salt
     * @return
     */
    private static byte[] concatenateArrays(byte[] hash, byte[] salt)
    {
        byte[] hashWithSalt = new byte[hash.length + salt.length];

        System.arraycopy(hash, 0, hashWithSalt, 0, hash.length);
        System.arraycopy(salt, 0, hashWithSalt, hash.length, salt.length);
        return hashWithSalt;
    }


    /**
     * Generates a 64 byte long random salt for use when hashing the password
     */
    private static byte[] generateSalt()
    {
        byte[] salt = new byte[64];
        try
        {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.nextBytes(salt);
            return salt;
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
