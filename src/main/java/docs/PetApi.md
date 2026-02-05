# PetApi

All URIs are relative to *https://petstore.swagger.io/v2*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**addPet**](PetApi.md#addPet) | **POST** /pet | Add a new pet to the store |
| [**deletePet**](PetApi.md#deletePet) | **DELETE** /pet/{petId} | Deletes a pet |
| [**findPetsByStatus**](PetApi.md#findPetsByStatus) | **GET** /pet/findByStatus | Finds Pets by status |
| [**getPetById**](PetApi.md#getPetById) | **GET** /pet/{petId} | Find pet by ID |
| [**updatePet**](PetApi.md#updatePet) | **PUT** /pet | Update an existing pet |



## addPet

> addPet(pet)

Add a new pet to the store

### Example

```java
// Import classes:
import com.example.demo.client.petstore.ApiClient;
import com.example.demo.client.petstore.ApiException;
import com.example.demo.client.petstore.Configuration;
import com.example.demo.client.petstore.models.*;
import com.example.demo.client.petstore.api.PetApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://petstore.swagger.io/v2");

        PetApi apiInstance = new PetApi(defaultClient);
        Pet pet = new Pet(); // Pet | Pet object that needs to be added to the store
        try {
            apiInstance.addPet(pet);
        } catch (ApiException e) {
            System.err.println("Exception when calling PetApi#addPet");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **pet** | [**Pet**](Pet.md)| Pet object that needs to be added to the store | |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json, application/xml
- **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **405** | Invalid input |  -  |


## deletePet

> deletePet(petId, apiKey)

Deletes a pet

### Example

```java
// Import classes:
import com.example.demo.client.petstore.ApiClient;
import com.example.demo.client.petstore.ApiException;
import com.example.demo.client.petstore.Configuration;
import com.example.demo.client.petstore.models.*;
import com.example.demo.client.petstore.api.PetApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://petstore.swagger.io/v2");

        PetApi apiInstance = new PetApi(defaultClient);
        Long petId = 56L; // Long | Pet id to delete
        String apiKey = "apiKey_example"; // String | API Key
        try {
            apiInstance.deletePet(petId, apiKey);
        } catch (ApiException e) {
            System.err.println("Exception when calling PetApi#deletePet");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **petId** | **Long**| Pet id to delete | |
| **apiKey** | **String**| API Key | [optional] |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **400** | Invalid ID supplied |  -  |
| **404** | Pet not found |  -  |


## findPetsByStatus

> List&lt;Pet&gt; findPetsByStatus(status)

Finds Pets by status

### Example

```java
// Import classes:
import com.example.demo.client.petstore.ApiClient;
import com.example.demo.client.petstore.ApiException;
import com.example.demo.client.petstore.Configuration;
import com.example.demo.client.petstore.models.*;
import com.example.demo.client.petstore.api.PetApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://petstore.swagger.io/v2");

        PetApi apiInstance = new PetApi(defaultClient);
        List<String> status = Arrays.asList("available"); // List<String> | Status values that need to be considered for filter
        try {
            List<Pet> result = apiInstance.findPetsByStatus(status);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling PetApi#findPetsByStatus");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **status** | [**List&lt;String&gt;**](String.md)| Status values that need to be considered for filter | [enum: available, pending, sold] |

### Return type

[**List&lt;Pet&gt;**](Pet.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/xml


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | successful operation |  -  |
| **400** | Invalid status value |  -  |


## getPetById

> Pet getPetById(petId)

Find pet by ID

### Example

```java
// Import classes:
import com.example.demo.client.petstore.ApiClient;
import com.example.demo.client.petstore.ApiException;
import com.example.demo.client.petstore.Configuration;
import com.example.demo.client.petstore.models.*;
import com.example.demo.client.petstore.api.PetApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://petstore.swagger.io/v2");

        PetApi apiInstance = new PetApi(defaultClient);
        Long petId = 56L; // Long | ID of pet to return
        try {
            Pet result = apiInstance.getPetById(petId);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling PetApi#getPetById");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **petId** | **Long**| ID of pet to return | |

### Return type

[**Pet**](Pet.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/xml


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | successful operation |  -  |
| **400** | Invalid ID supplied |  -  |
| **404** | Pet not found |  -  |


## updatePet

> updatePet(pet)

Update an existing pet

### Example

```java
// Import classes:
import com.example.demo.client.petstore.ApiClient;
import com.example.demo.client.petstore.ApiException;
import com.example.demo.client.petstore.Configuration;
import com.example.demo.client.petstore.models.*;
import com.example.demo.client.petstore.api.PetApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://petstore.swagger.io/v2");

        PetApi apiInstance = new PetApi(defaultClient);
        Pet pet = new Pet(); // Pet | Pet object that needs to be added to the store
        try {
            apiInstance.updatePet(pet);
        } catch (ApiException e) {
            System.err.println("Exception when calling PetApi#updatePet");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **pet** | [**Pet**](Pet.md)| Pet object that needs to be added to the store | |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json, application/xml
- **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **400** | Invalid ID supplied |  -  |
| **404** | Pet not found |  -  |
| **405** | Validation exception |  -  |

