package com.nakta.springlv1.comment.controller;

import com.nakta.springlv1.board.service.BoardService;
import com.nakta.springlv1.comment.dto.CommentRequestDto;
import com.nakta.springlv1.comment.dto.CommentResponseDto;
import com.nakta.springlv1.comment.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/board/{id}/comment")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long id, @RequestBody CommentRequestDto requestDto, HttpServletRequest req) {
        return ResponseEntity.ok(commentService.createComment(id,requestDto,req));
    }
//
//    @PutMapping("/board/{id}/comment/{id2}")
//    public ResponseEntity<CommentResponseDto> modifyComment(
//            @PathVariable Long id, @PathVariable Long id2,
//            @RequestBody CommentRequestDto requestDto, HttpServletRequest req) {
//        return null;
//    }
//
//    @DeleteMapping("/board/{id}/comment/{id2}")
//    public ResponseEntity<CommentResponseDto> deleteComment(
//            @PathVariable Long id, @PathVariable Long id2, HttpServletRequest req) {
//        return null;
//    }
}
