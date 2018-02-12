package org.hotelbyte.discordbot.util;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Scanner;

@Slf4j
public class Utils {


    public static void ignoreSSL() {
        SSLContext ctx = null;
        TrustManager[] trustAllCerts = new X509TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };
        try {
            ctx = SSLContext.getInstance("SSL");
            ctx.init(null, trustAllCerts, null);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.info("Error loading ssl context ", e);
        }
        SSLContext.setDefault(ctx);
    }

    public static String readFile(ClassLoader classLoader, String filePath) {
        File file = new File(classLoader.getResource(filePath).getFile());
        StringBuilder defaultData = new StringBuilder();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                defaultData.append(scanner.nextLine());
            }
            scanner.close();
        } catch (IOException e) {
            log.error("Error reading file", e);
        }
        return defaultData.toString();
    }

    public static String readFile(File file) {
        StringBuilder defaultData = new StringBuilder();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                defaultData.append(scanner.nextLine());
            }
            scanner.close();
        } catch (IOException e) {
            log.error("Error reading file", e);
        }
        return defaultData.toString();
    }

    public static String getIp(HttpServletRequest httpServletRequest) {
        String remoteAddr = "";
        if (httpServletRequest != null) {
            remoteAddr = httpServletRequest.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = httpServletRequest.getRemoteAddr();
            }
        }
        return remoteAddr;
    }
}
