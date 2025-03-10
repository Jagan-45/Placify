package com.murali.placify.repository.specification;

import com.murali.placify.entity.Leaderboard;
import com.murali.placify.enums.Level;
import com.murali.placify.model.LeaderboardFilterDTO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

//TODO: FIX RANGE FILTERS
@Component
public class LeaderboardSpecification {
    public Specification<Leaderboard> getAllSpecifications(LeaderboardFilterDTO filterDTO) {
        List<Specification<Leaderboard>> specs = new ArrayList<>();

        specs.add(departmentEquals(filterDTO.getDepartment()));
        specs.add(academicBatchEquals(filterDTO.getAcademicBatch()));
        specs.add(taskStreakBetween(filterDTO.getTaskStreakMin(), filterDTO.getTaskStreakMax()));
        specs.add(contestRatingBetween(filterDTO.getContestRatingMin(), filterDTO.getContestRatingMax()));
        specs.add(levelEquals(filterDTO.getLevel()));
        specs.add(overallRatingBetween(filterDTO.getOverallRatingMin(), filterDTO.getOverallRatingMax()));

        Specification<Leaderboard> result = Specification.where(null);

        for (Specification<Leaderboard> spec : specs) {
            result = result.and(spec);
        }

        return result;
    }

    private Specification<Leaderboard> overallRatingBetween(int overallRatingMin, int overallRatingMax) {
        return (root, query, criteriaBuilder) -> {
            if ((overallRatingMin == 0 && overallRatingMax == 0))
                return criteriaBuilder.conjunction();
            if (overallRatingMax < overallRatingMin)
                throw new IllegalArgumentException("Enter valid range for over all rating");

            return criteriaBuilder.between(root.get("overAllRating"), overallRatingMin, overallRatingMax);
        };
    }

    private Specification<Leaderboard> levelEquals(Level level) {
        return (root, query, criteriaBuilder) -> {
            if (level == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("level"), level.ordinal());
        };
    }

    private Specification<Leaderboard> contestRatingBetween(Integer contestRatingMin, Integer contestRatingMax) {
        return (root, query, criteriaBuilder) -> {
            if (contestRatingMin == null || contestRatingMax == null) return criteriaBuilder.conjunction();

            return criteriaBuilder.between(root.get("contestRating"), contestRatingMin, contestRatingMax);
        };
    }

    private Specification<Leaderboard> taskStreakBetween(Integer taskStreakMin, Integer taskStreakMax) {
        return (root, query, criteriaBuilder) -> {
          if (taskStreakMax == null || taskStreakMin == null)
              return criteriaBuilder.conjunction();

          return criteriaBuilder.between(root.get("taskStreak"), taskStreakMin, taskStreakMax);
        };
    }

    private Specification<Leaderboard> academicBatchEquals(String academicBatch) {
        return (root, query, criteriaBuilder) -> {
            if (academicBatch == null)
                return criteriaBuilder.conjunction();

            return criteriaBuilder.equal(root.get("user").get("academicBatch"), academicBatch);
        };
    }

    private Specification<Leaderboard> departmentEquals(String department) {
        return (root, query, criteriaBuilder) -> {
            if (department == null || department.isEmpty())
                return criteriaBuilder.conjunction();

            return criteriaBuilder.equal(root.get("user").get("department").get("departmentName"), department);
        };
    }
}
