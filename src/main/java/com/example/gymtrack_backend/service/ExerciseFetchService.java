package com.example.gymtrack_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Consumo de API externa: wger.de
 * Documentación: https://wger.de/api/v2/
 *
 * Obtiene sugerencias de ejercicios por categoría muscular.
 * La clave de búsqueda (categoryId) corresponde a los IDs de wger:
 *   8=Brazos, 9=Piernas, 10=Abdomen, 11=Pecho, 12=Espalda, 13=Hombros, 14=Glúteos
 */
@Service
public class ExerciseFetchService {

    @Value("${wger.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Devuelve una lista de nombres de ejercicios para una categoría muscular dada.
     * Si categoryId es null, devuelve ejercicios en general.
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getExercisesByCategory(Integer categoryId) {
        String url = baseUrl + "/exercise/?format=json&language=2&limit=15";
        if (categoryId != null) {
            url += "&category=" + categoryId;
        }

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || !response.containsKey("results")) {
            return List.of();
        }

        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

        // Extraer solo id + nombre de cada ejercicio
        return results.stream()
                .map(e -> Map.of(
                        "id",   e.getOrDefault("id", ""),
                        "name", e.getOrDefault("name", "Sin nombre")
                ))
                .toList();
    }

    /**
     * Devuelve las categorías musculares disponibles en wger.
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getCategories() {
        String url = baseUrl + "/exercisecategory/?format=json";
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || !response.containsKey("results")) {
            return List.of();
        }

        return (List<Map<String, Object>>) response.get("results");
    }
}
