package com.mukadobo.json.schema.poc.command

import org.apache.commons.io.IOUtils
import org.apache.http.HttpEntity
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.fluent.Content
import org.apache.http.client.fluent.Form
import org.apache.http.client.fluent.Request
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.apache.log4j.Logger
import spock.lang.Specification
import spock.lang.Unroll

class CommandPOC extends Specification
{
	static private Logger                  logger        = Logger.getLogger(CommandPOC.class)

	def "GET a web page ~ Apache quick start ~ HttpClient"()
	{
		// initially from: https://hc.apache.org/httpcomponents-client-ga/quickstart.html
		
		setup:
		
		HttpClients.createDefault().withCloseable { httpclient ->
			
			HttpGet httpGet = new HttpGet("http://targethost/homepage")
			
			// The underlying HTTP connection is still held by the response object
			// to allow the response content to be streamed directly from the network socket.
			// In order to ensure correct deallocation of system resources
			// the user MUST call CloseableHttpResponse#close() from a finally clause.
			// Please note that if response content is not fully consumed the underlying
			// connection cannot be safely re-used and will be shut down and discarded
			// by the connection manager.
			
			httpclient.execute(httpGet).withCloseable { response1 ->
				
				logger.debug(response1.getStatusLine())
				
				HttpEntity entity1 = response1.getEntity()
				
				// do something useful with the response body
				// and ensure it is fully consumed
				
				entity1.getContent().withCloseable { content ->
					logger.debug (IOUtils.toString(content, "UTF-8"))
				}
				
				EntityUtils.consume(entity1)
			}
			
			HttpPost httpPost = new HttpPost("http://httpbin.org/post")
			List <NameValuePair> nameValuePairs = new ArrayList <NameValuePair>()
			nameValuePairs.add(new BasicNameValuePair("username", "vip"))
			nameValuePairs.add(new BasicNameValuePair("password", "secret"))
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs))
			
			httpclient.execute(httpPost).withCloseable { response2 ->
				logger.debug(response2.getStatusLine())
				
				HttpEntity entity2 = response2.getEntity()
				// do something useful with the response body
				// and ensure it is fully consumed
				
				entity2.getContent().withCloseable { content ->
					logger.debug (IOUtils.toString(content, "UTF-8"))
				}
				
				EntityUtils.consume(entity2)
			}
		}
	}
	
	def "GET a web page ~ Apache quick start ~ Fluent API"()
	{
		setup:
			
			Content content1 = Request
					.Get("http://targethost/homepage")
					.execute()
					.returnContent()
		
			logger.debug("content1 = ${content1}")
		
			Content content2 = Request
					.Post("http://httpbin.org/post")
					.bodyForm(
						Form.form()
							.add("username", "vip")
							.add("password", "secret")
							.build()
					)
					.execute()
					.returnContent()
		
			logger.debug("content2 = ${content2}")
	}
	
	def "Perform ~ #sampleName [GROUP]"() { expect: false }
	
	@Unroll
	def "Perform ~ #sampleName"()
	{
		// curl -X GET "https://api-v3.mbta.com/stops/8553?api_key=2f1ac323439541c194f027bad0e59f5c" -H "accept: application/vnd.api+json"
		// curl -X GET "https://api-v3.mbta.com/stops/8553" -H "accept: application/vnd.api+json" -H "x-api-key: 2f1ac323439541c194f027bad0e59f5c"
		
		def path = "samples/${sampleName}.json"
		
		when:
			
			InputStream stream    = CommandTest.class.getResourceAsStream(path)
			Command     command   = new Command(stream)
		
		then:
			
			Command.class.getSimpleName() == command.getKind()
		
		when:
		
			Command.Result result = command.perform()
		
		then:
		
			result.code == Command.Result.Code.SUCCESS
		
		where:
			
			x | sampleName
			0 | "HttpRest/mbta-list"
	}
	
}
