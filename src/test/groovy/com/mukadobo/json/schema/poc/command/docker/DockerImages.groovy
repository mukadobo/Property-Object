package com.mukadobo.json.schema.poc.command.docker

import com.mukadobo.json.schema.EntityObject
import com.mukadobo.json.schema.JsonSchema
import com.mukadobo.json.schema.poc.command.Command
import com.spotify.docker.client.DefaultDockerClient
import com.spotify.docker.client.DockerClient
import com.spotify.docker.client.messages.Image
import org.apache.log4j.Logger
import org.json.JSONObject

class DockerImages extends DockerActor.Base
{
	static private Logger     logger = Logger.getLogger(DockerImages)
	static private JsonSchema schema = null
	
	final Boolean all
	final Boolean digests
	final String  filter
	final String  format
	final Boolean noTrunc
	final Boolean quiet
	final Boolean dangling	// non-standard option
	
	/**
	 * Default optional values follow Docker standards.
	 * <BR><BR>
	 * See <A HREF="https://docs.docker.com/engine/reference/commandline/images/#options">
	 *     https://docs.docker.com/engine/reference/commandline/images/#options
	 *     </A>
	 *
	 * @param jsonDom
	 */
	DockerImages(JSONObject jsonDom)
	{
		super(jsonDom)

		validate(jsonDom)
		
		this.all      = jsonDom.optBoolean("--all"     , false)
		this.digests  = jsonDom.optBoolean("--digests" , false)
		this.filter   = jsonDom.optString ("--filter"  , null )
		this.format   = jsonDom.optString ("--format"  , null )
		this.noTrunc  = jsonDom.optBoolean("--no-trunc", false)
		this.quiet    = jsonDom.optBoolean("--quiet"   , false)
		this.dangling = jsonDom.optBoolean("--dangling", false)
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
	
	
	@Override
	Command.Result perform(Map args)
	{
		Map<String,List<String>> logs = [ warnings : [] ]
		
		if (filter  ) logs.warnings.add(  "filter not supported (ignored): '$filter'")
		if (format  ) logs.warnings.add(  "format not supported (ignored): '$format'")
		if (dangling) logs.warnings.add("dangling not supported (ignored): '$dangling'")
		
		final List<DockerClient.ListImagesParam> params = [
			DockerClient.ListImagesParam.allImages(this.all),
			DockerClient.ListImagesParam.create("digests", this.digests ? "1" : "0"),
			
//			DockerClient.ListImagesParam.filter(this.all),
//			DockerClient.ListImagesParam.format(this.all),
//			danglingImages(final boolean dangling)
		]
		
		final DockerClient     docker    = new DefaultDockerClient("unix:///var/run/docker.sock")
		final List<Image>      imagesRaw = docker.listImages(params.toArray(new DockerClient.ListImagesParam[0]))
		final List<JSONObject> imagesDom = imagesRaw.stream().map {
			DockerConvert.toJSONObject(it,
					noTrunc : this.noTrunc,
					quiet   : this.quiet,
			).toMap()
		}.collect()
		
		Map<String, Object> product = [
			command : args,
			count   : imagesDom.size(),
			images  : imagesDom,
		]
		
		return Command.Result.success(summary: "Full list of containers", product: product, logs: logs)
	}
}
