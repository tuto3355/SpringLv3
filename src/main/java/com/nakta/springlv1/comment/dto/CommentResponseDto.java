package com.nakta.springlv1.comment.dto;

import com.nakta.springlv1.comment.entity.Comment;
import lombok.Getter;

@Getter
public class CommentResponseDto {

    private Long id;
    private String message;
    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.message = comment.getMessage();
    }
}