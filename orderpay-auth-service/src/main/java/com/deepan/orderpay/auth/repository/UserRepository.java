package com.deepan.orderpay.auth.repository;

import com.deepan.orderpay.auth.entity.Role;
import com.deepan.orderpay.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    @Transactional
    long deleteByEmailEndingWith(String suffix);

    @Query("select count(u) from User u where :role member of u.roles")
    long countWithRole(@Param("role") Role role);
}