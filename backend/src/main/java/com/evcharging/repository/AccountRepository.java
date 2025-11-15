package com.evcharging.repository;

import com.evcharging.entity.Account;
import com.evcharging.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    //Tìm account theo Id
    Optional<Account> findById(Long Id);

    //Tìm account theo email
    Optional<Account> findByEmail(String email);

    //Tìm account theo phone
    Optional<Account> findByPhone(String phone);

    //Kiểm tra email đã tồn tại
    boolean existsByEmail(String email);

    //Kiểm tra phone đã tồn tại
    boolean existsByPhone(String phone);

    //Lấy danh sách account theo role (có phân trang)
    Page<Account> findByRole(Role role, Pageable pageable);

    //Tìm kiếm account theo keyword (email, phone, fullName)
    @Query("SELECT a FROM Account a WHERE " +
            "LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Account> searchByKeyword(@Param("keyword") String keyword);

    //Đếm số account theo role
    long countByRole(Role role);

    //Lấy tất cả account theo role
    List<Account> findAllByRole(Role role);
}