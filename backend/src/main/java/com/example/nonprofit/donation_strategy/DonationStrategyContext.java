package com.example.nonprofit.donation_strategy;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Component;

@Component
public class DonationStrategyContext {

    private final Map<String, DonationStrategy> strategies;

    private DonationStrategy strategy;
    private String strategyName;

    public DonationStrategyContext(List<DonationStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        s -> s.getStrategyName().toUpperCase(),
                        Function.identity()
                ));
    }

    public void setStrategy(String strategyName) {

        if (strategyName == null) {
            throw new IllegalArgumentException("Strategy is required");
        }

        DonationStrategy selected = strategies.get(strategyName.toUpperCase());

        if (selected == null) {
            throw new IllegalArgumentException("Invalid donation type");
        }

        this.strategy = selected;
        this.strategyName = strategyName;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public void processDonation() {

        if (strategy == null) {
            throw new IllegalStateException("No strategy selected");
        }

        strategy.processDonation();
    }
}