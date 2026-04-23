package com.cryptoterm.backend.security;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory store of deleted user IDs.
 * Checked by JwtAuthFilter to reject tokens for deleted accounts
 * until the access token naturally expires.
 */
@Component
public class DeletedUserStore {

    private final Set<UUID> deletedUserIds = ConcurrentHashMap.newKeySet();

    public void markDeleted(UUID userId) {
        deletedUserIds.add(userId);
    }

    public boolean isDeleted(UUID userId) {
        return deletedUserIds.contains(userId);
    }
}
