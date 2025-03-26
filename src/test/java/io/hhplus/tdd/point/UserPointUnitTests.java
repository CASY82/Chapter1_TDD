package io.hhplus.tdd.point;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class UserPointUnitTests {
	
	// 층전 테스트
	
	static LongStream validChargeRange() {
        return LongStream.rangeClosed(0, 1000); // 0부터 100까지의 범위를 제공
    }
	
	// 정상 범주
	@ParameterizedTest
	@MethodSource("validChargeRange")
	public void 포인트_충전_테스트(long value) {
		// given
		UserPoint tester = UserPoint.empty(1L);
		
		// when
		UserPoint newData = tester.rechargePoint(value);
		
		// then
        assertEquals(tester.getPoint() + value, newData.getPoint(), "충전된 포인트가 올바르게 계산되어야 합니다.");
	}
	
	@Test
	public void 포인트_충전_최댓값일_때() {
		// given
		UserPoint tester = UserPoint.empty(1L);
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> tester.rechargePoint(1000000L), "100만을 넘길 수 없습니다.");
	}
	
	@Test
	public void 포인트_충전_최댓값을_넘길_때() {
		// given
		UserPoint tester = UserPoint.empty(1L);
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> tester.rechargePoint(999999999L), "100만을 넘길 수 없습니다.");
	}
	
	@Test
	public void 포인트_충전에_음수가_들어왔을_때() {
		// given
		UserPoint tester = UserPoint.empty(1L);
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> tester.rechargePoint(-1L), "음수가 들어올 수 없습니다.");
	}
	
	
	// 사용 테스트
	
	static LongStream validUseRange() {
        return LongStream.rangeClosed(0, 1000); // 0부터 100까지의 범위를 제공
    }
	
	// 정상 범주
	@ParameterizedTest
	@MethodSource("validUseRange")
	public void 포인트_사용_테스트(long value) {
		// given
		UserPoint tester = new UserPoint(1L, 10000L, System.currentTimeMillis());
		
		// when
		UserPoint newData = tester.usePoint(value);
		
		// then
        assertEquals(tester.getPoint() - value, newData.getPoint(), "사용된 포인트가 올바르게 계산되어야 합니다.");
	}
	
	@Test
	public void 가진_포인트보다_많이_사용할_때() {
		// given
		UserPoint tester = new UserPoint(1L, 10000L, System.currentTimeMillis());
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> tester.usePoint(1000000L), "포인트가 부족합니다.");
	}
	
	@Test
	public void 포인트_사용에_음수가_들어왔을_때() {
		// given
		UserPoint tester = new UserPoint(1L, 10000L, System.currentTimeMillis());
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> tester.usePoint(-1L), "음수가 들어올 수 없습니다.");
	}
}
