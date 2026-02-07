package com.library.manG.System.com.library.system.utils;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;

import static com.library.manG.System.com.library.system.Application.logger;

public class NetworkUtils {
    private static final Logger log = LoggerFactory.getLogger(NetworkUtils.class);

//    @Value("${certPath}")
//    public static String selfsignedcertificate;
//
//    @Value("${certPass}")
//    public static String pass;



    public static String callPostBypassMethod(String URL, String inputType,
                                       String body, JSONObject headerObj) throws Exception {

        logger.info("-----SSL Call using certificate POST METHOD---------");


            return postWithJKS(URL, body, BankHeader.getHeader());

    }

    public static String postWithJKS(String urlStr, String body, JSONObject headers) throws Exception {
        URL url = new URL(urlStr);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

        // SSL setup
        con.setSSLSocketFactory(getSocketFactory("/Users/lakshaychaudhary/Downloads/client.jks", "Test123"));
        con.setHostnameVerifier((hostname, session) -> true);

        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setConnectTimeout(200000);
        con.setReadTimeout(200000);

        // Headers
        if (headers != null) {
            Iterator<String> keys = headers.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                con.setRequestProperty(key, headers.getString(key));
            }
        }
        con.setRequestProperty("Content-Type", "application/json");

        // Body
        try (OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream())) {
            writer.write(body != null ? body : "");
        }

        // Response
        StringBuilder response = new StringBuilder();
        try (InputStreamReader reader = new InputStreamReader(con.getInputStream())) {
            char[] buffer = new char[2048];
            int n;
            while ((n = reader.read(buffer)) != -1) {
                response.append(buffer, 0, n);
            }
        }

        return response.toString();
    }


//    public static String callPostBypassMethodLocalToYesBank(String URL, String inputType,
//                                                     String body, JSONObject headerObj) throws Exception {
//
//
//        logger.info("-------CALL POST METHOD USING HTTP URL CONNECTION--------");
//        java.net.URL url = new URL(URL);
//        URLConnection con = null;
//
//        if (URL.contains("https")) {
//
//
//                HttpsURLConnection.setDefaultSSLSocketFactory(getSocketFactory("/Users/lakshaychaudhary/Downloads/client.jks", "Test123"));
//           // else
//            //    HttpsURLConnection.setDefaultSSLSocketFactory(getSocketFactoryV1(crtFile, keyFile));
//            logger.info("-------SET SSL SOCKET FACTORY SUCCESSFULLY--------");
//            // Create all-trusting host name verifier
//            HostnameVerifier allHostsValid = new HostnameVerifier() {
//                public boolean verify(String hostname, SSLSession session) {
//                    return true;
//                }
//            };
//            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
//
//
//            con = (HttpsURLConnection) url.openConnection();
//
//        } else {
//            con = url.openConnection();
//        }
//
//        con.setDoInput(true);
//        con.setDoOutput(true);
//        con.setConnectTimeout(200000); // long timeout,but not infinite
//        con.setReadTimeout(200000);
//        con.setUseCaches(false);
//        con.setDefaultUseCaches(false);
//        String contentType = "application/";
//        if (inputType == null || inputType.isEmpty()) {
//            contentType = contentType + "text";
//        } else if (inputType.contains("/")) {
//            contentType = inputType;
//        } else {
//            contentType = contentType + inputType;
//        }
//        con.setRequestProperty("Content-Type", contentType);
//        //con.setRequestMethod("POST");
//        ((HttpsURLConnection) con).setRequestMethod("POST");
//
//
//
//
//        if (headerObj != null) {
//            if (headerObj.length() > 0) {
//                @SuppressWarnings("unchecked")
//                Iterator<String> keyset = headerObj.keys();
//                while (keyset.hasNext()) {
//                    String key = keyset.next();
//                    String val = headerObj.getString(key);
//                    con.setRequestProperty(key, val);
//                }
//            }
//        }
//
//        // WRITING TO THE URL
//        if (body == null || body.isEmpty()) {
//            body = "";
//        } else if (("xml").equalsIgnoreCase(inputType)) {
//            body = URLEncoder.encode(body, "UTF-8");
//            logger.info("--------Network util final body" + body);
//        }
//
//        OutputStreamWriter writer = null;
//        writer = new OutputStreamWriter(con.getOutputStream());
//        writer.write(body);
//        writer.flush();
//        writer.close();
//
//        // READING FROM THE URL
//        InputStreamReader reader = new InputStreamReader(con.getInputStream());
//        StringBuilder buffer = new StringBuilder();
//        char[] cbuf = new char[2048];
//        int num;
//        while (-1 != (num = reader.read(cbuf))) {
//            buffer.append(cbuf, 0, num);
//        }
//
//        return buffer.toString();
//
//    }

    public static SSLSocketFactory getSocketFactory(String jksPath, String password) {

        try {
            // Load JKS keystore
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(jksPath), password.toCharArray());

            // Key manager for client certificate
            KeyManagerFactory kmf =
                    KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, password.toCharArray());

            // Trust manager (server cert validation)
            TrustManagerFactory tmf =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            // SSL context
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(
                    kmf.getKeyManagers(),
                    tmf.getTrustManagers(),
                    new java.security.SecureRandom()
            );

            return sslContext.getSocketFactory();

        } catch (Exception e) {
            log.info("error: " + e.getMessage());
        }
        return null;
    }


//    public static SSLSocketFactory getSocketFactory(String certpath, String password) {
//
//        logger.info("-------------------GET SSLSocketFactory----------------");
//      //  password="/Users/lakshaychaudhary/client.key";
//        try {
//
//            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//                @Override
//                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
//
//                }
//
//                @Override
//                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
//
//                }
//
//                @Override
//                public X509Certificate[] getAcceptedIssuers() {
//                    return new X509Certificate[0];
//                }
//            }};
//
//
//            // Create a key manager factory for our personal PKCS12 key file
//            KeyManagerFactory keyMgrFactory = KeyManagerFactory.getInstance("SunX509");
//            KeyStore keyStore = KeyStore.getInstance("p12");
//
//
//            logger.info("----Self signed certificate path-----" + certpath);
//            logger.info("----Self signed certificate password-----" + password);
//            char[] keyStorePassword = password.toCharArray();
//            keyStore.load(new FileInputStream("/Users/lakshaychaudhary/mykeystore.jks"),keyStorePassword );
//            keyMgrFactory.init(keyStore, keyStorePassword);
//            SSLContext context = SSLContext.getInstance("SSL");
//            context.init(keyMgrFactory.getKeyManagers(),
//                    trustAllCerts, null);
//
//            return context.getSocketFactory();
//        } catch (Exception e) {
//            System.err.println("Failed to create a server socket factory... " + e.getMessage());
//
//            return null;
//        }
//    }


}
