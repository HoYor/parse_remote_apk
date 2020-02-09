package apk;

import android.content.res.AXmlResourceParser;
import android.util.TypedValue;
import com.arjerine.flipsterfetcher.ARSCFileParser;
import org.xmlpull.v1.XmlPullParser;
import remote.zip.model.RemoteZipEntry;
import remote.zip.tools.RemoteZipFile;

import java.io.IOException;
import java.io.InputStream;

public class RemoteApkUtil {
    private final static String TAG_MANIFEST = "manifest";
    private final static String TAG_APPLICATION = "application";
    private final static String ATTRIBUTE_ICON = "icon";
    private final static String ATTRIBUTE_LABEL = "label";
    private final static String ATTRIBUTE_PACKAGE = "package";
    private final static String ATTRIBUTE_VERSIONCODE = "versionCode";
    private final static String ATTRIBUTE_VERSIONNAME = "versionName";

    public static Apk getApkInfo(String url) throws IOException {
        RemoteZipFile remoteZipFile = new RemoteZipFile();
        remoteZipFile.load(url);

        InputStream arscStream = getInputStream(remoteZipFile,"resources.arsc");
        new ARSCFileParser().parse(arscStream);

        InputStream xmlStream = getInputStream(remoteZipFile, "AndroidManifest.xml");
        return parseApkInfoFromXmlInputStream(xmlStream);
    }

    private static InputStream getInputStream(RemoteZipFile remoteZipFile, String fileName) throws IOException {
        if(fileName == null){
            return null;
        }
        for (RemoteZipEntry remoteZipEntry:remoteZipFile.getEntries()) {
            if(fileName.equals(remoteZipEntry.getName())){
                return remoteZipFile.getInputStream(remoteZipEntry);
            }
        }
        return null;
    }

    private static Apk parseApkInfoFromXmlInputStream(InputStream inputStream){
        Apk apk = new Apk();
        try {
            AXmlResourceParser parser=new AXmlResourceParser();
            parser.open(inputStream);
            StringBuilder indent=new StringBuilder(10);
            final String indentStep="	";
            while (true) {
                int type=parser.next();
                if (type== XmlPullParser.END_DOCUMENT) {
                    break;
                }
                switch (type) {
                    case XmlPullParser.START_DOCUMENT:
                    {
                        log("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                        break;
                    }
                    case XmlPullParser.START_TAG:
                    {
                        if(parser.getName().equals(TAG_MANIFEST) || parser.getName().equals(TAG_APPLICATION)){
                            log("%s<%s%s", indent,
                                    getNamespacePrefix(parser.getPrefix()), parser.getName());
                            indent.append(indentStep);

                            int namespaceCountBefore = parser.getNamespaceCount(parser.getDepth() - 1);
                            int namespaceCount = parser.getNamespaceCount(parser.getDepth());
                            for (int i = namespaceCountBefore; i != namespaceCount; ++i) {
                                log("%sxmlns:%s=\"%s\"",
                                        indent,
                                        parser.getNamespacePrefix(i),
                                        parser.getNamespaceUri(i));
                            }

                            for (int i = 0; i != parser.getAttributeCount(); ++i) {
                                log("%s%s%s=\"%s\"", indent,
                                        getNamespacePrefix(parser.getAttributePrefix(i)),
                                        parser.getAttributeName(i),
                                        getAttributeValue(parser, i));
                                fillApkInfo(apk, parser.getAttributeName(i), getAttributeValue(parser, i));
                            }
                            log("%s>", indent);
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG:
                    {
//                        indent.setLength(indent.length()-indentStep.length());
                        log("%s</%s%s>",indent,
                                getNamespacePrefix(parser.getPrefix()),
                                parser.getName());
                        break;
                    }
                    case XmlPullParser.TEXT:
                    {
                        log("%s%s",indent,parser.getText());
                        break;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return apk;
    }

    private static void fillApkInfo(Apk apk, String attributeName, String attributeValue) {
        switch (attributeName){
            case ATTRIBUTE_LABEL:
                break;
            case ATTRIBUTE_ICON:
                break;
            case ATTRIBUTE_PACKAGE:
                apk.setPackage_name(attributeValue);
                break;
            case ATTRIBUTE_VERSIONCODE:
                apk.setVersion_code(Integer.valueOf(attributeValue));
                break;
            case ATTRIBUTE_VERSIONNAME:
                apk.setVersion_name(attributeValue);
                break;
        }
    }

    private static String getNamespacePrefix(String prefix) {
        if (prefix==null || prefix.length()==0) {
            return "";
        }
        return prefix+":";
    }

    private static String getPackage(int id) {
        if (id>>>24==1) {
            return "android:";
        }
        return "";
    }

    private static String getAttributeValue(AXmlResourceParser parser,int index) {
        int type=parser.getAttributeValueType(index);
        int data=parser.getAttributeValueData(index);
        if (type== TypedValue.TYPE_STRING) {
            return parser.getAttributeValue(index);
        }
        if (type==TypedValue.TYPE_ATTRIBUTE) {
            return String.format("?%s%08X",getPackage(data),data);
        }
        if (type==TypedValue.TYPE_REFERENCE) {
            return String.format("@%s%08X",getPackage(data),data);
        }
        if (type==TypedValue.TYPE_FLOAT) {
            return String.valueOf(Float.intBitsToFloat(data));
        }
        if (type==TypedValue.TYPE_INT_HEX) {
            return String.format("0x%08X",data);
        }
        if (type==TypedValue.TYPE_INT_BOOLEAN) {
            return data!=0?"true":"false";
        }
        if (type==TypedValue.TYPE_DIMENSION) {
            return Float.toString(complexToFloat(data))+
                    DIMENSION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
        }
        if (type==TypedValue.TYPE_FRACTION) {
            return Float.toString(complexToFloat(data))+
                    FRACTION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
        }
        if (type>=TypedValue.TYPE_FIRST_COLOR_INT && type<=TypedValue.TYPE_LAST_COLOR_INT) {
            return String.format("#%08X",data);
        }
        if (type>=TypedValue.TYPE_FIRST_INT && type<=TypedValue.TYPE_LAST_INT) {
            return String.valueOf(data);
        }
        return String.format("<0x%X, type 0x%02X>",data,type);
    }

    private static void log(String format,Object...arguments) {
        System.out.printf(format,arguments);
        System.out.println();
    }

    private static float complexToFloat(int complex) {
        return (float)(complex & 0xFFFFFF00)*RADIX_MULTS[(complex>>4) & 3];
    }

    private static final float RADIX_MULTS[]={
            0.00390625F,3.051758E-005F,1.192093E-007F,4.656613E-010F
    };
    private static final String DIMENSION_UNITS[]={
            "px","dip","sp","pt","in","mm","",""
    };
    private static final String FRACTION_UNITS[]={
            "%","%p","","","","","",""
    };
}
