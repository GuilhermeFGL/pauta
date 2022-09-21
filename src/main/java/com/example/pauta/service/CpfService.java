package com.example.pauta.service;

import com.example.pauta.service.dto.CpfResponse;
import com.example.pauta.service.dto.enums.CpfStatus;
import com.example.pauta.service.exception.CpfIntegrationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class CpfService {

    @Value("${cfp.url}")
    private String cpfUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public CpfService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean cpfCanVote(String cpf) {
        CpfService.log.info("Check CPF can vote for CPF {}", cpf);

        String uri = UriComponentsBuilder.fromHttpUrl(this.cpfUrl)
                .path(cpf)
                .toUriString();

        CpfResponse result;
        try {
            result = this.restTemplate.getForObject(uri, CpfResponse.class);

        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            } else {
                CpfService.log.error("CPF service returned error: {}", e.getStatusCode());
                throw new CpfIntegrationException("Unable to validate CPF");
            }
        }

        return result != null && result.getStatus() != null && result.getStatus() == CpfStatus.ABLE_TO_VOTE;
    }

}
