package net.azisaba.taxoffice.manager;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Wraps the {@link PointsManager} and caches the points of the players.
 */
public class CachedPointsManager extends DelegatePointsManager {
    private final Map<UUID, Long> cache = new ConcurrentHashMap<>();
    private final PointsManager delegate;

    public CachedPointsManager(@NotNull PointsManager delegate) {
        this.delegate = delegate;
    }

    @NotNull
    public PointsManager delegate() {
        return delegate;
    }
    
    public void clearCache() {
        cache.clear();
    }

    @Override
    public long getPoints(@NotNull UUID uuid) {
        return cache.computeIfAbsent(uuid, this::getUncachedPoints);
    }

    public long getUncachedPoints(@NotNull UUID uuid) {
        return super.getPoints(uuid);
    }

    @Override
    public long givePoints(@NotNull UUID uuid, long points) {
        long l = super.givePoints(uuid, points);
        cache.remove(uuid);
        return l;
    }

    @Override
    public long takePoints(@NotNull UUID uuid, long points) {
        long l = super.takePoints(uuid, points);
        cache.remove(uuid);
        return l;
    }

    @Override
    public long setPoints(@NotNull UUID uuid, long points) {
        long l = super.setPoints(uuid, points);
        cache.put(uuid, points);
        return l;
    }
}
