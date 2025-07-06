IF NOT EXISTS(SELECT 1 FROM tb_department)
BEGIN
    INSERT INTO tb_department (name) VALUES ('IT');
    INSERT INTO tb_department (name) VALUES ('Business Risk Management');
    INSERT INTO tb_department (name) VALUES ('Client Desk');
    INSERT INTO tb_department (name) VALUES ('Compliance');
    INSERT INTO tb_department (name) VALUES ('Enterprise Risk Management');
    INSERT INTO tb_department (name) VALUES ('FLDS');
    INSERT INTO tb_department (name) VALUES ('Finance');
    INSERT INTO tb_department (name) VALUES ('Legal');
    INSERT INTO tb_department (name) VALUES ('Middle Office');
    INSERT INTO tb_department (name) VALUES ('Operations');
    INSERT INTO tb_department (name) VALUES ('Trading Desk');
END

IF NOT EXISTS(SELECT 1 FROM tb_role)
BEGIN
    INSERT INTO tb_role (code) VALUES ('ADMIN');
    INSERT INTO tb_role (code) VALUES ('SETTLEMENT_READ');
    INSERT INTO tb_role (code) VALUES ('SETTLEMENT_WRITE');
    INSERT INTO tb_role (code) VALUES ('COUNTERPARTY_READ');
    INSERT INTO tb_role (code) VALUES ('COUNTERPARTY_WRITE');
    INSERT INTO tb_role (code) VALUES ('TRADE_READ');
    INSERT INTO tb_role (code) VALUES ('TRADE_WRITE');
    INSERT INTO tb_role (code) VALUES ('DEPARTMENT_READ');
    INSERT INTO tb_role (code) VALUES ('DEPARTMENT_WRITE');
END

IF NOT EXISTS(SELECT 1 FROM tb_user)
BEGIN
    DECLARE @Id_User INT

    INSERT INTO tb_user (
        id_department,
        name,
        email,
        gender,
        username,
        password,
        is_active)
    SELECT
        id_department,
        name      = 'Admin',
        email     = 'admin@admin.com',
        gender    = 'MALE',
        username  = 'admin',
        password  = '{bcrypt}$2a$12$NYZurvH.l.vujYDufA6X6uFLBqQ1tDSDxX5VPTAcKSpNxJ3mBiWOW', -- 12345
        is_active = 1
    FROM
        tb_department
    WHERE
        name = 'IT'

    SET @Id_User = SCOPE_IDENTITY()

    INSERT INTO tb_user_role (id_user, id_role)
    SELECT
        @Id_User,
        id_role
    FROM
        tb_role
END

IF NOT EXISTS(SELECT 1 FROM tb_document_type)
BEGIN
	INSERT INTO tb_document_type (name) VALUES ('CNPJ');
	INSERT INTO tb_document_type (name) VALUES ('CPF');
	INSERT INTO tb_document_type (name) VALUES ('EIN');
END

