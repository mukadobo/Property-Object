package com.mukadobo.json.schema.poc.command.docker

import com.mukadobo.json.schema.EntityObject
import com.mukadobo.json.schema.JsonSchema
import com.mukadobo.json.schema.poc.command.Command
import com.spotify.docker.client.DefaultDockerClient
import com.spotify.docker.client.DockerClient
import com.spotify.docker.client.ProgressHandler
import com.spotify.docker.client.exceptions.DockerException
import com.spotify.docker.client.messages.ProgressMessage
import com.spotify.docker.client.messages.RegistryAuth
import org.apache.log4j.Logger
import org.json.JSONObject

class DockerPull extends DockerActor.Base
{
	static private Logger     logger = Logger.getLogger(DockerPull)
	static private JsonSchema schema = null
	
	final String  source
	final Boolean allTags
	final Boolean noTrust
	
	/**
	 * Default optional values follow Docker standards.
	 * <BR><BR>
	 * See <A HREF="https://docs.docker.com/engine/reference/commandline/pull/#options">
	 *     https://docs.docker.com/engine/reference/commandline/pull/#options
	 *     </A>
	 *
	 * @param jsonDom
	 */
	DockerPull(JSONObject jsonDom)
	{
		super(jsonDom)

		validate(jsonDom)
		
		this.source  = jsonDom.optString ("source")
		this.allTags = jsonDom.optBoolean("all-tags", false)
		this.noTrust = jsonDom.optBoolean("disable-content-trust", true)
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
		
		if (noTrust) logs.warnings.add("option not supported (ignored): no-trust=${noTrust}")
		
		String sourceWithTag = source
		if (source.indexOf(":") >= 0)
		{
			if (allTags) logs.warnings += "all-tags=true ignored when source locator has a tag: $source"
		}
		else
		{
			if (!allTags)
			{
				sourceWithTag = "${source}:latest"
				logs.warnings += "all-tags=false but source tag not given, using default tag: $sourceWithTag"
			}
		}
		
		final DockerClient  docker = new DefaultDockerClient("unix:///var/run/docker.sock")
		
		RegistryAuth    regAuth  = RegistryAuth.builder().build()
		ProgressHandler progress = new ProgressHandler() {
			
			List<String> messageList = []
			
			@Override
			void progress(ProgressMessage message) throws DockerException
			{
				String text = message.toString()
				messageList += text
			}
		}
		
		docker.pull(sourceWithTag, regAuth, progress)
		Map<String, Object> product = [
			command  : args,
			messages : progress.messageList,
		]
		
		return Command.Result.success(summary: "Pull image(s)", product: product, logs: logs)
	}
}
