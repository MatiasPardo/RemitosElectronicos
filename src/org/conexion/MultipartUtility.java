package org.conexion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.*;

import sun.net.www.protocol.http.HttpURLConnection;

public class MultipartUtility {
    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpsURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;
	private static final String PATHCERTIFICADOSDIGITALES = "C:\\ConfigFEArg\\certificados\\padronarba.jks"; 


    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     *
     * @param requestURL
     * @param charset
     * @throws Exception 
     */
    public MultipartUtility(String requestURL, String charset)
            throws Exception {
        this.charset = charset;

		SSLSocketFactory sslSocketFactory = this.obtenerCertificado();

        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "===";
        URL url = new URL(requestURL);
        httpConn = (HttpsURLConnection)url.openConnection();
        httpConn.setDoOutput(true); 
        httpConn.setDoInput(true); 
        httpConn.setRequestMethod("POST");
//        httpConn.setSSLSocketFactory(sslSocketFactory);
        httpConn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                true);
    }

    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */
    public void addFormField(String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName  name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();
        writer.append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a header field to the request.
     *
     * @param name  - name of the header field
     * @param value - value of the header field
     */
    public void addHeaderField(String name, String value) {
        writer.append(name + ": " + value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Completes the request and receives response from the server.
     *
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public List<String> finish2(File file) throws IOException {
		String encode = "UTF-8";
        List<String> response = new ArrayList<String>();

	    // user
	    writer.append("--" + boundary).append(LINE_FEED);
	    writer.append("Content-Disposition: form-data; name=\"" + URLEncoder.encode("user",encode) + "\"").append(LINE_FEED).append(LINE_FEED);
	    writer.append(URLEncoder.encode("33714210349", encode)).append(LINE_FEED);
	    // password
	    writer.append("--" + boundary).append(LINE_FEED);
	    writer.append("Content-Disposition: form-data; name=\"" + URLEncoder.encode("password",encode) + "\"").append(LINE_FEED).append(LINE_FEED);
	    writer.append(URLEncoder.encode("637884", encode)).append(LINE_FEED);
	    // file
	    writer.append("--" + boundary).append(LINE_FEED);
	    writer.append("Content-Disposition: form-data; name=\"" + URLEncoder.encode("file", encode) + "\"; filename=\"" + file.getName() + "\"").append(LINE_FEED).append(LINE_FEED);	    

	    FileInputStream inputStream = new FileInputStream(file);
	    byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
        	for(int i = 0; i< bytesRead;i++){
        		if((int)buffer[i] != 10){
        			writer.write(buffer[i]);
        		}
        	}
        }
        inputStream.close();
	    writer.append(LINE_FEED);
	    writer.append("--" + boundary).append(LINE_FEED);
	    writer.close();
	    
        // checks server's status code first
        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                response.add(line);
            }
            reader.close();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }
        return response;
    }
    
    public List<String> finish() throws IOException {
        List<String> response = new ArrayList<String>();
        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();

        // checks server's status code first
        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                response.add(line);
            }
            reader.close();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }
        return response;
    }
    
    private SSLSocketFactory obtenerCertificado() throws Exception{
		InputStream fileCertificadosConfianza = new FileInputStream(new File(MultipartUtility.PATHCERTIFICADOSDIGITALES));	    
		KeyStore ksCertificadosConfianza = KeyStore.getInstance(KeyStore.getDefaultType());
		ksCertificadosConfianza.load(fileCertificadosConfianza, "123456".toCharArray());
		fileCertificadosConfianza.close();
		// Ponemos el contenido en nuestro manager de certificados de confianza.
	    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
	    tmf.init(ksCertificadosConfianza);	    
	    // Creamos un contexto SSL con nuestro manager de certificados en los
	    // que confiamos.
	    SSLContext context = SSLContext.getInstance("TLS");
	    context.init(null, tmf.getTrustManagers(), null);
	    SSLSocketFactory sslSocketFactory = context.getSocketFactory();
	    
	    return sslSocketFactory;
	}
}
