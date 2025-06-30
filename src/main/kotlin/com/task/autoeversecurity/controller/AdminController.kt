package com.task.autoeversecurity.controller

import com.task.autoeversecurity.dto.KakaoTalkMessageSendingByAgeGroupRequest
import com.task.autoeversecurity.dto.UserDeleteRequest
import com.task.autoeversecurity.dto.UserResponse
import com.task.autoeversecurity.dto.UserUpdateRequest
import com.task.autoeversecurity.service.AdminService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/admins")
@SecurityRequirement(name = "basicAuth")
class AdminController(
    private val adminService: AdminService,
) {
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping
    fun updateUser(
        @RequestBody request: UserUpdateRequest,
    ) {
        adminService.updateUser(request)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    fun deleteUser(
        @RequestBody request: UserDeleteRequest,
    ) {
        adminService.deleteUser(request.userId)
    }

    @GetMapping
    fun getPagedUsers(pageable: Pageable): Page<UserResponse> {
        return adminService.getPagedUsers(pageable)
    }

    @PostMapping("/messages/kakao-talk/age-group")
    fun sendKakaoTalkMessageByAgeGroup(
        @RequestBody kakaoTalkMessageSendingByAgeGroupRequest: KakaoTalkMessageSendingByAgeGroupRequest,
    ) {
        adminService.sendKakaoMessageByAgeGroup(kakaoTalkMessageSendingByAgeGroupRequest.ageGroup)
    }
}
