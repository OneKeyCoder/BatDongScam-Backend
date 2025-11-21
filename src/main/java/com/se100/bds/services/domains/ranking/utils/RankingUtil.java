package com.se100.bds.services.domains.ranking.utils;

import java.util.Objects;

public final class RankingUtil {
    public static String getCustomerTier(int point) {
        if (point >= 90)
            return "PLATINUM";
        if (point >= 75)
            return "GOLD";
        if (point >= 50)
            return "SILVER";
        else
            return "BRONZE";
    }

    public static int getExtraPoint(String previousTier) {
        if (Objects.equals(previousTier, "PLATINUM"))
            return 25;
        else if (Objects.equals(previousTier, "GOLD"))
            return 15;
        else if (Objects.equals(previousTier, "SILVER"))
            return 5;
        else
            return 0;
    }
}
