CREATE TABLE paintings_concepts
(`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
`painting_id` int(11) NOT NULL,
`name` varchar(600) NOT NULL,
`value` decimal(6,5) NOT NULL,
`created_on` timestamp DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY fk_cat(`painting_id`) REFERENCES paintings(`id`));
