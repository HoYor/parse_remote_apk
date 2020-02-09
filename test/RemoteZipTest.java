package test;

import java.io.IOException;
import java.io.InputStream;

import apk.RemoteApkUtil;
import remote.zip.Config;
import remote.zip.model.RemoteZipEntry;
import remote.zip.tools.RemoteZipFile;
import remote.zip.tools.StreamUtils;
import test.AXMLPrinter;

public class RemoteZipTest {

	public static void main(String[] args) throws IOException {
//		Config.PROXY_URL = "proxy.up";
//		Config.PROXY_PORT = "8080";
//		RemoteZipFile rz = new RemoteZipFile();
//		 rz.load("http://acj3.pc6.com/pc6_soure/2019-6/com.gotokeep.keep_18898.apk");
//		int c = 0;
//		for (RemoteZipEntry e : rz.getEntries()) {
//			System.out.println("[" + c++ + "]" + e.getName() + " cmethod: "
//					+ e.getMethod() + " crc: " + e.getCrc() + " zsize: "
//					+ e.getSize() + " comp size:  " + e.getCompressedSize()
//					+ " time: " + e.getTime());
//		}
//		InputStream stream = rz.getInputStream(rz.getEntries()[0]);
//		System.out.println(StreamUtils.printString(stream));
//		AXMLPrinter.printXml(stream);

		RemoteApkUtil.getApkInfo("http://acj3.pc6.com/pc6_soure/2019-6/com.gotokeep.keep_18898.apk");
	}

}
