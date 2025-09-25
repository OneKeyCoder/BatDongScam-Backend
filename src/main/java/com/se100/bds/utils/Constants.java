package com.se100.bds.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

public final class Constants {
    public static final String SECURITY_SCHEME_NAME = "bearerAuth";

    public static final String TOKEN_HEADER = "Authorization";

    public static final String TOKEN_TYPE = "Bearer";

    @Getter
    @AllArgsConstructor
    public enum RoleEnum {
        ADMIN("ADMIN"),
        SALESAGENT("SEER"),
        GUEST("GUEST"),
        PROPERTY_OWNER("UNVERIFIED_SEER"),
        CUSTOMER("CUSTOMER");

        private final String value;

        public static RoleEnum get(final String name) {
            return Stream.of(RoleEnum.values())
                    .filter(p -> p.name().equals(name.toUpperCase()) || p.getValue().equals(name.toUpperCase()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Invalid role name: %s", name)));
        }
    }

    @Getter
    @AllArgsConstructor
    public enum StatusProfileEnum {
        ACTIVE("ACTIVE"),
        SUSPENDED("SUSPENDED"),
        PENDING_APPROVAL("PENDING_APPROVAL"),
        REJECTED("REJECTED");

        private final String value;
        public static StatusProfileEnum get(final String name) {
            return Stream.of(StatusProfileEnum.values())
                    .filter(p -> p.name().equals(name.toUpperCase()) || p.getValue().equals(name.toUpperCase()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Invalid status profile name: %s", name)));
        }
    }

    @Getter
    @AllArgsConstructor
    public enum CustomerTierEnum {
        BRONZE("BRONZE"),
        SILVER("SILVER"),
        GOLD("GOLD"),
        PLATINUM("PLATINUM");

        private final String value;

        public static CustomerTierEnum get(final String name) {
            return Stream.of(CustomerTierEnum.values())
                    .filter(p -> p.name().equals(name.toUpperCase()) || p.getValue().equals(name.toUpperCase()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Invalid customer tier name: %s", name)));
        }
    }

    @Getter
    @AllArgsConstructor
    public enum PerformanceTierEnum {
        BRONZE("BRONZE"),
        SILVER("SILVER"),
        GOLD("GOLD"),
        PLATINUM("PLATINUM");

        private final String value;

        public static PerformanceTierEnum get(final String name) {
            return Stream.of(PerformanceTierEnum.values())
                    .filter(p -> p.name().equals(name.toUpperCase()) || p.getValue().equals(name.toUpperCase()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Invalid customer tier name: %s", name)));
        }
    }
}