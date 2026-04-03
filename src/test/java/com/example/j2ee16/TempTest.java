package com.example.j2ee16;

import com.example.j2ee16.dto.request.WalletDepositRequest;
import com.example.j2ee16.entity.User;
import com.example.j2ee16.repository.UserRepository;
import com.example.j2ee16.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;

@SpringBootTest
public class TempTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testDeposit() {
        try {
            User user = userRepository.findAll().stream().findFirst().orElseThrow();
            WalletDepositRequest req = new WalletDepositRequest();
            req.setAmount(new BigDecimal("500000"));
            walletService.initiateDeposit(user.getId(), req);
            System.out.println("SUCCESS_TEST_DEPOSIT");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
