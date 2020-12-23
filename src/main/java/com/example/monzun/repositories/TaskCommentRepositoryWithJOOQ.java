package com.example.monzun.repositories;

import com.example.monzun.entities.Attachment;
import com.example.monzun.entities.TaskComment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskCommentRepositoryWithJOOQ {
    List<Attachment> getTaskCommentAttachments(TaskComment taskComment);
}
