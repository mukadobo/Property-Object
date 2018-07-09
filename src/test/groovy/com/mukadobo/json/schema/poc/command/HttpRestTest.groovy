package com.mukadobo.json.schema.poc.command

import spock.lang.Specification

/**
 * TODO:
 * - Setup for local HTTP server response (using a simple in-memory server? 3PLib?)
 * - Unreachable server
 * - Bad port
 * - Alternate port
 * - TLS
 *   - not allowed
 *   - required
 *   - authentication
 *     - TLS required
 *     - header (token)
 *     - qparams
 *       - token
 *       - user/passwd
 *   - Server-cert not required (ala --insecure-skip-tls-cert)
 * - POST
 * - UPDATE
 * - DELETE
 * - HTTP Response data (3PLib?)
 *   - JSON
 *   - YAML
 *   - XML
 *   - text
 *   - binary
 * - perform() Result
 *   -
 */
class HttpRestTest extends Specification
{
}
