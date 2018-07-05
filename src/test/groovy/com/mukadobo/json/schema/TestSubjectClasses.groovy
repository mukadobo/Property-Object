package com.mukadobo.json.schema

// These classes have no associated "jsonschema" resource; they are only used to validate URL construction

class Outer
{
	class Inner {
		static class Static {}
	}
	static class Static {}
}


// These classes each as an associated "jsonschema" resource, one each in different locations on the classpath
// NOTE: As the names imply, all the resource files are empty (not actually valid JSON-Schema, but that's okay)

class EmptyResource  {}
class SimpleResource {}


// These classes are themselves empty, but have actual resources associated with them,
// which may (or may not) be junk data, well-formed-but-not-schema JSON, or well-formed JSON-Schema

class Fstab {}

class Command   {}
class Subject   {}
class Predicate {}
