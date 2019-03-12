/**
 * Parsers classes in this package are automatically indexed to ParserFactory, but there are some requirements:
 * - Name pattern: *Parser,
 * - *Parser class needs to implement Parser interface
 * - *Parser class needs to be a Spring @Component with "prototype" scope (every ParserThread gets his own instance)
 * - *Parser class needs to be package-private
 */
package pl.tscript3r.notify.monitor.parsers;