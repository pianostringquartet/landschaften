CREATE TABLE wga_csv_rows
(`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
`author` varchar(600),
`born_died` varchar(600) NOT NULL,
`title` varchar(600) NOT NULL,
-- needs to be string because CSV has many formats: "1250", "c. 1250" etc.
`date` varchar(600) NOT NULL,
`technique` varchar(600) NOT NULL,
`location` varchar(600) NOT NULL,
`url` varchar(600) NOT NULL UNIQUE,
`form` varchar(128) NOT NULL,
`type` varchar(128) NOT NULL,
`school` varchar(128) NOT NULL,
`timeframe` varchar(600) NOT NULL,
`jpg` varchar(600) NOT NULL UNIQUE,
`created_on` timestamp DEFAULT CURRENT_TIMESTAMP,
UNIQUE KEY `id` (`id`));
