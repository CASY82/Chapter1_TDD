package io.hhplus.tdd.point;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);
    private final PointService pointService;

    // IllegalArgumentException은 광범위 하게 발생하므로 AOP로 분리
    
    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public UserPoint point(@PathVariable("id") long id) {
    	if (id < 0) {
            throw new IllegalArgumentException("Check Id.");
        }
    	
        return this.pointService.getPoint(id);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(@PathVariable("id") long id) {
    	if (id < 0) {
            throw new IllegalArgumentException("Check Id.");
        }
    	
        return this.pointService.getPointHistory(id);
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(@PathVariable("id") long id, @RequestBody long amount) {
    	if (id < 0) {
            throw new IllegalArgumentException("Check Id.");
        }
    	
    	if (amount < 0) {
            throw new IllegalArgumentException("Amount Over 1");
        }
    	
        return this.pointService.rechargePoint(id, amount);
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(@PathVariable("id") long id, @RequestBody long amount) {
    	if (id < 0) {
            throw new IllegalArgumentException("Check Id.");
        }
    	
    	if (amount < 0) {
            throw new IllegalArgumentException("Amount Over 1");
        }
    	
        return this.pointService.usePoint(id, amount);
    }
}
