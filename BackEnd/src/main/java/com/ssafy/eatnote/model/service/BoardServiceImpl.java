package com.ssafy.eatnote.model.service;

import com.ssafy.eatnote.model.dao.BoardDao;
import com.ssafy.eatnote.model.dto.Board;
import com.ssafy.eatnote.model.dto.request.BoardCreateRequest;
import com.ssafy.eatnote.model.dto.request.BoardUpdateRequest;
import com.ssafy.eatnote.model.dto.response.BoardResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardDao boardDao;

    @Override
    public void createBoard(BoardCreateRequest request) {
        Board board = Board.builder()
                .name(request.getName())
                .description(request.getDescription())
                .accessCode(request.getAccessCode())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        boardDao.createBoard(board);
    }

    @Override
    public void updateBoard(int boardId, BoardUpdateRequest request) {
        Board board = Board.builder()
                .boardId(boardId)
                .name(request.getName())
                .description(request.getDescription())
                .accessCode(request.getAccessCode())
                .updatedAt(LocalDateTime.now())
                .build();

        boardDao.updateBoard(board);
    }

    @Override
    public void deleteBoard(int boardId) {
        boardDao.deleteBoard(boardId);
    }

    @Override
    public BoardResponse getBoardResponseById(int boardId) {
        Board board = boardDao.getBoardById(boardId);
        if (board == null) return null;

        return toResponse(board);
    }

    @Override
    public List<BoardResponse> getAllBoardsAsResponse() {
        List<Board> boards = boardDao.getAllBoards();
        return boards.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Board → BoardResponse 변환 메서드
    private BoardResponse toResponse(Board board) {
        return BoardResponse.builder()
                .boardId(board.getBoardId())
                .name(board.getName())
                .description(board.getDescription())
                .accessCode(board.getAccessCode())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }
}
