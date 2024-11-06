package com.rimsha.model.db.repository;

import com.rimsha.model.db.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
