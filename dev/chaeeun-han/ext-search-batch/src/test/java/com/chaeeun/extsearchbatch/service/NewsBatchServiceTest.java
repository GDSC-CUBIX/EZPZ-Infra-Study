package com.chaeeun.extsearchbatch.service;

import com.chaeeun.extsearchbatch.domain.NaverNewsItem;
import com.chaeeun.extsearchbatch.repository.NaverNewsItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"spring.config.location = classpath:application.yml"})
public class NewsBatchServiceTest {

    @InjectMocks
    private NewsBatchService newsBatchService;

    @Autowired
    private NaverNewsItemRepository newsRepo;

    @Value("${NAVER.CLIENT_ID}")
    private String clientId;

    @Value("${NAVER.CLIENT_SECRET}")
    private String clientSecret;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // clientId와 clientSecret를 newsBatchService에 주입
        ReflectionTestUtils.setField(newsBatchService, "clientId", clientId);
        ReflectionTestUtils.setField(newsBatchService, "clientSecret", clientSecret);

        // newsRepo를 Mock 객체로 초기화
        newsBatchService.newsRepo = newsRepo;
    }


    @Test
    @Rollback(false)
    public void 적재_개수가_10개다() throws Exception {
        // 현재 데이터베이스에 저장된 행 수를 조회
        long currentRowCount = newsRepo.count();

        newsBatchService.naverApiTest();

        // 예상하는 값(현재 행 수 + 10)과 현재 데이터베이스의 행 수 비교
        assertEquals(currentRowCount + 10, newsRepo.count());
    }

    @Test
    public void 주식_단어가_포함되어있다() throws Exception {
        List<NaverNewsItem> newsData = newsRepo.findAll();

        assertTrue(!newsData.isEmpty());

        for (NaverNewsItem item : newsData) {
            assertTrue(item.getTitle().contains("주식") || item.getDescription().contains("주식"));
        }
    }
}
