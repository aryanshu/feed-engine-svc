package com.feed.engine.userprofile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepo extends JpaRepository<UserProfile,Long> {
    Optional<UserProfile> findByEmail(String email);
}
