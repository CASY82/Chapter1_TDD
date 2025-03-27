package io.hhplus.tdd.point;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.hhplus.tdd.ApiControllerAdvice;

@ExtendWith(MockitoExtension.class)
public class PointControllerTest {
	
	private MockMvc mockMvc;
	
    @Mock
    private PointService pointService;

    @InjectMocks
    private PointController pointController;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
        		.standaloneSetup(this.pointController)
                .setControllerAdvice(new ApiControllerAdvice()) // 여기서 Advice를 등록
        		.build();
    }

    @Test
    public void 유저_확인_정상케이스_테스트() throws Exception {
        UserPoint userPoint = UserPoint.empty(1L);

        when(this.pointService.getPoint(anyLong())).thenReturn(userPoint);

        this.mockMvc.perform(get("/point/{id}", 1L)
        		.contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.point").value(0))
                .andExpect(jsonPath("$.updateMillis").exists());
    }
    
    @Test
    public void 유저_확인_비정상_id_테스트() throws Exception {
        long invalidId = -1L;

        this.mockMvc.perform(get("/point/{id}", invalidId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Check Id."));
    }
    
    @Test
    public void 포인트_충전_정상케이스_테스트() throws Exception {
    	UserPoint userPoint = new UserPoint(1L, 100, System.currentTimeMillis());
    	Long amount = 100L;

        when(this.pointService.rechargePoint(anyLong(), anyLong())).thenReturn(userPoint);

        this.mockMvc.perform(patch("/point/{id}/charge", 1L)
        		 .contentType(MediaType.APPLICATION_JSON)
                 .content(String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.point").value(100))
                .andExpect(jsonPath("$.updateMillis").exists());
    }
    
    @Test
    public void 포인트_충전_비정상_id_테스트() throws Exception {
        long invalidId = -1L;
        Long amount = 100L;

        this.mockMvc.perform(patch("/point/{id}/charge", invalidId)
        		 .contentType(MediaType.APPLICATION_JSON)
                 .content(String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Check Id."));
    }
    
    @Test
    public void 포인트_충전_비정상_금액_테스트() throws Exception {
        Long amount = -1L;

        this.mockMvc.perform(patch("/point/{id}/charge", 1L)
        		 .contentType(MediaType.APPLICATION_JSON)
                 .content(String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Amount Over 1"));
    }
    
    @Test
    public void 포인트_사용_정상케이스_테스트() throws Exception {
    	UserPoint userPoint = new UserPoint(1L, 0, System.currentTimeMillis());
    	Long amount = 100L;

        when(this.pointService.usePoint(anyLong(), anyLong())).thenReturn(userPoint);

        this.mockMvc.perform(patch("/point/{id}/use", 1L)
        		 .contentType(MediaType.APPLICATION_JSON)
                 .content(String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.point").value(0))
                .andExpect(jsonPath("$.updateMillis").exists());
    }
    
    @Test
    public void 포인트_사용_비정상_id_테스트() throws Exception {
        long invalidId = -1L;
        Long amount = 100L;

        this.mockMvc.perform(patch("/point/{id}/use", invalidId)
        		 .contentType(MediaType.APPLICATION_JSON)
                 .content(String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Check Id."));
    }
    
    @Test
    public void 포인트_사용_비정상_금액_테스트() throws Exception {
        Long amount = -1L;

        this.mockMvc.perform(patch("/point/{id}/use", 1L)
        		 .contentType(MediaType.APPLICATION_JSON)
                 .content(String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Amount Over 1"));
    }
    
    @Test
    public void 포인트_조회_정상케이스_테스트() throws Exception {
    	List<PointHistory> historyList = Arrays.asList(
                new PointHistory(1L, 1L, 100L, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(1L, 1L, 50L, TransactionType.USE, System.currentTimeMillis())
        );
    	
        when(pointService.getPointHistory(anyLong())).thenReturn(historyList);
        
        this.mockMvc.perform(get("/point/{id}/histories", 1L))
        	.andExpect(status().isOk())
        	.andExpect(jsonPath("$[0].userId").value(1L)) 
        	.andExpect(jsonPath("$[0].amount").value(100L)) 
        	.andExpect(jsonPath("$[0].type").value("CHARGE"))
        	.andExpect(jsonPath("$[0].updateMillis").exists()) 
        	.andExpect(jsonPath("$[1].userId").value(1L)) 
        	.andExpect(jsonPath("$[1].amount").value(50L))
        	.andExpect(jsonPath("$[1].type").value("USE"))
        	.andExpect(jsonPath("$[1].updateMillis").exists());
    }
    
}

