
import java.net.*;
import java.util.*;
import java.util.ArrayList;
import java.io.*;

public class ServerHandler extends Thread {
	
	static ArrayList<String> domainList = new ArrayList<String>();
	static ArrayList<String> pathList = new ArrayList<String>();
	static ArrayList<String> dateList = new ArrayList<String>();
	static ArrayList<String> httpMethodList = new ArrayList<String>();
	static ArrayList<String> response = new ArrayList<String>();
	static ArrayList<String> report = new ArrayList<String>();
	static ArrayList<String> forbidden = new ArrayList<String>();
	
	Socket clientSocket;
	DataInputStream dis;
	DataOutputStream dos;
	
	

	public ServerHandler(Socket clientSocket) throws Exception {
		this.clientSocket = clientSocket;
		dis = new DataInputStream(clientSocket.getInputStream());
		dos = new DataOutputStream(clientSocket.getOutputStream());
	}


	@Override
	public void run() {

		try {

			byte[] headerArr = new byte[5000];
			int hc = 0;

			// only for header part
			while (true) {
				byte i = (byte) dis.read();
				headerArr[hc++] = i;
				if (headerArr[hc - 1] == '\n' && headerArr[hc - 2] == '\r' && headerArr[hc - 3] == '\n'
						&& headerArr[hc - 4] == '\r') { // \r\n\r\n
					break;
				}

			}

			String header = new String(headerArr, 0, hc);
			System.out.println("-------HEADER FROM CLIENT----");
			System.out.println(header);

			// GET / HTTP/1.1\r\n
			// Host: asd.com
			//
			int fsp = header.indexOf(' ');
			int ssp = header.indexOf(' ', fsp + 1);
			int eol = header.indexOf("\r\n");

			String methodName = header.substring(0, fsp);

			String restHeader = header.substring(eol + 2);

			String modHeader = restHeader;

			if (modHeader.contains("Proxy-Connection")) {
				int proxIndex = modHeader.indexOf("Proxy-Connection");
				int eolProxIndex = modHeader.indexOf("\r\n", proxIndex);

				modHeader = modHeader.substring(0, proxIndex) + modHeader.substring(eolProxIndex + 2);
			}

			if (modHeader.contains("Cookie: Unwanted Cookie")) {
				int cookieIndex = modHeader.indexOf("Cookie: Unwanted Cookie");
				int eolCookieIndex = modHeader.indexOf("\r\n", cookieIndex);

				modHeader = modHeader.substring(0, cookieIndex) + modHeader.substring(eolCookieIndex + 2);
			}

			System.out.println("--MOD HEADER --");
			System.out.println(modHeader);

			String fullpath = header.substring(fsp + 1, ssp);
			pathList.add(fullpath);

			URL url = new URL(fullpath);

			String domain = url.getHost();
			String shortpath = url.getPath().equals("") ? "/" : url.getPath();

			System.out.println(domain);
			System.out.println(shortpath);
			domainList.add(domain);
			
			
			


			if (methodName.equals("GET")) {

				if (domain.equals("www.facebook.com")) {

					String html =	"<html>\r\n" +
										"<head>\r\n" +
											"<title>401 Not Authorized</title>\r\n" +
										"</head>\r\n" +
										"<body>\r\n" +
											"<h1>401 Not Authorized</h1>\r\n" +
											"<h1>The domain " + domain + " is forbidden</h1>\r\n" +
										"</body>\r\n" +
									"</html>\r\n";

					String response =	"HTTP/1.1 401 Not Authorized\r\n" +
										"Server: CSE471Proxy\r\n" +
										"Content-Type: text/html\r\n" +
										"Content-Length: " + html.length() + "\r\n" +
										"\r\n" +
										html;

					
					dos.writeBytes(response);
					forbidden.add(domain);

				} 
				else if(domain.equals("www.yulearn.com")) {
					String html =	"<html>\r\n" +
								"<head>\r\n" +
									"<title>401 Not Authorized</title>\r\n" +
									"</head>\r\n" +
									"<body>\r\n" +
									"<h1>401 Not Authorized</h1>\r\n" +
									"<h1>The domain " + domain + " is forbidden</h1>\r\n" +
									"</body>\r\n" +
									"</html>\r\n";

					String response =	"HTTP/1.1 401 Not Authorized\r\n" +
								"Server: CSE471Proxy\r\n" +
								"Content-Type: text/html\r\n" +
								"Content-Length: " + html.length() + "\r\n" +
								"\r\n" +
								html;

					
					dos.writeBytes(response);
					forbidden.add(domain);

					
				}
				else if(domain.equals(MenuGui.host)) {
					String html =	"<html>\r\n" +
								"<head>\r\n" +
									"<title>401 Not Authorized</title>\r\n" +
									"</head>\r\n" +
									"<body>\r\n" +
									"<h1>401 Not Authorized</h1>\r\n" +
									"<h1>The domain " + domain + " is forbidden</h1>\r\n" +
									"</body>\r\n" +
									"</html>\r\n";

					String response =	"HTTP/1.1 401 Not Authorized\r\n" +
								"Server: CSE471Proxy\r\n" +
								"Content-Type: text/html\r\n" +
								"Content-Length: " + html.length() + "\r\n" +
								"\r\n" +
								html;

					forbidden.add(MenuGui.host);
					dos.writeBytes(response);
				}
				
				else {
					handleProxy(methodName, modHeader, null, domain, shortpath);
				}

			} else if (methodName.equals("POST")) {

				int contIndex = header.indexOf("Content-Length: ");
				int eol2 = header.indexOf("\r\n", contIndex);
				String contSize = header.substring(contIndex + 16, eol2);
				int contSizeInt = Integer.parseInt(contSize);

				System.out.println("Header from client ContLength: " + contSizeInt);

				byte[] headerPayload = new byte[contSizeInt];

				byte[] buffer = new byte[1024];

				int sum = 0;
				int read;

				while (sum < contSizeInt) {
					read = dis.read(buffer);
					System.arraycopy(buffer, 0, headerPayload, sum, read);
					sum += read;
				}

				handleProxy(methodName, modHeader, headerPayload, domain, shortpath);

			} else {

				String html =	"<html>\r\n" +
									"<head>\r\n" +
										"<title>405 Method Not Allowed</title>\r\n" +
									"</head>\r\n" +
									"<body>\r\n" +
										"<h1>405 Method Not Allowed</h1>\r\n" +
										"<h1>The HTTP Method " + methodName + " is not allowed</h1>\r\n" +
									"</body>\r\n" +
								"</html>\r\n";

				String response =	"HTTP/1.1 405 Method Not Allowed\r\n" +
									"Server: CSE471Proxy\r\n" +
									"Content-Type: text/html\r\n" +
									"Content-Length: " + html.length() + "\r\n" +
									"\r\n" +
									html;

				dos.writeBytes(response);

			}

			System.out.println("HANDLED CLIENT " + clientSocket.getInetAddress().getHostAddress() +
					" FOR ADDRESS " + fullpath);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

	private void handleProxy(String methodName, String restHeader, byte[] headerPayload, String domain,
			String shortpath) throws Exception {

		Socket proxiedSocket = new Socket(domain, 80);

		DataInputStream dis1 = new DataInputStream(proxiedSocket.getInputStream());
		DataOutputStream dos1 = new DataOutputStream(proxiedSocket.getOutputStream());

		// request sent to web server
		String constructedHeader = methodName + ' ' + shortpath + " HTTP/1.1\r\n" + restHeader;

		System.out.println("-------HEADER TO WEBSERVER----");
		System.out.println(constructedHeader);

		dos1.writeBytes(constructedHeader);

		if (methodName.equals("POST") && headerPayload != null) {
			dos1.write(headerPayload);
		}
		
		
		httpMethodList.add(methodName);

		// NOW READ HTTP RESPONSE FROM WEBSERVER

		// byte array for HTTP Response header
		byte[] reponseHdrArr = new byte[5000];
		int rc = 0;

		// only for response header part
		while (true) {
			byte i = (byte) dis1.read();
			reponseHdrArr[rc++] = i;
			if (reponseHdrArr[rc - 1] == '\n' && reponseHdrArr[rc - 2] == '\r' && reponseHdrArr[rc - 3] == '\n'
					&& reponseHdrArr[rc - 4] == '\r') { // \r\n\r\n
				break;
			}

		}
		
		

		System.out.println("-------RESPONSE HEADER FROM WEBSERVER----");
		String responseHdr = new String(reponseHdrArr, 0, rc);
		System.out.println(responseHdr);
		
		int datebegin = responseHdr.indexOf("Date");
		int end = responseHdr.indexOf("\r\n",datebegin);
		String date = responseHdr.substring(datebegin, end);
		dateList.add(date);
		//System.out.println(dateList); //Date: ...
		
		
		int http = responseHdr.indexOf("HTTP");
		int endofline = responseHdr.indexOf("\n");
		String responsestatus = responseHdr.substring(http, endofline);
		response.add(responsestatus);
		
		int contIndex = responseHdr.indexOf("Content-Length: ");
		int eol = responseHdr.indexOf("\r\n", contIndex);
		String contSize = responseHdr.substring(contIndex + 16, eol);
		int contSizeInt = Integer.parseInt(contSize);
		
		
		
		
		

		System.out.println("FOUND DATA SIZE IN RESPONSE: " + contSizeInt);

		byte[] payload = new byte[contSizeInt];

		byte[] buffer = new byte[1024];

		int sum = 0;
		int read;

		while (sum < contSizeInt) {
			read = dis1.read(buffer);
			System.arraycopy(buffer, 0, payload, sum, read);
			sum += read;
		}

		// header part of response back to client
		dos.write(reponseHdrArr, 0, rc);

		// payload part of the response back to client
		dos.write(payload);

		dos.flush();

		System.out.println();
		System.out.println("SENT HTTP RESPONSE & DATA BACK TO CLIENT");
		System.out.println();
		
		
		

		proxiedSocket.close();

	}
	
	public static String birlestir() throws IOException {
		
		for(int i=0;i<domainList.size();i++) {
			report.add("Domain: " + domainList.get(i)+"\n"+"Path: " + pathList.get(i)+"\n"+dateList.get(i)+"\n"+ "HTTP Method: " + httpMethodList.get(i)+"\n"+"Repsonse Status: " + response.get(i));
		}
		
		return String.valueOf(report);
		
	}
	
	public static void forbiddenbastir(){
		
		for(int i=0;i<forbidden.size();i++) {
			System.out.println(forbidden.get(i));
		}
		
	}


	
}