package com.example.monzun.repositories;

import com.example.monzun.dto.TaskListDTO;
import com.example.monzun.entities.Startup;
import com.example.monzun.entities.StartupTracking;
import com.example.monzun.entities.Task;
import com.example.monzun.entities.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, TaskRepositoryWithJOOQ {
    @Query("SELECT t FROM Task AS t WHERE t.tracking.id =:trackingId AND t.startup.id =:startupId")
    List<Task> findByTracking_IdAndStartup_Id(@Param("trackingId") Long trackingId, @Param("startupId") Long startupId);
}
