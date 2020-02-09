package remote.zip.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.logging.Level;
import java.util.logging.Logger;

import remote.zip.Config;
import remote.zip.model.RemoteZipEntry;
import remote.zip.service.RemoteZipService;
import remote.zip.tools.RemoteZipFile;
import remote.zip.tools.StreamUtils;

public class RemoteZipServiceImpl implements RemoteZipService {

	private static Logger logger = Logger.getLogger(RemoteZipServiceImpl.class.getName());

	public RemoteZipFile load(String path, String proxy) throws IOException {
		RemoteZipFile rf = null;
		if (proxy != null) {
			String[] splitted = proxy.split(":");
			if (splitted.length != 2) {
				throw new InvalidParameterException("Proxy address not valid");
			} else {
				Config.PROXY_URL = splitted[0];
				Config.PROXY_PORT = splitted[1];
			}
		}
		rf = new RemoteZipFile();
		if (rf.load(path)) {
			return rf;
		}
		return null;
	}

	@Override
	public InputStream getEntryStream(RemoteZipEntry entry, RemoteZipFile remoteZip) {
		try {
			return remoteZip.getInputStream(entry);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error while retrieving entry", e);
		}
		return null;
	}

	@Override
	public String streamToText(InputStream inputStream) {
		return StreamUtils.printString(inputStream);
	}

	@Override
	public String streamToHexaText(InputStream inputStream) {
		try {
			return StreamUtils.printHexa(inputStream);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error while converting stream to hexadecimal", e);
		}
		return null;
	}

}
