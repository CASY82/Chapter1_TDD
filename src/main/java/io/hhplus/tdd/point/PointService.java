package io.hhplus.tdd.point;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {
	
	private final UserPointTable userPointTable;
	private final PointHistoryTable pointHistoryTable;
	
	// 포인트 조회
	public UserPoint getPoint(Long id) {
		Assert.notNull(id, "id값이 없을 수 없습니다.");
	    return Optional.ofNullable(this.userPointTable.selectById(id))
	    		.orElse(UserPoint.empty(id));
	}

	// 포인트 충전
	public UserPoint rechargePoint(Long id, Long amount) {
		Assert.notNull(id, "id값이 없을 수 없습니다.");
		UserPoint point = Optional.ofNullable(this.userPointTable.selectById(id))
	    		.orElse(UserPoint.empty(id));
		
		UserPoint newPoint = point.rechargePoint(amount);
		this.pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
		
		return this.userPointTable.insertOrUpdate(id, newPoint.getPoint());
	}

	// 포인트 사용
	public UserPoint usePoint(Long id, Long amount) {
		Assert.notNull(id, "id값이 없을 수 없습니다.");
		UserPoint point = Optional.ofNullable(this.userPointTable.selectById(id))
	    		.orElse(UserPoint.empty(id));
		
		UserPoint newPoint = point.usePoint(amount);
		this.pointHistoryTable.insert(id, amount, TransactionType.USE, System.currentTimeMillis());
		
		return this.userPointTable.insertOrUpdate(id, newPoint.getPoint());
	}

	// 포인트 내역 조회
	public List<PointHistory> getPointHistory(Long id) {
		Assert.notNull(id, "id값이 없을 수 없습니다.");
		return Optional.ofNullable(this.pointHistoryTable.selectAllByUserId(id))
				.orElse(new ArrayList<>());
	}
}
