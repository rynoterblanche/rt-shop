package com.rt.shop;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.collect.ImmutableMap;

public class Shop {
	
	private ImmutableMap<String, BigDecimal> prices;

	public Shop(ImmutableMap<String, BigDecimal> prices) {
		this.prices = prices;
	}
	
	public BigDecimal sell(List<String> items) {
		return items.stream()
				.map(item -> prices.getOrDefault(item, BigDecimal.ZERO))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

}
