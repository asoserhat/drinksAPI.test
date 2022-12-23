package generic;

import com.google.gson.Gson;

public class ApiUtil {
	

	public static String toJson(final Object object) {
		final Gson gson = new Gson();
		return gson.toJson(object);
	}


	public static Object fromJson(final String json, final Class<?> object) {
		final Gson gson = new Gson();
		return gson.fromJson(json, object);
	}

}
