package com.example.j2ee16.service.impl;

import com.example.j2ee16.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendReminderEmail(String to, String passengerName, String routeName, String departureTime, String seatNumber, String ticketCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Nhắc nhở: Chuyến xe sắp khởi hành - " + routeName);
            
            String text = String.format(
                "Xin chào %s,\n\n" +
                "Đây là email nhắc nhở chuyến đi của bạn sắp khởi hành trong vòng 24 giờ tới.\n\n" +
                "Thông tin chuyến đi:\n" +
                "- Lộ trình: %s\n" +
                "- Giờ khởi hành: %s\n" +
                "- Mã vé: %s\n" +
                "- Số ghế: %s\n\n" +
                "Vui lòng có mặt tại bến xe trước 30 phút. Chúc bạn có một chuyến đi an toàn và vui vẻ!\n\n" +
                "Trân trọng,\n" +
                "Đội ngũ Vexere",
                passengerName, routeName, departureTime, ticketCode, seatNumber
            );
            
            message.setText(text);
            mailSender.send(message);
            logger.info("Email reminder sent to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send email reminder to {}", to, e);
        }
    }

    @Override
    public void sendPaymentSuccessEmail(String to, String customerName, String bookingCode, java.math.BigDecimal totalAmount) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Xác nhận thanh toán thành công - Mã đặt chỗ: " + bookingCode);
            
            // Format amount as VND
            java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));
            String formattedAmount = format.format(totalAmount);

            String text = String.format(
                "Xin chào %s,\n\n" +
                "Cảm ơn bạn đã sử dụng dịch vụ của Vexere!\n\n" +
                "Chúng tôi xin xác nhận thanh toán đã được thực hiện THÀNH CÔNG cho mã vé: %s.\n\n" +
                "Thông tin thanh toán:\n" +
                "- Tổng tiền: %s\n" +
                "- Hình thức thanh toán: VNPAY\n\n" +
                "Bạn có thể xem chi tiết vé và mã QR tại ứng dụng/website hoặc tải xuống vé điện tử trong phần 'Đơn hàng của tôi'.\n\n" +
                "Chúc bạn có một chuyến đi an toàn và vui vẻ!\n\n" +
                "Trân trọng,\n" +
                "Đội ngũ Vexere",
                customerName, bookingCode, formattedAmount
            );
            
            message.setText(text);
            mailSender.send(message);
            logger.info("Payment success email sent to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send payment success email to {}", to, e);
        }
    }
}
