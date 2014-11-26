package com.teamdev.arseniuk;

/**
 * Created by dmytro on 11/21/14.
 */
public class asdf {
    public static void main(String[] args) {
        String key = "\\/:*?\"<>|asdflkhjh";
        final String fileName = key.replaceAll("[\\\\/:*?\"<>|]", "_");
        System.out.println(fileName);
    }
}
