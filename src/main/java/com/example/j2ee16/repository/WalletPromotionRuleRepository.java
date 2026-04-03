package com.example.j2ee16.repository;

import com.example.j2ee16.entity.WalletPromotionRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface WalletPromotionRuleRepository extends JpaRepository<WalletPromotionRule, Long> {

    @Query("SELECT r FROM WalletPromotionRule r WHERE r.active = true AND r.minAmount <= :amount " +
           "AND (r.maxAmount IS NULL OR r.maxAmount >= :amount) ORDER BY r.bonusPercentage DESC")
    List<WalletPromotionRule> findApplicableRules(@Param("amount") BigDecimal amount);
}
