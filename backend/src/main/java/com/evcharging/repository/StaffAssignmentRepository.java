package com.evcharging.repository;

import com.evcharging.entity.StaffAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffAssignmentRepository extends JpaRepository<StaffAssignment, Long> {
    @Query("SELECT sa FROM StaffAssignment sa " +
            "JOIN FETCH sa.station " +
            "WHERE sa.staffAccountId = :staffAccountId AND sa.active = true")
    List<StaffAssignment> findActiveAssignmentsByStaffAccountId(@Param("staffAccountId") Long staffAccountId);

    @Query("SELECT sa.station.id FROM StaffAssignment sa " +
            "WHERE sa.staffAccountId = :staffAccountId AND sa.active = true")
    List<Long> findStationIdsByStaffAccountId(@Param("staffAccountId") Long staffAccountId);

    boolean existsByStaffAccountIdAndStationIdAndActiveTrue(Long staffAccountId, Long stationId);

    @Query("SELECT sa FROM StaffAssignment sa " +
            "WHERE sa.staffAccountId = :staffAccountId")
    List<StaffAssignment> findAllByStaffAccountId(@Param("staffAccountId") Long staffAccountId);
}
