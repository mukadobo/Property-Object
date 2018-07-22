package com.mukadobo.json.schema.poc.command.docker

import com.mukadobo.json.schema.EntityObject
import com.mukadobo.json.schema.JsonTo
import com.mukadobo.json.schema.poc.command.Command
import org.json.JSONObject

import java.lang.reflect.Constructor

interface DockerActor extends EntityObject, Command.Performer
{
	static class Base extends EntityObject.Base implements DockerActor
	{
		Base(JSONObject jsonDom)
		{
			super(jsonDom)
		}
		
		Command.Result perform(Map args)
		{
			Command.Result.nyi("Implementing class must override this method")
		}
		
		static class ActorNYI extends DockerActor.Base
		{
			ActorNYI(JSONObject jsonDom)
			{
				super(jsonDom)
			}
			
			Command.Result perform(Map args)
			{
				Command.Result.failure(summary: "Docker action NYI: ${args.docker.verb}")
			}
		}
		
		static class ActorNotSupported extends DockerActor.Base
		{
			ActorNotSupported(JSONObject jsonDom)
			{
				super(jsonDom)
			}
			
			Command.Result perform(Map args)
			{
				Command.Result.failure(summary: "Docker action not supported.")
			}
		}
		
		static DockerActor newActor(DockerVerb verb, JSONObject jsonDom)
		{
			Class verbClass
			if (! verb.supported)
			{
				verbClass = ActorNotSupported.class
			}
			else
			{
				String packageName   = DockerActor.class.getPackage().getName()
				String shortName     = "Docker" + verb.toString().toLowerCase().capitalize()
				String qualifiedName = "${packageName}.${shortName}"
				
				try
				{
					verbClass = Class.forName(qualifiedName)
				}
				catch (ClassNotFoundException ignored)
				{
					verbClass = ActorNYI.class
				}
			}
			
			Constructor constructor
			try
			{
				constructor = verbClass.getConstructor(JSONObject.class)
			}
			catch (NoSuchMethodException ignored)
			{
				throw new RuntimeException("verb-class has no appropriate constructor: $verbClass")
			}
			
			JSONObject twiddleDom = JsonTo.shallowCopy(jsonDom, kind: verbClass.getCanonicalName())
			
			Object twiddleDee = constructor.newInstance(twiddleDom)
			twiddleDee as DockerActor
		}
	}
}