package com.alonso.active4j.example.http.model.dto;

import java.math.BigDecimal;

public record CarRecord(String name, String brand, BigDecimal manufacturingValue, String description) {
}
