CREATE TABLE IF NOT EXISTS `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `email_verified` bit(1) NOT NULL,
  `account_active` bit(1) NOT NULL,
  `creation_time` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `modified_time` datetime(6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` varchar(255) NOT NULL,
  `username` varchar(20) DEFAULT NULL,
  `modified_by` bigint DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  KEY `FK3aqdj6u2t0by2dj24m141utm` (`modified_by`),
  CONSTRAINT `FK3aqdj6u2t0by2dj24m141utm` FOREIGN KEY (`modified_by`) REFERENCES `users` (`user_id`)
);

CREATE TABLE IF NOT EXISTS `mobile_number` (
  `mobile_number_id` bigint NOT NULL AUTO_INCREMENT,
  `label` varchar(255) DEFAULT NULL,
  `number` varchar(255) DEFAULT NULL,
  `primary_number` bit(1) NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`mobile_number_id`),
  KEY `FKnovv45tv161qso3i0sb4rpnga` (`user_id`),
  CONSTRAINT `FKnovv45tv161qso3i0sb4rpnga` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
);

CREATE TABLE IF NOT EXISTS `address` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `area` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `delivery_time_type` varchar(255) NOT NULL,
  `door_no` varchar(255) DEFAULT NULL,
  `is_delivery_address` bit(1) NOT NULL,
  `label` varchar(255) DEFAULT NULL,
  `landmark` varchar(255) DEFAULT NULL,
  `pincode` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `contact_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK73rtyylkoetvh0f5bopx8g0ac` (`contact_id`),
  KEY `FK6i66ijb8twgcqtetl8eeeed6v` (`user_id`),
  CONSTRAINT `FK6i66ijb8twgcqtetl8eeeed6v` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FK73rtyylkoetvh0f5bopx8g0ac` FOREIGN KEY (`contact_id`) REFERENCES `mobile_number` (`mobile_number_id`)
);

CREATE TABLE IF NOT EXISTS `brand` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) DEFAULT NULL,
  `modified_date` datetime(6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `modified_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjpodyjubh0sfo4f0o6ha779en` (`modified_by`),
  CONSTRAINT `FKjpodyjubh0sfo4f0o6ha779en` FOREIGN KEY (`modified_by`) REFERENCES `users` (`user_id`)
);

CREATE TABLE IF NOT EXISTS `cart` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKshu96dhjmw9ld1x5hapdw4do9` (`user`),
  CONSTRAINT `FKshu96dhjmw9ld1x5hapdw4do9` FOREIGN KEY (`user`) REFERENCES `users` (`user_id`)
);

