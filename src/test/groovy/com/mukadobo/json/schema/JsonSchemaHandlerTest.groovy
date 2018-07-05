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
	def "Handler ~ getContent()"()
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

	def "Load schema from classpath - URL variant 0"()
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
