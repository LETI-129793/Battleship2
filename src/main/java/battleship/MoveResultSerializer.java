package battleship;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.Map;

/**
 * Serializa em JSON os resultados de uma jogada ({@link Move}).
 */
public class MoveResultSerializer {

	private final ObjectMapper objectMapper;

	public MoveResultSerializer() {
		this.objectMapper = new ObjectMapper();
		this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	/**
	 * Converte o mapa de resultados de uma jogada numa string JSON indentada.
	 *
	 * @param response o mapa com os resultados da jogada
	 * @return a string JSON correspondente
	 * @throws RuntimeException se não for possível serializar o mapa
	 */
	public String toJson(Map<String, Object> response) {
		try {
			return objectMapper.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Erro ao serializar o JSON dos resultados da jogada", e);
		}
	}
}
