package com.murali.placify.repository.dynamic;

import com.murali.placify.Mapper.UserScoreDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.postgresql.util.PGInterval;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ContestLeaderboardRepo {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void createLeaderboardTable(UUID contestId) {
        String tableName = "contest_leaderboard_" + contestId.toString().replace("-", "_");
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + "id UUID PRIMARY KEY DEFAULT gen_random_uuid(), "
                + "user_id UUID NOT NULL UNIQUE, "
                + "points INT NOT NULL, "
                + "time_taken INTERVAL NOT NULL, "
                + "created_at TIMESTAMP DEFAULT now(), "
                + "CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users_table(user_id) ON DELETE CASCADE"
                + ")";
        entityManager.createNativeQuery(sql).executeUpdate();
    }


    @Transactional
    public void insertScore(UUID contestId, UUID userId, int points, String timeTaken) {
        String tableName = "contest_leaderboard_" + contestId.toString().replace("-", "_");
        String sql = "INSERT INTO " + tableName + " (user_id, points, time_taken) VALUES (?, ?, ?::INTERVAL)";
        entityManager.createNativeQuery(sql)
                .setParameter(1, userId)
                .setParameter(2, points)
                .setParameter(3, timeTaken)
                .executeUpdate();
    }

    public List<UserScoreDto> getLeaderboard(UUID contestId) {
        String tableName = "contest_leaderboard_" + contestId.toString().replace("-", "_");
        String sql = "SELECT user_id, points, " +
                "EXTRACT(HOUR FROM time_taken) || 'h ' || EXTRACT(MINUTE FROM time_taken) || 'm' AS time_taken " +
                "FROM " + tableName + " ORDER BY points DESC, time_taken ASC";

        List<Object[]> results = entityManager.createNativeQuery(sql).getResultList();

        return results.stream()
                .map(obj -> new UserScoreDto(
                        (UUID) obj[0],
                        (int) obj[1],
                        (String) obj[2])) // Now returns formatted "7h 5m"
                .toList();
    }

    @Transactional
    public Optional<UserScoreDto> getUserScore(UUID contestId, UUID userId) {
        String tableName = "contest_leaderboard_" + contestId.toString().replace("-", "_");
        String sql = "SELECT user_id, points, time_taken FROM " + tableName + " WHERE user_id = ?";

        List<Object[]> result = entityManager.createNativeQuery(sql)
                .setParameter(1, userId)
                .getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        Object[] row = result.get(0);

        UUID userID = (UUID) row[0];
        Integer points = (Integer) row[1];

        String timeTaken;
        if (row[2] instanceof PGInterval) {
            PGInterval interval = (PGInterval) row[2];
            timeTaken = interval.toString();
        } else {
            timeTaken = row[2].toString();
        }

        return Optional.of(new UserScoreDto(userID, points, timeTaken));
    }

    @Transactional
    public void upsertUserScore(UUID contestId, UUID userId, int newPoints, String newTimeTaken) {

        String tableName = "contest_leaderboard_" + contestId.toString().replace("-", "_");
        String sql = "INSERT INTO " + tableName + " (user_id, points, time_taken) VALUES (?, ?, ?::INTERVAL) " +
                "ON CONFLICT (user_id) DO UPDATE SET points = EXCLUDED.points, time_taken = EXCLUDED.time_taken";

        entityManager.createNativeQuery(sql)
                .setParameter(1, userId)
                .setParameter(2, newPoints)
                .setParameter(3, newTimeTaken)
                .executeUpdate();
    }

}
