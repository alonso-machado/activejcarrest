package com.alonso.active4j.example.http.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
@Entity
@Table(name = "car")
@Cacheable
public class Car {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	@Column(length = 25)
  private String name;
  private String brand;

	@DecimalMin(value = "0.0", inclusive = false)
	@DecimalMax(value = "15000000.00", inclusive = false)
  private BigDecimal manufacturingValue;

  private String description;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public BigDecimal getManufacturingValue() {
    return manufacturingValue;
  }

  public void setManufacturingValue(BigDecimal manufacturingValue) {
    this.manufacturingValue = manufacturingValue;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
