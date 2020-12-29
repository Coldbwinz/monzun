package com.example.monzun;

import com.example.monzun.entities.Startup;
import com.example.monzun.entities.User;
import com.example.monzun.enums.RoleEnum;
import com.example.monzun.repositories.StartupRepository;
import com.example.monzun.repositories.UserRepository;
import com.example.monzun.security.JwtRequestFilter;
import com.example.monzun.security.JwtUtil;
import com.example.monzun.services.MyUserDetailsService;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Configurable
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class StartupControllerTest extends BaseTest {
    private final String TEST_USER_EMAIL = "TESTOWNERSTARTUPS@MAIL.RU";
    private String jwt;
    private final String API_PREFIX = "/api/startups/";
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StartupRepository startupRepository;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MockMvc mockMvc;


    @Before
    public void setup() {
        User owner = new User();
        owner.setName(faker.name().name());
        owner.setEmail(TEST_USER_EMAIL);
        owner.setRole(RoleEnum.STARTUP.getRole());
        owner.setPassword(faker.phoneNumber().toString());
        userRepository.save(owner);

        jwt = jwtUtil.generateToken(myUserDetailsService.loadUserByUsername(TEST_USER_EMAIL));
    }

    @Test
    public void getUserStartupsTest() throws Exception {
        Startup startup = createTestStartupObject();

        mockMvc.perform(
                get(API_PREFIX)
                        .header(JwtRequestFilter.JWT_HEADER, JwtRequestFilter.JWT_HEADER_PREFIX + jwt)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(startup.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(startup.getName())));
    }

    @Test
    public void getUserStartupTest() throws Exception {
        Startup startup = createTestStartupObject();

        mockMvc.perform(
                get(API_PREFIX + startup.getId())
                        .header(JwtRequestFilter.JWT_HEADER, JwtRequestFilter.JWT_HEADER_PREFIX + jwt)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(startup.getId().intValue())))
                .andExpect(jsonPath("$.name", is(startup.getName())));
    }


    @Test
    public void createStartupTest() throws Exception {
        JSONObject params = new JSONObject();
        params.put("name", faker.name().name());

        mockMvc.perform(
                post(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(params.toString())
                        .header(JwtRequestFilter.JWT_HEADER, JwtRequestFilter.JWT_HEADER_PREFIX + jwt)
        ).andExpect(status().isOk());
    }

    @Test
    public void updateStartupTest() throws Exception {
        String updatedName = faker.name().name();
        Startup startup = createTestStartupObject();

        JSONObject params = new JSONObject();
        params.put("name", updatedName);

        mockMvc.perform(
                put(API_PREFIX + startup.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(params.toString())
                        .header(JwtRequestFilter.JWT_HEADER, JwtRequestFilter.JWT_HEADER_PREFIX + jwt)
        )
                .andExpect(status().isOk()).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(updatedName)));

    }


    @After
    public void teardown() {
        startupRepository.deleteAll();
        userRepository.findByEmail(TEST_USER_EMAIL).ifPresent(userRepository::delete);
    }

    private void setStartupOwner(Startup startup) {
        if (userRepository.findByEmail(TEST_USER_EMAIL).isPresent()) {
            startup.setOwner(userRepository.findByEmail(TEST_USER_EMAIL).get());
        }
    }

    private Startup createTestStartupObject() {
        Startup startup = new Startup();
        startup.setName(faker.name().name());
        setStartupOwner(startup);
        startupRepository.save(startup);

        return startup;
    }
}
