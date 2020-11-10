package topbloc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class ApacheHttpClientPost {

	public static void postToChallenge(String data) throws IOException {

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			// creates a POST request
			var request = new HttpPost("http://54.90.99.192:5000/challenge");
			request.setHeader("Content-Type", "application/json");
			request.setEntity(new StringEntity(data));

			// execute the request and get response
			HttpResponse response = client.execute(request);

			// read and print to console
			var bufReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			var builder = new StringBuilder();
			String line;
			while ((line = bufReader.readLine()) != null) {
				builder.append(line);
				builder.append(System.lineSeparator());
			}

			System.out.println(builder);
		}
	}

}
