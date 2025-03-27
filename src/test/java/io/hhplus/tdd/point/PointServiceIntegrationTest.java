package io.hhplus.tdd.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class PointServiceIntegrationTest {

    @Autowired
    private UserPointTable userPointTable;

    @Autowired
    private PointHistoryTable pointHistoryTable;

    private PointService pointService;

    @BeforeEach
    void setUp() {
    	this.pointService = new PointService(this.userPointTable, this.pointHistoryTable, new ReentrantLock());
    }

    @Test
    void 포인트_확인() {
        Long userId = 1L;
        UserPoint userPoint = this.pointService.getPoint(userId);
        
        assertThat(userPoint).isNotNull();
        assertThat(userPoint.getPoint()).isEqualTo(0);
    }

    @Test
    void 포인트_충전_정상() {
        Long userId = 1L;
        Long amount = 100L;
        
        UserPoint userPoint = this.pointService.rechargePoint(userId, amount);
        
        assertThat(userPoint.getPoint()).isEqualTo(amount);
    }

    @Test
    void 포인트_사용_정상() {
        Long userId = 2L;
        this.pointService.rechargePoint(userId, 200L);
        
        UserPoint userPoint = this.pointService.usePoint(userId, 100L);
        
        assertThat(userPoint.getPoint()).isEqualTo(100L);
    }

    @Test
    void 포인트_사용_예외_사용량이_더_높을때() {
        Long userId = 3L;
        UserPoint newUser = this.pointService.rechargePoint(userId, 50L);
        
        assertThrows(IllegalArgumentException.class, () -> this.pointService.usePoint(userId, 100L));
    }

    @Test
    void 포인트_충전_사용_내역_통합테스트() {
        Long userId = 4L;
        this.pointService.rechargePoint(userId, 100L);
        this.pointService.usePoint(userId, 50L);
        
        List<PointHistory> history = this.pointService.getPointHistory(userId);
        
        assertThat(history).hasSize(2);
    }
}
