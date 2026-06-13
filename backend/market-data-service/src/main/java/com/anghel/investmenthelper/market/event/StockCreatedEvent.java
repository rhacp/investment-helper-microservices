package com.anghel.investmenthelper.market.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockCreatedEvent {

    private final String ticker;
}
