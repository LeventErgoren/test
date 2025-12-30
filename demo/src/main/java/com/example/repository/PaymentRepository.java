package com.example.repository;

import com.example.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByDuesId(Long duesId);

    // SENARYO: Bir borcun toplam ne kadarı ödendi? (SQL aggregate fonksiyonu kullanımı)
    // Bunu Java Stream ile de yapabiliriz ama veritabanında yapmak performanstır.
    // Şimdilik basit liste dönüyoruz, Service katmanında stream ile toplayacağız (Logic testi olsun diye).
}
