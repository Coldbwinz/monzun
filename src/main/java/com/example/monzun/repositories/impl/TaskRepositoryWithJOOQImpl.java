package com.example.monzun.repositories.impl;

import com.example.monzun.entities.Attachment;
import com.example.monzun.entities.Task;
import com.example.monzun.enums.AttachmentPolytableTypeConstants;
import com.example.monzun.repositories.TaskRepositoryWithJOOQ;
import org.jooq.DSLContext;

import java.util.List;

public class TaskRepositoryWithJOOQImpl implements TaskRepositoryWithJOOQ {
    private final DSLContext jooq;

    public TaskRepositoryWithJOOQImpl(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Override
    public List<Attachment> getTaskAttachments(Task task) {
        return jooq.fetch(
                "SELECT DISTINCT(a.*) " +
                        "FROM attachments AS a " +
                        "WHERE a.polytable_id = " + task.getId() + " " +
                        "AND a.polytable_type = '" + AttachmentPolytableTypeConstants.TASK.getType() + "'")
                .into(Attachment.class);
    }
}
