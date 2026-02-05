package com.example.demo.client.petstore.api;

import com.example.demo.client.petstore.ApiClient;

import com.example.demo.client.petstore.model.Pet;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2026-02-05T20:16:37.023630+08:00[Asia/Shanghai]")
public class PetApi {
    private ApiClient apiClient;

    public PetApi() {
        this(new ApiClient());
    }

    @Autowired
    public PetApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Add a new pet to the store
     * 
     * <p><b>405</b> - Invalid input
     * @param pet Pet object that needs to be added to the store
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec addPetRequestCreation(Pet pet) throws WebClientResponseException {
        Object postBody = pet;
        // verify the required parameter 'pet' is set
        if (pet == null) {
            throw new WebClientResponseException("Missing the required parameter 'pet' when calling addPet", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json", "application/xml"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return apiClient.invokeAPI("/pet", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Add a new pet to the store
     * 
     * <p><b>405</b> - Invalid input
     * @param pet Pet object that needs to be added to the store
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Void> addPet(Pet pet) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return addPetRequestCreation(pet).bodyToMono(localVarReturnType);
    }

    /**
     * Add a new pet to the store
     * 
     * <p><b>405</b> - Invalid input
     * @param pet Pet object that needs to be added to the store
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<Void>> addPetWithHttpInfo(Pet pet) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return addPetRequestCreation(pet).toEntity(localVarReturnType);
    }

    /**
     * Add a new pet to the store
     * 
     * <p><b>405</b> - Invalid input
     * @param pet Pet object that needs to be added to the store
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec addPetWithResponseSpec(Pet pet) throws WebClientResponseException {
        return addPetRequestCreation(pet);
    }
    /**
     * Deletes a pet
     * 
     * <p><b>400</b> - Invalid ID supplied
     * <p><b>404</b> - Pet not found
     * @param petId Pet id to delete
     * @param apiKey API Key
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec deletePetRequestCreation(Long petId, String apiKey) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'petId' is set
        if (petId == null) {
            throw new WebClientResponseException("Missing the required parameter 'petId' when calling deletePet", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("petId", petId);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        if (apiKey != null)
        headerParams.add("api_key", apiClient.parameterToString(apiKey));
        final String[] localVarAccepts = { };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return apiClient.invokeAPI("/pet/{petId}", HttpMethod.DELETE, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Deletes a pet
     * 
     * <p><b>400</b> - Invalid ID supplied
     * <p><b>404</b> - Pet not found
     * @param petId Pet id to delete
     * @param apiKey API Key
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Void> deletePet(Long petId, String apiKey) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return deletePetRequestCreation(petId, apiKey).bodyToMono(localVarReturnType);
    }

    /**
     * Deletes a pet
     * 
     * <p><b>400</b> - Invalid ID supplied
     * <p><b>404</b> - Pet not found
     * @param petId Pet id to delete
     * @param apiKey API Key
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<Void>> deletePetWithHttpInfo(Long petId, String apiKey) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return deletePetRequestCreation(petId, apiKey).toEntity(localVarReturnType);
    }

    /**
     * Deletes a pet
     * 
     * <p><b>400</b> - Invalid ID supplied
     * <p><b>404</b> - Pet not found
     * @param petId Pet id to delete
     * @param apiKey API Key
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec deletePetWithResponseSpec(Long petId, String apiKey) throws WebClientResponseException {
        return deletePetRequestCreation(petId, apiKey);
    }
    /**
     * Finds Pets by status
     * 
     * <p><b>200</b> - successful operation
     * <p><b>400</b> - Invalid status value
     * @param status Status values that need to be considered for filter
     * @return List&lt;Pet&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec findPetsByStatusRequestCreation(List<String> status) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'status' is set
        if (status == null) {
            throw new WebClientResponseException("Missing the required parameter 'status' when calling findPetsByStatus", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("multi".toUpperCase(Locale.ROOT)), "status", status));

        final String[] localVarAccepts = { 
            "application/json", "application/xml"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Pet> localVarReturnType = new ParameterizedTypeReference<Pet>() {};
        return apiClient.invokeAPI("/pet/findByStatus", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Finds Pets by status
     * 
     * <p><b>200</b> - successful operation
     * <p><b>400</b> - Invalid status value
     * @param status Status values that need to be considered for filter
     * @return List&lt;Pet&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Flux<Pet> findPetsByStatus(List<String> status) throws WebClientResponseException {
        ParameterizedTypeReference<Pet> localVarReturnType = new ParameterizedTypeReference<Pet>() {};
        return findPetsByStatusRequestCreation(status).bodyToFlux(localVarReturnType);
    }

    /**
     * Finds Pets by status
     * 
     * <p><b>200</b> - successful operation
     * <p><b>400</b> - Invalid status value
     * @param status Status values that need to be considered for filter
     * @return ResponseEntity&lt;List&lt;Pet&gt;&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<List<Pet>>> findPetsByStatusWithHttpInfo(List<String> status) throws WebClientResponseException {
        ParameterizedTypeReference<Pet> localVarReturnType = new ParameterizedTypeReference<Pet>() {};
        return findPetsByStatusRequestCreation(status).toEntityList(localVarReturnType);
    }

    /**
     * Finds Pets by status
     * 
     * <p><b>200</b> - successful operation
     * <p><b>400</b> - Invalid status value
     * @param status Status values that need to be considered for filter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec findPetsByStatusWithResponseSpec(List<String> status) throws WebClientResponseException {
        return findPetsByStatusRequestCreation(status);
    }
    /**
     * Find pet by ID
     * 
     * <p><b>200</b> - successful operation
     * <p><b>400</b> - Invalid ID supplied
     * <p><b>404</b> - Pet not found
     * @param petId ID of pet to return
     * @return Pet
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getPetByIdRequestCreation(Long petId) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'petId' is set
        if (petId == null) {
            throw new WebClientResponseException("Missing the required parameter 'petId' when calling getPetById", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("petId", petId);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json", "application/xml"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Pet> localVarReturnType = new ParameterizedTypeReference<Pet>() {};
        return apiClient.invokeAPI("/pet/{petId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Find pet by ID
     * 
     * <p><b>200</b> - successful operation
     * <p><b>400</b> - Invalid ID supplied
     * <p><b>404</b> - Pet not found
     * @param petId ID of pet to return
     * @return Pet
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Pet> getPetById(Long petId) throws WebClientResponseException {
        ParameterizedTypeReference<Pet> localVarReturnType = new ParameterizedTypeReference<Pet>() {};
        return getPetByIdRequestCreation(petId).bodyToMono(localVarReturnType);
    }

    /**
     * Find pet by ID
     * 
     * <p><b>200</b> - successful operation
     * <p><b>400</b> - Invalid ID supplied
     * <p><b>404</b> - Pet not found
     * @param petId ID of pet to return
     * @return ResponseEntity&lt;Pet&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<Pet>> getPetByIdWithHttpInfo(Long petId) throws WebClientResponseException {
        ParameterizedTypeReference<Pet> localVarReturnType = new ParameterizedTypeReference<Pet>() {};
        return getPetByIdRequestCreation(petId).toEntity(localVarReturnType);
    }

    /**
     * Find pet by ID
     * 
     * <p><b>200</b> - successful operation
     * <p><b>400</b> - Invalid ID supplied
     * <p><b>404</b> - Pet not found
     * @param petId ID of pet to return
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getPetByIdWithResponseSpec(Long petId) throws WebClientResponseException {
        return getPetByIdRequestCreation(petId);
    }
    /**
     * Update an existing pet
     * 
     * <p><b>400</b> - Invalid ID supplied
     * <p><b>404</b> - Pet not found
     * <p><b>405</b> - Validation exception
     * @param pet Pet object that needs to be added to the store
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec updatePetRequestCreation(Pet pet) throws WebClientResponseException {
        Object postBody = pet;
        // verify the required parameter 'pet' is set
        if (pet == null) {
            throw new WebClientResponseException("Missing the required parameter 'pet' when calling updatePet", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json", "application/xml"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return apiClient.invokeAPI("/pet", HttpMethod.PUT, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Update an existing pet
     * 
     * <p><b>400</b> - Invalid ID supplied
     * <p><b>404</b> - Pet not found
     * <p><b>405</b> - Validation exception
     * @param pet Pet object that needs to be added to the store
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Void> updatePet(Pet pet) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return updatePetRequestCreation(pet).bodyToMono(localVarReturnType);
    }

    /**
     * Update an existing pet
     * 
     * <p><b>400</b> - Invalid ID supplied
     * <p><b>404</b> - Pet not found
     * <p><b>405</b> - Validation exception
     * @param pet Pet object that needs to be added to the store
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<Void>> updatePetWithHttpInfo(Pet pet) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return updatePetRequestCreation(pet).toEntity(localVarReturnType);
    }

    /**
     * Update an existing pet
     * 
     * <p><b>400</b> - Invalid ID supplied
     * <p><b>404</b> - Pet not found
     * <p><b>405</b> - Validation exception
     * @param pet Pet object that needs to be added to the store
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec updatePetWithResponseSpec(Pet pet) throws WebClientResponseException {
        return updatePetRequestCreation(pet);
    }
}
