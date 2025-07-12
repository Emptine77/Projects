package edu.dnu.movieplex.common.security.utils;

/**
 * Enum representing various user claims that can be extracted from a JWT token.
 */
public enum UserClaims {
  UUID("sub"),
  EMAIL("email"),
  PHONE_NUMBER("phone_number");

  private final String claimName;

  UserClaims(String claimName) {
    this.claimName = claimName;
  }

  public String getClaimName() {
    return claimName;
  }
}
