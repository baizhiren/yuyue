package com.yuyue.backend.component;

import com.google.gson.Gson;
import com.yuyue.backend.exception.MakeAppointmentErrorEnum;
import com.yuyue.backend.service.SessionService;
import io.renren.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private SessionService sessionService; // 使用前面定义的SessionService

    private static Gson gson = new Gson();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //return true;

        String sessionId = request.getHeader("yuyueSessionId");
        boolean isValidSession = sessionService.validateSession(sessionId);
        if (!isValidSession) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            writeErrorResponse(response, R.error(MakeAppointmentErrorEnum.USER_NOT_LOGIN.toException()));
            return false; // 返回false，请求将被终止
        }
        return true; // 会话有效，请求继续处理
    }

    private void writeErrorResponse(HttpServletResponse response, R errorResponse) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(gson.toJson(errorResponse));
    }
}