CREATE OR ALTER VIEW vw_fx_trade
AS
SELECT
    trd.id_fx_trade,
    trd.trade_id,
    trd.id_buy_currency,
    trd.id_sell_currency,
    trd.id_counterparty,
    trd.id_updated_by,
    ctp.short_name AS counterparty_short_name,
    ctp.long_name  AS counterparty_long_name,
    buy.iso_code   AS buy_currency_iso_code,
    sel.iso_code   AS sell_currency_iso_code,
    usu.name       AS updated_by_name,
    trd.trade_date,
    trd.value_date,
    trd.product,
    trd.buy_amount,
    trd.sell_amount,
    trd.exchange_rate,
    trd.investor_manager,
    trd.beneficiary,
    trd.purpose,
    trd.created_at,
    trd.updated_at
FROM
    tb_fx_trade AS trd

    LEFT JOIN tb_counterparty AS ctp
           ON ctp.id_counterparty = trd.id_counterparty

    LEFT JOIN tb_currency AS buy
           ON buy.id_currency = trd.id_buy_currency

    LEFT JOIN tb_currency AS sel
           ON sel.id_currency = trd.id_sell_currency

    LEFT JOIN tb_user AS usu
           ON usu.id_user = trd.id_updated_by
GO