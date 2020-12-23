package com.example.monzun.repositories;

import com.example.monzun.entities.Attachment;
import com.example.monzun.entities.Task;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepositoryWithJOOQ {
    List<Attachment> getTaskAttachments(Task task);
}
