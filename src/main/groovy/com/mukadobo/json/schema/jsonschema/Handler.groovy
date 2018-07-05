package com.mukadobo.json.schema.jsonschema

import org.json.JSONException

/**
 * Handler for URL based JSON-Schema resource retrieval.
 * <BR><BR>
 * Compliant URL's have only a required PATH part and optional QUERY PARAM parts:
 * <UL>
 * <LI><B>tag=VERSION-REQUEST </B>(Default = "*") : This is the version request
 * for the resource itself.
 * This handler will provide the highest version of the resource available that meets
 * the request.
 * <BR><BR>
 * <STRONG>NOTE: </STRONG>The exact manner in which this request is satisfied
 * depends upon the handler variant (see below).
 * Specifically, a handler MAY "short-circuit" the
 * source search sequence as soon as it finds a qualifying resource version even though
 * a later source would provide a higher version.
 * <BR><BR>
 * </LI>
 * </LI>
 * <LI><B>src=SOURCE </B>(Default = "@", may be repeated) : Exactly what SOURCE values
 * are supported (and how they behave) depends on the handler variant in force,
 * but must at least allow for a value of "@" to indicate "the current class path."
 * <BR><BR>
 * <STRONG>NOTE: </STRONG>If any "src" parameters are given, then the class path will NOT
 * be searched unless it is explicitly requested. In this way, the URL can control the
 * order of searching.
 * <BR><BR>
 * <STRONG>NOTE: </STRONG>As per normal, when embedding URL-within-URL, the SOURCE value
 * MUST BE "semi" URI encoded. Full encoding is permitted, but characters in the set [:=?]
 * may be left unencoded. This allows for more human-readable embedded URL's.
 * <BR><BR>
 * </LI>
 * <LI><B>variant=VERSION </B>(Default = "0") : The version of the jsonschema handler itself.
 * When provided, the handler must verify this value and behave as per the
 * processing specifications as of the given version. If the variant is not
 * supported, then an exception MUST BE thrown.
 * <BR><BR>
 * <STRONG>NOTE: </STRONG>This is a specific version, not a range.
 * <BR><BR>
 * </LI>
 * </LI>
 * <LI><B>rigor=LEVEL : </B>Controls how the handler will respond to the presence
 * or absence of query parameters. Available LEVEL values and their exact meanings
 * depend upon the handler variant, but in general SHOULD include:
 * <TABLE>
 *     <TR><TH>LEVEL     </TH><TH>Use Defaults?</TH><TH>Extraneous Params</TH></TR>
 *     <TR><TD>permissive</TD><TD>Yes         </TD><TD>Ignore </TD></TR>
 *     <TR><TD>clean     </TD><TD>Yes         </TD><TD>Error  </TD></TR>
 *     <TR><TD>explicit  </TD><TD>No          </TD><TD>Ignore </TD></TR>
 *     <TR><TD>strict    </TD><TD>No          </TD><TD>Error  </TD></TR>
 * </TABLE>
 * <STRONG>NOTE: </STRONG>When not given, the default rigor is "clean"
 * <BR><BR>
 *     <HR>
 *         <H3>Handler Variant "0"</H3>
 *         In the initial variant of the protocol, many things are left not-yet-implemented.
 *         In particular, the ONLY part of the URL that is supported is the PATH portion, with
 *         all other parameters taking on "as if" values:
 *         <UL>
 *             <LI><B>tag=; </B> The empty value will short circuit the tag request mechanism.
 *             If available, only an "untagged" resource is retrieved.
 *             </LI>
 *             <LI><B>src=@; </B> A single SOURCE which indicates "search the class path."
 *             </LI>
 *             <LI><B>rigor=permissive; </B> Any extra stuff is ignored. In fact, the entire
 *             query part is ignored, even if it provides explicit values that are contrary
 *             to the above "as if" values.
 *             </LI>
 *         </UL>
 *
 **/
class Handler extends URLStreamHandler
{
	static final String  RESOURCE_FILE_SUFFIX    = "-schema.json"

	/** The classloader to find resources from. */
	private final ClassLoader classLoader

	Handler() {
		this.classLoader = getClass().getClassLoader();
	}

	Handler(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	protected URLConnection openConnection(URL u) throws IOException
	{
		final String path    = u.getPath().replaceAll(/[.]/, "/") + RESOURCE_FILE_SUFFIX

		// the "as-if" param values presumed by this handler variant

		final String tag     = ""
		final String sources = ["@"]
		final String variant = "0"
		final String rigor   = "permissive"

		final URL resourceUrl = classLoader.getResource(path)
		if (resourceUrl == null)
			throw new JSONException("no such resource: $u")

		return resourceUrl.openConnection()
	}
}