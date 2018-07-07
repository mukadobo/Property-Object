package com.mukadobo.json.schema.poc.command

/**
 * Follows the log4j-1.2 level names and ranking.
 *
 * @see {@code org.apache.log4f.Level}
 */
enum Verbosity {
	OFF,
	FATAL,
	ERROR,
	WARN,
	INFO,
	DEBUG,
	TRACE,
	ALL;
}