package com.rt.shop;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;

public class Shop {

	private ImmutableMap<String, BigDecimal> prices;
	private ImmutableMap<String, BiFunction<BigDecimal, BigDecimal, BigDecimal>> offers = null;
	
	private BiFunction<String, BigDecimal, BigDecimal> getPrice = (item, qty) -> prices.getOrDefault(item, BigDecimal.ZERO).multiply(qty);
	private BiFunction<String, BigDecimal, BigDecimal> getDiscount = (item, qty)  -> offers.getOrDefault(item, (a, b) -> BigDecimal.ZERO)
			.apply(prices.get(item), qty);

	public Shop(ImmutableMap<String, BigDecimal> prices) {
		this.prices = prices;
	}

	public Shop withOffers(ImmutableMap<String, BiFunction<BigDecimal, BigDecimal, BigDecimal>> offers) {
		this.offers = offers;
		return this;
	}

	public BigDecimal sell(List<String> items) {
		return getFinalTotalPricePerItem(items).entrySet().stream()
                .map(e -> e.getValue())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_EVEN);
	}

	public Map<String, BigDecimal> getItemsGroupedAndTotalled(List<String> items) {
		return items.stream().collect(
				Collectors.groupingBy(item -> item, 
						Collectors.reducing(BigDecimal.ZERO, a -> BigDecimal.ONE, BigDecimal::add)));
	}
	
	public Map<String, BigDecimal> getTotalPricePerItem(List<String> items) {
		return getItemsGroupedAndTotalled(items).entrySet().stream().collect(
				Collectors.toMap(
						e -> e.getKey(), 
						e -> getPrice.apply(e.getKey(), e.getValue())));
	}
	
	public Map<String, BigDecimal> getTotalDiscountPerItem(List<String> items) {
		if (offers == null)
			return Collections.emptyMap();
		
		return getItemsGroupedAndTotalled(items).entrySet().stream().collect(
				Collectors.toMap(
						e -> e.getKey(), 
						e -> getDiscount.apply(e.getKey(), e.getValue())));
	}
		
	public Map<String, BigDecimal> getFinalTotalPricePerItem(List<String> items) {
		return Stream.of(getTotalPricePerItem(items), getTotalDiscountPerItem(items))
			.map(Map::entrySet)
			.flatMap(Collection::stream)
			.collect(
					Collectors.toMap(
							Map.Entry::getKey,
							Map.Entry::getValue,
							BigDecimal::subtract)
						);
	}
	
}
