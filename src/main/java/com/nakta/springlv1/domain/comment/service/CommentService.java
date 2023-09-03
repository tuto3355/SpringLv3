package com.nakta.springlv1.domain.comment.service;

import com.nakta.springlv1.domain.board.entity.Board;
import com.nakta.springlv1.domain.board.repository.BoardRepository;
import com.nakta.springlv1.domain.comment.dto.CommentRequestDto;
import com.nakta.springlv1.domain.comment.dto.CommentResponseDto;
import com.nakta.springlv1.domain.comment.entity.Comment;
import com.nakta.springlv1.domain.comment.repository.CommentRepository;
import com.nakta.springlv1.domain.user.repository.UserRepository;
import com.nakta.springlv1.domain.user.dto.StringResponseDto;
import com.nakta.springlv1.domain.user.entity.User;
import com.nakta.springlv1.domain.user.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final JwtUtil jwtUtil;

    public CommentResponseDto createComment(Long id, CommentRequestDto requestDto, HttpServletRequest req) {
        //토큰 검증
        String tokenValue = validateToken(req);

        Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
        User user = userRepository.findByUsername(info.getSubject()).orElseThrow(()->{
            throw new IllegalArgumentException("유저가 없을리가 없긴한데 일단 오류 만듬 ㅋㅋ");
        });
        Board board = boardRepository.findById(id).orElseThrow(()->{
            throw new IllegalArgumentException("게시글이 존재하지 않습니다.");
        });;
        Comment comment = new Comment(requestDto, user, board);
        Comment newComment = commentRepository.save(comment);

        return new CommentResponseDto(newComment);

    }

    @Transactional
    public CommentResponseDto modifyComment(Long id, Long id2, CommentRequestDto requestDto, HttpServletRequest req) {
        //토큰 검증
        String tokenValue = validateToken(req);

        Comment comment = commentRepository.findById(id2).orElseThrow(() ->
                new IllegalArgumentException("해당 댓글이 없습니다")
        );

        Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
        User user = userRepository.findByUsername(info.getSubject()).orElseThrow(()->{
            throw new IllegalArgumentException("유저가 없을리가 없긴한데 일단 오류 만듬 ㅋㅋ");
        });
        Board board = boardRepository.findById(id).orElseThrow(()->{
            throw new IllegalArgumentException("게시글이 존재하지 않습니다.");
        });

        if (!user.equals(comment.getUser()) || !board.equals(comment.getBoard())) {
            throw new IllegalArgumentException("댓글의 아이디나 보드명이 다릅니다.");
        }
        comment.update(requestDto);
        return new CommentResponseDto(comment);
    }

    public StringResponseDto deleteComment(Long id, Long id2, HttpServletRequest req) {
        //토큰 검증
        String tokenValue = validateToken(req);

        Comment comment = commentRepository.findById(id2).orElseThrow(() ->
                new IllegalArgumentException("해당 댓글이 없습니다")
        );
        Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
        User user = userRepository.findByUsername(info.getSubject()).orElseThrow(()->{
            throw new IllegalArgumentException("유저가 없을리가 없긴한데 일단 오류 만듬 ㅋㅋ");
        });
        Board board = boardRepository.findById(id).orElseThrow(()->{
            throw new IllegalArgumentException("게시글이 존재하지 않습니다.");
        });

        if (!user.equals(comment.getUser()) || !board.equals(comment.getBoard())) {
            throw new IllegalArgumentException("댓글의 아이디나 보드명이 다릅니다.");
        }

        commentRepository.deleteById(id2);
        return new StringResponseDto("삭제가 잘 되었따");
    }

    private String validateToken(HttpServletRequest req) {
        String tokenValue = jwtUtil.getTokenFromRequest(req);
        tokenValue = jwtUtil.substringToken(tokenValue);
        if (!jwtUtil.validateToken(tokenValue)) {
            throw new IllegalArgumentException("토큰이 유효하지 않음");
        }
        return tokenValue;
    }
}
