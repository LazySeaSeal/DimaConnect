package com.recrutement.app.controller;

import com.recrutement.app.dto.PublicationRequest;
import com.recrutement.app.model.PublicationCandidat;
import com.recrutement.app.service.PublicationCandidatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.recrutement.app.model.enums.TypeMedia;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/publications/candidats")
public class PublicationCandidatController {

    @Autowired
    private PublicationCandidatService publicationService;

    // Créer une nouvelle publication avec un fichier
@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> createPublication(
        @RequestParam("contenu") String contenu,
        @RequestParam("typeMedia") String typeMedia,
        @RequestParam(value = "file", required = false) MultipartFile file,  // rendre file optionnel
        @RequestParam("candidatId") Long candidatId
) {
    try {
        String mediaUrl = null;

        // Si le type est "LIEN", on ne s'attend pas à avoir un fichier
        if (file != null && !file.isEmpty()) {
            mediaUrl = file.getOriginalFilename(); // ou gérer l'upload réel
        } else if ("LIEN".equalsIgnoreCase(typeMedia)) {
            mediaUrl = contenu; // Le lien sera le contenu de la publication
        }

        TypeMedia mediaType;
        try {
            mediaType = TypeMedia.valueOf(typeMedia.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "TypeMedia invalide : " + typeMedia
            ));
        }

        // Créer un objet PublicationRequest avec les bonnes valeurs
        PublicationRequest request = new PublicationRequest();
        request.setContenu(contenu);
        request.setTypeMedia(mediaType);
        request.setMediaUrl(mediaUrl);
        request.setOwnerId(candidatId); // Assigner l'ID du candidat

        PublicationCandidat publication = publicationService.createPublication(request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("publication", publication);
        response.put("message", "Publication créée avec succès");

        return ResponseEntity.ok(response);
    } catch (Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.internalServerError().body(errorResponse);
    }
}



    // Récupérer toutes les publications d'un candidat
    @GetMapping("/candidat/{candidatId}")
    public ResponseEntity<?> getPublicationsByCandidat(@PathVariable Long candidatId) {
        try {
            List<PublicationCandidat> publications = publicationService.getPublicationsByCandidatId(candidatId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("publications", publications);
            response.put("count", publications.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse(e));
        }
    }

    // Récupérer une publication par son ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPublicationById(@PathVariable Long id) {
        try {
            PublicationCandidat publication = publicationService.getPublicationById(id);

            if (publication == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("publication", publication);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse(e));
        }
    }

    // Mettre à jour une publication
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePublication(@PathVariable Long id, @RequestBody PublicationRequest request) {
        try {
            PublicationCandidat updatedPublication = publicationService.updatePublication(id, request);

            if (updatedPublication == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("publication", updatedPublication);
            response.put("message", "Publication mise à jour avec succès");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse(e));
        }
    }

    // Supprimer une publication
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePublication(@PathVariable Long id) {
        try {
            boolean deleted = publicationService.deletePublication(id);

            Map<String, Object> response = new HashMap<>();
            if (deleted) {
                response.put("success", true);
                response.put("message", "Publication supprimée avec succès");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Publication non trouvée");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse(e));
        }
    }

    // Ajouter un like à une publication
    @PostMapping("/{id}/like")
    public ResponseEntity<?> addLike(@PathVariable Long id) {
        try {
            PublicationCandidat publication = publicationService.addLike(id);

            if (publication == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("publication", publication);
            response.put("message", "Like ajouté avec succès");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse(e));
        }
    }

    // Retirer un like d'une publication
    @PostMapping("/{id}/unlike")
    public ResponseEntity<?> removeLike(@PathVariable Long id) {
        try {
            PublicationCandidat publication = publicationService.removeLike(id);

            if (publication == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("publication", publication);
            response.put("message", "Like retiré avec succès");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse(e));
        }
    }

    // Méthode utilitaire pour créer une réponse d'erreur
    private Map<String, Object> createErrorResponse(Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", e.getClass().getSimpleName());
        errorResponse.put("message", e.getMessage());
        return errorResponse;
    }
}
