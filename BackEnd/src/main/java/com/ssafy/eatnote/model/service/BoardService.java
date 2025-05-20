package com.ssafy.eatnote.model.service;

import com.ssafy.eatnote.model.dto.request.BoardCreateRequest;
import com.ssafy.eatnote.model.dto.request.BoardUpdateRequest;
import com.ssafy.eatnote.model.dto.response.BoardResponse;

import java.util.List;

public interface BoardService {

    // 게시판 생성
    void createBoard(BoardCreateRequest request);

    // 게시판 수정
    void updateBoard(int boardId, BoardUpdateRequest request);

    // 게시판 삭제
    void deleteBoard(int boardId);

    // 게시판 단건 조회 (응답용)
    BoardResponse getBoardResponseById(int boardId);

    // 전체 게시판 목록 조회 (응답용)
    List<BoardResponse> getAllBoardsAsResponse();
}
