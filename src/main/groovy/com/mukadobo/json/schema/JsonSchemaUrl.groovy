package com.mukadobo.json.schema

class JsonSchemaUrl
{
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
}
