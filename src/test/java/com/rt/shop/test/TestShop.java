package com.rt.shop.test;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.rt.shop.Shop;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

public class TestShop {
	
	Shop shop;
	ImmutableMap<String, BigDecimal> prices;

	@Before
	public void init() {
		prices = new ImmutableMap.Builder<String, BigDecimal>()
				.put("Apple", BigDecimal.valueOf(.60))
				.put("Orange", BigDecimal.valueOf(.25))
				.build();
		shop = new Shop(prices);
	}

	@Test
	public void shouldSellItemsAtCorrectTotalCost() {
		assertThat(shop.sell(Arrays.asList("Apple", "Orange"))).isEqualTo(BigDecimal.valueOf(.85));
		assertThat(shop.sell(Arrays.asList("Apple", "Apple", "Orange", "Apple"))).isEqualTo(BigDecimal.valueOf(2.05));
	}
	
	@Test
	public void shouldReturnZeroCostForEmptyItemList() {
		assertThat(shop.sell(Collections.emptyList())).isEqualTo(BigDecimal.ZERO);
	}
	
	@Test
	public void shouldIgnoreUnknownItemsAndNotFail() {
		assertThat(shop.sell(Arrays.asList("Apple", "SomeItem"))).isEqualTo(BigDecimal.valueOf(.60));
	}
	
}
