package com.mukadobo.json.schema

import org.apache.commons.io.IOUtils
import org.everit.json.schema.ObjectSchema
import org.everit.json.schema.Schema
import spock.lang.Specification

/**
 *
 */
class JsonSchemaHandlerTest extends Specification
{
	def "getContent"()
	{
		when:
			InputStream urlStream = (InputStream) JsonSchemaUrl.toUrl(refClass).getContent()

		then:
			urlStream != null

		when:
			String urlData = IOUtils.toString(urlStream, "UTF-8")

		then:
			urlData == expectedData

		where:
			refClass                  | expectedData
			EmptyResource      .class | ''
			SimpleResource     .class | '{"test":"SimpleResource"}'
	}

	def "Use by JSON Schema loader"()
	{
		setup:

			ObjectSchema        fstabRootSchema = JsonSchemaUrl.loadSchema(Fstab.class)
			Map<String, Schema> propertySchemas = fstabRootSchema.getPropertySchemas()


		expect:

			fstabRootSchema.getId() == 'jsonschema:jsonschema/Fstab'
			propertySchemas != null
			propertySchemas.size() == 1
			propertySchemas.entrySet().first().getValue().getReferredSchema().getId() == "http://example.com/entry-schema"
	}
}
