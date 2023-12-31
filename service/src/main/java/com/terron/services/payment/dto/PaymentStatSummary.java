package com.terron.services.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatSummary {
  private double totalThisMonth;
  private double totalLastMonth;
  private List<GraphData> graphData;
}