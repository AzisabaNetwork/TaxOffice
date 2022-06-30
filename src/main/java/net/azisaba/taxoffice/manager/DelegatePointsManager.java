package net.azisaba.taxoffice.manager;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class DelegatePointsManager extends PointsManager {
    @NotNull
    public abstract PointsManager delegate();

    @Override
    public long getPoints(@NotNull UUID uuid) {
        return delegate().getPoints(uuid);
    }

    @Override
    public long givePoints(@NotNull UUID uuid, long points) {
        return delegate().givePoints(uuid, points);
    }

    @Override
    public long takePoints(@NotNull UUID uuid, long points) {
        return delegate().takePoints(uuid, points);
    }

    @Override
    public long setPoints(@NotNull UUID uuid, long points) {
        return delegate().setPoints(uuid, points);
    }
}
