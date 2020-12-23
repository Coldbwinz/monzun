package com.example.monzun.repositories;

import com.example.monzun.entities.Task;
import com.example.monzun.entities.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskCommentRepository extends JpaRepository<TaskComment, Long>, TaskCommentRepositoryWithJOOQ {
    List<TaskComment> findAllByTask(Task task);
}
