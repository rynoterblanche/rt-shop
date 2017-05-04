package com.rt.shop.test;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.rt.shop.Shop;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.BiFunction;

public class TestShop {
	
	static final String ORANGE = "Orange";
	static final String APPLE = "Apple";
	
	Shop shop;
	ImmutableMap<String, BigDecimal> shopItems;
	ImmutableMap<String, BiFunction<BigDecimal, BigDecimal, BigDecimal>> shopOffers;
	
	// Here we can supply any type of discount calculations we require
	static final BiFunction<BigDecimal, BigDecimal, BigDecimal> TWO_FOR_ONE = (price, qty) -> price.multiply(qty.divideToIntegralValue(new BigDecimal(2)));
	static final BiFunction<BigDecimal, BigDecimal, BigDecimal> THREE_FOR_TWO = (price, qty) -> price.multiply(qty.divideToIntegralValue(new BigDecimal(3)));
	
	@Before
	public void init() {
		shopItems = new ImmutableMap.Builder<String, BigDecimal>()
				.put(APPLE, BigDecimal.valueOf(.60))
				.put(ORANGE, BigDecimal.valueOf(.25))
				.build();
		shopOffers = new ImmutableMap.Builder<String, BiFunction<BigDecimal, BigDecimal, BigDecimal>>()
				.put(APPLE, TWO_FOR_ONE)
				.put(ORANGE, THREE_FOR_TWO)
				.build();
		shop = new Shop(shopItems);
	}

	@Test
	public void shouldSellItemsAtCorrectTotalCost() {
		assertThat(shop.sell(Arrays.asList(APPLE, ORANGE))).isEqualTo(BigDecimal.valueOf(.85).setScale(2));
		assertThat(shop.sell(Arrays.asList(APPLE, APPLE, ORANGE, APPLE))).isEqualTo(BigDecimal.valueOf(2.05).setScale(2));
	}
	
	@Test
	public void shouldReturnZeroCostForEmptyItemList() {
		assertThat(shop.sell(Collections.emptyList())).isEqualTo(BigDecimal.ZERO.setScale(2));
	}
	
	@Test
	public void shouldIgnoreUnknownItemsAndNotFail() {
		assertThat(shop.sell(Arrays.asList(APPLE, "SomeItem"))).isEqualTo(BigDecimal.valueOf(.60).setScale(2));
	}
	
	@Test
	public void shouldSellItemsOnOfferAtCorrectTotalCost() {
		assertThat(shop.withOffers(shopOffers).sell(Arrays.asList(APPLE, APPLE))).isEqualTo(BigDecimal.valueOf(.60).setScale(2));
		assertThat(shop.withOffers(shopOffers).sell(Arrays.asList(APPLE, APPLE, APPLE))).isEqualTo(BigDecimal.valueOf(1.20).setScale(2));
		assertThat(shop.withOffers(shopOffers).sell(Arrays.asList(APPLE, APPLE, APPLE, APPLE))).isEqualTo(BigDecimal.valueOf(1.20).setScale(2));
		assertThat(shop.withOffers(shopOffers).sell(Arrays.asList(ORANGE, ORANGE, ORANGE))).isEqualTo(BigDecimal.valueOf(.50).setScale(2));
		assertThat(shop.withOffers(shopOffers).sell(Arrays.asList(ORANGE, ORANGE, ORANGE, APPLE, APPLE))).isEqualTo(BigDecimal.valueOf(1.10).setScale(2));
	}
}
