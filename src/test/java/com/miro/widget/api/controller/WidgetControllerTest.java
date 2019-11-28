package com.miro.widget.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miro.widget.api.contract.WidgetService;
import com.miro.widget.api.model.dto.PageableDto;
import com.miro.widget.api.model.dto.WidgetDto;
import com.miro.widget.api.model.entity.Page;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class WidgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WidgetService widgetService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void getAll() throws Exception {
        Page<WidgetDto> page = createPage();
        doReturn(page).when(widgetService).findPage(any());

        WidgetDto widget = page.getItems().iterator().next();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mockMvc.perform(get("/api/widgets"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(widget.getId().toString())))
                .andExpect(jsonPath("$.content[0].xСoordinate", Matchers.equalTo(40)))
                .andExpect(jsonPath("$.content[0].yСoordinate", Matchers.equalTo(50)))
                .andExpect(jsonPath("$.content[0].zIndex", Matchers.equalTo(5)))
                .andExpect(jsonPath("$.content[0].width", Matchers.equalTo(100)))
                .andExpect(jsonPath("$.content[0].height", Matchers.equalTo(50)))
                .andExpect(jsonPath("$.content[0].modifiedAt", is(simpleDateFormat.format(widget.getModifiedAt()))));
    }

    @Test
    public void getOne_WhenWidgetWasFound_ReturnFilledResponse() throws Exception {
        WidgetDto widgetDto = createWidgetDto();
        doReturn(widgetDto).when(widgetService).findById(any());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mockMvc.perform(get("/api/widgets/" + widgetDto.getId().toString()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(widgetDto.getId().toString())))
                .andExpect(jsonPath("$.xСoordinate", Matchers.equalTo(40)))
                .andExpect(jsonPath("$.yСoordinate", Matchers.equalTo(50)))
                .andExpect(jsonPath("$.zIndex", Matchers.equalTo(5)))
                .andExpect(jsonPath("$.width", Matchers.equalTo(100)))
                .andExpect(jsonPath("$.height", Matchers.equalTo(50)))
                .andExpect(jsonPath("$.modifiedAt", is(simpleDateFormat.format(widgetDto.getModifiedAt()))));
    }

    @Test
    public void getOne_WhenWidgetWasNotFound_ReturnFilledResponse() throws Exception {
        doReturn(null).when(widgetService).findById(any());

        mockMvc.perform(get("/api/widgets/" + UUID.randomUUID().toString()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void create() throws Exception {
        WidgetDto widgetDto = createWidgetDto();
        doReturn(widgetDto).when(widgetService).save(any());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mockMvc.perform(post("/api/widgets/")
                //.content(mapper.writeValueAsString(createJSONWidgetRequest()))
                .content(createJSONWidgetRequest())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(widgetDto.getId().toString())))
                .andExpect(jsonPath("$.xСoordinate", Matchers.equalTo(40)))
                .andExpect(jsonPath("$.yСoordinate", Matchers.equalTo(50)))
                .andExpect(jsonPath("$.zIndex", Matchers.equalTo(5)))
                .andExpect(jsonPath("$.width", Matchers.equalTo(100)))
                .andExpect(jsonPath("$.height", Matchers.equalTo(50)))
                .andExpect(jsonPath("$.modifiedAt", is(simpleDateFormat.format(widgetDto.getModifiedAt()))));

        verify(widgetService, times(1)).save(any(WidgetDto.class));
    }

    @Test
    public void update_WhenWidgetWasFoundAndRequestBodyIsInvalid_ReturnFailedResponse() throws Exception {
        WidgetDto widgetDto = createWidgetDto();
        doReturn(widgetDto).when(widgetService).findById(any());

        mockMvc.perform(put("/api/widgets/" + widgetDto.getId().toString())
                .content(createInvalidJSONWidgetRequest())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void update_WhenWidgetWasFoundAndRequestBodyIsValid_ReturnUpdatedResponse() throws Exception {
        WidgetDto widgetDto = createWidgetDto();
        doReturn(widgetDto).when(widgetService).findById(any());
        doReturn(widgetDto).when(widgetService).update(eq(widgetDto.getId()), any());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mockMvc.perform(put("/api/widgets/" + widgetDto.getId().toString())
                .content(createJSONWidgetRequest())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(widgetDto.getId().toString())))
                .andExpect(jsonPath("$.xСoordinate", Matchers.equalTo(40)))
                .andExpect(jsonPath("$.yСoordinate", Matchers.equalTo(50)))
                .andExpect(jsonPath("$.zIndex", Matchers.equalTo(5)))
                .andExpect(jsonPath("$.width", Matchers.equalTo(100)))
                .andExpect(jsonPath("$.height", Matchers.equalTo(50)))
                .andExpect(jsonPath("$.modifiedAt", is(simpleDateFormat.format(widgetDto.getModifiedAt()))));

        verify(widgetService, times(1)).update(eq(widgetDto.getId()), any(WidgetDto.class));
    }

    @Test
    public void delete_WhenWidgetWasFound_ReturnFilledDeleteResponse() throws Exception {
        WidgetDto widgetDto = createWidgetDto();
        doReturn(widgetDto).when(widgetService).findById(any());
        doReturn(widgetDto).when(widgetService).delete(eq(widgetDto.getId()));

        mockMvc.perform(delete("/api/widgets/" + widgetDto.getId().toString()))
                .andExpect(status().isOk());

        verify(widgetService, times(1)).delete(eq(widgetDto.getId()));
    }

    private static Page<WidgetDto> createPage() {
        return Page.createPage(
                Collections.singleton(createWidgetDto()),
                createPageableDto(),
                1
        );
    }

    private static PageableDto createPageableDto() {
        return new PageableDto(1, 10);
    }

    private static String createJSONWidgetRequest() {
        return "{\"xCoordinate\":40,\"yCoordinate\":50,\"zIndex\":5,\"width\":100,\"height\":50}";
    }

    private static String createInvalidJSONWidgetRequest() {
        return "{\"xCoordinate\":40,\"yCoordinate\":50,\"zIndex\":5,\"width\":-100,\"height\":-50}";
    }


    private static WidgetDto createWidgetDto() {
        return new WidgetDto(
                UUID.randomUUID(),
                40,
                50,
                5L,
                100,
                50,
                Date.from(Instant.now())
        );
    }
}