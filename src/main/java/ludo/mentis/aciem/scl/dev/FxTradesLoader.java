package ludo.mentis.aciem.scl.dev;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Component;

import ludo.mentis.aciem.scl.domain.FxTrade;
import ludo.mentis.aciem.scl.model.FxTradePurpose;
import ludo.mentis.aciem.scl.model.Product;
import ludo.mentis.aciem.scl.repos.CounterpartyRepository;
import ludo.mentis.aciem.scl.repos.CurrencyRepository;
import ludo.mentis.aciem.scl.repos.FxTradeRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.util.RandomUtils;
import net.datafaker.Faker;

@Component
public class FxTradesLoader implements DataLoaderCommand {
	
	private final Random random;
	private final RandomUtils randomUtils;
	private final UserRepository userRepository;
	private final FxTradeRepository fxTradeRepository;
	private final CurrencyRepository currencyRepository;
	private final CounterpartyRepository counterpartyRepository;
	private final Map<String, BigDecimal> g10RatesVsBrl = new HashMap<>();
	private final Map<String, BigDecimal> g10RatesVsUsd = new HashMap<>();
	
	public record CurrencyPair(String baseCurrency, String quoteCurrency, BigDecimal rate) {}
	
	public FxTradesLoader(final RandomUtils randomUtils,
			              final UserRepository userRepository,
			              final FxTradeRepository fxTradeRepository,
			              final CurrencyRepository currencyRepository,
			              final CounterpartyRepository counterpartyRepository) {
		this.random = new Random();
		this.randomUtils = randomUtils;
		this.userRepository = userRepository;
		this.fxTradeRepository = fxTradeRepository;
		this.currencyRepository = currencyRepository;
		this.counterpartyRepository = counterpartyRepository;

        g10RatesVsBrl.put("USD", new BigDecimal("5.49"));  // United States Dollar
        g10RatesVsBrl.put("EUR", new BigDecimal("6.44"));  // Euro
        g10RatesVsBrl.put("JPY", new BigDecimal("0.038")); // Japanese Yen
        g10RatesVsBrl.put("GBP", new BigDecimal("7.47"));  // British Pound
        g10RatesVsBrl.put("CHF", new BigDecimal("6.89"));  // Swiss Franc
        g10RatesVsBrl.put("CAD", new BigDecimal("4.01"));  // Canadian Dollar
        g10RatesVsBrl.put("AUD", new BigDecimal("3.57"));  // Australian Dollar
        g10RatesVsBrl.put("NZD", new BigDecimal("3.29"));  // New Zealand Dollar
        g10RatesVsBrl.put("SEK", new BigDecimal("0.58"));  // Swedish Krona
        g10RatesVsBrl.put("NOK", new BigDecimal("0.54"));  // Norwegian Krone
        
        g10RatesVsUsd.put("EUR", new BigDecimal("1.17"));   // Euro
        g10RatesVsUsd.put("JPY", new BigDecimal("0.0069")); // Japanese Yen
        g10RatesVsUsd.put("GBP", new BigDecimal("1.36"));   // British Pound
        g10RatesVsUsd.put("CHF", new BigDecimal("1.25"));   // Swiss Franc
        g10RatesVsUsd.put("CAD", new BigDecimal("0.73"));   // Canadian Dollar
        g10RatesVsUsd.put("AUD", new BigDecimal("0.65"));   // Australian Dollar
        g10RatesVsUsd.put("NZD", new BigDecimal("0.60"));   // New Zealand Dollar
        g10RatesVsUsd.put("SEK", new BigDecimal("0.11"));   // Swedish Krona
        g10RatesVsUsd.put("NOK", new BigDecimal("0.099"));  // Norwegian Krone
	}

	@Override
	public int getOrder() {
		return 2;
	}

	@Override
	public String getName() {
		return "FX Trades";
	}

	@Override
	public boolean canItRun() {
		return fxTradeRepository.count() == 0;
	}

	@Override
	public int run() {
		var count = 0;
		var faker = new Faker();
		var totalCounterparties = counterpartyRepository.count();
		var totalUsers = userRepository.count();
		
		for (;count < 300; count++) {
			var fxTrade = new FxTrade();
			var product = randomUtils.pickRandomEnumValue(Product.class);
			var tradeDate = randomUtils.getRandomDate(LocalDate.now().minusMonths(1), LocalDate.now());
			LocalDate valueDate;
			CurrencyPair pair;

			switch (product) {
			case FX_SPOT:
				valueDate = tradeDate.plusDays(1);
				pair = getRandomCurrencyPair(null);
				break;
			case FX_FORWARD:
				valueDate = tradeDate.plusDays(faker.random().nextInt(4, 180));
				pair = getRandomCurrencyPair(null);
				break;
			case NDF:
				valueDate = tradeDate.plusDays(faker.random().nextInt(180, 360));
				pair = getRandomCurrencyPair(true);
				break;
			default:
				valueDate = tradeDate.plusDays(faker.random().nextInt(4, 30));
				pair = getRandomCurrencyPair(null);
				break;
			}

			fxTrade.setTradeId("TRD-" + faker.random().nextInt(10000, 99999));
			fxTrade.setProduct(product);
			fxTrade.setTradeDate(tradeDate);
			fxTrade.setValueDate(valueDate);
			
			fxTrade.setBuyCurrency(currencyRepository.findByIsoCodeIgnoreCase(pair.baseCurrency).orElseThrow());
			fxTrade.setSellCurrency(currencyRepository.findByIsoCodeIgnoreCase(pair.quoteCurrency).orElseThrow());
			
			var buyAmount = BigDecimal.valueOf(random.nextInt(1000000, 10000000));
			var sellAmount = buyAmount.divide(pair.rate, RoundingMode.CEILING);
			fxTrade.setBuyAmount(buyAmount);
			fxTrade.setSellAmount(sellAmount);
			fxTrade.setExchangeRate(pair.rate);
			fxTrade.setInvestorManager(faker.company().name());
			fxTrade.setBeneficiary(faker.artist().name());
			
			fxTrade.setPurpose(randomUtils.pickRandomEnumValue(FxTradePurpose.class));
			
			fxTrade.setCounterparty(counterpartyRepository.findById(random.nextLong(1, totalCounterparties)).orElseThrow());
			fxTrade.setUpdatedBy(userRepository.findById(random.nextLong(1, totalUsers)).orElseThrow());
			
			fxTradeRepository.save(fxTrade);
		}
		return count;
	}
	
	public CurrencyPair getRandomCurrencyPair(Boolean useUsdAsBase) {
		if (useUsdAsBase == null) {
	        useUsdAsBase = random.nextBoolean();	
		}
        String baseCurrency;
        Map<String, BigDecimal> ratesToUse;
        if (useUsdAsBase) {
            baseCurrency = "USD";
            ratesToUse = g10RatesVsUsd;
        } else {
            baseCurrency = "BRL";
            ratesToUse = g10RatesVsBrl;
        }
        var quoteCurrencies = new ArrayList<>(ratesToUse.keySet());
        var quoteCurrency = quoteCurrencies.get(random.nextInt(quoteCurrencies.size()));
        var rate = ratesToUse.get(quoteCurrency);
        return new CurrencyPair(baseCurrency, quoteCurrency, rate);
    }
}
