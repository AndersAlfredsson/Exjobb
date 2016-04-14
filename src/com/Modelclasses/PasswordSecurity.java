package com.Modelclasses;

import sun.plugin2.message.Message;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

/**
 * Created by Gustav on 2016-04-14.
 * The class that takes a password and makes it secure for saving on the database
 */
public class PasswordSecurity
{
    public PasswordSecurity(ApplicationUser user)
    {
        hashPassword(user);
    }

    private boolean hashPassword(ApplicationUser user)
    {
        byte[] salt = generateSalt();
        try
        {
            //Gets the hashing algorithm
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            try
            {
                byte[] hash = user.getPassword().getBytes("UTF-8");
                byte[] hashWithSalt = new byte[hash.length + salt.length];
                System.arraycopy(hash, 0, hashWithSalt, 0, hash.length);
                System.arraycopy(salt, 0, hashWithSalt, hash.length, salt.length);

                messageDigest.update(hashWithSalt);
                byte[] digestedPassword = messageDigest.digest();
                String hs = new String(digestedPassword, "UTF-8");


                user.setSalt(new String(salt, "UTF-8"));
                user.setPassword(hs);

                System.out.println("password: " + new String(hash, "UTF-8"));
                System.out.println("salt: " + new String(salt, "UTF-8"));
                System.out.println("hash+salt: " + hs);
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return false;
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Generates a 64 byte long random salt for use when hashing the password
     */
    private byte[] generateSalt()
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
        }
        return null;
    }
}
