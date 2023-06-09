package com.example.backend.safe_guide.controller;

import com.example.backend.safe_guide.controller.request.UserJoinRequest;
import com.example.backend.safe_guide.controller.request.UserLoginRequest;
import com.example.backend.safe_guide.exception.ErrorCode;
import com.example.backend.safe_guide.exception.SafeGuideApplicationException;
import com.example.backend.safe_guide.model.User;
import com.example.backend.safe_guide.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithAnonymousUser
    public void 회원가입() throws Exception {
        String userId = "userid";
        String password = "password";
        String userName = "userName";
        String phoneNumber = "phoneNumber";
        String birth = "birth";
        String gender = "gender";
        String address = "address";

        when(userService.join(userId, password, userName, phoneNumber, birth, gender, address))
                .thenReturn(mock(User.class));

        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest("userid", "password",
                                "userName", "phoneNumber","birth",  "gender", "address"))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void 회원가입시_같은_아이디로_회원가입하면_에러발생() throws Exception {
        String userId = "userid";
        String password = "password";
        String userName = "userName";
        String phoneNumber = "phoneNumber";
        String birth = "birth";
        String gender = "gender";
        String address = "address";

        when(userService.join(userId, password, userName, phoneNumber, birth, gender, address))
                .thenThrow(new SafeGuideApplicationException(ErrorCode.DUPLICATED_USER_ID));

        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest("userid", "password",
                                "userName", "phoneNumber","birth",  "gender", "address"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.DUPLICATED_USER_ID.getStatus().value()));
    }

    @Test
    @WithAnonymousUser
    public void 로그인() throws Exception {
        String userId = "userId";
        String password = "password";

        when(userService.login(userId, password)).thenReturn("testToken");

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest("userId", "password"))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void 로그인시_회원가입한적이_없다면_에러발생() throws Exception {
        String userId = "userId";
        String password = "password";

        when(userService.login(userId, password)).thenThrow(new SafeGuideApplicationException(ErrorCode.USER_NOT_FOUND));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest("userId", "password"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.USER_NOT_FOUND.getStatus().value()));
    }

    @Test
    @WithAnonymousUser
    public void 로그인시_비밀번호가_다르면_에러발생() throws Exception {
        String userId = "userId";
        String password = "password";

        when(userService.login(userId, password)).thenThrow(new SafeGuideApplicationException(ErrorCode.INVALID_PASSWORD));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest("userId", "password"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PASSWORD.getStatus().value()));
    }

    /**
     * 알람
     */
    @Test
    @WithMockUser
    void 알람기능() throws Exception {
        when(userService.alarmList(any(), any())).thenReturn(Page.empty());
        mockMvc.perform(get("/api/v1/users/alarm")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @WithAnonymousUser
    void 알람기능시_로그인하지_않은경우() throws Exception {
        when(userService.alarmList(any(), any())).thenReturn(Page.empty());
        mockMvc.perform(get("/api/v1/users/alarm")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

}
