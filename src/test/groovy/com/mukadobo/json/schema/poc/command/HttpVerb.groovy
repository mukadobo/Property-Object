package com.mukadobo.json.schema.poc.command

import org.apache.http.client.methods.*

import javax.naming.OperationNotSupportedException

enum HttpVerb
{
	CONNECT,
	DELETE ,
	GET    ,
	HEAD   ,
	PATCH  ,
	POST   ,
	PUT    ,
	TRACE  ,
	;
	
	static HttpVerb from(String text)
	{
		HttpVerb.valueOf(text?.trim()?.toUpperCase())
	}
	
	HttpUriRequest newRequest(String url)
	{
		switch (this)
		{
			case CONNECT: throw new OperationNotSupportedException("Can't make a request for HTTP-VERB: $this")
			case DELETE : new HttpDelete (url);     break
			case GET    : new HttpGet    (url);     break
			case HEAD   : new HttpHead   (url);     break
			case PATCH  : new HttpPatch  (url);     break
			case POST   : new HttpPost   (url);     break
			case PUT    : new HttpPut    (url);     break
			case TRACE  : new HttpTrace  (url);     break
		}
	}
}