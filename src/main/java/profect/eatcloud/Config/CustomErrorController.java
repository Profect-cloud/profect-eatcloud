package profect.eatcloud.Config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        
        // 공통 에러 정보 설정
        model.addAttribute("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        model.addAttribute("path", requestUri);
        model.addAttribute("error", errorMessage);
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            model.addAttribute("status", statusCode);
            model.addAttribute("errorCode", "ERR_" + statusCode);
            
            // HTTP 상태 코드별 페이지 라우팅
            switch (statusCode) {
                case 403:
                    model.addAttribute("title", "접근 거부");
                    model.addAttribute("message", "이 리소스에 접근할 권한이 없습니다.");
                    return "error/403";
                    
                case 404:
                    model.addAttribute("title", "페이지를 찾을 수 없음");
                    model.addAttribute("message", "요청하신 페이지가 존재하지 않습니다.");
                    return "error/404";
                    
                case 500:
                    model.addAttribute("title", "서버 내부 오류");
                    model.addAttribute("message", "서버에서 오류가 발생했습니다.");
                    return "error/500";
                    
                default:
                    model.addAttribute("title", "오류 발생");
                    model.addAttribute("message", "알 수 없는 오류가 발생했습니다.");
                    return "error/500";
            }
        }
        
        // 기본 에러 페이지
        return "error/500";
    }
    
    /**
     * 주문 관련 에러 페이지
     */
    @RequestMapping("/error/order")
    public String handleOrderError(HttpServletRequest request, Model model) {
        String error = request.getParameter("error");
        String orderId = request.getParameter("orderId");
        String orderNumber = request.getParameter("orderNumber");
        
        model.addAttribute("error", error != null ? error : "주문 처리 중 오류가 발생했습니다.");
        model.addAttribute("orderId", orderId);
        model.addAttribute("orderNumber", orderNumber);
        model.addAttribute("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        model.addAttribute("errorCode", "ORDER_ERROR");
        
        return "order/error";
    }
    
    /**
     * 장바구니 비어있음 에러 페이지
     */
    @RequestMapping("/error/cart-empty")
    public String handleCartEmptyError() {
        return "order/cart-empty";
    }
    
    /**
     * 포인트 부족 에러 페이지
     */
    @RequestMapping("/error/insufficient-points")
    public String handleInsufficientPointsError(HttpServletRequest request, Model model) {
        String currentPoints = request.getParameter("currentPoints");
        String requestedPoints = request.getParameter("requestedPoints");
        
        model.addAttribute("currentPoints", currentPoints != null ? Integer.parseInt(currentPoints) : 0);
        model.addAttribute("requestedPoints", requestedPoints != null ? Integer.parseInt(requestedPoints) : 0);
        
        return "payment/insufficient-points";
    }
    
    /**
     * 인증 실패 에러 페이지
     */
    @RequestMapping("/error/unauthorized")
    public String handleUnauthorizedError(HttpServletRequest request, Model model) {
        String error = request.getParameter("error");
        model.addAttribute("error", error != null ? error : "로그인이 필요합니다.");
        return "error/unauthorized";
    }
}
