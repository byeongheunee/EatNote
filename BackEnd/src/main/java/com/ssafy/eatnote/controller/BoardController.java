package com.ssafy.eatnote.controller;

import com.ssafy.eatnote.model.dto.Board;
import com.ssafy.eatnote.model.dto.request.BoardCreateRequest;
import com.ssafy.eatnote.model.dto.request.BoardUpdateRequest;
import com.ssafy.eatnote.model.dto.response.BoardResponse;
import com.ssafy.eatnote.model.dto.response.MyApiResponse;
import com.ssafy.eatnote.model.security.UserDetailsImpl;
import com.ssafy.eatnote.model.service.BoardService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "게시판 API", description = "게시판 관련 API입니다.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "게시판 목록 조회", description = "모든 게시판 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<MyApiResponse<List<BoardResponse>>> list() {
        List<BoardResponse> boards = boardService.getAllBoardsAsResponse();
        return ResponseEntity.ok(MyApiResponse.success(boards, "BOARD_LIST_SUCCESS", "게시판 목록 조회 성공"));
    }

    @Operation(summary = "게시판 생성", description = "관리자 권한으로 게시판을 생성합니다.")
    @PostMapping
    public ResponseEntity<MyApiResponse<Void>> create(
            @RequestBody BoardCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        if (userDetails == null || userDetails.getUser().getUserType() != 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(MyApiResponse.failure("AUTH_FORBIDDEN", "관리자만 게시판을 생성할 수 있습니다."));
        }

        boardService.createBoard(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MyApiResponse.success(null, "BOARD_CREATE_SUCCESS", "게시판 생성 완료"));
    }

    @Operation(summary = "게시판 상세 조회", description = "게시판 ID로 해당 게시판의 정보를 조회합니다.")
    @GetMapping("/{boardId}")
    public ResponseEntity<MyApiResponse<BoardResponse>> getBoard(@PathVariable int boardId) {
        BoardResponse board = boardService.getBoardResponseById(boardId);
        if (board == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(MyApiResponse.failure("BOARD_NOT_FOUND", "해당 게시판이 존재하지 않습니다."));
        }
        return ResponseEntity.ok(MyApiResponse.success(board, "BOARD_FETCH_SUCCESS", "게시판 조회 성공"));
    }

    @Operation(summary = "게시판 수정", description = "관리자 권한으로 게시판 정보를 수정합니다.")
    @PutMapping("/{boardId}")
    public ResponseEntity<MyApiResponse<Void>> update(
            @PathVariable int boardId,
            @RequestBody BoardUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        if (userDetails == null || userDetails.getUser().getUserType() != 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(MyApiResponse.failure("AUTH_FORBIDDEN", "관리자만 수정할 수 있습니다."));
        }

        boardService.updateBoard(boardId, request);
        return ResponseEntity.ok(MyApiResponse.success(null, "BOARD_UPDATE_SUCCESS", "게시판 수정 완료"));
    }

    @Operation(summary = "게시판 삭제", description = "관리자 권한으로 게시판을 삭제합니다.")
    @DeleteMapping("/{boardId}")
    public ResponseEntity<MyApiResponse<Void>> delete(
            @PathVariable int boardId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        if (userDetails == null || userDetails.getUser().getUserType() != 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(MyApiResponse.failure("AUTH_FORBIDDEN", "관리자만 삭제할 수 있습니다."));
        }

        boardService.deleteBoard(boardId);
        return ResponseEntity.ok(MyApiResponse.success(null, "BOARD_DELETE_SUCCESS", "게시판 삭제 완료"));
    }

}
