package org.mmx.comment.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mmx.comment.domain.Comment;
import org.mmx.comment.exception.CommentNotFoundException;
import org.mmx.comment.service.CommentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Web MVC Test for CommentController
 */
@WebMvcTest(CommentController.class)
public class CommentControllerWebMvcTest {
    @MockBean
    private CommentService service;

    @Autowired
    private MockMvc mockMvc;

    private long commentId = 5l;
    private long authorId = 2l;
    private String newContent = "this is the new comment";
    private Comment expComment = new Comment(Long.valueOf(commentId), Long.valueOf(authorId), newContent);

    @Test
    public void testEdit_success() throws Exception {
        // given:
        doReturn(expComment).when(service).editComment(commentId, newContent);

        // when:
        mockMvc.perform(post("/api/v1/comments/{id}/editComment", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newContent))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(commentId))
            .andExpect(jsonPath("$.authorId").value(authorId))
            .andExpect(jsonPath("$.comment").value(newContent))
            ;
    }

    @Test
    public void testEdit_notFound() throws Exception {
        // given:
        doThrow(new CommentNotFoundException(commentId)).when(service).editComment(commentId, newContent);

        // when:
        mockMvc.perform(post("/api/v1/comments/{id}/editComment", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newContent))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.commentId").value(commentId))
            ;
    }

    @Test
    public void testDelete_success() throws Exception {
        // given:

        // when:
        mockMvc.perform(delete("/api/v1/comments/{id}", commentId)
                        .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNoContent())
            ;
        verify(service).delete(eq(commentId));
    }

    @Test
    public void testDelete_notFound() throws Exception {
        // given:
        doThrow(new CommentNotFoundException(commentId)).when(service).delete(commentId);

        // when:
        mockMvc.perform(delete("/api/v1/comments/{id}", commentId)
                        .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.commentId").value(commentId))
            ;
    }
}