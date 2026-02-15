package ludo.mentis.aciem.scl.domain;

import jakarta.persistence.*;
import ludo.mentis.aciem.scl.model.FxTradePurpose;
import ludo.mentis.aciem.scl.model.Product;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Immutable
@Table(name = "vw_fx_trade")
public class FxTradeView {
	
	@Id
	@Column(name = "id_fx_trade")
	private Long id;

	@Column(name = "trade_id")
	private String tradeId;

	@Column(name = "trade_date")
	private LocalDate tradeDate;

	@Column(name = "value_date")
	private LocalDate valueDate;

	@Column(name = "id_buy_currency")
	private Long buyCurrencyId;

	@Column(name = "buy_currency_iso_code")
	private String buyCurrencyIso;

	@Column(name = "buy_amount")
	private BigDecimal buyAmount;

	@Column(name = "id_sell_currency")
	private Long sellCurrencyId;

	@Column(name = "sell_currency_iso_code")
	private String sellCurrencyIso;

	@Column(name = "sell_amount")
	private BigDecimal sellAmount;

	@Enumerated(EnumType.STRING)
	@Column(name = "product")
	private Product product;

	@Enumerated(EnumType.STRING)
	@Column(name = "purpose")
	private FxTradePurpose purpose;

	@Column(name = "id_counterparty")
	private Long counterpartyId;

	@Column(name = "counterparty_short_name")
	private String counterpartyShortName;
	
	@Column(name = "counterparty_long_name")
	private String counterpartyLongName;
	
	@Column(name = "id_updated_by")
	private Long updatedById;
	
	@Column(name = "updated_by_name")
	private String updatedByName;

	@Column(name = "exchange_rate")
	private BigDecimal exchangeRate;
	
	@Column(name = "investor_manager")
	private String investorManager;

	@Column(name = "beneficiary")
	private String beneficiary;

	public Long getId() {
		return id;
	}

	public String getTradeId() {
		return tradeId;
	}

	public LocalDate getTradeDate() {
		return tradeDate;
	}

	public LocalDate getValueDate() {
		return valueDate;
	}

	public Long getBuyCurrencyId() {
		return buyCurrencyId;
	}

	public String getBuyCurrencyIso() {
		return buyCurrencyIso;
	}

	public BigDecimal getBuyAmount() {
		return buyAmount;
	}

	public Long getSellCurrencyId() {
		return sellCurrencyId;
	}

	public String getSellCurrencyIso() {
		return sellCurrencyIso;
	}

	public BigDecimal getSellAmount() {
		return sellAmount;
	}

	public Product getProduct() {
		return product;
	}

	public FxTradePurpose getPurpose() {
		return purpose;
	}

	public Long getCounterpartyId() {
		return counterpartyId;
	}

	public String getCounterpartyShortName() {
		return counterpartyShortName;
	}

	public String getCounterpartyLongName() {
		return counterpartyLongName;
	}

	public Long getUpdatedById() {
		return updatedById;
	}

	public String getUpdatedByName() {
		return updatedByName;
	}

	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public String getInvestorManager() {
		return investorManager;
	}

	public String getBeneficiary() {
		return beneficiary;
	}
}