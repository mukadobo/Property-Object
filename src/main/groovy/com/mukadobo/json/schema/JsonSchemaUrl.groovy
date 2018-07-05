package com.mukadobo.json.schema

import org.everit.json.schema.ObjectSchema
import org.everit.json.schema.ReferenceSchema
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.json.JSONTokener

class JsonSchemaUrl
{
	static final String  SCHEME                  = "jsonschema"
	static final String  DEFAULT_PATH_PREFIX     = "jsonschema"
	static final Boolean DEFAULT_CLASS_CANONICAL = true
	static final String  RESOURCE_FILE_SUFFIX    = "-schema.json"
	
	/**
	 * Modify the system property {@code java.protocol.handler.pkgs} to include
	 * the package which provides {@code **.jsonschema.Handler}.
	 */
	static
	{
		String jphpGiven = System.getProperty('java.protocol.handler.pkgs')
		String jphpPlus  = (jphpGiven ? "$jphpGiven|" : "") + 'com.mukadobo.json.schema'
		
		System.setProperty('java.protocol.handler.pkgs', jphpPlus)
	}
	
	/**
	 *
	 */
	static String toUrlText(
		Class   classRef,
		Boolean canonical = DEFAULT_CLASS_CANONICAL,
		String  prefix    = DEFAULT_PATH_PREFIX
	)
	{
		String classPathPart = (canonical
				? classRef.getName()
				: classRef.getName().replaceFirst(/^.*[.]/, "")
			)
			.replaceAll(/[.]/, "/")
			.replaceAll(/[$]/, ".")


		String path = ("${(!canonical && prefix) ? (prefix + '/') : ''}${classPathPart}" + RESOURCE_FILE_SUFFIX)
			.replaceFirst('^/+', "")

		"$SCHEME:$path"
	}
	
	static URL toUrl(
		Class classRef,
		Boolean canonical = true,
		String prefix = DEFAULT_PATH_PREFIX)
	{
		String urlText = toUrlText(classRef, canonical, prefix)

		new URL(urlText)
	}
	
	static ObjectSchema loadSchema(
		Class   classRef,
		Boolean canonical = DEFAULT_CLASS_CANONICAL,
		String  prefix    = DEFAULT_PATH_PREFIX
	)
	{
		String url = toUrlText(classRef, canonical, prefix)

		String referringSchemaJsonText = '''\
			{
				"$id": "AD-HOC",
				"$schema": "http://json-schema.org/draft-07/schema#",
				"$ref": "''' + url + '''"
			}
			'''.stripIndent()

		JSONObject referringSchemaJsonDom = new JSONObject(new JSONTokener(referringSchemaJsonText))

		SchemaLoader referringSchemaLoader = SchemaLoader
			.builder()
			.draftV6Support()
			.schemaJson(referringSchemaJsonDom)
			.build()

		ReferenceSchema referringSchema = (ReferenceSchema) referringSchemaLoader.load().build()

		(ObjectSchema) referringSchema.getReferredSchema()
	}

}
