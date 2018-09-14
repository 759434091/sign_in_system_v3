package team.a9043.cache;

import java.time.LocalDateTime;

public interface CacheProcess {
    String insertSignIn(String userId, int schId, LocalDateTime siTime, int siWeek);
}
