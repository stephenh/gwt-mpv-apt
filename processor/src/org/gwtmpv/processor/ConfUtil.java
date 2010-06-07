package org.gwtmpv.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.tools.JavaFileManager.Location;

public class ConfUtil {

	/** Attempts to load {@code fileName} and return its properties. */
	public static Map<String, String> loadProperties(ProcessingEnvironment env, String fileName) {
		Map<String, String> properties = new LinkedHashMap<String, String>();

		// Eclipse, ant, and maven all act a little differently here, so try both source and class output
		File file = null;
		for (Location location : new Location[] { StandardLocation.SOURCE_OUTPUT, StandardLocation.CLASS_OUTPUT }) {
			file = resolveBindgenPropertiesIfExists(location, env, fileName);
			if (file != null) {
				break;
			}
		}

		if (file != null) {
			Properties p = new Properties();
			try {
				p.load(new FileInputStream(file));
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (Map.Entry<Object, Object> entry : p.entrySet()) {
				properties.put((String) entry.getKey(), (String) entry.getValue());
			}
		}

		return properties;
	}

	/** Finds a file by starting by <code>location</code> and walkig up.
	 *
	 * This uses a heuristic because in Eclipse we will not know what our
	 * working directory is (it is wherever Eclipse was started from), so
	 * project/workspace-relative paths will not work.
	 *
	 * As far as passing in a the properties location as a {@code -Afile=path}
	 * setting, Eclipse also lacks any {@code ${basepath}}-type interpolation
	 * in its APT key/value pairs (like Ant would be able to do). So only fixed
	 * values are accepted, meaning an absolute path, which would be too tied
	 * to any one developer's particular machine.
	 *
	 * The one thing the APT API gives us is the CLASS_OUTPUT (e.g. bin/apt).
	 * So we start there and walk up parent directories looking for
	 * {@code bindgen.properties} files.
	 */
	private static File resolveBindgenPropertiesIfExists(Location location, ProcessingEnvironment env, String fileName) {
		// Find a dummy /bin/apt/dummy.txt path to start at
		final String dummyPath;
		try {
			// We don't actually create this, we just want its URI
			FileObject dummyFileObject = env.getFiler().getResource(location, "", "dummy.txt");
			dummyPath = dummyFileObject.toUri().toString().replaceAll("file:", "");
		} catch (IOException e1) {
			return null;
		}

		// Walk up looking for a bindgen.properties
		File current = new File(dummyPath).getParentFile();
		while (current != null) {
			File possible = new File(current, fileName);
			if (possible.exists()) {
				return possible;
			}
			current = current.getParentFile();
		}

		// Before giving up, try just grabbing it from the current directory
		File possible = new File(fileName);
		if (possible.exists()) {
			return possible;
		}

		// No file found
		return null;
	}

}
