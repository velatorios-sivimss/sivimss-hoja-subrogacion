package com.imss.sivimss.hojasubrogacion.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imss.sivimss.hojasubrogacion.model.request.TareasDTO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RestTemplateUtil {

	private final RestTemplate restTemplate;
	private static final String ERROR_AL_ENVIAR = "Ha ocurrido un error al enviar";
	private static final String FALLO_CONSUMIR_SERVICIO = "Fallo al consumir el servicio, {}";
	public RestTemplateUtil(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/**
	 * Env&iacute;a una petici&oacute;n con Body.
	 *
	 * @param url
	 * @param clazz
	 * @return
	 */
	public Response<Object> sendPostRequestByteArray(String url, EnviarDatosRequest body, Class<?> clazz)
			throws IOException {
		HttpHeaders headers = RestTemplateUtil.createHttpHeaders();

		HttpEntity<Object> request = new HttpEntity<>(body, headers);

		return consumirServicio(url, request, clazz, null);

	}

	/**
	 * Env&iacute;a una petici&oacute;n con Body y token.
	 *
	 * @param url
	 * @param clazz
	 * @return
	 * @throws IOException 
	 */
	public Response<Object> sendPostRequestByteArrayToken(String url, EnviarDatosRequest body, String subject,
			Class<?> clazz) throws IOException {
		HttpHeaders headers = RestTemplateUtil.createHttpHeadersToken(subject);

		HttpEntity<Object> request = new HttpEntity<>(body, headers);

		return consumirServicio(url, request, clazz, null);

	}

	/**
	 * Crea los headers para la petici&oacute;n falta agregar el tema de seguridad
	 * para las peticiones
	 *
	 * @return
	 */
	private static HttpHeaders createHttpHeaders() {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		return header;
	}

	/**
	 * Crea los headers para la petici&oacute;n con token - falta agregar el
	 * tema de seguridad para las peticiones
	 *
	 * @return
	 */
	private static HttpHeaders createHttpHeadersToken(String subject) {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		header.set("Authorization", "Bearer " + subject);

		header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		return header;
	}

	///////////////////////////////////////////////////// peticion con archivos
	/**
	 * Crea los headers para la petici&oacute;n con token - falta agregar el
	 * tema de seguridad para las peticiones
	 *
	 * @return
	 */
	/**
	 * Env&iacute;a una petici&oacute;n con Body, archivos y token.
	 *
	 * @param url
	 * @param clazz
	 * @return
	 */
	public Response<Object> sendPostRequestByteArrayArchviosToken(String url, EnviarDatosArchivosRequest body,
			String subject, Class<?> clazz) throws IOException {
		Response<Object> responseBody = new Response<>();
		HttpHeaders headers = RestTemplateUtil.createHttpHeadersArchivosToken(subject);

		try {

			LinkedMultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

			for (MultipartFile file : body.getArchivos()) {
				if (!file.isEmpty()) {
					parts.add("files",
							new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
				}
			}

			parts.add("datos", body.getDatos());
			HttpEntity<LinkedMultiValueMap<String, Object>> request = new HttpEntity<>(parts, headers);
			responseBody = consumirServicio(url, null, clazz, request);
		} catch (Exception e) {
			log.error(FALLO_CONSUMIR_SERVICIO, e.getMessage());
		}

		return responseBody;
	}

	private static HttpHeaders createHttpHeadersArchivosToken(String subject) {

		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.MULTIPART_FORM_DATA);
		header.setAccept(Arrays.asList(MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON));
		header.set("Authorization", "Bearer " + subject);
		return header;
	}

	//////////////////////////////////////////
	/**
	 * Enviar una peticion con Body para reportes.
	 *
	 * @param url
	 * @param clazz
	 * @return
	 */
	public Response<Object> sendPostRequestByteArrayReportesToken(String url, DatosReporteDTO body, String subject,
			Class<?> clazz) {
		Response<Object> responseBody;
		HttpHeaders headers = RestTemplateUtil.createHttpHeadersToken(subject);

		HttpEntity<Object> request = new HttpEntity<>(body, headers);
		ResponseEntity<?> responseEntity = null;
		responseEntity = restTemplate.postForEntity(url, request, clazz);
		responseBody = (Response<Object>) responseEntity.getBody();

		return responseBody;
	}

	//////// peticion api externa
	/**
	 * Env&iacute;a una petici&oacute;n de tipo POST a la url que se seleccione
	 *
	 * @param url
	 * @param clazz
	 * @return
	 */
	public Response<Object> sendGetRequest(String url) {
		Response<Object> response = new Response<>();
		ResponseEntity<?> responseEntity = null;
		try {
			responseEntity = restTemplate.getForEntity(url, String.class);
			if (responseEntity.getStatusCode() == HttpStatus.OK) {

				response = mapearRespuesta(responseEntity);

			} else {
				response.setCodigo(responseEntity.getStatusCodeValue());
				response.setError(true);
			}
		} catch (HttpClientErrorException e) {
			response.setError(true);
			response.setCodigo(e.getRawStatusCode());
		}
		return response;
	}

	private Response<Object> mapearRespuesta(ResponseEntity<?> responseEntity) {
		Response<Object> response = new Response<>();
		JsonNode json;
		ObjectMapper mapper = new ObjectMapper();
		Object object = responseEntity.getBody();
		if (object != null) {
			try {
				json = mapper.readTree(String.valueOf(object));
				response.setError(false);
				response.setCodigo(responseEntity.getStatusCodeValue());
				response.setMensaje(AppConstantes.EXITO);
				response.setDatos(json);
			} catch (Exception e) {
				response.setError(true);
				response.setCodigo(HttpStatus.INTERNAL_SERVER_ERROR.value());
				response.setMensaje("186");
			}

		}
		return response;
	}

	/**
	 * Env&iacute;a una petici&oacute;n con Body.
	 *
	 * @param url
	 * @param clazz
	 * @return
	 */
	public Response<Object> sendPostRequestByteArrayTokenProcesos(String url, TareasDTO body, String subject,
			Class<?> clazz) throws IOException {
		HttpHeaders headers = RestTemplateUtil.createHttpHeadersToken(subject);
		HttpEntity<Object> request = new HttpEntity<>(body, headers);
		return consumirServicio(url, request, clazz, null);
	}
	
	private Response<Object> consumirServicio(String url, HttpEntity<Object> request, Class<?> clazz, HttpEntity<LinkedMultiValueMap<String, Object>> request1)
			throws IOException {
		ResponseEntity<?> responseEntity = null ;
		Response<Object> responseBody = new Response<>();
		try {
			if(request != null)
				responseEntity = restTemplate.postForEntity(url, request, clazz);
			else if (request1 != null)
				responseEntity = restTemplate.postForEntity(url, request1, clazz);
			
			if (responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
				responseBody = (Response<Object>) responseEntity.getBody();
			} else {
				throw new IOException(ERROR_AL_ENVIAR);
			}
		} catch (IOException ioException) {
			throw ioException;
		} catch (Exception e) {
			log.error(FALLO_CONSUMIR_SERVICIO, e.getMessage());
			responseBody.setCodigo(HttpStatus.INTERNAL_SERVER_ERROR.value());
			responseBody.setError(true);
			responseBody.setMensaje(e.getMessage());
		}
		return responseBody;
	}
}
