package edu.dnu.movieplex.movie.persistance.model.enums;

import java.math.BigDecimal;


/**
 * Enumeration representing different movie formats.
 */
public enum MovieFormat {
    TWOD("2D", new BigDecimal("2.00")),
    THREED("3D", new BigDecimal("10.00")),
    IMAX("IMAX", new BigDecimal("5.00"));
  private final String name;
  private final BigDecimal additionalCost;

  MovieFormat(String name, BigDecimal additionalCost) {
    this.name = name;
    this.additionalCost = additionalCost;
  }

  public String getName() {
    return name;
  }

  public BigDecimal getAdditionalCost() {
    return additionalCost;
  }
}
