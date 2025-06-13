package pl.crystalek.budgetweb.utils;

import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PROTECTED)
public class BaseTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserAccountUtil userAccountUtil;
    @Autowired
    EntityManager entityManager;
}
