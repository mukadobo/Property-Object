package com.mukadobo.json.schema.poc.command

import com.mukadobo.json.schema.JsonSchema
import com.mukadobo.propertyobject.KindAndVersion.Base
import groovy.json.JsonOutput
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
import org.json.JSONObject
import org.json.JSONTokener
import spock.lang.Specification
import spock.lang.Unroll

class CommandPOC extends Specification
{
	static private Logger                  logger        = Logger.getLogger(CommandPOC.class)
	static private Map<String, String>     sampleText    = new LinkedHashMap<>()
	static private Map<String, JSONObject> sampleJsonDom = new LinkedHashMap<>()
	
	static private String getSampleText(String name)
	{
		if (sampleText.get(name) == null)
		{
			String path = "samples/$name"
			
			InputStream stream = CommandPOC.class.getResourceAsStream(path)
			if (!stream)
			{
				throw new RuntimeException("can't open resource-stream for: $name")
			}
			
			stream.withCloseable {
				String text = IOUtils.toString(it, "UTF-8")
				
				sampleText.put(name, text)
			}
		}
		
		sampleText.get(name)
	}
	
	static private JSONObject getSampleJsonDom(String name)
	{
		if (sampleJsonDom.get(name) == null)
		{
			String     text    = getSampleText(name)
			JSONObject jsonDom = new JSONObject(new JSONTokener(text))
			
			sampleJsonDom.put(name, jsonDom)
		}
		
		sampleJsonDom.get(name)
	}
	
	@Unroll
	def "JSON validation ~ #pogoClass.getSimpleName()"()
	{
		expect:
			
			def schema = new JsonSchema(pogoClass)
			schema.validate(jsonText) == null

		where:
			
			pogoClass       | jsonText
			Subject  .class | getSampleJsonDom("simple/subject.json")
			Predicate.class | getSampleJsonDom("simple/predicate.json")
			Command  .class | getSampleJsonDom("simple/command.json")
	}
	
	def "POGO from JSON ~ Subject"()
	{
		when:
			
			Subject probject = new Subject(getSampleJsonDom("simple/subject.json"))
		
		then:
			
			probject.kind == "Subject"
	}
	
	def "POGO from JSON ~ Predicate"()
	{
		when:
			
			Predicate probject = new Predicate(getSampleJsonDom("simple/predicate.json"))
		
		then:
			
			probject.kind == "Predicate"
	}
	
	def "POGO from JSON ~ Command"()
	{
		when:
			
			Command probject = new Command(getSampleJsonDom("simple/command.json"))
		
		then:
			
			probject.kind == "Command"
	}
	
	@Unroll
	def "JSON round-trip ~ #refClass.getSimpleName()"()
	{
		when:
		
			JSONObject firstDom  = new JSONObject(new JSONTokener(refText))
			Base       firstObj  = refClass.newInstance(firstDom)
		
		then:
			
			refClass.getSimpleName() == firstObj.getKind()
			
		when:
			
			String     dupeText  = JsonOutput.toJson(firstObj)
			JSONObject dupeDom   = new JSONObject(new JSONTokener(dupeText))
			
		then:
		
			def schema = new JsonSchema(refClass)
			schema.validate(dupeDom) == null
		
		when:
			
			Base dupeObj = refClass.newInstance(dupeDom)
		
		then:
			
			firstObj == dupeObj
			JsonOutput.toJson(firstObj) == JsonOutput.toJson(dupeObj)
			
		where:
			
			refClass        | refText
			Predicate.class | getSampleText('simple/predicate.json')
			Subject  .class | getSampleText('simple/subject.json')
			Command  .class | getSampleText('simple/command.json')
	}
	
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
}
