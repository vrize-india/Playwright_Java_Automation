package com.tonic.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils{
	private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

	public String getFileType(File file) {
		String fileExtension = null;
		String fileName = file.toString();
		int fileIndex = fileName.lastIndexOf('.');
		if ((fileIndex > 0))
		{
			fileExtension = fileName.substring(fileIndex + 1);
		}
		return fileExtension;
	}

	public String getFileSize(File file) {
		float sizeInKb = (file.length()/1024);
		float sizeInMb = sizeInKb/1024;
		return Float.toString(sizeInMb);
	}

	public void appendToFile(String content,String filePath)
	{
		try {
			FileWriter fwrite = new FileWriter(filePath, true);
			BufferedWriter bufferWriter = new BufferedWriter(fwrite);
			bufferWriter.append(content);
			bufferWriter.close();
		}
		catch(Exception e)
		{
			logger.error("Failed to append content to file: {}", filePath, e);
		}
	}
}



