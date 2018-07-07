package com.mukadobo.json.schema.classpath

/** A {@link URLStreamHandler} that handles resources on the classpath.
 * <P>
 * <P>Cribbed from: https://stackoverflow.com/a/1769454/5957643
 * <P>Answered by:  https://stackoverflow.com/users/37193/stephen
 *
 **/
class Handler extends URLStreamHandler
{
	/** The classloader to find resources from. */
	private final ClassLoader classLoader

	Handler() {
		this.classLoader = getClass().getClassLoader()
	}

	Handler(ClassLoader classLoader) {
		this.classLoader = classLoader
	}

	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		final URL resourceUrl = classLoader.getResource(u.getPath())
		if (resourceUrl == null) throw new IllegalArgumentException("no such resource: $u")
		return resourceUrl.openConnection()
	}
}