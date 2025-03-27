package io.hhplus.tdd.point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class PointServiceUnitTests {
	
	private final ReentrantLock lock = new ReentrantLock(); // ✅ 실제 객체 사용
;
	
	@Mock
	private PointHistoryTable pointHistoryTable;
	
	@Mock
	private UserPointTable userPointTable;
	
	private PointService pointService;
	
	@BeforeEach
    public void setup() {
        this.pointService = new PointService(userPointTable, pointHistoryTable, lock);  // ✅ 직접 주입
    }
	
	// 포인트 조회
	
	@Test
	public void 새로운_유저_포인트_조회_정상() {
		// given
		Long userId = 2L;
	    UserPoint expectedUserPoint = UserPoint.empty(1L);
	    UserPoint expectedUserPoint2 = new UserPoint(userId, 10, System.currentTimeMillis());
		when(this.userPointTable.selectById(1L)).thenReturn(expectedUserPoint);
		when(this.userPointTable.selectById(userId)).thenReturn(expectedUserPoint2);
		
		// when
		UserPoint nowPoint = this.pointService.getPoint(1L);
		UserPoint nowPoint2 = this.pointService.getPoint(userId);
		
		// then
		assertEquals(nowPoint.getPoint(), 0);
		assertEquals(nowPoint2.getPoint(), 10);
	}
	
	@Test
	public void 포인트_조회_id값이_없는_경우() {
		assertThrows(IllegalArgumentException.class, () -> this.pointService.getPoint(null), "id값이 null일 수 없습니다.");
	}
	
	@Test
	public void 포인트_조회_id에_해당하는_유저가_없을_경우() {
		// given
		when(this.userPointTable.selectById(anyLong())).thenReturn(null);
		
		// when
		UserPoint test1 = this.pointService.getPoint(1L);
		UserPoint test2 = this.pointService.getPoint(2L);
		
		// then
		assertNotEquals(test1, test2);
	}
	
	// 포인트 충전
	
	@Test
	public void 포인트_충전_정상() {
	    // given
	    Long userId = 1L;
	    UserPoint initialUserPoint = new UserPoint(userId, 0L, System.currentTimeMillis());
	    when(this.userPointTable.selectById(userId)).thenReturn(initialUserPoint);
	    
	    UserPoint updatedUserPoint = new UserPoint(userId, 10L, System.currentTimeMillis());
	    when(this.userPointTable.insertOrUpdate(userId, 10)).thenReturn(updatedUserPoint);
	    
	    // when
	    UserPoint compareUserPoint = this.pointService.rechargePoint(userId, 10L);

	    // then
	    assertEquals(compareUserPoint.point(), 10L);
	}
	
	@Test
	public void 포인트_충전_음수() {
		// given
		Long userId = 1L;
	    UserPoint initialUserPoint = new UserPoint(userId, 0L, System.currentTimeMillis());
	    when(this.userPointTable.selectById(userId)).thenReturn(initialUserPoint);
	    
	    assertThrows(IllegalArgumentException.class, () -> this.pointService.rechargePoint(userId, -1L));
	}
	
	@Test
	public void 포인트_충전_최댓값_오버() {
		// given
		Long userId = 1L;
	    UserPoint initialUserPoint = new UserPoint(userId, 0L, System.currentTimeMillis());
	    when(this.userPointTable.selectById(userId)).thenReturn(initialUserPoint);
	    
	    assertThrows(IllegalArgumentException.class, () -> this.pointService.rechargePoint(userId, 9999999999999L));
	}
	
	// 포인트 사용
	
	@Test
	public void 포인트_사용_정상() {
		// given
	    Long userId = 1L;
	    UserPoint initialUserPoint = new UserPoint(userId, 10L, System.currentTimeMillis());
	    when(this.userPointTable.selectById(userId)).thenReturn(initialUserPoint);
	    
	    UserPoint updatedUserPoint = new UserPoint(userId, 0L, System.currentTimeMillis());
	    when(this.userPointTable.insertOrUpdate(userId, 0)).thenReturn(updatedUserPoint);
	    
	    // when
	    UserPoint compareUserPoint = this.pointService.usePoint(userId, 10L);

	    // then
	    assertEquals(compareUserPoint.point(), 0L);
	}
	
	@Test
	public void 포인트_사용_음수() {
		// given
		Long userId = 1L;
	    UserPoint initialUserPoint = new UserPoint(userId, 0L, System.currentTimeMillis());
	    when(this.userPointTable.selectById(userId)).thenReturn(initialUserPoint);
	    
	    assertThrows(IllegalArgumentException.class, () -> this.pointService.usePoint(userId, -1L));
	}
	
	@Test
	public void 포인트_사용_0미만() {
		// given
		Long userId = 1L;
	    UserPoint initialUserPoint = new UserPoint(userId, 0L, System.currentTimeMillis());
	    when(this.userPointTable.selectById(userId)).thenReturn(initialUserPoint);
	    
	    assertThrows(IllegalArgumentException.class, () -> this.pointService.usePoint(userId, 9999999999999L));
	}
	
	// 포인트 내역 조회
	
	private List<PointHistory> makeHistoryData() {
		List<PointHistory> data = new ArrayList<>();
		
		for (int i = 0; i < 5; i++) {
			data.add(new PointHistory(i, i, i * 10, (i % 2 == 0)? TransactionType.CHARGE: TransactionType.USE, System.currentTimeMillis()));
		}
		
		return data;
	}
	
	@Test
	public void 포인트_내역_조회_정상() {
		// given
		when(this.pointHistoryTable.selectAllByUserId(anyLong())).thenReturn(this.makeHistoryData());
		
		// when
		List<PointHistory> compareList = this.pointService.getPointHistory(1L);
		
		// then
		assertEquals(compareList.size(), 5);
	}
	
	@Test
	public void 포인트_내역_없을_때() {
		// given
		when(this.pointHistoryTable.selectAllByUserId(anyLong())).thenReturn(new ArrayList<>());
		
		// when
		List<PointHistory> compareList = this.pointService.getPointHistory(1L);
		
		// then
		assertEquals(compareList.size(), 0);
	}
	
    @Test
    public void 동일한_유저에게_동시성_요청_테스트() throws InterruptedException {
        // given
        Long userId = 1L;
        UserPoint initialUserPoint = new UserPoint(userId, 100L, System.currentTimeMillis());
        when(this.userPointTable.selectById(userId)).thenReturn(initialUserPoint);

        // CountDownLatch를 사용하여 모든 스레드가 동시에 시작되도록 설정
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // 여러 스레드를 생성하여 동일한 사용자에게 요청
        for (int i = 0; i < 3; i++) {
            executor.submit(() -> {
                try {
                    latch.await();
                    this.pointService.rechargePoint(userId, 50L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        latch.countDown();

        executor.shutdown();
        while (!executor.isTerminated()) {
            // 대기
        }

        UserPoint finalPoint = this.pointService.getPoint(userId);
        
        assertEquals(100L + (3 * 50L), finalPoint.getPoint());
    }
}
