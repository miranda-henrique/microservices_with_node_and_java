package com.microservicesnodejava.productapi.modules.supplier.repository;

import com.microservicesnodejava.productapi.modules.supplier.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

    List<Supplier> findByNameContainingIgnoreCase(String name);
}
