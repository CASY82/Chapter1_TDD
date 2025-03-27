package io.hhplus.tdd.point;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }
    
    public UserPoint rechargePoint(Long amount) {
    	if (amount < 0) {
    		throw new IllegalArgumentException("충전 시, amount는 음수일 수 없습니다. 사용 함수를 사용해주시기 바랍니다.");
    	}
    	
    	Long charger = this.point + amount;
    	
    	if (charger >= 1000000) {
    		throw new IllegalArgumentException("최대 저장 가능한 포인트가 넘었습니다.");
    	}
    	
    	return new UserPoint(this.id, charger, System.currentTimeMillis());
    }
    
    public Long getPoint() {
    	return this.point;
    }
    
    public UserPoint usePoint(Long amount) {
    	if (amount < 0) {
    		throw new IllegalArgumentException("사용 시, amount는 음수일 수 없습니다. 충전 함수를 사용해주시기 바랍니다.");
    	}
    	
    	Long remains = this.point - amount;
    	
    	if (remains < 0) {
    		throw new IllegalArgumentException("잔여 포인트가 부족합니다.");
    	}
    	
    	return new UserPoint(this.id, remains, System.currentTimeMillis());
    }
}
