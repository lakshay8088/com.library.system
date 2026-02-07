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


}
