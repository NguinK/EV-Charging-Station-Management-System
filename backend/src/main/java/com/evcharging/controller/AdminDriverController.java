//package com.evcharging.controller;
//
//import com.evcharging.dto.DriverProfileDTO;
//import com.evcharging.service.AdminDriverService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/admin/drivers")
//public class AdminDriverController {
//
//    private final AdminDriverService adminDriverService;
//
//    public AdminDriverController(AdminDriverService adminDriverService) {
//        this.adminDriverService = adminDriverService;
//    }
//
//    @GetMapping
//    public List<DriverProfileDTO> getAllDrivers() {
//        return adminDriverService.getAllDrivers();
//    }
//
//    @GetMapping("/{id}")
//    public DriverProfileDTO getDriver(@PathVariable Long id) {
//        return adminDriverService.getDriver(id);
//    }
//
//    @PostMapping
//    public DriverProfileDTO createDriver(@RequestBody DriverProfileDTO dto) {
//        return adminDriverService.createDriver(dto);
//    }
//
//    @PutMapping("/{id}")
//    public DriverProfileDTO updateDriver(@PathVariable Long id, @RequestBody DriverProfileDTO dto) {
//        return adminDriverService.updateDriver(id, dto);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
//        adminDriverService.deleteDriver(id);
//        return ResponseEntity.noContent().build();
//    }
//}