-- ============================================================
-- Shopping Cart Localization Database Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS shopping_cart_localization
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE shopping_cart_localization;

CREATE TABLE IF NOT EXISTS cart_records (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    total_items INT    NOT NULL,
    total_cost  DOUBLE NOT NULL,
    language    VARCHAR(10),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cart_items (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    cart_record_id INT,
    item_number    INT    NOT NULL,
    price          DOUBLE NOT NULL,
    quantity       INT    NOT NULL,
    subtotal       DOUBLE NOT NULL,
    FOREIGN KEY (cart_record_id) REFERENCES cart_records(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS localization_strings (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    `key`    VARCHAR(100) NOT NULL,
    `value`  VARCHAR(255) NOT NULL,
    language VARCHAR(10)  NOT NULL
);

INSERT INTO localization_strings (`key`, `value`, language) VALUES
('app.title',           'Shopping Cart - Osama Aamer',           'en_US'),
('language.select',     'Select Language:',                      'en_US'),
('items.prompt',        'Enter number of items:',                'en_US'),
('item.label',          'Item',                                  'en_US'),
('item.price',          'Price:',                                'en_US'),
('item.quantity',       'Quantity:',                             'en_US'),
('item.total',          'Total:',                                'en_US'),
('total.cost',          'Total Cost:',                           'en_US'),
('button.calculate',    'Calculate',                             'en_US'),
('button.clear',        'Clear',                                 'en_US'),
('button.exit',         'Exit',                                  'en_US'),
('calculation.complete','Calculation completed successfully!',   'en_US'),
('currency',            '€',                                     'en_US');

INSERT INTO localization_strings (`key`, `value`, language) VALUES
('app.title',           'Ostoskori - Osama Aamer',               'fi_FI'),
('language.select',     'Valitse kieli:',                        'fi_FI'),
('items.prompt',        'Syötä tuotteiden määrä:',               'fi_FI'),
('item.label',          'Tuote',                                  'fi_FI'),
('item.price',          'Hinta:',                                'fi_FI'),
('item.quantity',       'Määrä:',                                'fi_FI'),
('item.total',          'Yhteensä:',                             'fi_FI'),
('total.cost',          'Kokonaishinta:',                        'fi_FI'),
('button.calculate',    'Laske',                                 'fi_FI'),
('button.clear',        'Tyhjennä',                              'fi_FI'),
('button.exit',         'Sulje',                                 'fi_FI'),
('calculation.complete','Laskenta valmistui!',                   'fi_FI'),
('currency',            '€',                                     'fi_FI');


INSERT INTO localization_strings (`key`, `value`, language) VALUES
('app.title',           'Kundvagn - Osama Aamer',                'sv_SE'),
('language.select',     'Välj språk:',                           'sv_SE'),
('items.prompt',        'Ange antal artiklar:',                  'sv_SE'),
('item.label',          'Artikel',                               'sv_SE'),
('item.price',          'Pris:',                                 'sv_SE'),
('item.quantity',       'Antal:',                                'sv_SE'),
('item.total',          'Totalt:',                               'sv_SE'),
('total.cost',          'Totalkostnad:',                         'sv_SE'),
('button.calculate',    'Beräkna',                               'sv_SE'),
('button.clear',        'Rensa',                                 'sv_SE'),
('button.exit',         'Avsluta',                               'sv_SE'),
('calculation.complete','Beräkning klar!',                       'sv_SE'),
('currency',            '€',                                     'sv_SE');


INSERT INTO localization_strings (`key`, `value`, language) VALUES
('app.title',           'ショッピングカート - Osama Aamer',       'ja_JP'),
('language.select',     '言語を選択:',                           'ja_JP'),
('items.prompt',        '商品数を入力:',                         'ja_JP'),
('item.label',          '商品',                                  'ja_JP'),
('item.price',          '価格:',                                 'ja_JP'),
('item.quantity',       '数量:',                                 'ja_JP'),
('item.total',          '小計:',                                 'ja_JP'),
('total.cost',          '合計金額:',                             'ja_JP'),
('button.calculate',    '計算',                                  'ja_JP'),
('button.clear',        'クリア',                                'ja_JP'),
('button.exit',         '終了',                                  'ja_JP'),
('calculation.complete','計算が完了しました！',                  'ja_JP'),
('currency',            '¥',                                     'ja_JP');


INSERT INTO localization_strings (`key`, `value`, language) VALUES
('app.title',           'سلة التسوق - Osama Aamer',             'ar_SA'),
('language.select',     'اختر اللغة:',                          'ar_SA'),
('items.prompt',        'أدخل عدد العناصر:',                    'ar_SA'),
('item.label',          'العنصر',                               'ar_SA'),
('item.price',          'السعر:',                               'ar_SA'),
('item.quantity',       'الكمية:',                              'ar_SA'),
('item.total',          'الإجمالي:',                            'ar_SA'),
('total.cost',          'التكلفة الإجمالية:',                   'ar_SA'),
('button.calculate',    'حساب',                                 'ar_SA'),
('button.clear',        'مسح',                                  'ar_SA'),
('button.exit',         'خروج',                                  'ar_SA'),
('calculation.complete','تم الحساب بنجاح!',                     'ar_SA'),
('currency',            '€',                                     'ar_SA');
