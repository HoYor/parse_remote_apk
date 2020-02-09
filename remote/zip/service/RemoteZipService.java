package remote.zip.service;

import java.io.IOException;
import java.io.InputStream;

import remote.zip.model.RemoteZipEntry;
import remote.zip.tools.RemoteZipFile;

public interface RemoteZipService {
	
	public RemoteZipFile load(String path, String proxy) throws IOException;
	
	public InputStream getEntryStream(RemoteZipEntry entry, RemoteZipFile remoteZip);
	
	public String streamToText(InputStream inputStream);
	
	public String streamToHexaText(InputStream inputStream);
	
}
