package com.carsharing.bonus.repository;

import com.carsharing.bonus.entity.BonusTransaction;
import com.carsharing.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BonusTransactionRepository extends JpaRepository<BonusTransaction, Long> {

    List<BonusTransaction> findByUserOrderByCreatedAtDesc(User user);
}