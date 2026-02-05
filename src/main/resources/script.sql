SELECT
    [Titular]                = counterparty_short_name,
    [Contrato]               = trade_id,
    [Moeda]                  = buy_currency_iso_code,
    [Valor-ME]               = buy_amount,
    [Valor-MN-Liquido-S-IR]  = sell_amount,
    [Datalq]                 = trade_date,
    [Pagador]                = investor_manager,
    [Titular Original/Final] = beneficiary,
    [Instruction]            = NULL,
    [Subcust Approval]       = NULL,
    [G10]                    = NULL,
    [BRL]                    = NULL,
    [WSS QUEUE]              = NULL
FROM
    vw_fx_trade
