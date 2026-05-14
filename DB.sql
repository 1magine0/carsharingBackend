-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema carsharing_db
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema carsharing_db
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `carsharing_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci ;
USE `carsharing_db` ;

-- -----------------------------------------------------
-- Table `carsharing_db`.`car_locations`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `carsharing_db`.`car_locations` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `address` VARCHAR(255) NOT NULL,
  `latitude` DECIMAL(10,7) NOT NULL,
  `longitude` DECIMAL(10,7) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `carsharing_db`.`cars`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `carsharing_db`.`cars` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `brand` VARCHAR(80) NOT NULL,
  `model` VARCHAR(80) NOT NULL,
  `year` SMALLINT UNSIGNED NOT NULL,
  `registration_number` VARCHAR(20) NOT NULL,
  `color` VARCHAR(40) NOT NULL,
  `price_per_hour` DECIMAL(10,2) NOT NULL,
  `price_per_day` DECIMAL(10,2) NOT NULL,
  `price_per_month` DECIMAL(10,2) NOT NULL,
  `status` ENUM('AVAILABLE', 'RESERVED', 'RENTED', 'SERVICE', 'INACTIVE') NOT NULL,
  `location_id` BIGINT UNSIGNED NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uq_cars_registration_number` (`registration_number` ASC) VISIBLE,
  INDEX `idx_cars_status` (`status` ASC) VISIBLE,
  INDEX `idx_cars_location_id` (`location_id` ASC) VISIBLE,
  INDEX `idx_cars_brand_model` (`brand` ASC, `model` ASC) VISIBLE,
  CONSTRAINT `fk_cars_location`
    FOREIGN KEY (`location_id`)
    REFERENCES `carsharing_db`.`car_locations` (`id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `carsharing_db`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `carsharing_db`.`users` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `full_name` VARCHAR(150) NOT NULL,
  `email` VARCHAR(120) NOT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `phone` VARCHAR(30) NOT NULL,
  `role` ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
  `status` ENUM('ACTIVE', 'BLOCKED', 'PENDING') NOT NULL DEFAULT 'ACTIVE',
  `referral_code` VARCHAR(64) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uq_users_email` (`email` ASC) VISIBLE,
  UNIQUE INDEX `uq_users_referral_code` (`referral_code` ASC) VISIBLE,
  UNIQUE INDEX `uk_users_phone` (`phone` ASC) VISIBLE,
  UNIQUE INDEX `uk_users_email` (`email` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 21
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `carsharing_db`.`rentals`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `carsharing_db`.`rentals` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `car_id` BIGINT UNSIGNED NOT NULL,
  `tariff_type` ENUM('HOUR', 'DAY', 'MONTH') NOT NULL,
  `start_time` DATETIME NOT NULL,
  `end_time` DATETIME NOT NULL,
  `total_price` DECIMAL(10,2) NOT NULL DEFAULT '0.00',
  `bonus_used` DECIMAL(10,2) NOT NULL DEFAULT '0.00',
  `discount_amount` DECIMAL(10,2) NOT NULL DEFAULT '0.00',
  `status` ENUM('BOOKED', 'ACTIVE', 'FINISHED', 'CANCELED', 'EXPIRED') NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_rentals_user_id` (`user_id` ASC) VISIBLE,
  INDEX `idx_rentals_car_id` (`car_id` ASC) VISIBLE,
  INDEX `idx_rentals_status` (`status` ASC) VISIBLE,
  CONSTRAINT `fk_rentals_car`
    FOREIGN KEY (`car_id`)
    REFERENCES `carsharing_db`.`cars` (`id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_rentals_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `carsharing_db`.`users` (`id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 30
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `carsharing_db`.`bonus_transactions`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `carsharing_db`.`bonus_transactions` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `rental_id` BIGINT UNSIGNED NULL DEFAULT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `operation_type` ENUM('EARN', 'SPEND', 'REFERRAL') NOT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_bonus_transactions_user_id` (`user_id` ASC) VISIBLE,
  INDEX `idx_bonus_transactions_rental_id` (`rental_id` ASC) VISIBLE,
  INDEX `idx_bonus_transactions_operation_type` (`operation_type` ASC) VISIBLE,
  CONSTRAINT `fk_bonus_transactions_rental`
    FOREIGN KEY (`rental_id`)
    REFERENCES `carsharing_db`.`rentals` (`id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `fk_bonus_transactions_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `carsharing_db`.`users` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 36
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `carsharing_db`.`car_images`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `carsharing_db`.`car_images` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `car_id` BIGINT UNSIGNED NOT NULL,
  `image_url` VARCHAR(500) NOT NULL,
  `image_public_id` VARCHAR(255) NULL DEFAULT NULL,
  `is_main` TINYINT(1) NOT NULL DEFAULT '0',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_car_images_car_id` (`car_id` ASC) VISIBLE,
  CONSTRAINT `fk_car_images_car`
    FOREIGN KEY (`car_id`)
    REFERENCES `carsharing_db`.`cars` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 8
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `carsharing_db`.`driver_licenses`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `carsharing_db`.`driver_licenses` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `document_number` VARCHAR(50) NOT NULL,
  `issue_date` DATE NOT NULL,
  `expiry_date` DATE NOT NULL,
  `image_url` VARCHAR(500) NOT NULL,
  `image_public_id` VARCHAR(255) NULL DEFAULT NULL,
  `verification_status` ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
  `verified_at` DATETIME NULL DEFAULT NULL,
  `rejection_reason` VARCHAR(500) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uq_driver_licenses_user_id` (`user_id` ASC) VISIBLE,
  UNIQUE INDEX `uq_driver_licenses_document_number` (`document_number` ASC) VISIBLE,
  INDEX `idx_driver_licenses_verification_status` (`verification_status` ASC) VISIBLE,
  CONSTRAINT `fk_driver_licenses_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `carsharing_db`.`users` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 11
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `carsharing_db`.`payments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `carsharing_db`.`payments` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `rental_id` BIGINT UNSIGNED NOT NULL,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `currency` VARCHAR(10) NOT NULL DEFAULT 'UAH',
  `status` VARCHAR(30) NOT NULL,
  `provider` VARCHAR(30) NOT NULL,
  `order_id` VARCHAR(100) NOT NULL,
  `provider_payment_id` VARCHAR(100) NULL DEFAULT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  `created_at` DATETIME NOT NULL,
  `paid_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `order_id` (`order_id` ASC) VISIBLE,
  INDEX `fk_payments_rental` (`rental_id` ASC) VISIBLE,
  INDEX `fk_payments_user` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_payments_rental`
    FOREIGN KEY (`rental_id`)
    REFERENCES `carsharing_db`.`rentals` (`id`),
  CONSTRAINT `fk_payments_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `carsharing_db`.`users` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 21
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `carsharing_db`.`referrals`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `carsharing_db`.`referrals` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `referrer_user_id` BIGINT UNSIGNED NOT NULL,
  `referred_user_id` BIGINT UNSIGNED NOT NULL,
  `referral_bonus_granted` TINYINT(1) NOT NULL DEFAULT '0',
  `referred_discount_granted` TINYINT(1) NOT NULL DEFAULT '0',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uq_referrals_referred_user` (`referred_user_id` ASC) VISIBLE,
  INDEX `idx_referrals_referrer_user_id` (`referrer_user_id` ASC) VISIBLE,
  CONSTRAINT `fk_referrals_referred`
    FOREIGN KEY (`referred_user_id`)
    REFERENCES `carsharing_db`.`users` (`id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_referrals_referrer`
    FOREIGN KEY (`referrer_user_id`)
    REFERENCES `carsharing_db`.`users` (`id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `carsharing_db`.`rental_photos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `carsharing_db`.`rental_photos` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `rental_id` BIGINT UNSIGNED NOT NULL,
  `photo_type` ENUM('BEFORE', 'AFTER') NOT NULL,
  `image_url` VARCHAR(500) NOT NULL,
  `image_public_id` VARCHAR(255) NULL DEFAULT NULL,
  `uploaded_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_rental_photos_rental_id` (`rental_id` ASC) VISIBLE,
  INDEX `idx_rental_photos_type` (`photo_type` ASC) VISIBLE,
  CONSTRAINT `fk_rental_photos_rental`
    FOREIGN KEY (`rental_id`)
    REFERENCES `carsharing_db`.`rentals` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 37
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

USE `carsharing_db` ;

-- -----------------------------------------------------
-- Placeholder table for view `carsharing_db`.`available_cars_view`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `carsharing_db`.`available_cars_view` (`id` INT, `brand` INT, `model` INT, `year` INT, `color` INT, `price_per_hour` INT, `price_per_day` INT, `price_per_month` INT, `status` INT, `address` INT, `latitude` INT, `longitude` INT, `main_image_url` INT);

-- -----------------------------------------------------
-- procedure apply_referral_bonus
-- -----------------------------------------------------

DELIMITER $$
USE `carsharing_db`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `apply_referral_bonus`(
    IN p_referred_user_id BIGINT UNSIGNED,
    IN p_bonus_amount DECIMAL(10,2)
)
BEGIN
    DECLARE v_referral_id BIGINT UNSIGNED;
    DECLARE v_referrer_user_id BIGINT UNSIGNED;
    DECLARE v_bonus_granted BOOLEAN;

    IF p_bonus_amount <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка реферального нарахування: сума бонусу повинна бути більшою за нуль';
    END IF;

    SELECT id, referrer_user_id, referral_bonus_granted
    INTO v_referral_id, v_referrer_user_id, v_bonus_granted
    FROM referrals
    WHERE referred_user_id = p_referred_user_id;

    IF v_referral_id IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка реферального нарахування: реферальний зв’язок не знайдено';
    END IF;

    IF v_bonus_granted = TRUE THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка реферального нарахування: бонус уже був нарахований';
    END IF;

    INSERT INTO bonus_transactions (
        user_id,
        rental_id,
        amount,
        operation_type,
        description
    ) VALUES (
        v_referrer_user_id,
        NULL,
        p_bonus_amount,
        'REFERRAL',
        CONCAT('Реферальний бонус за запрошення користувача з id = ', p_referred_user_id)
    );

    UPDATE referrals
    SET referral_bonus_granted = TRUE
    WHERE id = v_referral_id;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- function calculate_bonus_amount
-- -----------------------------------------------------

DELIMITER $$
USE `carsharing_db`$$
CREATE DEFINER=`root`@`localhost` FUNCTION `calculate_bonus_amount`(
    p_total_price DECIMAL(10,2)
) RETURNS decimal(10,2)
    DETERMINISTIC
BEGIN
    IF p_total_price IS NULL OR p_total_price < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Сума оренди не може бути від’ємною або порожньою';
    END IF;

    RETURN ROUND(p_total_price * 0.01, 2);
END$$

DELIMITER ;

-- -----------------------------------------------------
-- function calculate_rental_price
-- -----------------------------------------------------

DELIMITER $$
USE `carsharing_db`$$
CREATE DEFINER=`root`@`localhost` FUNCTION `calculate_rental_price`(
    p_car_id BIGINT UNSIGNED,
    p_tariff_type ENUM('HOUR', 'DAY', 'MONTH'),
    p_start_time DATETIME,
    p_end_time DATETIME
) RETURNS decimal(10,2)
    READS SQL DATA
    DETERMINISTIC
BEGIN
    DECLARE v_price_per_hour DECIMAL(10,2);
    DECLARE v_price_per_day DECIMAL(10,2);
    DECLARE v_price_per_month DECIMAL(10,2);
    DECLARE v_result DECIMAL(10,2) DEFAULT 0.00;
    DECLARE v_hours INT;
    DECLARE v_days INT;
    DECLARE v_months INT;

    IF p_end_time <= p_start_time THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Некоректний період оренди: час завершення повинен бути більшим за час початку';
    END IF;

    SELECT price_per_hour, price_per_day, price_per_month
    INTO v_price_per_hour, v_price_per_day, v_price_per_month
    FROM cars
    WHERE id = p_car_id;

    IF v_price_per_hour IS NULL OR v_price_per_day IS NULL OR v_price_per_month IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Автомобіль не знайдено';
    END IF;

    CASE p_tariff_type
        WHEN 'HOUR' THEN
            SET v_hours = TIMESTAMPDIFF(HOUR, p_start_time, p_end_time);
            IF TIMESTAMPDIFF(MINUTE, p_start_time, p_end_time) % 60 <> 0 THEN
                SET v_hours = v_hours + 1;
            END IF;
            IF v_hours < 1 THEN
                SET v_hours = 1;
            END IF;
            SET v_result = v_hours * v_price_per_hour;

        WHEN 'DAY' THEN
            SET v_days = TIMESTAMPDIFF(DAY, p_start_time, p_end_time);
            IF TIMESTAMPDIFF(HOUR, p_start_time, p_end_time) % 24 <> 0 THEN
                SET v_days = v_days + 1;
            END IF;
            IF v_days < 1 THEN
                SET v_days = 1;
            END IF;
            SET v_result = v_days * v_price_per_day;

        WHEN 'MONTH' THEN
            SET v_months = TIMESTAMPDIFF(MONTH, p_start_time, p_end_time);
            IF DATE_ADD(p_start_time, INTERVAL v_months MONTH) < p_end_time THEN
                SET v_months = v_months + 1;
            END IF;
            IF v_months < 1 THEN
                SET v_months = 1;
            END IF;
            SET v_result = v_months * v_price_per_month;

        ELSE
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Непідтримуваний тип тарифу';
    END CASE;

    RETURN v_result;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure create_rental
-- -----------------------------------------------------

DELIMITER $$
USE `carsharing_db`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `create_rental`(
    IN p_user_id BIGINT UNSIGNED,
    IN p_car_id BIGINT UNSIGNED,
    IN p_tariff_type ENUM('HOUR', 'DAY', 'MONTH'),
    IN p_start_time DATETIME,
    IN p_end_time DATETIME,
    IN p_bonus_used DECIMAL(10,2),
    IN p_discount_amount DECIMAL(10,2),
    IN p_status ENUM('BOOKED', 'ACTIVE', 'FINISHED', 'CANCELED')
)
BEGIN
    DECLARE v_user_exists INT DEFAULT 0;
    DECLARE v_car_exists INT DEFAULT 0;
    DECLARE v_license_status VARCHAR(20);
    DECLARE v_car_status VARCHAR(20);
    DECLARE v_total_price DECIMAL(10,2);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    IF p_end_time <= p_start_time THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка створення оренди: час завершення повинен бути більшим за час початку';
    END IF;

    IF p_bonus_used < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка створення оренди: використані бонуси не можуть бути від’ємними';
    END IF;

    IF p_discount_amount < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка створення оренди: сума знижки не може бути від’ємною';
    END IF;

    SELECT COUNT(*)
    INTO v_user_exists
    FROM users
    WHERE id = p_user_id;

    IF v_user_exists = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка створення оренди: користувача не знайдено';
    END IF;

    SELECT COUNT(*)
    INTO v_car_exists
    FROM cars
    WHERE id = p_car_id;

    IF v_car_exists = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка створення оренди: автомобіль не знайдено';
    END IF;

    SELECT verification_status
    INTO v_license_status
    FROM driver_licenses
    WHERE user_id = p_user_id;

    IF v_license_status IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка створення оренди: посвідчення водія не знайдено';
    END IF;

    IF v_license_status <> 'APPROVED' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка створення оренди: посвідчення водія не підтверджене';
    END IF;

    SELECT status
    INTO v_car_status
    FROM cars
    WHERE id = p_car_id
    FOR UPDATE;

    IF v_car_status <> 'AVAILABLE' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка створення оренди: автомобіль недоступний';
    END IF;

    SET v_total_price = calculate_rental_price(
        p_car_id,
        p_tariff_type,
        p_start_time,
        p_end_time
    );

    IF p_bonus_used + p_discount_amount > v_total_price THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка створення оренди: сума бонусів і знижки перевищує вартість оренди';
    END IF;

    INSERT INTO rentals (
        user_id,
        car_id,
        tariff_type,
        start_time,
        end_time,
        total_price,
        bonus_used,
        discount_amount,
        status
    ) VALUES (
        p_user_id,
        p_car_id,
        p_tariff_type,
        p_start_time,
        p_end_time,
        v_total_price,
        p_bonus_used,
        p_discount_amount,
        p_status
    );

    COMMIT;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure finish_rental
-- -----------------------------------------------------

DELIMITER $$
USE `carsharing_db`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `finish_rental`(
    IN p_rental_id BIGINT UNSIGNED
)
BEGIN
    DECLARE v_status VARCHAR(20);
    DECLARE v_exists_count INT DEFAULT 0;

    SELECT COUNT(*)
    INTO v_exists_count
    FROM rentals
    WHERE id = p_rental_id;

    IF v_exists_count = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка завершення оренди: оренду не знайдено';
    END IF;

    SELECT status
    INTO v_status
    FROM rentals
    WHERE id = p_rental_id;

    IF v_status = 'FINISHED' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка завершення оренди: оренда вже завершена';
    END IF;

    IF v_status = 'CANCELED' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка завершення оренди: скасовану оренду не можна завершити';
    END IF;

    UPDATE rentals
    SET status = 'FINISHED',
        updated_at = CURRENT_TIMESTAMP
    WHERE id = p_rental_id;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- function get_user_bonus_balance
-- -----------------------------------------------------

DELIMITER $$
USE `carsharing_db`$$
CREATE DEFINER=`root`@`localhost` FUNCTION `get_user_bonus_balance`(
    p_user_id BIGINT UNSIGNED
) RETURNS decimal(10,2)
    READS SQL DATA
    DETERMINISTIC
BEGIN
    DECLARE v_balance DECIMAL(10,2);

    SELECT
        COALESCE(SUM(
            CASE
                WHEN operation_type IN ('EARN', 'REFERRAL') THEN amount
                WHEN operation_type = 'SPEND' THEN -amount
                ELSE 0
            END
        ), 0.00)
    INTO v_balance
    FROM bonus_transactions
    WHERE user_id = p_user_id;

    RETURN v_balance;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- View `carsharing_db`.`available_cars_view`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `carsharing_db`.`available_cars_view`;
USE `carsharing_db`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `carsharing_db`.`available_cars_view` AS select `c`.`id` AS `id`,`c`.`brand` AS `brand`,`c`.`model` AS `model`,`c`.`year` AS `year`,`c`.`color` AS `color`,`c`.`price_per_hour` AS `price_per_hour`,`c`.`price_per_day` AS `price_per_day`,`c`.`price_per_month` AS `price_per_month`,`c`.`status` AS `status`,`cl`.`address` AS `address`,`cl`.`latitude` AS `latitude`,`cl`.`longitude` AS `longitude`,`ci`.`image_url` AS `main_image_url` from ((`carsharing_db`.`cars` `c` join `carsharing_db`.`car_locations` `cl` on((`c`.`location_id` = `cl`.`id`))) left join `carsharing_db`.`car_images` `ci` on(((`c`.`id` = `ci`.`car_id`) and (`ci`.`is_main` = true)))) where (`c`.`status` = 'AVAILABLE');
USE `carsharing_db`;

DELIMITER $$
USE `carsharing_db`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `carsharing_db`.`trg_rentals_after_insert_set_car_status`
AFTER INSERT ON `carsharing_db`.`rentals`
FOR EACH ROW
BEGIN
    IF NEW.status = 'ACTIVE' THEN
        UPDATE cars
        SET status = 'RENTED'
        WHERE id = NEW.car_id;
    END IF;
END$$

USE `carsharing_db`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `carsharing_db`.`trg_rentals_after_update_bonus`
AFTER UPDATE ON `carsharing_db`.`rentals`
FOR EACH ROW
BEGIN
    DECLARE v_bonus DECIMAL(10,2);

    IF NEW.status = 'FINISHED' AND OLD.status <> 'FINISHED' THEN

        SET v_bonus = calculate_bonus_amount(NEW.total_price);

        INSERT INTO bonus_transactions (
            user_id,
            rental_id,
            amount,
            operation_type,
            description
        ) VALUES (
            NEW.user_id,
            NEW.id,
            v_bonus,
            'EARN',
            'Нарахування 1% бонусів за завершену оренду'
        );

    END IF;
END$$

USE `carsharing_db`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `carsharing_db`.`trg_rentals_after_update_return_car`
AFTER UPDATE ON `carsharing_db`.`rentals`
FOR EACH ROW
BEGIN
    IF NEW.status = 'FINISHED' AND OLD.status <> 'FINISHED' THEN
        UPDATE cars
        SET status = 'AVAILABLE'
        WHERE id = NEW.car_id;
    END IF;
END$$

USE `carsharing_db`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `carsharing_db`.`trg_rentals_before_insert_validate`
BEFORE INSERT ON `carsharing_db`.`rentals`
FOR EACH ROW
BEGIN
    DECLARE v_car_status VARCHAR(20);
    DECLARE v_license_status VARCHAR(20);

    IF NEW.end_time <= NEW.start_time THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка створення оренди: час завершення повинен бути більшим за час початку';
    END IF;

    IF NEW.total_price < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка створення оренди: загальна вартість не може бути від’ємною';
    END IF;

    IF NEW.bonus_used < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка створення оренди: кількість використаних бонусів не може бути від’ємною';
    END IF;

    IF NEW.discount_amount < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка створення оренди: сума знижки не може бути від’ємною';
    END IF;

    SELECT status
    INTO v_car_status
    FROM cars
    WHERE id = NEW.car_id;

    IF v_car_status IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка створення оренди: автомобіль не знайдено';
    END IF;

    IF v_car_status <> 'AVAILABLE' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка створення оренди: автомобіль недоступний для оренди';
    END IF;

    SELECT verification_status
    INTO v_license_status
    FROM driver_licenses
    WHERE user_id = NEW.user_id;

    IF v_license_status IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка створення оренди: водійське посвідчення користувача не знайдено';
    END IF;

    IF v_license_status <> 'APPROVED' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка створення оренди: користувач не має підтвердженого водійського посвідчення';
    END IF;
END$$

USE `carsharing_db`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `carsharing_db`.`trg_driver_licenses_before_insert_validate`
BEFORE INSERT ON `carsharing_db`.`driver_licenses`
FOR EACH ROW
BEGIN
    IF NEW.document_number IS NULL OR TRIM(NEW.document_number) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка додавання посвідчення: номер документа не може бути порожнім';
    END IF;

    IF NEW.image_url IS NULL OR TRIM(NEW.image_url) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка додавання посвідчення: посилання на зображення не може бути порожнім';
    END IF;

    IF NEW.expiry_date <= NEW.issue_date THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка додавання посвідчення: дата завершення дії повинна бути пізнішою за дату видачі';
    END IF;
END$$

USE `carsharing_db`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `carsharing_db`.`trg_referrals_before_insert_validate`
BEFORE INSERT ON `carsharing_db`.`referrals`
FOR EACH ROW
BEGIN
    DECLARE v_referrer_exists INT DEFAULT 0;
    DECLARE v_referred_exists INT DEFAULT 0;
    DECLARE v_referral_exists INT DEFAULT 0;

    IF NEW.referrer_user_id = NEW.referred_user_id THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка створення реферального зв’язку: користувач не може запросити сам себе';
    END IF;

    SELECT COUNT(*)
    INTO v_referrer_exists
    FROM users
    WHERE id = NEW.referrer_user_id;

    IF v_referrer_exists = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка створення реферального зв’язку: користувача, який запрошує, не знайдено';
    END IF;

    SELECT COUNT(*)
    INTO v_referred_exists
    FROM users
    WHERE id = NEW.referred_user_id;

    IF v_referred_exists = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка створення реферального зв’язку: запрошеного користувача не знайдено';
    END IF;

    SELECT COUNT(*)
    INTO v_referral_exists
    FROM referrals
    WHERE referred_user_id = NEW.referred_user_id;

    IF v_referral_exists > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Помилка створення реферального зв’язку: цей користувач уже бере участь у реферальній програмі';
    END IF;
END$$


DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
