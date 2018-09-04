package com.binance.mybatis.dao;

import java.math.BigDecimal;

import com.binance.mybatis.model.OkexTradeRecord;

public interface OkexExtMapper {
	public OkexTradeRecord selectLastedBuyRecord();
	public BigDecimal selectMyAvgPriceRecord();
	public BigDecimal selectLastBuyPrice();
	public BigDecimal selectCostUsdtByAda();
}
