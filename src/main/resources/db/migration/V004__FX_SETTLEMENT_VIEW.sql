CREATE OR ALTER VIEW vw_fx_settlement
AS
SELECT
    id_fx_settlement = ISNULL(stl.id_fx_settlement, trd.id_fx_trade * -1),
    id_fx_trade      = trd.id_fx_trade,
    id_counterparty  = trd.id_counterparty,
    counterparty     = trd.counterparty_long_name,
    investor_manager = trd.investor_manager,
    contract_id      = trd.trade_id,
    currency         = IIF(trd.buy_currency_iso_code = 'BRL', trd.sell_currency_iso_code, trd.buy_currency_iso_code),
    trade_type       = IIF(trd.buy_currency_iso_code = 'BRL', 'Financeiro Venda', 'Financeiro Compra'),
    g10_amount       = IIF(trd.buy_currency_iso_code = 'BRL', trd.sell_amount * -1, trd.buy_amount),
    brl_amount       = IIF(trd.buy_currency_iso_code = 'BRL', trd.buy_amount, trd.sell_amount * -1),
    trade_date       = trd.trade_date,
    beneficiary      = trd.beneficiary,
    instruction      = ISNULL(stl.ins_flag, 0),
    g10              = ISNULL(stl.g10_flag, 0),
    brl              = ISNULL(stl.brl_flag, 0),
    ion              = ISNULL(stl.ion_flag, 0)
FROM
    vw_fx_trade AS trd

    LEFT JOIN tb_fx_settlement AS stl
           ON stl.id_fx_trade = trd.id_fx_trade
WHERE
      (trd.product = 'FX_SPOT')
  AND (trd.buy_currency_iso_code = 'BRL' OR trd.sell_currency_iso_code = 'BRL')
GO