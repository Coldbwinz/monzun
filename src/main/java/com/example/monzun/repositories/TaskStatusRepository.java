package com.example.monzun.repositories;

import com.example.monzun.entities.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Integer>, TaskRepositoryWithJOOQ {
    boolean existsByTaskStatusId(Integer id);
}
