package io.hhplus.tdd.point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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
	
	@Mock
	private PointHistoryTable pointHistoryTable;
	
	@Mock
	private UserPointTable userPointTable;
	
	@InjectMocks
	private PointService pointService;
	
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
		// 조회시, null로 없다면 새로 생성된 두 객체는 같을 수가 없다.
		when(this.pointService.getPoint(anyLong())).thenReturn(null);
		UserPoint test1 = this.pointService.getPoint(1L);
		UserPoint test2 = this.pointService.getPoint(2L);
		
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
		List<PointHistory> compareList = this.pointService.getPointHistory(anyLong());
		
		// then
		assertEquals(compareList.size(), 5);
	}
}