CREATE TABLE IF NOT EXISTS `category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) DEFAULT NULL,
  `modified_date` datetime(6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `modified_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKr4nnqkds2l6i2fhc7tsu60xs9` (`modified_by`),
  CONSTRAINT `FKr4nnqkds2l6i2fhc7tsu60xs9` FOREIGN KEY (`modified_by`) REFERENCES `users` (`user_id`)
);

CREATE TABLE IF NOT EXISTS `category_sub_category` (
  `category_id` bigint NOT NULL,
  `sub_category` varchar(255) DEFAULT NULL,
  KEY `FKdwq6h046fgtvq0y6437mom18b` (`category_id`),
  CONSTRAINT `FKdwq6h046fgtvq0y6437mom18b` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
);

CREATE TABLE IF NOT EXISTS `orders` (
  `order_id` bigint NOT NULL AUTO_INCREMENT,
  `cancel_date` datetime(6) DEFAULT NULL,
  `cancel_reason` varchar(255) DEFAULT NULL,
  `delivery_date` datetime(6) DEFAULT NULL,
  `expected_delivery_date` datetime(6) DEFAULT NULL,
  `is_closed` bit(1) NOT NULL,
  `modified_date` datetime(6) DEFAULT NULL,
  `order_date` datetime(6) DEFAULT NULL,
  `order_status` varchar(255) NOT NULL,
  `payment_type` varchar(255) DEFAULT NULL,
  `tax_percentage` float NOT NULL,
  `total_value` float NOT NULL,
  `value` float NOT NULL,
  `delivery_address` bigint NOT NULL,
  `placed_by` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`order_id`),
  KEY `FKktv9qk6kio1qrkgkw8w23ebr6` (`delivery_address`),
  KEY `FKb0v5ktoqxc9og11vt4dlmh8us` (`placed_by`),
  KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`),
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKb0v5ktoqxc9og11vt4dlmh8us` FOREIGN KEY (`placed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKktv9qk6kio1qrkgkw8w23ebr6` FOREIGN KEY (`delivery_address`) REFERENCES `address` (`id`)
);

CREATE TABLE IF NOT EXISTS `invoice` (
  `invoice_id` bigint NOT NULL AUTO_INCREMENT,
  `amount_pending` float NOT NULL,
  `pending_returns` float NOT NULL,
  `total_amount_receivable` float NOT NULL,
  `total_amount_received` float NOT NULL,
  `total_amount_returnable` float NOT NULL,
  `total_amount_returned` float NOT NULL,
  `invoice_of_order` bigint NOT NULL,
  `invoice_of_user` bigint NOT NULL,
  PRIMARY KEY (`invoice_id`),
  KEY `FKnjlcb2e5n1ek1seegg49a9t19` (`invoice_of_order`),
  KEY `FK35odutxlti1u2mwym8lru557k` (`invoice_of_user`),
  CONSTRAINT `FK35odutxlti1u2mwym8lru557k` FOREIGN KEY (`invoice_of_user`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKnjlcb2e5n1ek1seegg49a9t19` FOREIGN KEY (`invoice_of_order`) REFERENCES `orders` (`order_id`)
);

CREATE TABLE IF NOT EXISTS `product` (
  `product_id` bigint NOT NULL AUTO_INCREMENT,
  `category` varchar(255) DEFAULT NULL,
  `creation_date` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `modified_date` datetime(6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price` float NOT NULL,
  `quantity` int DEFAULT NULL,
  `rating` float NOT NULL,
  `thumbnail` varchar(255) DEFAULT NULL,
  `brand_id` bigint NOT NULL,
  `modified_by` bigint DEFAULT NULL,
  PRIMARY KEY (`product_id`),
  KEY `FKs6cydsualtsrprvlf2bb3lcam` (`brand_id`),
  KEY `FKkgp7jev4m8l8e7a7t5wrv793x` (`modified_by`),
  CONSTRAINT `FKkgp7jev4m8l8e7a7t5wrv793x` FOREIGN KEY (`modified_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKs6cydsualtsrprvlf2bb3lcam` FOREIGN KEY (`brand_id`) REFERENCES `brand` (`id`)
);

CREATE TABLE IF NOT EXISTS `product_images` (
  `product_product_id` bigint NOT NULL,
  `images` varchar(255) DEFAULT NULL,
  KEY `FKhrvh0hklwgllpdjlra6hx7p9u` (`product_product_id`),
  CONSTRAINT `FKhrvh0hklwgllpdjlra6hx7p9u` FOREIGN KEY (`product_product_id`) REFERENCES `product` (`product_id`)
);

CREATE TABLE IF NOT EXISTS `product_quantity` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `quantity` int NOT NULL,
  `product_id` bigint NOT NULL,
  `order_id` bigint DEFAULT NULL,
  `cart_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKi5bxaxet5yf16aj98dt7s064p` (`product_id`),
  KEY `FKctjy27mx6s4vd7k8emtuuv4u7` (`order_id`),
  KEY `FK8x1b1xg21d6sd2m8mh00asxsd` (`cart_id`),
  CONSTRAINT `FK8x1b1xg21d6sd2m8mh00asxsd` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`id`),
  CONSTRAINT `FKctjy27mx6s4vd7k8emtuuv4u7` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `FKi5bxaxet5yf16aj98dt7s064p` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
) ;