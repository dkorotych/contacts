/*
 * Copyright 2013 Dmitry Korotych &lt;dkorotych at exadel dot com&gt;.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package contacts.rest;

import contacts.model.Contact;
import contacts.service.SearchContactsService;
import contacts.util.JsonUtils;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.hamcrest.Matchers.is;
import org.junit.After;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * @author Dmitry Korotych &lt;dkorotych at exadel dot com&gt;
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:/spring/test-context.xml",
    "classpath:/spring/app-context.xml",
    "classpath:/spring/rest-servlet.xml",
    "classpath:/spring/security-context.xml"
})
@WebAppConfiguration
public class SearchControllerTests {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
    private static final String IVANOV_NAME = "ivanov";
    private static final String LOCATION = "Donetsk";
    private static final String SEARCH_URI = "/search";
    private static final String MY_URI = "/my";
    private MockMvc mockMvc;
    @Autowired
    private SearchContactsService searchContactsService;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void tearDown() {
        //We have to reset our mock between tests because the mock objects
        //are managed by the Spring container. If we would not reset them,
        //stubbing and verified behavior would "leak" from one test to another.
        Mockito.reset(searchContactsService);
    }

    @Test
    public void testMy() throws Exception {
        Contact ivanov = createExpectedContact();

        when(searchContactsService.findByUserName(IVANOV_NAME)).thenReturn(ivanov);

        createExpectedResultActions(MY_URI)
                .andExpect(jsonPath("$.mail", is(ivanov.getMail())))
                .andExpect(jsonPath("$.phone", is(ivanov.getPhone())))
                .andExpect(jsonPath("$.location", is(ivanov.getLocation())))
                .andExpect(jsonPath("$.userName", is(ivanov.getUserName())))
                .andExpect(jsonPath("$.firstName", is(ivanov.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(ivanov.getLastName())));

        verify(searchContactsService, times(1)).findByUserName(IVANOV_NAME);
        verifyNoMoreInteractions(searchContactsService);
    }

    @Test
    public void testSearchWithLocation() throws Exception {
        Contact ivanov = createExpectedContact();
        List<Contact> contacts = createExpectedContacts();

        when(searchContactsService.findByLocation(LOCATION)).thenReturn(contacts);

        MockHttpServletRequestBuilder requestBuilder = createExpectedRequestBuilder(SEARCH_URI);
        requestBuilder.param("locations", new String[]{LOCATION});

        createExpectedResultActions(requestBuilder)
                .andExpect(jsonPath("$..userName", Matchers.hasSize(5)))
                .andExpect(jsonPath("$..location", Matchers.everyItem(is(ivanov.getLocation()))));

        verify(searchContactsService, times(1)).findByLocation(LOCATION);
        verifyNoMoreInteractions(searchContactsService);
    }

    @Test
    public void testSearchWithoutLocation() throws Exception {
        Contact ivanov = createExpectedContact();
        List<Contact> contacts = createExpectedContacts();

        when(searchContactsService.findByUserName(IVANOV_NAME)).thenReturn(ivanov);
        when(searchContactsService.findByLocation(LOCATION)).thenReturn(contacts);

        createExpectedResultActions(SEARCH_URI)
                .andExpect(jsonPath("$..userName", Matchers.hasSize(5)))
                .andExpect(jsonPath("$..userName", Matchers.hasItems("ivanov", "popov", "petrov", "kuznetsov")))
                .andExpect(jsonPath("$..location", Matchers.everyItem(is(ivanov.getLocation()))));

        verify(searchContactsService, times(1)).findByUserName(IVANOV_NAME);
        verify(searchContactsService, times(1)).findByLocation(LOCATION);
        verifyNoMoreInteractions(searchContactsService);
    }

    private static Contact createExpectedContact() {
        Contact ivanov = new Contact();
        ivanov.setUserName(IVANOV_NAME);
        ivanov.setFirstName("Ivan");
        ivanov.setLastName("Ivanov");
        ivanov.setMail("ivanov@test.com");
        ivanov.setPhone("3800000000");
        ivanov.setLocation("Donetsk");
        return ivanov;
    }

    private static List<Contact> createExpectedContacts() throws IOException {
        List<Contact> returnValue = Collections.emptyList();
        final String json = "["
                + "{\"userName\":\"ivanov\",\"firstName\":\"Ivan\",\"lastName\":\"Ivanov\",\"mail\":\"ivanov@test.com\",\"phone\":\"3800000000\",\"location\":\"Donetsk\"},"
                + "{\"userName\":\"petrov\",\"firstName\":\"Petr\",\"lastName\":\"Petrov\",\"mail\":\"petrov@test.ua.com\",\"phone\":\"3800000001\",\"location\":\"Donetsk\"},"
                + "{\"userName\":\"kuznetsov\",\"firstName\":\"Kuzma\",\"lastName\":\"Kuznetsov\",\"mail\":\"kuznetsov@test.com\",\"phone\":\"3800000002\",\"location\":\"Donetsk\"},"
                + "{\"userName\":\"popov\",\"firstName\":\"Pavel\",\"lastName\":\"Popov\",\"mail\":\"popov@test.com\",\"phone\":\"3800000003\",\"location\":\"Donetsk\"},"
                + "{\"userName\":\"grytsenko\",\"firstName\":\"Anton\",\"lastName\":\"Grytsenko\",\"mail\":\"grytsenko@test.com\",\"phone\":\"3800000004\",\"location\":\"Donetsk\"}"
                + "]";
        Contact[] tmp = JsonUtils.createObjectMapper().readValue(json, Contact[].class);
        if (tmp != null) {
            returnValue = Arrays.asList(tmp);
        }
        return returnValue;
    }

    private MockHttpServletRequestBuilder createExpectedRequestBuilder(String uri) throws Exception {
        return get(uri).principal(new IvanovPrincipal());
    }

    private ResultActions createExpectedResultActions(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        return mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8));
    }

    private ResultActions createExpectedResultActions(String uri) throws Exception {
        return createExpectedResultActions(createExpectedRequestBuilder(uri));
    }

    private static class IvanovPrincipal implements Principal {

        @Override
        public String getName() {
            return IVANOV_NAME;
        }
    }
}