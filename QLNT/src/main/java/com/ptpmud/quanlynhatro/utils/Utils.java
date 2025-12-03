package com.ptpmud.quanlynhatro.utils;

import java.awt.Component;
import java.awt.Container;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author Bùi Thanh Quân - int710 - CT070242
 */
public class Utils {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public static String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }
    public static boolean checkPassword(String password, String hashed) {
        return passwordEncoder.matches(password, hashed);
    }
    
    public static Component findComponentByName(Container root, String name) {
        for (Component c : root.getComponents()) {
            if (name.equals(c.getName())) return c;
            if (c instanceof Container) {
                Component r = findComponentByName((Container) c, name);
                if (r != null) return r;
            }
        }
        return null;
    }
    
}