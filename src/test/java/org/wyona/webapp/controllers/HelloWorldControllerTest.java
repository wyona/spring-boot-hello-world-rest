package org.wyona.webapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.wyona.webapp.models.LanguageEmail;
import org.wyona.webapp.services.GreetingService;
import org.wyona.webapp.services.MailerService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WebMvcTest(HelloWorldController.class)
public class HelloWorldControllerTest {

    @InjectMocks
    private HelloWorldController controller;

    @Mock
    private MailerService mailerService;

    @Mock
    private GreetingService greetingService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void greetingTest() throws Exception {
        when(greetingService.getGreetingText("de")).thenReturn("Hallo");
        doNothing().when(mailerService).sendEmail(anyString(), anyString(), anyString());

        MvcResult mvcResult = mockMvc.perform(post("/api/greeting/send/lang")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "de")
                .content(objectMapper.writeValueAsString(new LanguageEmail("test@test.com"))))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String body = mvcResult.getResponse().getContentAsString();

        Assert.assertEquals("Hallo", body);
    }

    @Test
    public void greetingTestWithoutEmail() throws Exception {
        when(greetingService.getGreetingText("de")).thenReturn("Hallo");
        doNothing().when(mailerService).sendEmail(anyString(), anyString(), anyString());

        mockMvc.perform(post("/api/greeting/send/lang")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "de")
                .content(objectMapper.writeValueAsString(new LanguageEmail())))
                .andExpect(status().is4xxClientError())
                .andDo(MockMvcResultHandlers.print());
    }

}
