package com.mukadobo.json.schema.poc.command.docker

import com.spotify.docker.client.messages.Image
import org.json.JSONObject

class DockerConvert
{
	static JSONObject toJSONObject(Map args = [:], Image image)
	{
		JSONObject conversion = new JSONObject()
		
		conversion.put("created"      , image.created())
		conversion.put("id"           , image.id())
		conversion.put("parentId"     , image.parentId())
		conversion.put("repoTags"     , image.repoTags())
		conversion.put("repoDigests"  , image.repoDigests())
		conversion.put("size"         , image.size())
		conversion.put("virtualSize"  , image.virtualSize())
		conversion.put("labels"       , image.labels())
		
		conversion
	}
}
