package ispp.project.dondesiempre.modules.clients.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ispp.project.dondesiempre.config.GlobalExceptionHandler;
import ispp.project.dondesiempre.config.security.SecurityConfig;
import ispp.project.dondesiempre.modules.clients.dtos.ClientDTO;
import ispp.project.dondesiempre.modules.clients.dtos.ClientUpdateDTO;
import ispp.project.dondesiempre.modules.clients.services.ClientService;
import ispp.project.dondesiempre.modules.common.exceptions.AlreadyExistsException;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = ClientController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {GlobalExceptionHandler.class}))
@Import(SecurityConfig.class)
public class ClientControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private ClientService clientService;

  @Test
  @WithMockUser(roles = "CLIENT")
  void shouldReturnDTOWhenSucess() throws Exception {
    ClientUpdateDTO dto = new ClientUpdateDTO();
    dto.setEmail("client@test.com");
    dto.setName("client");
    dto.setSurname("surname");

    ClientDTO response = new ClientDTO();
    response.setEmail("client@test.com");
    response.setName("client");
    response.setSurname("surname");

    when(clientService.updateClient(any(ClientUpdateDTO.class))).thenReturn(response);

    mockMvc
        .perform(
            put("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(dto.getEmail()))
        .andExpect(jsonPath("$.name").value(dto.getName()));

    verify(clientService).updateClient(any(ClientUpdateDTO.class));
  }

  @Test
  void shouldReturnForbiddenWhenNotAutenticated() throws Exception {
    ClientUpdateDTO dto = new ClientUpdateDTO();
    dto.setEmail("client@test.com");

    mockMvc
        .perform(
            put("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isForbidden());

    verify(clientService, never()).updateClient(any());
  }

  @Test
  @WithMockUser(roles = "CLIENT")
  void shouldReturnBadRequestWhenInvalidData() throws Exception {
    ClientUpdateDTO dto1 = new ClientUpdateDTO();
    dto1.setEmail("correo-invalido");
    dto1.setName("client");
    dto1.setSurname("surname");

    mockMvc
        .perform(
            put("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto1)))
        .andExpect(status().isBadRequest());

    ClientUpdateDTO dto2 = new ClientUpdateDTO();
    dto2.setEmail("client@test.com");
    dto2.setName(null);
    dto2.setSurname("surname");

    mockMvc
        .perform(
            put("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto2)))
        .andExpect(status().isBadRequest());

    verify(clientService, never()).updateClient(any());
  }

  @Test
  @WithMockUser(roles = "CLIENT")
  void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
    ClientUpdateDTO dto = new ClientUpdateDTO();
    dto.setEmail("correo.ocupado@test.com");
    dto.setName("client");
    dto.setSurname("surname");

    when(clientService.updateClient(any(ClientUpdateDTO.class)))
        .thenThrow(new AlreadyExistsException("Email already in use."));

    mockMvc
        .perform(
            put("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isConflict());
  }

  @Test
  @WithMockUser(roles = "STORE")
  void shouldReturnNotFoundWhenStoreTriesToUpdateClient() throws Exception {
    ClientUpdateDTO dto = new ClientUpdateDTO();
    dto.setEmail("store@test.com");
    dto.setName("store");
    dto.setSurname("surname");

    when(clientService.updateClient(any(ClientUpdateDTO.class)))
        .thenThrow(new ResourceNotFoundException("Current client not found."));

    mockMvc
        .perform(
            put("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isNotFound());
  }
}
