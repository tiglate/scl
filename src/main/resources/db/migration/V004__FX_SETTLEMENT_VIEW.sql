CREATE OR ALTER VIEW vw_fx_settlement
AS
WITH cte_step AS (
    SELECT
        a.id_fx_settlement,
        b.*
    FROM
        tb_fx_settlement_steps AS a

        INNER JOIN tb_fx_settlement_step AS b
                  ON a.id_fx_settlement_step = b.id_fx_settlement_step
)
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
    instruction      = CONVERT(BIT, IIF(ins.id_fx_settlement_step IS NULL, 0, 1)),
    g10              = CONVERT(BIT, IIF(g10.id_fx_settlement_step IS NULL, 0, 1)),
    brl              = CONVERT(BIT, IIF(brl.id_fx_settlement_step IS NULL, 0, 1)),
    ion              = CONVERT(BIT, IIF(ion.id_fx_settlement_step IS NULL, 0, 1))
FROM
    vw_fx_trade AS trd

    LEFT JOIN tb_fx_settlement AS stl
           ON stl.id_fx_trade = trd.id_fx_trade

    LEFT JOIN cte_step AS ins
           ON ins.id_fx_settlement = stl.id_fx_settlement
          AND ins.step             = 'INSTRUCTION_RECEIVED'

    LEFT JOIN cte_step AS g10
           ON g10.id_fx_settlement = stl.id_fx_settlement
          AND g10.step             = 'RECEIVED_OR_PAID_FOREIGN_CURRENCY'

    LEFT JOIN cte_step AS brl
           ON brl.id_fx_settlement = stl.id_fx_settlement
          AND brl.step             = 'RECEIVED_OR_PAID_LOCAL_CURRENCY'

    LEFT JOIN cte_step AS ion
           ON ion.id_fx_settlement = stl.id_fx_settlement
          AND ion.step             = 'UPSTREAM_RELEASE_OR_CONFIRMATION'
WHERE
      (trd.product = 'FX_SPOT')
  AND (trd.buy_currency_iso_code = 'BRL' OR trd.sell_currency_iso_code = 'BRL')
GO