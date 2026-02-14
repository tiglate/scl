CREATE OR ALTER VIEW vw_fx_settlement
AS
SELECT
    id_fx_settlement = trd.id_fx_trade,
    counterparty     = trd.counterparty_long_name,
    investor_manager = trd.investor_manager,
    contract_id      = trd.trade_id,
    currency         = IIF(trd.buy_currency_iso_code = 'BRL', trd.sell_currency_iso_code, trd.buy_currency_iso_code),
    trade_type       = IIF(trd.buy_currency_iso_code = 'BRL', 'Financeiro Venda', 'Financeiro Compra'),
    g10_amount       = IIF(trd.buy_currency_iso_code = 'BRL', trd.sell_amount * -1, trd.buy_amount),
    brl_amount       = IIF(trd.buy_currency_iso_code = 'BRL', trd.buy_amount, trd.sell_amount * -1),
    trade_date       = trd.trade_date,
    beneficiary      = trd.beneficiary,
    instruction      = CONVERT(BIT, 0),
    g10              = CONVERT(BIT, 0),
    brl              = CONVERT(BIT, 0),
    ion              = CONVERT(BIT, 0)
FROM
    vw_fx_trade AS trd
WHERE
    trd.product = 'FX_SPOT'
  AND (trd.buy_currency_iso_code = 'BRL' OR trd.sell_currency_iso_code = 'BRL')
GO