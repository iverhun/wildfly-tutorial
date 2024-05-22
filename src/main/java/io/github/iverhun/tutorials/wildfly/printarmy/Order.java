package io.github.iverhun.tutorials.wildfly.printarmy;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Entity
@Table(name = "orders", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Data
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private long submittedAt;

    @NotBlank
    @Size(min = 8, max = 50)
    @Column(nullable = false/*, unique = true*/)
    private String name;

    @NotBlank
    private String customer;

    @NotBlank
    @Digits(fraction = 0, integer = 12)
    @Size(min = 10, max = 12)
    private String phoneNumber;

}
