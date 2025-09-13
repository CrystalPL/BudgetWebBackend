package pl.crystalek.budgetweb.auth.device;

import jakarta.persistence.Embeddable;

@Embeddable
public record DeviceInfo(String OSName, String browserName) {
}
