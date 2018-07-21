package com.mukadobo.json.schema.poc.command

import com.mukadobo.json.schema.EntityObject
import com.mukadobo.json.schema.JsonSchema
import org.apache.commons.io.IOUtils
import org.apache.http.HttpEntity
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.apache.log4j.Logger
import org.json.JSONObject
import org.json.JSONTokener
/**
 *  <PRE>TODO:
 *  - !! Actually pull elements from perform(...) arguments
 *  - !! Some kind of authentication secret coordination
 *  ---------------------------------------------------------------------------
 *  - At least GET & PUT, probably the whole shaboodle
 *  - If response is known to be JSON, then convert it to JSONObject
 *  - If response is NOT known to be text, then BASE64
 *  - Limit the size of the response
 *  - Add timeout control
 *  - Better response
 *    - should always include non-secret URL parts
 *    - include response time
 *
 * </PRE>
 * See <A HREF="https://hc.apache.org/index.html">https://hc.apache.org/index.html</A>
 */
class HttpRest extends EntityObject.Base implements Command.Performer
{
	static private Logger     logger = Logger.getLogger(HttpRest)
	static private JsonSchema schema = null
	
	final String  server
	final Integer port
	final Boolean tls
	final Map<String,Object> headers

	HttpRest(JSONObject jsonDom)
	{
		super(jsonDom)
		
		validate(jsonDom)
		
		this.tls = jsonDom.optBoolean("tls", false)
		int defaultPort = tls ? 443 : 80
		
		this.server  = jsonDom.optString("server", "localhost")
		this.port    = jsonDom.optInt   ("port"  , defaultPort)
		this.headers = jsonDom.has("headers") ? jsonDom.getJSONObject("headers").toMap() : [:]
	}

	static private JsonSchema myJsonSchema()
	{
		if (!schema)
		{
			schema = new JsonSchema(EntityObject.class)
		}
		
		schema
	}
	
	static private void validate(JSONObject jsonDom)
	{
		try
		{
			myJsonSchema().validate(jsonDom)
		}
		catch (Exception e)
		{
			throw new RuntimeException("JSON input not valid", e)
		}
	}
	
	Command.Result perform(Map args)
	{
		HttpPredicate predicate = args.predicate
		
		// cribbed from: https://hc.apache.org/httpcomponents-client-ga/quickstart.html
		
		String scheme   = tls ? "https" : "http"
		String urlBase  = "$scheme://$server:$port"
		String urlPath  = predicate.path
		String urlBody  = "$urlBase/$urlPath"
		String urlQuery = ""
		String urlFull  = "$urlBody$urlQuery"
		
		String summary = "HTTP-${predicate.verb}: $urlFull"
		Object product = null
		
		logger.info("urlFull=$urlFull")
	
		HttpClients.createDefault().withCloseable { httpclient ->
			
			HttpUriRequest httpRequest = predicate.verb.newRequest(urlFull)
			headers.entrySet().each { Map.Entry<String, Object> entry ->
				httpRequest.setHeader(entry.getKey(), entry.getValue().toString())
			}
			
			httpclient.execute(httpRequest).withCloseable { response1 ->
				
				logger.info(response1.getStatusLine())
				
				HttpEntity entity1 = response1.getEntity()
				
				// do something useful with the response body
				// and ensure it is fully consumed
				
				entity1.getContent().withCloseable { content ->
					String contentText = IOUtils.toString(content, "UTF-8")
					
					product = new JSONObject(new JSONTokener(contentText)).toMap()
				}
				
				EntityUtils.consume(entity1)
			}
		}
		
		new Command.Result(Command.Result.Status.SUCCESS,
			   summary : summary,
			   product : product,
		)
	}
	
}
