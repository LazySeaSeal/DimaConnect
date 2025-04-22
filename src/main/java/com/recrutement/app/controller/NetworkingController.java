package com.recrutement.app.controller;

import com.recrutement.app.dto.CandidatProfileDTO;
import com.recrutement.app.dto.ContactRequestDTO;
import com.recrutement.app.dto.ContactSearchDTO;
import com.recrutement.app.service.NetworkingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/networking")
@Tag(name = "Networking", description = "Networking and connections APIs")
public class NetworkingController {

    private final NetworkingService networkingService;

    @Autowired
    public NetworkingController(NetworkingService networkingService) {
        this.networkingService = networkingService;
    }

    @PostMapping("/contact-requests")
    @Operation(summary = "Send a contact request")
    public ResponseEntity<Void> sendContactRequest(@Valid @RequestBody ContactRequestDTO requestDTO) {
        networkingService.sendContactRequest(requestDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/contact-requests/{contactId}/accept")
    @Operation(summary = "Accept a contact request")
    public ResponseEntity<Void> acceptContactRequest(@PathVariable Long contactId) {
        networkingService.acceptContactRequest(contactId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/contact-requests/{contactId}")
    @Operation(summary = "Reject a contact request")
    public ResponseEntity<Void> rejectContactRequest(@PathVariable Long contactId) {
        networkingService.rejectContactRequest(contactId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{candidateId}/connections")
    @Operation(summary = "Get candidate connections")
    public ResponseEntity<List<CandidatProfileDTO>> getConnections(@PathVariable Long candidateId) {
        return ResponseEntity.ok(networkingService.getConnections(candidateId));
    }

    @GetMapping("/search")
    @Operation(summary = "Search candidates by name or skill")
    public ResponseEntity<List<CandidatProfileDTO>> searchContacts(@Valid ContactSearchDTO searchDTO) {
        return ResponseEntity.ok(networkingService.searchContacts(searchDTO));
    }
}
