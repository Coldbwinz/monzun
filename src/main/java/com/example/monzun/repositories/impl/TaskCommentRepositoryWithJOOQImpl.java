package com.example.monzun.repositories.impl;

import com.example.monzun.entities.Attachment;
import com.example.monzun.entities.TaskComment;
import com.example.monzun.enums.AttachmentPolytableTypeConstants;
import com.example.monzun.repositories.TaskCommentRepositoryWithJOOQ;
import org.jooq.DSLContext;

import java.util.List;

public class TaskCommentRepositoryWithJOOQImpl implements TaskCommentRepositoryWithJOOQ {
    private final DSLContext jooq;

    public TaskCommentRepositoryWithJOOQImpl(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Override
    public List<Attachment> getTaskCommentAttachments(TaskComment taskComment) {
        return jooq.fetch(
                "SELECT DISTINCT(a.*) " +
                        "FROM attachments AS a " +
                        "WHERE a.polytable_id = " + taskComment.getId() + " " +
                        "AND a.polytable_type = '" + AttachmentPolytableTypeConstants.TASK_COMMENT.getType() + "'")
                .into(Attachment.class);
    }
}
