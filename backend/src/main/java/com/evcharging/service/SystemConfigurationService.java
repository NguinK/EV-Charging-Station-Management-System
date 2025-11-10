package com.evcharging.service;

import com.evcharging.entity.SystemConfiguration;
import com.evcharging.repository.SystemConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemConfigurationService {
    private final SystemConfigurationRepository configRepository;

    public static final String RESERVATION_HOLD_FEE_PER_HOUR = "reservation_hold_fee_per_hour";
    public static final String RESERVATION_PENALTY_AMOUNT = "reservation.penalty.amount";
    public static final String CHARGING_SERVICE_FEE = "charging.service.fee";
    public static final String DEFAULT_CURRENCY = "system.currency.default";

    //Lấy giá trị config dạng String
    @Cacheable(value = "systemConfig", key = "#configKey")
    public String getConfigValue(String configKey, String defaultValue) {
        return configRepository.findByConfigKey(configKey)
                .map(SystemConfiguration::getConfigValue)
                .orElseGet(() -> {
                    log.warn("Configuration key '{}' not found, using default: {}", configKey, defaultValue);
                    return defaultValue;
                });
    }

    //Lấy giá trị config dạng BigDecimal
    public BigDecimal getConfigValueAsDecimal(String configKey, BigDecimal defaultValue) {
        try {
            String value = getConfigValue(configKey, null);
            return value != null ? new BigDecimal(value) : defaultValue;
        } catch (NumberFormatException e) {
            log.error("Invalid decimal format for config key: {}", configKey, e);
            return defaultValue;
        }
    }

    //Lấy giá trị config dạng Integer
    public Integer getConfigValueAsInteger(String configKey, Integer defaultValue) {
        try {
            String value = getConfigValue(configKey, null);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            log.error("Invalid integer format for config key: {}", configKey, e);
            return defaultValue;
        }
    }

    //Lấy giá trị config dạng Boolean
    public Boolean getConfigValueAsBoolean(String configKey, Boolean defaultValue) {
        String value = getConfigValue(configKey, null);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    //Cập nhật giá trị config
    @Transactional
    @CacheEvict(value = "systemConfig", key = "#configKey")
    public void updateConfigValue(String configKey, String newValue, String updatedBy) {
        SystemConfiguration config = configRepository.findByConfigKey(configKey)
                .orElseThrow(() -> new IllegalArgumentException("Configuration not found: " + configKey));

        if (!config.getIsEditable()) {
            throw new IllegalStateException("Configuration is not editable: " + configKey);
        }

        config.setConfigValue(newValue);
        config.setUpdatedBy(updatedBy);
        configRepository.save(config);

        log.info("Configuration updated: {} = {} by {}", configKey, newValue, updatedBy);
    }

    //Tạo hoặc cập nhật config
    @Transactional
    public void createOrUpdateConfig(String configKey, String configValue, String description,
                                     String dataType, String category) {
        Optional<SystemConfiguration> existing = configRepository.findByConfigKey(configKey);

        if (existing.isPresent()) {
            SystemConfiguration config = existing.get();
            config.setConfigValue(configValue);
            config.setDescription(description);
            configRepository.save(config);
        } else {
            SystemConfiguration config = new SystemConfiguration();
            config.setConfigKey(configKey);
            config.setConfigValue(configValue);
            config.setDescription(description);
            config.setDataType(dataType);
            config.setCategory(category);
            config.setIsEditable(true);
            configRepository.save(config);
        }
    }
}
