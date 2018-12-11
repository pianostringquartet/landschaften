CREATE TABLE wga_concepts
(`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
 `author` VARCHAR(600),
`title` varchar(600) NOT NULL,
`date` varchar(600) NOT NULL,
`form` varchar(128) NOT NULL,
`type` varchar(128) NOT NULL,
`school` varchar(128) NOT NULL,
`timeframe` varchar(600) NOT NULL,
`jpg` varchar(600) NOT NULL UNIQUE,
`concepts` text NOT NULL,
`created_on` timestamp DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY fk_cat(`id`) REFERENCES wga_csv_rows(`id`),
FOREIGN KEY fk_cat(`jpg`) REFERENCES wga_csv_rows(`jpg`));
