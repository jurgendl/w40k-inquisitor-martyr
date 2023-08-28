package test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public interface JsonHelper {
	public default JsonArray jsonArray(String json) {
		return _jsonArray(json);
	}

	static JsonArray _jsonArray(String json) {
		return _internal(json, false).asJsonArray();
	}

	public default JsonArray jsonArrayLenient(String json) {
		return _jsonArrayLenient(json);
	}

	static JsonArray _jsonArrayLenient(String json) {
		return _internal(json, true).asJsonArray();
	}

	public default JsonObject jsonObject(String json) {
		return _jsonObject(json);
	}

	static JsonObject _jsonObject(String json) {
		return _internal(json, false).asJsonObject();
	}

	public default JsonObject jsonObjectLenient(String json) {
		return _jsonObjectLenient(json);
	}

	static JsonObject _jsonObjectLenient(String json) {
		return _internal(json, true).asJsonObject();
	}

	public default JsonStructure jsonStruct(String json) {
		return _jsonStruct(json);
	}

	static JsonStructure _jsonStruct(String json) {
		return _internal(json, false);
	}

	public default JsonStructure jsonStructLenient(String json) {
		return _jsonStructLenient(json);
	}

	static JsonStructure _jsonStructLenient(String json) {
		return _internal(json, true);
	}

	static JsonStructure _internal(String json, boolean lenient) {
		if (lenient) {
			JsonReader reader = new JsonReader(new StringReader(json));
			reader.setLenient(true);
			JsonElement el = JsonParser.parseReader(reader);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			json = gson.toJson(el);
		}
		return Json.createReader(new InputStreamReader(new ByteArrayInputStream(json.getBytes()))).read();
	}

	public default JsonStructure internal(String json, boolean lenient) {
		return _internal(json, lenient);
	}

	public default Map<String, List<String>> map(JsonObject object) {
		return _map(object);
	}

	static Map<String, List<String>> _map(JsonObject object) {
		Map<String, List<String>> map = new LinkedHashMap<>();
		return _map(object, map);
	}

	// Map<String, List<String>> map = new LinkedHashMap<>();
	// map(tmp2.get(0).asJsonObject(), map);
	// map.entrySet().forEach(System.out::println);
	public default Map<String, List<String>> map(JsonObject object, Map<String, List<String>> map) {
		return internalMap(null, object, map);
	}

	public static Map<String, List<String>> _map(JsonObject object, Map<String, List<String>> map) {
		return internalMap(null, object, map);
	}

	public static Map<String, List<String>> internalMap(String prefix, JsonObject object, Map<String, List<String>> map) {
		object.keySet().forEach(_k -> {
			JsonValue v = object.get(_k);
			String k = prefix == null ? _k : prefix + "." + _k;
			if (v instanceof JsonObject) {
				internalMap(k, v.asJsonObject(), map);
			} else if (v instanceof JsonArray) {
				JsonArray ar = v.asJsonArray();
				List<String> list = map.get(k);
				if (list == null) {
					list = new ArrayList<>();
					map.put(k, list);
				}
				for (JsonValue element : ar) {
					list.add(String.valueOf(element));
				}
			} else if (v instanceof JsonString) {
				JsonString s = JsonString.class.cast(v);
				List<String> list = map.get(k);
				if (list == null) {
					list = new ArrayList<>();
					map.put(k, list);
				}
				list.add(s.getString());
			} else if (v instanceof JsonNumber) {
				JsonNumber n = JsonNumber.class.cast(v);
				List<String> list = map.get(k);
				if (list == null) {
					list = new ArrayList<>();
					map.put(k, list);
				}
				list.add(String.valueOf(n.numberValue()));
			} else {
				List<String> list = map.get(k);
				if (list == null) {
					list = new ArrayList<>();
					map.put(k, list);
				}
				list.add(String.valueOf(v));
			}
		});
		return map;
	}

	static String json(Map<String, ?> map) {
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(map);
		return json;
	}

	default String _json(Map<String, ?> map) {
		return json(map);
	}

	static public <T> void _write(ObjectMapper om, OutputStream out, T object) {
		try {
			om.writerFor(object.getClass()).writeValue(out, object);
			out.close();
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	static public <T> void _writeList(ObjectMapper om, OutputStream out, List<T> list, TypeReference<List<T>> type) {
		try {
			om.writerFor(type).writeValue(out, list);
			out.close();
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	static public <T> String _write(ObjectMapper om, T object) {
		try {
			return om.writerFor(object.getClass()).writeValueAsString(object);
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	static public <T> String _writeList(ObjectMapper om, List<T> list, TypeReference<List<T>> type) {
		try {
			return om.writerFor(type).writeValueAsString(list);
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}
}