IF NOT EXISTS(SELECT 1 FROM tb_currency)
BEGIN
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('AFN', '005', 'AFGHANISTAN AFGAN', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('ZAR', '785', 'SOUTH AFRICAN RAND', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('ALL', '490', 'ALBANIAN LEK', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('AOA', '635', 'ANGOLAN KWANZA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('SAR', '820', 'SAUDI RIYAL', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('DZD', '095', 'ALGERIAN DINAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('ARS', '706', 'ARGENTINE PESO', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('AMD', '275', 'ARMENIA DRAM', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('AWG', '328', 'ARUBA / FLORIM', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('AUD', '150', 'AUSTRALIAN DOLLAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('AZN', '607', 'AZERBAIJAN MANAT', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('BSD', '155', 'BAHAMAS DOLLAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('BHD', '105', 'BAHRAINI DINAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('BDT', '905', 'BANGLADESH TAKA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('BBD', '175', 'BARBADOS DOLLAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('BYN', '831', 'BELARUS ROUBLE', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('BZD', '180', 'BELIZE DOLLAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('BMD', '160', 'BERMUDIAN DOLLAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('BOB', '030', 'BOLIVIAN BOLIVIANO', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('BAM', '612', 'BOSNIA-HERZEGOVIN', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('BWP', '755', 'BOTSWANA PULA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('BRL', '790', 'BRAZIL REAL', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('BND', '185', 'BRUNEI DOLLAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('BGN', '510', 'BULGARIAN LEV', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('BIF', '365', 'BARUNDI / FRANC', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('BTN', '665', 'BHUTAN NGULTRUM', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('CVE', '295', 'CAPE VERDE ESCUDO', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('KHR', '825', 'CAMBODIA / RIEL', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('CAD', '165', 'CANADIAN DOLLAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('QAR', '800', 'QATARI RIYAL', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('KYD', '190', 'CAYMAN ISLANDS', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('KZT', '913', 'KAZAKHSTAN TENGE', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('CLF', '197', 'CHILEAN UNIDAD DE FOMENTO', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('CLP', '715', 'CHILEAN PESO', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('CNY', '795', 'CHINESE RENMINBI', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('SGD', '195', 'SINGAPORE DOLLAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('COP', '720', 'COLOMBIAN PESO', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('COU', '721', 'COLOMBIAN GOVERNMENT UVR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('KMF', '368', 'COMOROS FRANC', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('CDF', '363', 'CONGOLESE FRANC', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('KPW', '925', 'KOREA P R / WON', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('KRW', '930', 'SOUTH KOREAN WON', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('CRC', '040', 'COSTA RICA COLON', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('KWD', '100', 'KUWAITI DINAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('CUP', '725', 'CUBAN PESO', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('DKK', '055', 'DANISH KRONE', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('DJF', '390', 'DJIBOUTI FRANC', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('EGP', '535', 'EGYPTIAN POUND', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('SVC', '045', 'EL SALVADOR COLON', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('AED', '145', 'UAE DIRHAM', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('ERN', '625', 'ERITREAN NAKFA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('ETB', '009', 'ETHIOPIAN BIRR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('FKP', '545', 'FALKLAND ISLAND POUND', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('FJD', '200', 'FIJI DOLLAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('PHP', '735', 'PHILIPPINES PESO', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('TWD', '640', 'TAIWAN DOLLAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('GMD', '090', 'GAMBIAN DALASI', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('GHS', '035', 'GHANA CEDI', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('GEL', '482', 'GEORGIAN LARI', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('GIP', '530', 'GIBRALTAR POUND', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('GTQ', '770', 'GUATEMALA QUETZAL', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('GYD', '170', 'GUYANA DOLLAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('GNF', '398', 'GUINEA FRANC', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('HTG', '440', 'HAITI GOURDE', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('HNL', '495', 'HONDURAS LEMPIRA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('HKD', '205', 'HONG KONG DOLLAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('HUF', '345', 'HUNGARIAN FORINT', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('YER', '810', 'YEMENI RIAL', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('INR', '860', 'INDIAN RUPEE', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('IDR', '865', 'INDONESIAN RUPIAH', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('IRR', '815', 'IRANIAN RIAL', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('IQD', '115', 'IRAQI DINAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('ISK', '060', 'ICELAND KRONA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('ILS', '880', 'ISRAELI SHEKEL', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('JMD', '230', 'JAMAICA DOLLAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('JPY', '470', 'JAPANESE YEN', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('JOD', '125', 'JORDANIAN DINAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('LAK', '780', 'LAOS NEW KIP', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('LSL', '603', 'LESOTHO LOTI', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('LBP', '560', 'LEBANESE POUND', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('LRD', '235', 'LIBERIAN DOLLAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('LYD', '130', 'LYBIA / DINAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('MOP', '685', 'MACAU PATACA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('MKD', '132', 'MACEDONIA / DENAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('MGA', '406', 'MADAGASCAR ARIARY', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('MYR', '828', 'MALAYSIAN RINGGIT', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('MWK', '760', 'MALAWI KWACHA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('MVR', '870', 'MALDIVES / RUFIYA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('MAD', '139', 'MOROCCAN DIRHAM', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('MUR', '840', 'MAURITIUS RUPEE', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('MRO', '670', 'MAURITIAN OUGUIYA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('MRU', '671', 'MAURITANIAN OUGUYIA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('MXN', '741', 'MEXICAN PESO', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('MMK', '775', 'MYANMAR KYAT', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('MZN', '622', 'NEW MOZAMBIQUE ME', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('MDL', '503', 'MOLDOVA / LEU', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('MNT', '915', 'MONGOLIA TUGRIK', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('NAD', '173', 'NAMIBIAN DOLLAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('NPR', '845', 'NEPAL / RUPEE', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('NIO', '051', 'NICARAGUA CORDOBA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('NGN', '630', 'NIGERIA NAIRA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('NOK', '065', 'NORWEGIAN KRONE', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('OMR', '805', 'OMANI RIAL', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('PAB', '020', 'PANAMANIAN BALBOA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('PGK', '778', 'PAPUA NEW GUINEA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('PKR', '875', 'PAKISTANI RUPEE', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('PYG', '450', 'PARAGUAY GUARANI', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('PEN', '660', 'PERUVIAN NEW SOL', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('XPF', '380', 'FRENCH POLYNESIAN FRANC', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('PLN', '975', 'POLISH ZLOTY', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('KES', '950', 'KENYAN SHILLING', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('KGS', '892', 'KYRGYZSTAN SOM', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('DOP', '730', 'DOMINICAN REPUBLIC', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('RON', '506', 'ROMANIAN NEW LEU', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('RWF', '420', 'RWANDA FRANC', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('RUB', '830', 'RUSSIAN RUBLE', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('SBD', '250', 'SOLOMON ISLAND DOLLAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('WST', '912', 'TALA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('SHP', '570', 'ST. HELENA POUND', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('STN', '149', 'SAO TOME DOBRA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('SLL', '500', 'SIERRA LEONE LEONE', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('RSD', '133', 'SERBIAN DINAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('SCR', '850', 'SEYCHELLES RUPEE', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('SYP', '575', 'SYRIAN POUND', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('SOS', '960', 'SOMALI SCHILLING', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('LKR', '855', 'SRI LANKAN RUPEE', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('SZL', '585', 'SWAZILAND LILANGENI', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('SDG', '134', 'NEW SUDANESE POUND', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('SSP', '136', 'SOUTH SUDANESE POUND', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('SEK', '070', 'SWEDISH KRONA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('SRD', '255', 'SURINAM DOLLAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('TJS', '835', 'TAJIKISTANI SOMONI', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('THB', '015', 'THAI BAHT', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('TZS', '946', 'TANZANIAN SHILLING', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('CZK', '075', 'CZECH KORUNA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('TOP', '680', 'TONGA / PA''ANGA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('TTD', '210', 'TRINIDAD/TOBAGO DOLLAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('TND', '135', 'TUNISIAN DINAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('TMT', '608', 'NEW TURKMEN MANAT', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('TRY', '642', 'TURKISH LIRA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('UAH', '460', 'UKRAINIAN HRYVNIA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('UGX', '955', 'UGANDAN SHILLING', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('UYU', '745', 'URUGUAY PESO', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('UZS', '893', 'UZBEKISTAN SUM', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('VUV', '920', 'VANUATU VATU', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('VES', '027', 'SOVEREIGN BOLIVAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('VND', '260', 'VIETNAM DONG', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('ZMW', '766', 'ZAMBIAN KWACHA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('ZWG', '218', 'ZIMBABWE GOLD', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('XAU', '998', 'GOLD', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('EUR', '978', 'EURO', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('XEU', '918', 'EUROPEAN MONETARY', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('CNH', '796', 'OFFSHORE CHINESE RENMINBI', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('GBP', '540', 'BRITISH POUND', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('CHF', '425', 'SWISS FRANC', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('XOF', '372', 'CFA WEST FRANC', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('XAF', '370', 'CFA CENTRAL / FRA', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('XCG', '326', 'CARIBBEAN GUILDER', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('NZD', '245', 'NEW ZEALAND DOLLAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('USD', '220', 'UNITED STATES DOLLAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('XCD', '215', 'EAST CARIBBEAN DOLLAR', NULL);
	INSERT INTO tb_currency (iso_code, bacen_code, name, end_date) VALUES ('SDR', '138', 'SPECIAL DRAWING RIGHT', NULL);
END