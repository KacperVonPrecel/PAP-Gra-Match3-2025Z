package pap.project.user_stats.model;

public record RankingEntry(
        long userId,
        String username,
        int eloPoints
) {
}
