package com.group22.mobility.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Decimal128;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@NoArgsConstructor
public class MongoMaintenanceCostRow {

    private String stationAddress;
    private String vehicleVin;
    private String vehicleModel;
    private Long totalLogs;
    private BigDecimal totalCost;
    private BigDecimal avgCost;

    // ── Custom setters to handle MongoDB Decimal128 ───────────

    public void setTotalCost(Object value) {
        this.totalCost = convert(value);
    }

    public void setAvgCost(Object value) {
        this.avgCost = convert(value);
    }

    // Keep normal BigDecimal setters too for direct construction
    public void setTotalCostDirect(BigDecimal value) {
        this.totalCost = value;
    }

    public void setAvgCostDirect(BigDecimal value) {
        this.avgCost = value;
    }

    private BigDecimal convert(Object value) {
        if (value == null)
            return BigDecimal.ZERO;
        if (value instanceof BigDecimal bd)
            return bd;
        if (value instanceof Decimal128 d)
            return d.bigDecimalValue();
        if (value instanceof Double d)
            return BigDecimal.valueOf(d);
        if (value instanceof Integer i)
            return BigDecimal.valueOf(i);
        if (value instanceof Long l)
            return BigDecimal.valueOf(l);
        try {
            return new BigDecimal(value.toString());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public String getTotalCostFormatted() {
        if (totalCost == null)
            return "0.00";
        return totalCost.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public String getAvgCostFormatted() {
        if (avgCost == null)
            return "0.00";
        return avgCost.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
