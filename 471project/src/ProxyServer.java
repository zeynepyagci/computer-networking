
import java.io.IOException;
import java.net.*;

//GET
//curl -v -x http://127.0.0.1:8080 \
//-H 'Connection: close' \
//http://neverssl.com

//POST
//curl -v -x http://127.0.0.1:8080 \
//-X POST
//-H 'Content-Type: application/json' \
//-H 'Connection: close' \
//-d '{"name": "YOURNAME", "surname": "YOURSURNAME"}' \
//http://httpbin.org/post

public class ProxyServer extends Thread {
	
	ServerSocket s = null;

	
	public void stopmethod() {
		try {
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startmethod() {
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					s = new ServerSocket(8080);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// TODO Auto-generated method stub
				while (true) {
					Socket clientSocket = null;
					try {
						clientSocket = s.accept();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("error");
						if(s.isClosed()) break;
					}
					try {
						new ServerHandler(clientSocket).start();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
			}
			
			
			
		}).start();
		
		
	}
	
	
	
	

}
