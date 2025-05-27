package com.tonic.utils;

import com.tonic.constants.FrameworkConstants;
import com.tonic.enums.ConfigProperties;
import com.tonic.exceptions.PropertyFileHandleException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Builds and provides access to configuration properties from the framework config file.
 * Author: Gaurav Purwar
 */
public final class PropertyBuilder {

	private PropertyBuilder() {
	}

	private static Properties property = new Properties();
	private static final Map<String, String> CONFIG_MAP = new HashMap<String, String>();

	static {
		readPropertyFile(FrameworkConstants.getPropertyFilePath());
	}

	public static String getPropValue(ConfigProperties key) {
		if (Objects.isNull(CONFIG_MAP.get(key.name().toLowerCase()))){
			throw new PropertyFileHandleException("Property name "+ key + " is not found. Please check config Properties" );
		}
		return CONFIG_MAP.get(key.name().toLowerCase());
	}

	private static void readPropertyFile(String path) {
		try {
			//load Global File Properties
			FileInputStream env_file = new FileInputStream(path);
			property.load(env_file);

			for(Map.Entry<Object, Object> entry: property.entrySet()){
				CONFIG_MAP.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()).trim());
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
