package com.nakta.springlv1.domain.board.service;

import com.nakta.springlv1.domain.board.dto.BoardRequestDto;
import com.nakta.springlv1.domain.board.dto.BoardResponseDto;
import com.nakta.springlv1.domain.board.repository.BoardRepository;
import com.nakta.springlv1.domain.comment.dto.CommentResponseDto;
import com.nakta.springlv1.domain.comment.entity.Comment;
import com.nakta.springlv1.domain.comment.repository.CommentRepository;
import com.nakta.springlv1.domain.user.dto.StringResponseDto;
import com.nakta.springlv1.domain.board.entity.Board;
import com.nakta.springlv1.domain.user.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    private final JwtUtil jwtUtil;

    public BoardResponseDto createBoard(BoardRequestDto requestDto, HttpServletRequest req) {

        //토큰 검증
        String tokenValue = validateToken(req);

        Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
        Board board = new Board(requestDto, info.getSubject()); //username을 따로 받기 위한 생성자 생성
        Board newboard = boardRepository.save(board);
        return new BoardResponseDto(newboard);
    }

    public List<BoardResponseDto> getAllBoard() {
        return boardRepository.findAllByOrderByModifiedAtDesc().stream()
                .map(BoardResponseDto::new)
                .map((a)-> {
                    List<Comment> list = commentRepository.findAllByBoard_IdOrderByModifiedAtDesc(a.getId());
                    a.addCommentList(list.stream().map(CommentResponseDto::new).toList());
                    return a;
                })
                .toList();
    }

    public BoardResponseDto getOneBoard(Long id) {
        Board board = findById(id);
        BoardResponseDto responseDto = new BoardResponseDto(board);
        List<Comment> list = commentRepository.findAllByBoard_IdOrderByModifiedAtDesc(responseDto.getId());
        responseDto.addCommentList(list.stream().map(CommentResponseDto::new).toList());
        return responseDto;
    }

    @Transactional
    public BoardResponseDto modifyBoard(Long id, BoardRequestDto requestDto, HttpServletRequest req) {

        //토큰 검증
        String tokenValue = validateToken(req);

        //작성자 일치 확인
        Board board = findById(id);
        Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
        if (info.getSubject().equals(board.getUsername())) {
            board.update(requestDto, info.getSubject());
            return new BoardResponseDto(board);
        } else {
            throw new IllegalArgumentException("작성자가 일치하지 않습니다");
        }
    }

    public StringResponseDto deleteBoard(Long id, HttpServletRequest req) {

        //토큰 검증
        String tokenValue = validateToken(req);

        //작성자 일치 확인
        Board board = findById(id);
        Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
        if (info.getSubject().equals(board.getUsername())) {
            boardRepository.deleteById(id);
            return new StringResponseDto("삭제를 성공하였음");
        } else {
            throw new IllegalArgumentException("작성자가 일치하지 않습니다");
        }

    }

    private Board findById(Long id) {
        return boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("선택한 메모는 존재하지 않습니다."));
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