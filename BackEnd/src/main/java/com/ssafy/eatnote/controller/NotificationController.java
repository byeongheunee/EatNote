package com.ssafy.eatnote.controller;

import com.ssafy.eatnote.model.dto.Notification;
import com.ssafy.eatnote.model.dto.NotificationMessage;
import com.ssafy.eatnote.model.dto.response.MyApiResponse;
import com.ssafy.eatnote.model.security.UserDetailsImpl;
import com.ssafy.eatnote.model.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@Tag(name = "알림 API", description = "알림 관련 API입니다.")
public class NotificationController {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    /**
     * WebSocket 알림 수신 및 실시간 전송
     * (Swagger 문서화 대상 아님)
     */
    @MessageMapping("/notify")
    public void notify(NotificationMessage message) {
        notificationService.saveNotification(message);
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + message.getReceiverId(),
                message
        );
    }

    @Operation(summary = "사용자 알림 목록 조회", description = "해당 사용자의 알림을 최신순으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공"),
    })
    @GetMapping
    public ResponseEntity<MyApiResponse<List<Notification>>> getNotifications(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getUserId();
        List<Notification> notifications = notificationService.getNotificationsByUser(userId);
        return ResponseEntity.ok(
                MyApiResponse.success(notifications, "NOTIFICATION_LIST_SUCCESS", "알림 목록을 성공적으로 조회했습니다.")
        );
    }

    @Operation(summary = "알림 읽음 처리", description = "알림 ID를 받아 해당 알림을 읽음 상태로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 읽음 처리 완료"),
            @ApiResponse(responseCode = "404", description = "해당 알림이 존재하지 않음")
    })
    @PutMapping("/{id}/read")
    public ResponseEntity<MyApiResponse<Void>> markAsRead(@PathVariable("id") Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(
                MyApiResponse.success(null, "NOTIFICATION_MARK_READ", "알림을 읽음 처리했습니다.")
        );
    }
    
    @Operation(summary = "안 읽은 알림 개수 조회", description = "사용자의 읽지 않은 알림 개수를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    @GetMapping("/unread-count")
    public ResponseEntity<MyApiResponse<Integer>> countUnread(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getUserId();
        int count = notificationService.countUnreadNotifications(userId);
        return ResponseEntity.ok(
                MyApiResponse.success(count, "NOTIFICATION_UNREAD_COUNT", "안 읽은 알림 개수를 성공적으로 조회했습니다.")
        );
    }
    
    @Operation(summary = "모든 알림 읽음 처리", description = "해당 사용자의 모든 알림을 읽음 상태로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 알림 읽음 처리 완료")
    })
    @PutMapping("/read-all")
    public ResponseEntity<MyApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getUserId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(
                MyApiResponse.success(null, "NOTIFICATION_MARK_ALL_READ", "모든 알림을 읽음 처리했습니다.")
        );
    }
}
