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
	
	static LongStream validRange() {
        return LongStream.rangeClosed(0, 1000); // 0부터 100까지의 범위를 제공
    }
	
	// 정상 범주
	@ParameterizedTest
	@MethodSource("validRange")
	public void 포인트_충전_테스트(long value) {
		// given
		UserPoint tester = UserPoint.empty(1L);
		
		// when
		UserPoint newData = tester.rechargePoint(value);
		
		// then
        assertEquals(tester.getPoint() + value, newData.getPoint(), "충전된 포인트가 올바르게 계산되어야 합니다.");
	}
	
	// 최댓값 1000000일때
	@Test
	public void 포인트_충전_최댓값일_때() {
		// given
		UserPoint tester = UserPoint.empty(1L);
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> tester.rechargePoint(1000000L), "100만을 넘길 수 없습니다.");
	}
	
	// 최댓값을 넘겼을 때
	@Test
	public void 포인트_충전_최댓값을_넘길_때() {
		// given
		UserPoint tester = UserPoint.empty(1L);
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> tester.rechargePoint(999999999L), "100만을 넘길 수 없습니다.");
	}
	
	// 음수가 들어왔을 때
	@Test
	public void 포인트_충전에_음수가_들어왔을_때() {
		// given
		UserPoint tester = UserPoint.empty(1L);
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> tester.rechargePoint(-1L), "100만을 넘길 수 없습니다.");
	}
}
