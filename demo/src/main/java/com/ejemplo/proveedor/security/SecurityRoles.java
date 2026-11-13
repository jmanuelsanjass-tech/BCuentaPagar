package com.ejemplo.proveedor.security;

import java.util.Locale;

public final class SecurityRoles {

  private static final String ROLE_PREFIX = "ROLE_";

  private SecurityRoles() {}

  public static String normalizeName(String roleName) {
    String normalized = roleName.trim().toUpperCase(Locale.ROOT);
    return normalized.startsWith(ROLE_PREFIX)
        ? normalized.substring(ROLE_PREFIX.length())
        : normalized;
  }

  public static String authority(String roleName) {
    return ROLE_PREFIX + normalizeName(roleName);
  }
}
