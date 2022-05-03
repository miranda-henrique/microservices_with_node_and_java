package com.microservicesnodejava.productapi.modules.supplier.model;

import com.microservicesnodejava.productapi.modules.supplier.dto.SupplierRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "SUPPLIER")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "NAME", nullable = false)
    private String name;

    public static Supplier of(SupplierRequest request) {
        var supplier = new Supplier();
        BeanUtils.copyProperties(request, supplier);

        return supplier;
    }

}
