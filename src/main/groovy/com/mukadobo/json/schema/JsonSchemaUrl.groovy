package com.mukadobo.json.schema

class JsonSchemaUrl
{
	static final SCHEME = "jsonschema"

	/**
	 * Modify the system property {@code java.protocol.handler.pkgs} to include
	 * the package which provides {@code **.jsonschema.Handler}.
	 *
	 * @return The original value of the property, which may be null.
	 */
	static String registerHandlerPackage()
	{
		String jphpGiven = System.getProperty('java.protocol.handler.pkgs')
		String jphpPlus  = (jphpGiven ? "$jphpGiven|" : "") + 'com.mukadobo.json.schema'

		System.setProperty('java.protocol.handler.pkgs', jphpPlus)

		jphpGiven
	}

	/**
	 *
	 */
	static String toUrlText(Class classRef, Boolean canonical = true, String prefix = "jsonschema")
	{
		String classPathPart = (canonical
				? classRef.getName()
				: classRef.getName().replaceFirst(/^.*[.]/, "")
			)
			.replaceAll(/[.]/, "/")
			.replaceAll(/[$]/, ".")


		String path = "${(!canonical && prefix) ? (prefix + '/') : ''}${classPathPart}-schema.json"
			.replaceFirst('^/+', "")

		"$SCHEME:$path"
	}

	static URL toUrl(Class classRef, Boolean canonical = true, String prefix = "jsonschema")
	{
		String urlText = toUrlText(classRef, canonical, prefix)

		new URL(urlText)
	}
}
