package com.ssafy.eatnote.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.eatnote.model.dto.Board;

@Mapper
public interface BoardDao {

    // 게시판 생성
    int createBoard(Board board);

    // 게시판 수정
    int updateBoard(Board board);

    // 게시판 삭제
    int deleteBoard(int boardId);

    // 게시판 단건 조회
    Board getBoardById(int boardId);

    // 전체 게시판 목록 조회
    List<Board> getAllBoards();
    
    // 게시판 유형 조회
    String selectAccessCodeByBoardId(int boardId);
}