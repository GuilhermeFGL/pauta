package com.example.pauta.service;

import com.example.pauta.service.dto.CpfResponse;
import com.example.pauta.service.dto.enums.CpfStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CpfServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private CpfService service;

    @BeforeEach
    public void setUp() {
        this.service = new CpfService(restTemplate);
        ReflectionTestUtils.setField(service, "cpfUrl", "http://cpf.url");
    }

    @Test
    public void testCpfCanVoteShouldBeAbleToVote() {
        CpfResponse cpfResponse = new CpfResponse();
        cpfResponse.setStatus(CpfStatus.ABLE_TO_VOTE);

        when(restTemplate.getForObject(anyString(), eq(CpfResponse.class)))
                .thenReturn(cpfResponse);

        boolean result = this.service.cpfCanVote("000");

        assertTrue(result);
    }

    @Test
    public void testCpfCanVoteShouldNotBeAbleToVote() {
        CpfResponse cpfResponse = new CpfResponse();
        cpfResponse.setStatus(CpfStatus.UNABLE_TO_VOTE);

        when(restTemplate.getForObject(anyString(), eq(CpfResponse.class)))
                .thenReturn(cpfResponse);

        boolean result = this.service.cpfCanVote("000");

        assertFalse(result);
    }

    @Test
    public void testCpfCanInvalidCpf() {
        when(restTemplate.getForObject(anyString(), eq(CpfResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        boolean result = this.service.cpfCanVote("000");

        assertFalse(result);
    }

}