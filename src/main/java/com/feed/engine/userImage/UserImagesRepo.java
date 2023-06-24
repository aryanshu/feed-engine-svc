package com.feed.engine.userImage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserImagesRepo extends JpaRepository<UserImage,Long> {
    @Query("SELECT img FROM UserImage img WHERE img.Id = :value1 AND img.imageOrderId = :value2")
    UserImage findByAttributes(@Param("value1") Long Id, @Param("value2") Integer imageOrderId);

}
