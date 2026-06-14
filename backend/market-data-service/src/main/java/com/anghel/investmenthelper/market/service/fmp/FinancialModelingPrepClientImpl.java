package com.anghel.investmenthelper.market.service.fmp;

import com.anghel.investmenthelper.market.exception.FinancialModelingPrepException;
import com.anghel.investmenthelper.market.exception.ResourceNotFoundException;
import com.anghel.investmenthelper.market.model.dto.fmp.FinancialModelingPrepHistoricalPriceDTO;
import com.anghel.investmenthelper.market.model.dto.fmp.FinancialModelingPrepProfileDTO;
import com.anghel.investmenthelper.market.util.property.FinancialModelingPrepProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinancialModelingPrepClientImpl implements FinancialModelingPrepClient {

    private final RestClient financialModelingPrepRestClient;

    private final FinancialModelingPrepProperties properties;

    @Override
    public FinancialModelingPrepProfileDTO getStockProfile(String ticker) {
        try {
            FinancialModelingPrepProfileDTO[] response = financialModelingPrepRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/stable/profile")
                            .queryParam("symbol", ticker)
                            .queryParam("apikey", properties.getApiKey())
                            .build())
                    .retrieve()
                    .body(FinancialModelingPrepProfileDTO[].class);

            if (response == null || response.length == 0) {
                throw new ResourceNotFoundException("Stock profile not found: " + ticker);
            }

            log.debug("Fetched stock profile from Financial Modeling Prep [ticker={}]", ticker);
            return response[0];
        } catch (ResourceNotFoundException exception) {
            log.debug("Financial Modeling Prep returned no profile data [ticker={}]", ticker);
            throw exception;
        } catch (RestClientException exception) {
            log.error("Failed to fetch stock profile from Financial Modeling Prep [ticker={}]", ticker, exception);
            throw new FinancialModelingPrepException("Failed to fetch stock profile from Financial Modeling Prep: " + ticker, exception);
        }
    }

    @Override
    public List<FinancialModelingPrepHistoricalPriceDTO> getHistoricalPrices(String ticker, LocalDate startDate, LocalDate endDate) {
        try {
            FinancialModelingPrepHistoricalPriceDTO[] response = financialModelingPrepRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/stable/historical-price-eod/full")
                            .queryParam("symbol", ticker)
                            .queryParam("from", startDate)
                            .queryParam("to", endDate)
                            .queryParam("apikey", properties.getApiKey())
                            .build())
                    .retrieve()
                    .body(FinancialModelingPrepHistoricalPriceDTO[].class);

            if (response == null || response.length == 0) {
                throw new ResourceNotFoundException("Historical prices not found: " + ticker);
            }

            log.debug(
                    "Fetched historical prices from Financial Modeling Prep [ticker={}, records={}]",
                    ticker,
                    response.length
            );

            return List.of(response);
        } catch (ResourceNotFoundException exception) {
            log.debug(
                    "Financial Modeling Prep returned no historical data [ticker={}, from={}, to={}]",
                    ticker,
                    startDate,
                    endDate
            );
            throw exception;
        } catch (RestClientException exception) {
            log.error("Failed to fetch historical prices from Financial Modeling Prep [ticker={}]", ticker, exception);
            throw new FinancialModelingPrepException("Failed to fetch historical prices from Financial Modeling Prep: " + ticker, exception);
        }
    }

    @Override
    public FinancialModelingPrepHistoricalPriceDTO getHistoricalPriceForDate(String ticker, LocalDate date) {
        return getHistoricalPrices(ticker, date, date).getFirst();
    }

    @Override
    public FinancialModelingPrepHistoricalPriceDTO getLatestHistoricalPrice(String ticker) {
        return getHistoricalPrices(ticker, LocalDate.now().minusDays(10), LocalDate.now()).stream()
                .max(Comparator.comparing(FinancialModelingPrepHistoricalPriceDTO::getDate))
                .orElseThrow(() -> new ResourceNotFoundException("Latest historical price not found: " + ticker));
    }
}
