CREATE OR ALTER VIEW vw_fx_settlement_log
AS
SELECT
    [id]               = a.id_fx_settlement_log,
    [fx_settlement_id] = a.id_fx_settlement,
    [fx_trade_id]      = d.id_fx_trade,
    [user_name]        = b.name,
    [timestamp]        = a.event_date,
    [action]           = IIF(a.flag = 1, 'SET', 'UNSET'),
    [step]             = a.step,
    [comments]         = a.comments,
    [file_id]          = c.id_file_content,
    [file_name]        = c.file_name,
    [file_type]        = c.file_type
FROM
    tb_fx_settlement_log AS a

    LEFT JOIN tb_user AS b
           ON a.id_user = b.id_user

    LEFT JOIN tb_file_content AS c
           ON a.id_file_content = c.id_file_content

    INNER JOIN tb_fx_settlement AS d
            ON d.id_fx_settlement = a.id_fx_settlement
GO